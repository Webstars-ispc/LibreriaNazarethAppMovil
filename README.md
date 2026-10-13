# Librería Nazareth - App Móvil 📚

Aplicación móvil Android para la gestión de inventario y ventas de la Librería Nazareth. Permite a los trabajadores iniciar sesión, gestionar el catálogo de productos (con escaneo de código de barras) y registrar ventas.

> **Estado**: proyecto académico en desarrollo (Sprint 2). Este README refleja el estado real implementado, incluyendo lo que todavía está pendiente. Ver sección [Estado del proyecto](#estado-del-proyecto).

---

## Estado del proyecto

### Implementado
- Login con JWT y manejo de expiración de token
- Registro de usuarios (público desde la app)
- CRUD de productos (listar, paginar, agregar, editar, eliminar)
- Búsqueda de productos por nombre y por código de barras (escaneo)
- CRUD de usuarios (solo administrador)
- Registro de ventas y listado de ventas
- Pantalla de contacto (muestra los datos de la librería)
- Configuración de usuario y cierre de sesión
- Tema claro/oscuro (DayNight)
- Tests unitarios de la lógica de login (11 casos)

### Pendiente / no implementado
- Filtros de catálogo por rubro y por marca (solo hay búsqueda por nombre/código)
- Envío real del formulario de contacto (la pantalla solo muestra los datos; no hay endpoint `/api/contacto/` en el backend)
- "Creación sugerida" de producto al escanear un código que no existe (el escaneo solo completa el campo de búsqueda)
- Exportación de APK y publicación en Releases
- Verificación de despliegue en producción

---

## Funcionalidades

### Autenticación y usuarios
- **Login**: autenticación con usuario/email y contraseña, token JWT
- **Registro**: alta de usuario desde la app
- **Gestión de usuarios** (administrador): listar, agregar, editar y eliminar usuarios
- **Roles**: `Empleado` y `Administrador` (el administrador ve las acciones de edición/eliminación)

### Gestión de catálogo
- **Listado paginado** de productos (10 por página)
- **Registro de productos**: nombre, descripción, código de barras, rubro, marca, precio costo, precio venta y stock
- **Edición** de productos existentes
- **Eliminación** con confirmación
- **Búsqueda** por nombre
- **Escaneo de código de barras** (ZXing) que completa el campo de búsqueda

### Ventas
- **Registro de venta**: selección de productos, cantidades y cálculo de subtotales/total
- **Listado de ventas** con detalle

### Navegabilidad
- Menú principal y barra de navegación inferior (`bottom_nav`)
- Botón "volver" desde cada Activity hija

### Contacto
- Muestra email, teléfono y dirección de la librería

---

## Pantallas de la App

| Pantalla | Descripción |
|----------|-------------|
| `SplashActivity` | Pantalla de inicio; valida el token |
| `LoginActivity` | Inicio de sesión |
| `RegistroActivity` | Registro de usuario |
| `MainActivity` | Menú principal |
| `CatalogoActivity` | Listado paginado de productos + búsqueda y escaneo |
| `FormularioCatalogoActivity` | Alta/edición de producto |
| `UsuariosActivity` | Listado de usuarios (admin) |
| `FormularioUsuarioActivity` | Alta/edición de usuario (admin) |
| `VentaActivity` | Registro de una venta |
| `VentasActivity` | Listado de ventas |
| `ContactoActivity` | Datos de contacto |
| `ConfiguracionActivity` | Configuración y cierre de sesión |
| `CaptureActivityPortrait` | Pantalla de captura de ZXing |

---

## Tecnologías

| Capa | Tecnología | Versión |
|------|-----------|---------|
| App móvil | Android (Java) | - |
| Compile/Target SDK | Android SDK | 34 |
| SDK mínimo | Android | API 24 (Android 7.0) |
| Backend | Django | 6.0.4 |
| API REST | Django REST Framework | 3.17.1 |
| Autenticación | SimpleJWT (JWT) | 5.5.1 |
| Base de datos | MySQL | mysqlclient 2.2.8 |
| Cliente HTTP | Retrofit + OkHttp + Gson | 2.11.0 / 4.12.0 |
| Escaneo de códigos | ZXing (zxing-android-embedded) | 4.3.0 |
| Control de versiones | Git / GitHub | - |

---

## Requerimientos Mínimos

- **Sistema operativo**: Android 7.0 (API 24) o superior
- **Conexión**: internet (WiFi o datos móviles) hacia el backend
- **Cámara**: necesaria para el escaneo de código de barras

---

## Instalación

### Para desarrolladores
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Webstars-ispc/LibreriaNazarethAppMovil.git
   ```
2. Abrir el proyecto en Android Studio
3. Sincronizar Gradle
4. Configurar la URL del backend en `local.properties` (raíz del proyecto):
   ```properties
   API_BASE_URL=http://10.0.2.2:8000/
   ```
   - `10.0.2.2` apunta al `localhost` de la máquina anfitriona desde el emulador.
   - Para un dispositivo físico usar la IP de la PC en la red local.
   - Si no se define, se usa por defecto `http://10.0.2.2:8000/`.
5. Ejecutar en emulador o dispositivo físico.

### Compilar un APK de release
La configuración de firma se lee desde `keystore.properties` (no versionado). Crear el archivo en la raíz con:
```properties
storeFile=...
storePassword=...
keyAlias=...
keyPassword=...
```
Luego:
```bash
./gradlew assembleRelease
```

> La exportación y publicación del APK en GitHub Releases todavía está pendiente.

---

## Tests

Se ejecutan con:
```bash
./gradlew testDebugUnitTest
```

Actualmente hay **11 tests unitarios** sobre la lógica de login (`LoginValidatorTest` y `LoginViewModelTest`).

---

## Seguridad

- **JWT**: token con expiración validada en `SplashActivity`
- **HTTPS obligatorio**: `android:usesCleartextTraffic="false"` + `network_security_config`
- **Sin backups**: `android:allowBackup="false"`
- **Ofuscación**: `minifyEnabled true` y `shrinkResources true`
- **Firma**: `signingConfigs` para el build de release
- **Roles**: permisos diferenciados para administrador y empleado
- **Validaciones**: contraseñas y campos validados antes de enviar

---

## Integrantes y Roles

| Nombre                           | Rol           | GitHub                                                     |
|----------------------------------|---------------|------------------------------------------------------------|
| Claudia Del Pilar Farias         | Scrum Master  | [@Claudiafarias2022](https://github.com/Claudiafarias2022) |
| Anabella Lujan Medrano           | Desarrollador | [@Anaabella](https://github.com/Anaabella)                 |
| Sofia Gimena Ledesma             | Desarrollador | [@SOFILEDESMA](https://github.com/SOFILEDESMA)             |
| Franco Agustin Trivini De Ejalde | Desarrollador | [@FrancoTrivini](https://github.com/FrancoTrivini)         |
| Jesica Analia Aramayo            | Desarrollador | [@Jesica-A](https://github.com/Jesica-A)                   |

---

## Licencia

Este proyecto fue desarrollado con fines académicos.
