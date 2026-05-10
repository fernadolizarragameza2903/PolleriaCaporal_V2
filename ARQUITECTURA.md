# Pollería Caporal - Sistema de Gestión

## 📋 Descripción

Sistema de gestión integral para "Pollería Caporal" desarrollado con **Java 24** y **Spring Boot 3.4**, que integra un moderno sistema de autenticación, gestión de usuarios, productos, pedidos y reportes de ventas.

---

## 🏗️ Arquitectura

### Patrón MVC con Capa de Servicios

```
┌─────────────────────────────────────────────┐
│         Capa de Presentación (Web)          │
│      (Vistas Thymeleaf + Bootstrap 5)       │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         Capa de Controladores               │
│   (MainController, UsuarioController, etc)  │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         Capa de Servicios                   │
│    (UsuarioService, ProductoService, etc)   │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         Capa de Acceso a Datos              │
│    (Repositorios JPA - Spring Data)         │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         Base de Datos                       │
│    (PostgreSQL en Producción, H2 en Dev)    │
└─────────────────────────────────────────────┘
```

---

## 🔐 Seguridad y Autenticación

### Spring Security Configuration

- **Autenticación:** Basada en usuario/contraseña con BCryptPasswordEncoder
- **Roles:** 
  - `ROLE_ADMIN`: Acceso completo al sistema
  - `ROLE_EMPLOYEE`: Acceso limitado (pedidos y ventas)
- **Autorización:** Basada en roles mediante anotaciones `@PreAuthorize`
- **Contraseña:** Encriptadas con BCrypt (10 rondas de salto)

### Seguridad Implementada

```
- CSRF: Deshabilitado para desarrollo (activar en producción)
- Headers: Configurados para permitir iframes (H2 console)
- Session: Invalidada al logout
- Virtual Threads (Java 24): Optimización de peticiones concurrentes
```

---

## 📦 Estructura de Paquetes

```
com/polleriacaporal/
├── config/
│   ├── SecurityConfig.java           # Configuración de Spring Security
│   └── CustomUserDetailsService.java  # Servicio de autenticación
├── controller/
│   ├── MainController.java           # Controlador principal (rutas públicas)
│   ├── UsuarioController.java        # Gestión de usuarios (ADMIN)
│   ├── AdminController.java          # Dashboard admin
│   └── EmpleadoController.java       # Dashboard empleado
├── model/
│   ├── Usuario.java                  # Entidad Usuario
│   ├── Producto.java                 # Entidad Producto
│   ├── Pedido.java                   # Entidad Pedido
│   ├── DetallePedido.java            # Entidad Detalle Pedido
│   ├── RolUsuario.java               # Enum de roles
│   ├── EstadoVenta.java              # Enum de estados
│   └── Reporte.java                  # Entidad Reporte
├── repository/
│   ├── UsuarioRepository.java        # Repositorio Usuario
│   ├── ProductoRepository.java       # Repositorio Producto
│   ├── PedidoRepository.java         # Repositorio Pedido
│   ├── DetallePedidoRepository.java  # Repositorio Detalle Pedido
│   ├── ReporteRepository.java        # Repositorio Reporte
│   └── VentaRepository.java          # Repositorio Venta
├── service/
│   ├── UsuarioService.java           # Lógica de negocio Usuario
│   ├── ProductoService.java          # Lógica de negocio Producto
│   ├── PedidoService.java            # Lógica de negocio Pedido
│   └── ReporteService.java           # Lógica de negocio Reporte
└── PolleriaCaporalApplication.java   # Clase principal
```

---

## 🗄️ Modelo de Datos (JPA)

### Entidad Usuario
```java
- id: Long (PK)
- username: String (UNIQUE, NOT NULL)
- password: String (encriptado)
- nombreCompleto: String
- email: String
- telefono: String
- rol: RolUsuario (ENUM)
- estado: Boolean
- fechaCreacion: LocalDateTime
- fechaActualizacion: LocalDateTime
- pedidos: List<Pedido> (OneToMany)
```

### Entidad Producto
```java
- id: Long (PK)
- nombre: String (NOT NULL)
- descripcion: String
- categoria: String
- precio: BigDecimal
- stock: Integer
- estado: Boolean
- fechaCreacion: LocalDateTime
- fechaActualizacion: LocalDateTime
- detallesPedidos: List<DetallePedido> (OneToMany)
```

### Entidad Pedido
```java
- id: Long (PK)
- fechaPedido: LocalDateTime
- clienteNombre: String
- clienteTelefono: String
- clienteDireccion: String
- total: BigDecimal
- nota: String
- estado: EstadoVenta (ENUM)
- fechaEntrega: LocalDateTime
- usuario: Usuario (ManyToOne)
- detalles: List<DetallePedido> (OneToMany)
```

### Entidad DetallePedido
```java
- id: Long (PK)
- cantidad: Integer
- precioUnitario: BigDecimal
- subtotal: BigDecimal
- pedido: Pedido (ManyToOne)
- producto: Producto (ManyToOne)
```

### Relaciones

```
Usuario (1) ──────────── (N) Pedido
Pedido (1) ──────────── (N) DetallePedido
Producto (1) ──────────── (N) DetallePedido
```

---

## 🌐 Módulos y Rutas

### Módulo Público (Sin autenticación)

| Ruta | Descripción |
|------|-------------|
| `/` | Página de inicio |
| `/inicio` | Landing page |
| `/login` | Formulario de login |
| `/css/**` | Recursos CSS |
| `/js/**` | Recursos JavaScript |
| `/img/**` | Imágenes |

