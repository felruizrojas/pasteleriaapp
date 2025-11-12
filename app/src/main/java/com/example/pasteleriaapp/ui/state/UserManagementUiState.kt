package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario

data class UserManagementUiState(
    val isLoading: Boolean = false,
    val isActionInProgress: Boolean = false,
    val usuarios: List<Usuario> = emptyList(),
    val usuariosFiltrados: List<Usuario> = emptyList(),
    val searchQuery: String = "",
    val filtroRol: TipoUsuario? = null,
    val error: String? = null,
    val mensaje: String? = null,
    val operador: Usuario? = null
)
