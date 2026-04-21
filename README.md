# Gym Manager

Sistema de gestión de miembros para gimnasios. Permite registrar miembros, controlar pagos, fechas de vencimiento y generar reportes en Excel.

---

## Tecnologías

**Backend**
- Kotlin + Spring Boot 4.0.5
- PostgreSQL 18
- Apache POI (reportes Excel)
- Gradle

**Frontend**
- React + Vite
- Axios

---

## Requisitos previos

- [Java 17](https://adoptium.net/temurin/releases/?version=17)
- [Node.js 18+](https://nodejs.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Git](https://git-scm.com/)

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/chunchiter/gym-registry.git
cd gym-registry
```

### 2. Levantar la base de datos con Docker

```bash
docker compose up -d
```

Esto crea un contenedor PostgreSQL con:

| Campo    | Valor    |
|----------|----------|
| Host     | localhost |
| Puerto   | 5432     |
| Base de datos | gymdb |
| Usuario  | gymuser  |
| Contraseña | gympass |

> Si prefieres usar tu PostgreSQL local, edita `src/main/resources/application.properties` con tus credenciales y crea manualmente la base de datos `gymdb`.

### 3. Configurar el backend

Abre `src/main/resources/application.properties` y verifica:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gymdb
spring.datasource.username=gymuser
spring.datasource.password=gympass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Correr el backend

```bash
./gradlew bootRun
```

El backend estará disponible en `http://localhost:8080`

> Las tablas se crean automáticamente al iniciar por primera vez.

### 5. Instalar y correr el frontend

```bash
cd gym-frontend
npm install
npm run dev
```

El frontend estará disponible en `http://localhost:5173`

---

## Estructura del proyecto

```
gym-registry/
├── src/
│   └── main/
│       └── kotlin/com/gymapp/gymmanager/
│           ├── controller/       # Endpoints REST
│           ├── service/          # Lógica de negocio
│           ├── repository/       # Acceso a base de datos
│           ├── entity/           # Modelos de base de datos
│           └── dto/              # Objetos de transferencia
├── gym-frontend/
│   └── src/
│       ├── components/           # Modales y componentes
│       ├── App.jsx               # Componente principal
│       └── App.css               # Estilos
├── docker-compose.yml
└── build.gradle.kts
```

---

## Endpoints del API

### Miembros

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/members` | Lista todos los miembros |
| GET | `/api/members/{id}` | Obtiene un miembro por ID |
| POST | `/api/members` | Crea un nuevo miembro |
| PUT | `/api/members/{id}` | Edita un miembro |
| DELETE | `/api/members/{id}` | Elimina un miembro |

### Membresías

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/memberships` | Lista todas las membresías |
| GET | `/api/memberships/member/{id}` | Historial de pagos de un miembro |
| POST | `/api/memberships` | Registra un nuevo pago |
| GET | `/api/memberships/expired` | Lista membresías vencidas |
| GET | `/api/memberships/expiring-soon` | Lista por vencer en 5 días |

### Reportes

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/reports/members` | Descarga reporte Excel completo |
| GET | `/api/reports/template` | Descarga plantilla para importar |
| POST | `/api/reports/import` | Importa miembros desde Excel |

---

## Funcionalidades

- **Dashboard** con conteo de miembros por estado (al día, por vencer, vencidos, inactivos)
- **Tabla de miembros** con colores por estado
- **Búsqueda y filtros** por estado
- **Nuevo miembro** con registro de membresía en un solo formulario
- **Renovar membresía** directamente desde la tabla
- **Editar miembro** con opción de marcar como inactivo
- **Eliminar miembro** con confirmación
- **Historial de pagos** al hacer clic en el nombre del miembro
- **Reporte Excel** con 3 hojas: resumen, miembros y historial completo
- **Importar desde Excel** con plantilla descargable y protección contra duplicados

---

## Estados de membresía

| Estado | Color | Descripción |
|--------|-------|-------------|
| Al día | Verde | Vencimiento mayor a 5 días |
| Por vencer | Amarillo | Vence en menos de 5 días |
| Vencido | Rojo | Fecha de vencimiento pasada |
| Sin membresía | Rojo | No tiene ningún pago registrado |
| Inactivo | Gris | Miembro marcado como inactivo |

---

## Importar miembros desde Excel

1. Descarga la plantilla desde el botón **Importar Excel** → **Descargar plantilla**
2. Llena los campos respetando el formato:

| Campo | Formato | Ejemplo |
|-------|---------|---------|
| nombre | Texto | Juan Pérez |
| telefono | Número | 9611234567 |
| email | Texto | juan@gmail.com |
| fechaPago | YYYY-MM-DD | 2026-04-20 |
| fechaVencimiento | YYYY-MM-DD | 2026-05-20 |
| montoPagado | Número | 500 |
| metodoPago | Texto | Efectivo / Transferencia / Tarjeta |

3. Sube el archivo desde **Paso 2**
4. Si un miembro con el mismo teléfono y fecha de pago ya existe, se omite automáticamente

---

## Comandos útiles

```bash
# Iniciar base de datos
docker compose up -d

# Detener base de datos
docker compose down

# Ver logs del backend
./gradlew bootRun

# Build del frontend para producción
cd gym-frontend
npm run build
```

---

## Notas de desarrollo

- Las tablas `members` y `memberships` se crean automáticamente al iniciar el backend
- El frontend corre en el puerto `5173` y hace proxy al backend en `8080`
- Al eliminar un miembro se eliminan automáticamente todas sus membresías asociadas
- La detección de vencimiento se calcula en tiempo real comparando con la fecha actual

---

## Versión

**V1.0** — Gestión básica de miembros, pagos y reportes Excel
