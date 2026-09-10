# Historias de Usuario - Librería Nazareth App Móvil

## Sprint 1

---

### #US-01 Escaneo de código de barras

Como vendedor quiero escanear el código de barras de un producto. Para encontrarlo rápidamente sin escribir.

Dado que el vendedor está en la pantalla de catálogo. Cuando presiona el ícono de escáner y apunta la cámara al código de barras. Entonces se muestran los datos del producto (nombre, precio, stock).

**Criterios de Aceptación:**
- CA01: La app debe acceder a la cámara del dispositivo
- CA02: Al escanear un código existente, se muestran los datos del producto
- CA03: Al escanear un código inexistente, se sugiere crear un nuevo producto
- CA04: El escaneo se completa en menos de 3 segundos

**Contexto:** El vendedor necesita identificar un producto rápidamente durante la venta sin escribir el nombre
**Dependencia:** Ninguna
**Módulo:** Catálogo
**Prioridad:** Alta

---

### #US-02 Registro de producto

Como administrador quiero registrar un nuevo producto con su código, precio y stock. Para mantener el catálogo actualizado.

Dado que el administrador está en el formulario de registro de producto. Cuando completa los campos nombre, rubro y precio venta (obligatorios) y presiona Guardar. Entonces se crea el producto y se muestra mensaje de confirmación.

**Criterios de Aceptación:**
- CA01: El formulario tiene campos: nombre, descripción, código de barras, rubro, marca, precio costo, precio venta, stock
- CA02: Los campos nombre, rubro y precio venta son obligatorios
- CA03: Al guardar se muestra mensaje de confirmación
- CA04: Si falta un campo obligatorio se muestra error

**Contexto:** El administrador necesita cargar nuevos productos al catálogo cuando llega mercadería
**Dependencia:** Ninguna
**Módulo:** Catálogo
**Prioridad:** Alta

---

### #US-03 Búsqueda de productos

Como vendedor quiero buscar un producto por nombre o código. Para encontrarlo rápidamente durante una venta.

Dado que el vendedor está en la pantalla de búsqueda. Cuando escribe el nombre o código del producto. Entonces se muestran los resultados que coinciden con la búsqueda en menos de 2 segundos.

**Criterios de Aceptación:**
- CA01: Se puede buscar escribiendo nombre o código
- CA02: Se puede filtrar por rubro y marca
- CA03: Los resultados se muestran en una lista
- CA04: La búsqueda es instantánea (menos de 2 segundos)

**Contexto:** El vendedor necesita encontrar un producto rápido durante la atención al cliente
**Dependencia:** US-02
**Módulo:** Catálogo
**Prioridad:** Alta

---

### #US-04 Confirmación de venta

Como vendedor quiero confirmar una venta y obtener un comprobante. Para entregárselo al cliente o guardarlo.

Dado que el vendedor tiene productos en el carrito. Cuando presiona "Confirmar venta". Entonces se descuenta el stock, se genera un comprobante con fecha, productos y total, y se muestra mensaje de éxito.

**Criterios de Aceptación:**
- CA01: Al confirmar se descuenta el stock automáticamente
- CA02: Se genera un comprobante con fecha, productos y total
- CA03: El comprobante se puede visualizar en pantalla
- CA04: Se muestra un mensaje de confirmación

**Contexto:** El vendedor necesita finalizar la venta y entregar un comprobante al cliente
**Dependencia:** US-02
**Módulo:** Ventas
**Prioridad:** Alta

---

### #US-05 Historial de ventas

Como administrador quiero ver el historial de ventas del día. Para controlar los ingresos.

Dado que el administrador está en la pantalla de historial. Cuando selecciona la fecha del día. Entonces se muestra la lista de ventas con fecha, productos, total y el acumulado del día.

**Criterios de Aceptación:**
- CA01: Se muestra la lista de ventas del día
- CA02: Cada venta muestra fecha, productos y total
- CA03: Se puede filtrar por fecha
- CA04: Se muestra el total acumulado del día

**Contexto:** El administrador necesita revisar cuánto se vendió en el día para controlar ingresos
**Dependencia:** US-04
**Módulo:** Ventas
**Prioridad:** Media

