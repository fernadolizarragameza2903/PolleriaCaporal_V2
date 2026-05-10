# 🚀 Guía de Instalación y Configuración

## Requisitos del Sistema

Antes de comenzar, asegúrate de tener instalado:

### Software Requerido

| Software | Versión Mínima | Descarga |
|----------|---|----------|
| Java JDK | 24 | https://www.oracle.com/java/technologies/downloads/ |
| Maven | 3.8.0+ | https://maven.apache.org/download.cgi |
| Git (opcional) | Última | https://git-scm.com/ |
| PostgreSQL (opcional) | 12+ | https://www.postgresql.org/download/ |

### Verificar Instalación

```bash
# Verificar Java
java -version
# Debe mostrar: openjdk 24.x.x o superior

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.8.0 o superior
```

---

## 📦 Instalación del Proyecto

### Paso 1: Clonar o Descargar el Proyecto

**Opción A: Con Git**
```bash
git clone https://github.com/usuario/Polleria_Caporal.git
cd Polleria_Caporal
```

**Opción B: Descargar ZIP**
- Descargar archivo ZIP del repositorio
- Extraer en carpeta deseada
- Abrir terminal en esa carpeta

### Paso 2: Actualizar Maven

```bash
cd Polleria_Caporal
mvn clean install
```

Este comando:
- Descarga todas las dependencias
- Compila el proyecto
- Ejecuta tests (si existen)

---

## ⚙️ Configuración de Base de Datos

### Opción 1: Desarrollo (H2 - Sin Instalación)

**Ventaja:** No requiere instalación adicional, ideal para desarrollo

```yaml
# El archivo application.yml ya está configurado:
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:h2:file:./data/polleria-caporal
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
```

**Para acceder a H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/polleria-caporal
- User: sa
- Password: (vacío)

### Opción 2: Producción (PostgreSQL)

#### 1. Instalar PostgreSQL

**Windows:**
```
Descargar desde: https://www.postgresql.org/download/windows/
Ejecutar instalador
Recordar contraseña de usuario postgres
```

**macOS:**
```bash
brew install postgresql@15
brew services start postgresql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo service postgresql start
```

#### 2. Crear Base de Datos

```bash
# Conectarse a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE polleria_caporal;

# Crear usuario (opcional)
CREATE USER polleria WITH PASSWORD 'polleria123';
GRANT ALL PRIVILEGES ON DATABASE polleria_caporal TO polleria;

# Verificar
\l  # Listar bases de datos
\q  # Salir
```

#### 3. Configurar Conexión

Editar `src/main/resources/application.yml`:

```yaml
spring:
  profiles:
    active: prod
  
  datasource:
    url: jdbc:postgresql://localhost:5432/polleria_caporal
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: tu_password_aqui
    hikari:
      maximum-pool-size: 20
  
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
```

---

## 🔧 Ejecutar la Aplicación

### Opción 1: Desde Maven (Recomendado para Desarrollo)

```bash
# Terminal en raíz del proyecto
mvn spring-boot:run
```

**Salida esperada:**
```
...
Started PolleriaCaporalApplication in 5.234 seconds
INFO: Tomcat started on port(s): 8080 (http)
```

### Opción 2: Desde JAR (Producción)

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/polleria-caporal-0.0.1-SNAPSHOT.jar
```

### Opción 3: Desde IDE

**IntelliJ IDEA:**
1. Click derecho en `PolleriaCaporalApplication.java`
2. Run 'PolleriaCaporalApplication'

**Eclipse:**
1. Click derecho en proyecto
2. Run As → Spring Boot App

**Visual Studio Code:**
1. Instalar extensión "Spring Boot Extension Pack"
2. Click en botón "Run" junto al método main

---

## 🔐 Acceso Inicial

### URL
```
http://localhost:8080
```

### Credenciales de Prueba

#### Administrador
```
Usuario: admin
Contraseña: admin123
```

#### Empleado
```
Usuario: empleado
Contraseña: empleado123
```

---

## 🛠️ Problemas Comunes y Soluciones

### Error: "Port 8080 is already in use"

**Solución 1:** Cambiar puerto en `application.yml`
```yaml
server:
  port: 8081
