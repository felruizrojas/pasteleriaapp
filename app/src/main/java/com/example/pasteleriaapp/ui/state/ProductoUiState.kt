package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Producto

data class ProductoUiState(
    val estaCargando : Boolean = false,
    val productos : List<Producto> = emptyList(), // Esta será la lista FILTRADA
    val error: String? = null,
    val searchQuery: String = "" // <-- CAMPO AÑADIDO
) {
    val hayProductos : Boolean
        get() = productos.isNotEmpty()
}