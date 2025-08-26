# 📌 Upload Download Document (UDD) API Rest

UDD es un sistema que permite gestionar y validar los documentos de los sindicalizados
de la Universidad Autónoma de Chiapas.  
Su objetivo principal es **facilitar la administración documental**, garantizando seguridad
y control de acceso según roles.

---

## 📑 Contenido

- [Tecnologías usadas](#-tecnologías-usadas)
- [Características](#características)
- [Arquitectura](#-arquitectura)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Autenticación](#-autenticación)
- [Endpoints principales](#-endpoints-principales)
- [Ejemplos](#-ejemplos)
- [Contacto](#-contacto)

---

## 🚀 Tecnologías usadas
- **Framework**: Spring Boot
- **Base de datos**: MongoDB
- **Autenticación**: JWT (JSON Web Token)
- **Security**: SHA-256 + Salt, encriptación de documentos.

---

## Características
- API modular con servicios y controladores genéricos
- Registro y login de usuarios
- Autenticación con JWT y autorización basada en roles
- Encriptación de contraseñas y documentos
- Distinción de acceso entre usuarios y administradores

---

## 🛠️ Arquitectura

- `controller/`: Controladores REST
- `service/`: Lógica de negocio
- `repository/`: Consultas MongoDB
- `persistence/`: Modelo de negocio y mappers
- `configuration/`: Clases de configuración de Spring, Inyectables, Filtros
- `utils/`: Core del sistema

---

## 📦 Instalación

```bash
# Clonar el repositorio
git clone https://github.com/KevinFranG/udd-api

# Entrar al directorio
cd udd-api

# Instalar dependencias
mvn clean install
```

---

## 👤 Uso
Si cuentas con un IDE que sea compatible con maven y Spring Boot, te facilitará mucho
que puedan correr el servicio web; si no, puedes compilar el proyecto y correr el jar.

```bash
#correr el servicio web
java -jar target/UDDApi-0.0.1-SNAPSHOT.jar
```

Previamente, tienes que tener instalado `MongoDB` como servidor o tener una instancia válida
a la que el servicio pueda acceder. La configuración de las credenciales se encuentra en
`src/main/resources/application.properties`, solo debes cambiar la URI de MongoDB para que
apunte a tu instancia.

Este servicio utiliza jwt para la autenticación, los únicos recursos libres son los de 
autenticación, por lo que para poder utilizar los otros end points tendrás que enviar un
`BEARER Token` en la cabecera de la `request`. Para facilitar esto, se pueden usar programas
externos como Postman que son más amigables con el usuario.

---

### 🔑 Autenticación

Todos los end points (excepto login/register) requieren un **Bearer Token** en el header:
```text
Authorization: Bearer <Token>
```

---

## 📚 Endpoints principales

| Método | Endpoint                                        | Descripción                       |
|:-------|:------------------------------------------------|:----------------------------------|
| POST   | `udd/api/{maestros/admin}/auth/login`           | Iniciar sesión                    |
| POST   | `udd/api/{maestros/admin}/auth/register`        | Registrar usuario                 |
| POST   | `udd/api/{maestros/admin/facultad}`             | Crear entidad                     |
| GET    | `udd/api/{maestros/admin/facultad}`             | Obtener todos los datos           |
| PUT    | `udd/api/{maestros/admin/facultad}`             | Actualizar una entidad            |
| POST   | `udd/api/{maestros/admin/facultad}/where/id-is` | Obtener una entidad por id        |
| POST   | `udd/api/maestros/add/documentos`               | Añade un documento a un maestro   |
| POST   | `udd/api/documentos/as-pdf`                     | Obtener los bytes de un documento |

## 📦 Ejemplos

### Iniciar Sesión
```http request
POST /udd/api/admin/auth/login
Content-Type: application/json

{
    "correo": {
        "direccion": "correo@email.com"
    },
    "password": "password"
}
```

### Buscar por id
```http request
POST /udd/api/maestros/where/id-is
Content-Type: application/json

{
    "id": "object-id"
}
```

### Añadir Documento
```http request
POST /udd/api/maestros/add/documentos
Content-Type: application/json

{
    "id": "object-id",
    "rol": "maestro",
    "documentos": [
        {
            "formato": "ACTA_NACIMIENTO",
            "content": "pdf",
            "bytes": [...]
        }
    ]
}
```

### Cargar documento
```http request
POST /udd/api/documentos/as-pdf
Content-Type: application/json

{
    "maestro": {
        "id": "object-id",
        "rol": "maestro"
    },
    "documento": {
        "id": "object-id",
        "content": "pdf"
    }
}
```

---

### 🔗 Contacto
- **GitHub**: [KevinFranG](https://github.com/KevinFranG)
- **LinkedIn**: [kevinfrang](https://www.linkedin.com/in/kevinfrang)
- **Correo**: kevinfrancisco.dev18@gmail.com
