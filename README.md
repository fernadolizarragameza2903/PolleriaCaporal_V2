# 🍗 Pollería Caporal - Sistema de Gestión

Sistema integral de gestión para "Pollería Caporal" desarrollado con **Java 24** y **Spring Boot 3.4** con autenticación segura, gestión de usuarios, productos y pedidos en tiempo real.

## ✨ Características Principales

### 🔐 Seguridad
- Autenticación con usuario/contraseña
- Control de acceso basado en roles (ADMIN, EMPLOYEE)
- Contraseñas encriptadas con BCrypt
- Spring Security configurado
- Protección CSRF

### 👥 Gestión de Usuarios
- Crear, editar y eliminar usuarios
- Asignación de roles (Administrador, Empleado)
- Activar/desactivar usuarios
- Seguimiento de creación y actualización

### 📦 Gestión de Productos
- Catálogo de productos con precios
- Control de stock en tiempo real
- Categorización de productos
- Estados activo/inactivo

### 🛒 Gestión de Pedidos
- Registro de nuevos pedidos
- Detalles de pedido con productos múltiples
- Cálculo automático de totales
- Información de cliente (nombre, teléfono, dirección)
- Seguimiento del estado del pedido

### 📊 Reportes y Estadísticas
- Dashboard administrativo con KPIs
- Reportes de ventas por período
- Historial de ventas por empleado
- Gráficos y visualizaciones

### 🎨 Interfaz Moderna
- Bootstrap 5 responsivo
- Thymeleaf para templates dinámicos
- Diseño profesional con gradientes
- Iconos con Font Awesome
- Navegación intuitiva

## 🏗️ Arquitectura

### Stack Tecnológico
```
Backend:
- Java 24 (Virtual Threads para optimización)
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- Hibernate ORM

Frontend:
- Thymeleaf
- Bootstrap 5
- Font Awesome 6.4
- Thymeleaf Layout Dialect

Base de Datos:
- PostgreSQL (Producción)
- H2 (Desarrollo/Testing)

Build:
- Maven 3.8+
```

### Patrón MVC + Servicios

```
Vistas (Thymeleaf) → Controladores → Servicios → Repositorios → Base de Datos
```

## 📋 Módulos

### Público
- Página de inicio (inicio.html)
- Formulario de login
- Información sobre la pollería

### Administrativo (ROLE_ADMIN)
- Dashboard con estadísticas
- Gestión de usuarios (CRUD)
- Gestión de productos
- Reportes de ventas detallados

### Empleados (ROLE_EMPLOYEE)
- Dashboard personal
- Crear y gestionar pedidos
- Ver historial de ventas
- Seguimiento de pedidos

## 🚀 Inicio Rápido

### Requisitos Previos
```
- Java 24+
- Maven 3.8+
- PostgreSQL 12+ (opcional, usa H2 para desarrollo)
```

### Pasos de Instalación

1. **Clonar o descargar el proyecto**
```bash
cd Polleria_Caporal
```

2. **Configurar la base de datos**

**Opción A: Desarrollo (H2 - automático)**
```yaml
# application.yml ya configurado
spring.profiles.active: dev
# Base de datos en memoria, no requiere configuración
```

**Opción B: Producción (PostgreSQL)**
```yaml
spring.profiles.active: prod
spring.datasource.url: jdbc:postgresql://localhost:5432/polleria_caporal
spring.datasource.username: postgres
spring.datasource.password: tu_password
```

3. **Compilar el proyecto**
```bash
mvn clean install
```

4. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

5. **Acceder a la aplicación**
```
URL: http://localhost:8080
Usuario: admin
Contraseña: admin123
```

## 📖 Documentación Completa

Para una documentación técnica completa incluyendo:
- Diagrama de base de datos
- Descripción detallada de entidades
- Relaciones entre tablas
- Rutas de API
- Ejemplos de uso

Consulta el archivo: [ARQUITECTURA.md](ARQUITECTURA.md)

## 🔑 Credenciales de Prueba

### Usuario Administrador
```
Username: admin
Contraseña: admin123
Rol: ROLE_ADMIN
```

### Usuario Empleado
```
Username: empleado
Contraseña: empleado123
Rol: ROLE_EMPLOYEE
```

## 📂 Estructura de Carpetas

