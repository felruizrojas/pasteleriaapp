package com.example.pasteleriaapp.ui.navigation

object Rutas {
    const val ARG_ID_CATEGORIA = "idCategoria"
    const val ARG_ID_PRODUCTO = "idProducto"

    const val HOME = "home"
    const val CATEGORIAS = "categorias"
    private const val PRODUCTOS = "productos"

    const val PRODUCTOS_RUTA = "$PRODUCTOS/{$ARG_ID_CATEGORIA}"
    const val DETALLE_PRODUCTO_RUTA = "$PRODUCTOS/detalle/{$ARG_ID_PRODUCTO}"

    fun obtenerRutaProductos(idCategoria: Int): String {
        return "$PRODUCTOS/$idCategoria"
    }

    fun obtenerRutaDetalleProducto(idProducto: Int): String {
        return "$PRODUCTOS/detalle/$idProducto"
    }
}
