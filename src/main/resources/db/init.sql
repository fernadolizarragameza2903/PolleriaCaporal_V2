-- ========================================================
-- SQL de inicialización completo para PostgreSQL
-- Pollería Caporal
-- ========================================================

-- SECCIÓN 1: EXTENSIONES
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SECCIÓN 2: TIPOS ENUM
CREATE TYPE rol_usuario AS ENUM
  ('ROLE_ADMIN', 'ROLE_EMPLOYEE');

CREATE TYPE estado_venta AS ENUM
  ('PENDIENTE', 'RECIBIDO', 'COMPLETO', 'CANCELADO');

-- SECCIÓN 3: TABLA usuarios
CREATE TABLE usuarios (
  id                 BIGSERIAL PRIMARY KEY,
  username           VARCHAR(50) NOT NULL UNIQUE,
  password           VARCHAR(255) NOT NULL,
  nombre_completo    VARCHAR(100),
  email              VARCHAR(100),
  telefono           VARCHAR(20),
  rol                VARCHAR(20) NOT NULL DEFAULT 'ROLE_EMPLOYEE'
                     CHECK (rol IN ('ROLE_ADMIN','ROLE_EMPLOYEE')),
  estado             BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_usuarios_username UNIQUE (username)
);

-- SECCIÓN 4: TABLA productos
CREATE TABLE productos (
  id                  BIGSERIAL PRIMARY KEY,
  nombre              VARCHAR(150) NOT NULL,
  descripcion         TEXT,
  categoria           VARCHAR(50),
  precio              NUMERIC(10,2) NOT NULL
                      CONSTRAINT chk_precio_positivo CHECK (precio > 0),
  stock               INTEGER NOT NULL DEFAULT 0
                      CONSTRAINT chk_stock_positivo CHECK (stock >= 0),
  estado              BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SECCIÓN 5: TABLA pedidos
CREATE TABLE pedidos (
  id                  BIGSERIAL PRIMARY KEY,
  fecha_pedido        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cliente_nombre      VARCHAR(100),
  cliente_telefono    VARCHAR(20),
  cliente_direccion   VARCHAR(200),
  subtotal            NUMERIC(12,2) NOT NULL DEFAULT 0.00
                      CONSTRAINT chk_subtotal_positivo CHECK (subtotal >= 0),
  igv                 NUMERIC(12,2) NOT NULL DEFAULT 0.00
                      CONSTRAINT chk_igv_positivo CHECK (igv >= 0),
  total               NUMERIC(12,2) NOT NULL DEFAULT 0.00
                      CONSTRAINT chk_total_positivo CHECK (total >= 0),
  nota                TEXT,
  estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                      CONSTRAINT chk_estado_valido CHECK (estado IN ('PENDIENTE','RECIBIDO','COMPLETO','CANCELADO')),
  fecha_entrega       TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  usuario_id          BIGINT NOT NULL,
  CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

-- SECCIÓN 6: TABLA detalles_pedido
CREATE TABLE detalles_pedido (
  id               BIGSERIAL PRIMARY KEY,
  cantidad         INTEGER NOT NULL
                   CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0),
  precio_unitario  NUMERIC(10,2) NOT NULL
                   CONSTRAINT chk_precio_unit_positivo CHECK (precio_unitario > 0),
  subtotal         NUMERIC(12,2) NOT NULL
                   CONSTRAINT chk_subtotal_detalle CHECK (subtotal >= 0),
  pedido_id        BIGINT NOT NULL,
  producto_id      BIGINT NOT NULL,
  CONSTRAINT fk_detalles_pedido FOREIGN KEY (pedido_id)
    REFERENCES pedidos(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_detalles_producto FOREIGN KEY (producto_id)
    REFERENCES productos(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

-- SECCIÓN 7: TABLAS AUXILIARES
CREATE TABLE empleados (
  id             BIGSERIAL PRIMARY KEY,
  nombres        VARCHAR(100),
  apellidos      VARCHAR(100),
  dni            VARCHAR(15) UNIQUE,
  telefono       VARCHAR(20),
  cargo          VARCHAR(50),
  fecha_ingreso  DATE,
  estado         BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE reportes (
  id               BIGSERIAL PRIMARY KEY,
  fecha            DATE NOT NULL,
  total_ventas     NUMERIC(12,2) DEFAULT 0.00,
  cantidad_ventas  INTEGER DEFAULT 0,
  observacion      TEXT
);

CREATE TABLE ventas (
  id        BIGSERIAL PRIMARY KEY,
  cliente   VARCHAR(100),
  productos TEXT,
  total     NUMERIC(12,2),
  estado    VARCHAR(20) DEFAULT 'COMPLETO'
            CHECK (estado IN ('PENDIENTE','RECIBIDO','COMPLETO','CANCELADO')),
  fecha     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SECCIÓN 8: ÍNDICES
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_estado ON productos(estado);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_fecha ON pedidos(fecha_pedido);
CREATE INDEX idx_pedidos_usuario ON pedidos(usuario_id);
CREATE INDEX idx_detalles_pedido_id ON detalles_pedido(pedido_id);
CREATE INDEX idx_detalles_producto_id ON detalles_pedido(producto_id);

-- SECCIÓN 9: TRIGGERS
CREATE OR REPLACE FUNCTION fn_actualizar_fecha_usuarios()
RETURNS trigger AS $$
BEGIN
  NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_actualizar_fecha_usuarios
BEFORE UPDATE ON usuarios
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_usuarios();

CREATE OR REPLACE FUNCTION fn_actualizar_fecha_productos()
RETURNS trigger AS $$
BEGIN
  NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_actualizar_fecha_productos
BEFORE UPDATE ON productos
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_productos();

CREATE OR REPLACE FUNCTION fn_actualizar_fecha_pedidos()
RETURNS trigger AS $$
BEGIN
  NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_actualizar_fecha_pedidos
BEFORE UPDATE ON pedidos
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_pedidos();

CREATE OR REPLACE FUNCTION fn_calcular_subtotal_detalle()
RETURNS trigger AS $$
BEGIN
  IF NEW.precio_unitario IS NOT NULL AND NEW.cantidad IS NOT NULL THEN
    NEW.subtotal := NEW.precio_unitario * NEW.cantidad;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_calcular_subtotal_detalle
BEFORE INSERT OR UPDATE ON detalles_pedido
FOR EACH ROW EXECUTE FUNCTION fn_calcular_subtotal_detalle();

CREATE OR REPLACE FUNCTION fn_recalcular_total_pedido()
RETURNS trigger AS $$
DECLARE
  total_con_igv NUMERIC(12,2);
  subtotal_sin_igv NUMERIC(12,2);
  igv_calc NUMERIC(12,2);
BEGIN
  SELECT COALESCE(SUM(subtotal), 0)
    INTO total_con_igv
    FROM detalles_pedido
    WHERE pedido_id = COALESCE(NEW.pedido_id, OLD.pedido_id);

  subtotal_sin_igv := CASE WHEN total_con_igv > 0
    THEN ROUND(total_con_igv / 1.18, 2)
    ELSE 0.00 END;
  igv_calc := ROUND(total_con_igv - subtotal_sin_igv, 2);

  UPDATE pedidos
  SET subtotal = subtotal_sin_igv,
      igv = igv_calc,
      total = total_con_igv
  WHERE id = COALESCE(NEW.pedido_id, OLD.pedido_id);

  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_recalcular_total_pedido
AFTER INSERT OR UPDATE OR DELETE ON detalles_pedido
FOR EACH ROW EXECUTE FUNCTION fn_recalcular_total_pedido();

CREATE OR REPLACE FUNCTION fn_reducir_stock_producto()
RETURNS trigger AS $$
DECLARE
  nuevo_stock INTEGER;
BEGIN
  UPDATE productos
  SET stock = stock - NEW.cantidad
  WHERE id = NEW.producto_id
  RETURNING stock INTO nuevo_stock;

  IF nuevo_stock < 0 THEN
    RAISE EXCEPTION 'Stock insuficiente para el producto %', NEW.producto_id;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_reducir_stock_producto
AFTER INSERT ON detalles_pedido
FOR EACH ROW EXECUTE FUNCTION fn_reducir_stock_producto();

CREATE OR REPLACE FUNCTION fn_restaurar_stock_producto()
RETURNS trigger AS $$
BEGIN
  UPDATE productos
  SET stock = stock + OLD.cantidad
  WHERE id = OLD.producto_id;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_restaurar_stock_producto
AFTER DELETE ON detalles_pedido
FOR EACH ROW EXECUTE FUNCTION fn_restaurar_stock_producto();

-- SECCIÓN 10: DATOS INICIALES
INSERT INTO usuarios
  (username, password, nombre_completo, rol, estado)
VALUES
  ('admin',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Administrador Principal',
   'ROLE_ADMIN',
   TRUE),
  ('empleado',
   '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1OHkf9ThC',
   'Mesero Prototipo',
   'ROLE_EMPLOYEE',
   TRUE);

INSERT INTO productos
  (nombre, descripcion, categoria, precio, stock, estado)
VALUES
  ('1/4 de Pollo a la brasa con papas y ensalada',
   'Porción de 1/4 de pollo a la brasa acompañado de papas fritas y ensalada fresca',
   'Pollo a la brasa', 20.00, 50, TRUE),
  ('1/2 Pollo a la brasa con papas y ensalada',
   'Media porción de pollo a la brasa con papas y ensalada',
   'Pollo a la brasa', 38.00, 40, TRUE),
  ('1 Pollo entero a la brasa con papas y ensalada',
   'Pollo entero a la brasa con papas fritas y ensalada',
   'Pollo a la brasa', 70.00, 20, TRUE),
  ('Inka Kola 1.5 Litros',
   'Gaseosa Inka Kola botella de 1.5 litros',
   'Bebidas', 12.00, 60, TRUE),
  ('Coca Cola 1.5 Litros',
   'Gaseosa Coca Cola botella de 1.5 litros',
   'Bebidas', 12.00, 60, TRUE),
  ('Inka Kola 3 Litros',
   'Gaseosa Inka Kola botella familiar de 3 litros',
   'Bebidas', 20.00, 40, TRUE),
  ('Combo Mostrito (1/4 pollo + chaufa + papas)',
   'Combo especial con cuarto de pollo, arroz chaufa y papas',
   'Promociones', 24.00, 30, TRUE),
  ('Porción de papas fritas extra',
   'Porción adicional de papas fritas crocantes',
   'Extras', 10.00, 100, TRUE),
  ('Porción de Arroz Chaufa extra',
   'Porción adicional de arroz chaufa',
   'Extras', 12.00, 80, TRUE),
  ('Anticuchos (3 palitos con papa)',
   'Tres palitos de anticucho acompañados de papa',
   'Parrillas', 18.00, 50, TRUE);

-- SECCIÓN 11: VISTAS ÚTILES
CREATE OR REPLACE VIEW vista_ventas_por_empleado AS
SELECT
  u.id AS usuario_id,
  u.username,
  u.nombre_completo,
  COUNT(p.id) AS total_pedidos,
  SUM(p.total) AS monto_total,
  AVG(p.total) AS promedio_venta,
  MAX(p.fecha_pedido) AS ultima_venta
FROM usuarios u
LEFT JOIN pedidos p ON u.id = p.usuario_id
  AND p.estado != 'CANCELADO'
GROUP BY u.id, u.username, u.nombre_completo;

CREATE OR REPLACE VIEW vista_productos_mas_vendidos AS
SELECT
  pr.id AS producto_id,
  pr.nombre,
  pr.categoria,
  pr.precio,
  pr.stock,
  COALESCE(SUM(dp.cantidad), 0) AS total_vendido,
  COALESCE(SUM(dp.subtotal), 0) AS ingreso_total
FROM productos pr
LEFT JOIN detalles_pedido dp ON pr.id = dp.producto_id
LEFT JOIN pedidos p ON dp.pedido_id = p.id
  AND p.estado != 'CANCELADO'
GROUP BY pr.id, pr.nombre, pr.categoria, pr.precio, pr.stock
ORDER BY total_vendido DESC;

CREATE OR REPLACE VIEW vista_pedidos_detalle AS
SELECT
  p.id AS pedido_id,
  p.fecha_pedido,
  p.cliente_nombre,
  p.cliente_telefono,
  p.cliente_direccion,
  p.estado,
  p.subtotal,
  p.igv,
  p.total,
  p.nota,
  u.username AS empleado,
  u.nombre_completo AS nombre_empleado,
  COUNT(dp.id) AS cantidad_items
FROM pedidos p
JOIN usuarios u ON p.usuario_id = u.id
LEFT JOIN detalles_pedido dp ON p.id = dp.pedido_id
GROUP BY p.id, p.fecha_pedido, p.cliente_nombre, p.cliente_telefono, p.cliente_direccion,
         p.estado, p.subtotal, p.igv, p.total, p.nota,
         u.username, u.nombre_completo
ORDER BY p.fecha_pedido DESC;