---

### #US-06 Alerta de stock bajo

Como administrador quiero recibir una alerta cuando el stock de un producto esté por agotarse. Para reponer a tiempo.

Dado que el administrador inicia sesión en la app. Cuando hay productos con stock menor a 5 unidades. Entonces se muestra una alerta con la lista de productos con stock bajo.

**Criterios de Aceptación:**
- CA01: Se muestra una alerta cuando stock < 5
- CA02: La alerta aparece al iniciar sesión
- CA03: Se puede ver la lista de productos con stock bajo
- CA04: Cada producto muestra el stock actual

**Contexto:** El administrador necesita saber cuándo un producto se está agotando para reponerlo a tiempo
**Dependencia:** US-02
**Módulo:** Inventario
**Prioridad:** Baja

---

### #US-07 Formulario de producto

Como vendedor quiero ver el formulario de producto en la app. Para registrar productos desde el celular.

Dado que el vendedor presiona "Nuevo producto". Cuando se abre el formulario. Entonces se muestran todos los campos (nombre, descripción, código de barras, rubro, marca, precio costo, precio venta, stock) con sus labels y botones Guardar/Cancelar.

**Criterios de Aceptación:**
- CA01: El formulario tiene todos los campos requeridos
- CA02: Los spinners muestran rubros y marcas disponibles
- CA03: Los botones Guardar y Cancelar funcionan correctamente
- CA04: El formulario es responsive en diferentes tamaños de pantalla

**Contexto:** El vendedor necesita una pantalla para cargar productos desde el celular
**Dependencia:** Ninguna
**Módulo:** Catálogo
**Prioridad:** Alta

---

### #US-08 Catálogo de productos

Como vendedor quiero ver el catálogo en la app. Para revisar los productos disponibles.

Dado que el vendedor accede a la pantalla de catálogo. Cuando se cargan los productos desde la API. Entonces se muestra una lista con nombre, precio y stock de cada producto, con scroll si hay muchos.

**Criterios de Aceptación:**
- CA01: Se muestra una lista con nombre, precio y stock
- CA02: Se puede hacer scroll si hay muchos productos
- CA03: Al tocar un producto se muestra su detalle
- CA04: Se muestra un mensaje si no hay productos

**Contexto:** El vendedor necesita ver la lista de productos disponibles para revisar stock
**Dependencia:** US-02
**Módulo:** Catálogo
**Prioridad:** Alta

---

### #US-09 Login seguro

Como usuario quiero iniciar sesión en la app. Para acceder al sistema de forma segura.

Dado que el usuario abre la app. Cuando ingresa email y contraseña correctos y presiona "Iniciar sesión". Entonces se valida el token JWT y se accede al menú principal.

**Criterios de Aceptación:**
- CA01: Se ingresan email y contraseña
- CA02: Si son correctos se accede al menú principal
- CA03: Si son incorrectos se muestra error "Credenciales inválidas"
- CA04: La contraseña se envía de forma segura (HTTPS)

**Contexto:** El usuario necesita autenticarse para acceder al sistema de forma segura
**Dependencia:** Ninguna
**Módulo:** Seguridad
**Prioridad:** Alta

---

### #US-10 Pantalla de contacto

Como usuario quiero ver la pantalla de contacto. Para comunicarme con la librería.

Dado que el usuario presiona "Contacto" en el menú. Cuando se abre la pantalla de contacto. Entonces se muestra email, teléfono y dirección de la librería, con opción de llamar tocando el teléfono.

**Criterios de Aceptación:**
- CA01: Se muestra email de contacto
- CA02: Se muestra teléfono
- CA03: Se muestra dirección
- CA04: Se puede llamar directamente tocando el teléfono

**Contexto:** El usuario necesita comunicarse con la librería para consultas o soporte
**Dependencia:** Ninguna
**Módulo:** General
**Prioridad:** Media

---

### #US-11 Navegabilidad

Como usuario quiero navegar entre pantallas de la app y poder volver atrás fácilmente. Para tener una experiencia fluida.

Dado que el usuario está en una Activity hija (Detalle, Formulario, Contacto). Cuando presiona el botón volver o la flecha de atrás. Entonces regresa a la Activity padre sin perder datos.

