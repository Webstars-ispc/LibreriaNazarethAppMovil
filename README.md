# Librería Nazareth - App Móvil 📚
Aplicación móvil Android para la gestión de inventario y ventas de la Librería Nazareth. Permite a los trabajadores registrar productos, escanear códigos de barras, gestionar el catálogo y realizar ventas de manera ágil y eficiente.

---

## Funcionalidades

### Gestión de Catálogo
- **Registro de productos**: Formulario completo con nombre, descripción, código de barras, rubro, marca, precio costo, precio venta y stock
- **Edición de productos**: Modificar datos de productos existentes
- **Eliminación de productos**: Quitar productos del catálogo con confirmación
- **Lista de productos**: Vista del catálogo completo con información resumida

### Búsqueda y Filtros
- **Búsqueda por nombre**: Encontrar productos escribiendo su nombre
- **Búsqueda por código de barras**: Localizar producto mediante escaneo
- **Filtros por rubro**: Filtrar productos por categoría
- **Filtros por marca**: Filtrar productos por marca

### Escaneo de Código de Barras
- **Cámara integrada**: Escanear códigos de barras desde la app
- **Carga rápida**: Identificar productos existentes al escanear
- **Creación sugerida**: Si el producto no existe, sugerir crearlo

### Seguridad
- **Login seguro**: Autenticación con email y contraseña
- **JWT**: Tokens de autenticación con expiración de 24 horas
- **Roles diferenciados**: Administrador y Vendedor con permisos distintos

### Navegabilidad
- **Navegación intuitiva**: Acceso rápido a todas las funciones
- **Botón volver**: Siempre se puede volver desde una Activity hija a su padre
- **Paso de información**: Transferencia de datos entre Activities

### Contacto
- **Datos de la librería**: Email, teléfono y dirección de contacto
- **Información de ubicación**: Mapa o referencia de ubicación

---

## Pantallas de la App

| Pantalla | Descripción |
|----------|-------------|
| `LoginActivity` | Inicio de sesión con email y contraseña |
| `MainActivity` | Menú principal con opciones de navegación |
| `ListaCatalogoActivity` | Lista completa de productos del catálogo |
| `DetalleProductoActivity` | Información detallada de un producto |
| `FormularioCatalogoActivity` | Formulario para agregar/editar productos |

---

## Tecnologías

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Frontend Móvil | Android (Java) | - |
| SDK Android | Android SDK | API 21+ (Android 5.0) |
| Backend | Django | 5.x |
| API REST | Django REST Framework | 3.x |
| Base de datos | MySQL | 8.x |
| Autenticación | JWT | - |
| Networking | Retrofit | 2.x |
| Escaneo | ZXing / ML Kit | - |
| Control de versiones | Git / GitHub | - |

---

## Requerimientos Mínimos

- **Sistema operativo**: Android 5.0 (API 21) o superior
- **Conexión**: Internet (WiFi o datos móviles)
- **Cámara**: Necesaria para escaneo de código de barras

---

## Instalación

### Para usuarios
1. Descargar el archivo APK desde la sección de Releases
2. Habilitar "Fuentes desconocidas" en configuración de Android
3. Instalar la aplicación
4. Abrir y completar los datos de conexión al servidor

### Para desarrolladores
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Webstars-ispc/LibreriaNazarethAppMovil.git
   ```
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar en emulador o dispositivo físico

---

## Seguridad

- **Autenticación JWT**: Tokens con expiración de 24 horas
- **Contraseñas robustas**: Mínimo 8 caracteres, alfanuméricos y especiales
- **HTTPS**: Toda la comunicación se realiza de forma cifrada
- **Control de sesiones**: Gestión de inicio y cierre de sesión
- **Roles**: Permisos diferenciados para administrador y vendedor

---

## Integrantes y Roles

| Nombre                           | Rol           | GitHub                                                     |
|----------------------------------|---------------|------------------------------------------------------------|
| Claudia Del Pilar Farias         | Scrum Master  | [@Claudiafarias2022](https://github.com/Claudiafarias2022) |
| Anabella Lujan Medrano           | Desarollador  | [@Anaabella](https://github.com/Anaabella)                 |
| Sofia Gimena Ledesma             | Desarrollador | [@SOFILEDESMA](https://github.com/SOFILEDESMA)             |
| Franco Agustin Trivini De Ejalde | Desarrollador | [@FrancoTrivini](https://github.com/FrancoTrivini)         |
| Jesica Analia Aramayo            | Desarrollador | [@Jesica-A](https://github.com/Jesica-A)                   |

---

## Licencia

Este proyecto fue desarrollado con fines académicos.