```
Polleria_Caporal/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/polleriacaporal/
│   │   │       ├── config/          # Configuración (Security)
│   │   │       ├── controller/      # Controladores
│   │   │       ├── model/           # Entidades JPA
│   │   │       ├── repository/      # Repositorios
│   │   │       ├── service/         # Servicios
│   │   │       └── PolleriaCaporalApplication.java
│   │   └── resources/
│   │       ├── templates/           # Vistas Thymeleaf
│   │       │   ├── admin/
│   │       │   ├── empleado/
│   │       │   └── layout.html
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── img/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

## 🎯 Flujo de Uso

### Para Administrador

1. Acceder a `/login` con credenciales admin
2. Dashboard muestra estadísticas generales
3. Gestionar usuarios: crear, editar, eliminar
4. Gestionar productos: CRUD
5. Ver reportes: ventas por período, empleado, producto

### Para Empleado

1. Acceder a `/login` con credenciales de empleado
2. Dashboard personal con resumen del día
3. Crear nuevo pedido:
   - Datos del cliente
   - Seleccionar productos
   - Indicar cantidad
   - Notas especiales
4. Ver histórico de pedidos
5. Seguimiento de ventas

## 🛠️ Desarrollo

### Agregar Nueva Entidad

1. Crear clase en `model/`
2. Crear interfaz Repository en `repository/`
3. Crear Servicio en `service/`
4. Crear Controlador en `controller/`
5. Crear Vistas en `templates/`

## 🐛 Troubleshooting

### Error: "Base de datos no encontrada"
```bash
# Asegúrate que PostgreSQL esté corriendo o usa perfil dev (H2)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Error: "Puerto 8080 en uso"
```bash
# Cambiar puerto en application.yml
server:
  port: 8081
```

### Error: "Usuario no autenticado"
- Limpia cookies del navegador
- Intenta incógnito/privado
- Verifica credenciales en BD

## 📞 Soporte

Para problemas o sugerencias:
1. Revisar logs en `target/` o consola
2. Consultar ARQUITECTURA.md para detalles técnicos
3. Verificar configuración de base de datos

## 📝 Notas de Versión

### v0.0.1-SNAPSHOT (Actual)
- Estructura base completa
- Autenticación y autorización
- CRUD de usuarios
- Interfaz moderna con Bootstrap 5
- Gestión de pedidos básica
- Reportes iniciales

### Próximas Versiones
- API REST completa
- Aplicación móvil
- Integración de pagos
- Sistema de notificaciones
- Generación de reportes PDF

---

**Desarrollado con ❤️ usando Java 24 y Spring Boot 3.4**

**Última actualización:** Mayo 2024

Bebidas
Combos
Extras
Ver reportes
Gestionar usuarios
Gestionar pedidos
Ver historial de ventas
Control de stock
Configurar precios y promociones

2. Empleado (caja / atención) 🍗

Funciones principales:
Registrar pedidos
Ver menú
Generar boletas
Consultar estado de pedidos
Cobrar pedidos
Buscar pedidos por cliente o número
Aplicar promociones o descuentos
Registrar tipo de pago
Efectivo
Yape / Plin
Tarjeta

Módulos recomendados

Módulo de ventas
Registrar venta
Agregar productos al pedido
Calcular total
Aplicar descuentos
Generar comprobante

Módulo de productos
Crear producto
Editar producto
Eliminar producto
Clasificar por categorías
Controlar disponibilidad

Módulo de pedidos
Pedido pendiente
En preparación
Listo para entregar
Entregado

Módulo de reportes
Ventas por día
Ventas por semana
Ventas por mes
Productos más vendidos
Métodos de pago más usados

Módulo de usuarios
Administrador
Empleado
Permisos por rol

Flujo básico del sistema
El empleado ingresa al sistema.
Selecciona los productos del menú.
Registra el pedido.
El sistema calcula el total.
Se genera la boleta.
El pedido cambia de estado.
El administrador puede ver la venta en reportes.

Versión más formal para presentar tu idea
Sistema web para la gestión de una pollería
El sistema contará con dos tipos de usuarios: administrador y empleado.

Administrador:
podrá visualizar las ventas, gestionar productos, revisar reportes y administrar usuarios del sistema.

Empleado (caja / atención):
podrá registrar pedidos, visualizar el menú, generar boletas y consultar el estado de los pedidos.

Además, el sistema permitirá llevar un mejor control de ventas, productos, usuarios y atención al cliente.

Extra: funciones que te conviene agregar después
Dashboard con resumen de ventas
Impresión de boleta
Control de stock de insumos
Promociones y combos
Delivery
Registro de clientes frecuentes
Cierre de caja diario