**Criterios de Aceptación:**
- CA01: Todas las Activities tienen botón de retorno
- CA02: El botón volver regresa a la Activity padre
- CA03: No se pierden datos al navegar entre pantallas
- CA04: La navegación es fluida sin pantallas en blanco

**Contexto:** El usuario necesita moverse entre pantallas y volver atrás sin perderse en la app
**Dependencia:** Ninguna
**Módulo:** General
**Prioridad:** Alta

---

### #US-12 Recurso multimedia

Como usuario quiero ver una imagen o video de la librería en la app. Para conocer el local.

Dado que el usuario accede a la pantalla de multimedia. Cuando se carga el recurso. Entonces se muestra la imagen o se reproduce el video correctamente, con opción de zoom.

**Criterios de Aceptación:**
- CA01: Se muestra al menos una imagen de la librería
- CA02: La imagen se carga correctamente
- CA03: Se puede hacer zoom en la imagen
- CA04: El recurso multimedia no ralentiza la app

**Contexto:** El usuario quiere conocer la librería visualmente a través de imágenes o video
**Dependencia:** Ninguna
**Módulo:** General
**Prioridad:** Media

---

## Tabla resumen

| ID | Historia de Usuario | Rol | Contexto | Evento (Dado/Cuando/Entonces) | Resultado | Prioridad | Dependencia | Módulo |
|----|---------------------|-----|----------|-------------------------------|-----------|-----------|-------------|--------|
| US-01 | Escanear código de barras | Vendedor | Identificar producto rápido durante venta | Dado catálogo, cuando escanea código, entonces muestra datos | Producto encontrado | Alta | Ninguna | Catálogo |
| US-02 | Registro de producto | Administrador | Cargar productos nuevos al catálogo | Dado formulario, cuando completa campos y guarda, entonces crea producto | Producto creado | Alta | Ninguna | Catálogo |
| US-03 | Búsqueda de productos | Vendedor | Encontrar producto rápido durante atención | Dado búsqueda, cuando escribe nombre/código, entonces muestra resultados | Lista encontrados | Alta | US-02 | Catálogo |
| US-04 | Confirmación de venta | Vendedor | Finalizar venta y entregar comprobante | Dado carrito, cuando confirma venta, entonces descuenta stock y genera comprobante | Comprobante generado | Alta | US-02 | Ventas |
| US-05 | Historial de ventas | Administrador | Revisar ventas del día para controlar ingresos | Dado historial, cuando selecciona fecha, entonces muestra ventas del día | Lista de ventas | Media | US-04 | Ventas |
| US-06 | Alerta de stock bajo | Administrador | Saber cuándo reponer productos | Dado login, cuando hay stock < 5, entonces muestra alerta | Alerta visible | Baja | US-02 | Inventario |
| US-07 | Formulario de producto | Vendedor | Pantalla para cargar productos desde celular | Dado nuevo producto, cuando abre formulario, entonces muestra campos y botones | Formulario visible | Alta | Ninguna | Catálogo |
| US-08 | Catálogo de productos | Vendedor | Ver lista de productos para revisar stock | Dado catálogo, cuando carga productos, entonces muestra lista con scroll | Lista visible | Alta | US-02 | Catálogo |
| US-09 | Login seguro | Usuario | Autenticarse para acceder al sistema | Dado login, cuando ingresa credenciales correctas, entonces accede al menú | Menú principal | Alta | Ninguna | Seguridad |
| US-10 | Pantalla de contacto | Usuario | Comunicarse con la librería | Dado contacto, cuando abre pantalla, entonces muestra datos de la librería | Datos visibles | Media | Ninguna | General |
| US-11 | Navegabilidad | Usuario | Moverse entre pantallas y volver atrás | Dado Activity hija, cuando presiona volver, entonces regresa a Activity padre | Navegación fluida | Alta | Ninguna | General |
| US-12 | Recurso multimedia | Usuario | Conocer la librería visualmente | Dado multimedia, cuando carga recurso, entonces muestra imagen/video | Multimedia visible | Media | Ninguna | General |