### Módulo Admin (ROLE_ADMIN)

| Ruta | Descripción |
|------|-------------|
| `/admin/dashboard` | Dashboard administrativo |
| `/usuarios` | Listar usuarios |
| `/usuarios/nuevo` | Crear usuario |
| `/usuarios/editar/{id}` | Editar usuario |
| `/usuarios/{id}` | Detalles de usuario |
| `/admin/productos` | Gestión de productos |
| `/admin/reportes` | Reportes de ventas |

### Módulo Empleado (ROLE_EMPLOYEE, ROLE_ADMIN)

| Ruta | Descripción |
|------|-------------|
| `/empleados/dashboard` | Dashboard del empleado |
| `/empleados/pedidos/nuevo` | Crear nuevo pedido |
| `/empleados/pedidos` | Listar mis pedidos |
| `/empleados/ventas` | Historial de ventas |

---

## 🎨 Frontend

### Bootstrap 5
- Componentes responsivos
- Grid system flexible
- Formularios validados
- Tablas con estilos
- Alertas y notificaciones

### Thymeleaf
- Layout principal (`layout.html`)
- Fragmentos reutilizables
- Integración con Spring Security (`sec:authorize`)
- Expresiones dinámicas

### Características Visuales
- Gradientes en navbar y hero
- Diseño moderno con tarjetas
- Iconos de Font Awesome 6.4
- Paleta de colores: Naranja quemado (#d84315) y Naranja (#f57c00)
- Sidebar colapsable
- Dashboard responsivo

---

## 📋 Configuración

### application.yml

```yaml
# Desarrollo (H2 Database)
spring.profiles.active: dev
spring.datasource.url: jdbc:h2:file:./data/polleria-caporal
spring.datasource.driver-class-name: org.h2.Driver

# Producción (PostgreSQL)
spring.datasource.url: jdbc:postgresql://localhost:5432/polleria_caporal
spring.datasource.username: postgres
spring.datasource.password: postgres

# JPA
spring.jpa.hibernate.ddl-auto: update
spring.jpa.properties.hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect

# Thymeleaf
spring.thymeleaf.cache: false
```

### pom.xml - Dependencias Principales

```xml
<!-- Spring Boot 3.4 -->
<version>4.0.6</version>

<!-- Spring Security -->
<dependency>spring-boot-starter-security</dependency>

<!-- Thymeleaf + Layout Dialect -->
<dependency>spring-boot-starter-thymeleaf</dependency>
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
    <version>3.3.0</version>
</dependency>

<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>

<!-- Validation -->
<dependency>spring-boot-starter-validation</dependency>
```

---

## 🚀 Características Especiales

### Java 24 Virtual Threads
- Optimización automática de peticiones concurrentes
- Mejora de rendimiento en operaciones I/O
- Compatible con Spring Boot 3.4+

### Auditoría
- Campos `fechaCreacion` y `fechaActualizacion` automáticos
- Decoradores `@PrePersist` y `@PreUpdate`

### Validación
- Anotaciones `@NotBlank`, `@Size`, `@Positive`
- Validación de formularios con Thymeleaf
- Manejo de errores en controladores

### Transacciones
- `@Transactional` en servicios
- `readOnly = true` para consultas
- Manejo de rollback automático

---

## 📝 Guía de Uso

### Crear Usuario Inicial (Admin)

```bash
# Base de datos H2 (desarrollo)
INSERT INTO usuarios (username, password, nombre_completo, rol, estado)
VALUES ('admin', 'BCrypt_encoded_password', 'Administrador', 'ROLE_ADMIN', true);
```

### Flujo de Autenticación

1. Usuario accede a `/login`
2. Ingresa credenciales (username/password)
3. `CustomUserDetailsService` busca en BD
4. Password validado con BCryptPasswordEncoder
5. Si es válido → Crea sesión → Redirige a `/dashboard`
6. Si es inválido → Muestra error y vuelve a `/login`

### Crear Pedido (Flujo Empleado)

1. Empleado accede a `/empleados/pedidos/nuevo`
2. Ingresa datos del cliente
3. Agrega productos y cantidad
4. Sistema calcula subtotal y total
5. Guarda pedido en BD
6. Reduce stock del producto
7. Registra en historial de ventas

---

## 🛠️ Desarrollo y Ejecución

### Requisitos
- Java 24+
- Maven 3.8+
- PostgreSQL 12+ (para producción)

### Ejecución Desarrollo
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Build Producción
```bash
mvn clean package
java -jar target/polleria-caporal-0.0.1-SNAPSHOT.jar
```

### Acceso

- **URL:** http://localhost:8080
- **Usuario demo:** admin / admin123

---

## 📊 Próximas Mejoras

- [ ] Generar reportes en PDF
- [ ] Integración con WhatsApp para notificaciones
- [ ] Dashboard con gráficos estadísticos
- [ ] Sistema de promociones y descuentos
- [ ] Gestión de inventario avanzada
- [ ] Integración de pagos online
- [ ] API REST para aplicación móvil
- [ ] Testing unitario y de integración

---

## 👨‍💼 Autor

Diseño Arquitectónico realizado como Software Architect.

**Tecnologías Utilizadas:** Java 24, Spring Boot 3.4, PostgreSQL, Bootstrap 5, Thymeleaf, Spring Security

---

**Última actualización:** Mayo 2024
