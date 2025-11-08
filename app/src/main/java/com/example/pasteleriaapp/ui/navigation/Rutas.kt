package com.example.pasteleriaapp.ui.navigation

/**
 * Objeto que define todas las rutas y argumentos de navegación de la app.
 */
object Rutas {
    // Definición de los nombres de los argumentos
    const val ARG_ID_CATEGORIA = "idCategoria"
    const val ARG_ID_PRODUCTO = "idProducto"

    // Rutas base
    const val HOME = "home"
    const val CATEGORIAS = "categorias"
    private const val PRODUCTOS = "productos" // Prefijo para rutas de productos

    // --- Rutas Compuestas (con argumentos) ---

    // 1. Ruta para la lista de productos de UNA categoría
    //    Formato: "productos/{idCategoria}"
    const val PRODUCTOS_RUTA = "$PRODUCTOS/{$ARG_ID_CATEGORIA}"

    // 2. Ruta para el detalle de UN producto
    //    Formato: "productos/detalle/{idProducto}"
    //    (Usamos "detalle" para diferenciarla de la ruta de categoría)
    const val DETALLE_PRODUCTO_RUTA = "$PRODUCTOS/detalle/{$ARG_ID_PRODUCTO}"


    // --- Funciones auxiliares para construir las rutas dinámicas ---

    /**
     * Crea la ruta de navegación para ver los productos de una categoría específica.
     * @param idCategoria El ID de la categoría.
     * @return String de la ruta (ej: "productos/5")
     */
    fun obtenerRutaProductos(idCategoria: Int): String {
        return "$PRODUCTOS/$idCategoria"
    }

    /**
     * Crea la ruta de navegación para ver el detalle de un producto específico.
     * @param idProducto El ID del producto.
     * @return String de la ruta (ej: "productos/detalle/10")
     */
    fun obtenerRutaDetalleProducto(idProducto: Int): String {
        return "$PRODUCTOS/detalle/$idProducto"
    }
}