package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.Categoria

data class CategoriaUiState(
    val estaCargando: Boolean = false,
    val categorias: List<Categoria> = emptyList(), // Esta será la lista FILTRADA
    val error: String? = null,
    val searchQuery: String = "" // <-- CAMPO AÑADIDO
) {
    val hayCategorias: Boolean
        get() = categorias.isNotEmpty()
}