```

**Solución 2:** Matar proceso que usa puerto
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Error: "Could not find PostgreSQL driver"

**Solución:**
```bash
mvn clean install
```

Asegúrate que la dependencia PostgreSQL está en `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

### Error: "Connection to database failed"

**Verificar:**
1. PostgreSQL está corriendo: `sudo service postgresql status`
2. Credenciales en `application.yml` son correctas
3. Base de datos existe: `createdb -U postgres -l`
4. Puerto: Por defecto PostgreSQL usa 5432

### Error: "No suitable driver found"

**Solución:**
- Cambiar a perfil `dev` (H2) temporalmente:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Aplicación lenta al iniciar

- Es normal la primera vez (descargas dependencias)
- Subsecuentes deberían ser más rápidas (~5-10 segundos)

---

## 📝 Estructura de Carpetas Generada

Después de ejecutar, se crea:

```
Polleria_Caporal/
├── data/                      # Base de datos H2
│   └── polleria-caporal.mv.db
├── target/                    # Compilación
│   ├── classes/
│   ├── generated-sources/
│   └── polleria-caporal-0.0.1-SNAPSHOT.jar
├── src/
├── pom.xml
├── README.md
├── ARQUITECTURA.md
└── SETUP.md (este archivo)
```

---

## 🔄 Actualizar Dependencias

```bash
# Verificar dependencias obsoletas
mvn versions:display-dependency-updates

# Actualizar a versiones más recientes
mvn versions:use-latest-releases
```

---

## 📊 Variables de Entorno (Opcional)

Para seguridad en producción, usar variables de entorno:

```bash
# Linux/Mac
export DB_URL=jdbc:postgresql://localhost:5432/polleria_caporal
export DB_USER=postgres
export DB_PASSWORD=tu_password
export SERVER_PORT=8080

# Windows (PowerShell)
$env:DB_URL="jdbc:postgresql://localhost:5432/polleria_caporal"
$env:DB_USER="postgres"
$env:DB_PASSWORD="tu_password"
$env:SERVER_PORT="8080"
```

Luego en `application.yml`:
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

server:
  port: ${SERVER_PORT:8080}
```

---

## ✅ Checklist de Instalación

- [ ] Java 24+ instalado y verificado
- [ ] Maven 3.8+ instalado y verificado
- [ ] Proyecto descargado/clonado
- [ ] Dependencias descargadas (`mvn clean install`)
- [ ] Base de datos configurada (H2 o PostgreSQL)
- [ ] Aplicación iniciada correctamente
- [ ] Acceso a http://localhost:8080
- [ ] Login con credenciales de prueba funcionando
- [ ] Dashboard visible después de login

---

## 📚 Próximos Pasos

1. **Explorar la aplicación:**
   - Crear usuarios
   - Registrar productos
   - Hacer pedidos de prueba

2. **Familiarizarse con la estructura:**
   - Revisar [ARQUITECTURA.md](ARQUITECTURA.md)
   - Explorar código fuente

3. **Desarrollo:**
   - Agregar nuevas características
   - Modificar diseño
   - Integrar nuevos módulos

---

## 📞 Soporte y Recursos

- **Documentación Spring Boot:** https://spring.io/projects/spring-boot
- **PostgreSQL Docs:** https://www.postgresql.org/docs/
- **Bootstrap Docs:** https://getbootstrap.com/docs/
- **Thymeleaf Docs:** https://www.thymeleaf.org/documentation.html

---

**¡Sistema listo para usar!** 🎉

Si encuentras problemas, revisa los logs en la consola y verifica los pasos anteriores.

