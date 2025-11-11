# PASTELERIA MIL SABORES - APP
# Pastelería Mil Sabores — App Móvil

> **Credenciales de prueba**
> - Superadmin: `ana@duoc.cl` / `123q`
> - Administrador: `luis@duoc.cl` / `123q`
> - Vendedor: `marcela@profesor.duoc.cl` / `123q`

## 🔗 Enlaces
- Repositorio GitHub: https://github.com/felruizrojas/PasteleriaApp.git
- Tablero Trello: https://trello.com/invite/b/68ffbb12ffdab6e0d9e1d482/ATTI5b1666790b7b98cc5ac0cefb41b937290A472219/pasteleria-app

## 📝 Descripción
Aplicación Android Jetpack Compose para la pastelería “Mil Sabores”. Permite a clientes navegar el catálogo, gestionar compras y seguir pedidos, mientras que el personal autorizado administra inventario, usuarios y órdenes en un panel integral.

## 🎯 Objetivo del proyecto
Digitalizar el flujo de venta y administración de la pastelería, ofreciendo una experiencia moderna tanto para clientes como para el equipo de ventas y gestión.

## ✅ Funcionalidades principales
1. Registro, autenticación y recuperación de sesión del cliente.
2. Exploración de categorías, productos y detalle con acciones de compartir.
3. Carrito, checkout y seguimiento de pedidos.
4. Panel administrativo con gestión de pedidos, catálogo y usuarios (según rol).
5. Integración con acciones rápidas como compartir productos y controles accesibles por voz en formularios.

## 🛠️ Tecnologías y herramientas
- Kotlin + Jetpack Compose Material 3
- Arquitectura MVVM con ViewModel y StateFlow
- Room / SQLite y DataStore para persistencia
- Coroutines para operaciones asíncronas
- Navigation Compose, Ktor HTTP Client (si aplica), utilidades de AndroidX
- Gradle (KTS) y KSP

## 📁 Estructura del proyecto (`app/src/main`)
```text
app/src/main/
├── java/com/example/pasteleriaapp/
│   ├── data/             # DAOs, base de datos y repositorios
│   ├── domain/           # Modelos y reglas de negocio
│   ├── ui/
│   │   ├── components/   # Scaffold, top bar, campos reutilizables
│   │   ├── navigation/   # NavGraph y rutas
│   │   ├── screen/       # Pantallas (auth, home, productos, admin, etc.)
│   │   └── theme/        # Paleta y temas Material
│   └── utils/            # Helpers y extensiones
└── res/                  # Recursos gráficos, layouts y strings
```

## 🧭 Mapa del sitio / flujo de pantallas
- Inicio → Catálogo → Detalle de producto → Carrito → Checkout.
- Inicio → Blog / Nosotros.
- Perfil → Edición, pedidos propios y accesos administrativos (según rol).
- Panel de administración → Pedidos | Productos y categorías | Usuarios.

## 🎨 Paleta de colores
| Nombre | Hex | Uso |
| --- | --- | --- |
| TituloMain | `#D67BA8` | Acentos y encabezados principales |
| TituloSecondary | `#5AA58D` | Botones secundarios / enlaces |
| TituloTertiary | `#C4A35A` | Destacados y estados especiales |
| PastelStrawberry | `#F7B7D1` | Fondos suaves |
| PastelMint | `#BFE2D5` | Secciones informativas |
| Ink | `#2A2A2A` | Texto principal |
| InkMuted | `#6C757D` | Texto secundario |

## 🧠 Validaciones clave
- RUN chileno: formato numérico con guion y dígito verificador (`0-9` o `k`).
- Nombre, apellidos, región y comuna: solo letras y espacios.
- Correo electrónico: requiere `@` y validación adicional en el ViewModel.
- Contraseña: restringida a caracteres alfanuméricos y coincidencia de confirmación.
- Reglas de negocio adicionales en checkout (descuentos por edad, código promocional y dominio DUOC).

## 📜 Licencia
Este proyecto fue desarrollado con fines académicos.
