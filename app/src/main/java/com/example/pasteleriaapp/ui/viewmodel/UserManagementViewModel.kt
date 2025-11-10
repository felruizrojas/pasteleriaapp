package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.state.UserManagementUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState(isLoading = true))
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    private var usuariosBase: List<Usuario> = emptyList()

    init {
        observarUsuarios()
    }

    fun configurarOperador(usuario: Usuario?) {
        _uiState.update { it.copy(operador = usuario) }
    }

    private fun observarUsuarios() {
        viewModelScope.launch {
            repository.observarUsuarios()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error al cargar usuarios"
                        )
                    }
                }
                .collectLatest { usuarios ->
                    usuariosBase = usuarios
                    _uiState.update { estadoActual ->
                        val filtrados = aplicarFiltros(
                            estadoActual.searchQuery,
                            estadoActual.filtroRol,
                            usuarios
                        )
                        estadoActual.copy(
                            isLoading = false,
                            error = null,
                            usuarios = usuarios,
                            usuariosFiltrados = filtrados
                        )
                    }
                }
        }
    }

    private fun aplicarFiltros(
        query: String,
        filtroRol: TipoUsuario?,
        usuarios: List<Usuario>
    ): List<Usuario> {
        val normalizado = query.trim()
        return usuarios.filter { usuario ->
            val coincideRol = filtroRol?.let { usuario.tipoUsuario == it } ?: true
            val coincideBusqueda = if (normalizado.isEmpty()) {
                true
            } else {
                val texto = listOf(
                    usuario.nombre,
                    usuario.apellidos,
                    usuario.correo,
                    usuario.run
                ).joinToString(" ")
                texto.contains(normalizado, ignoreCase = true)
            }
            coincideRol && coincideBusqueda
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { estado ->
            val filtrados = aplicarFiltros(query, estado.filtroRol, usuariosBase)
            estado.copy(
                searchQuery = query,
                usuariosFiltrados = filtrados
            )
        }
    }

    fun onFiltroRolChange(tipoUsuario: TipoUsuario?) {
        _uiState.update { estado ->
            val filtrados = aplicarFiltros(estado.searchQuery, tipoUsuario, usuariosBase)
            estado.copy(
                filtroRol = tipoUsuario,
                usuariosFiltrados = filtrados
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensaje = null, error = null) }
    }

    fun puedeGestionarUsuario(usuario: Usuario): Boolean = puedeGestionar(_uiState.value.operador, usuario)

    fun puedeEliminarUsuario(usuario: Usuario): Boolean = puedeEliminar(_uiState.value.operador, usuario)

    private fun puedeGestionar(operador: Usuario?, objetivo: Usuario): Boolean {
        val op = operador ?: return false
        if (op.tipoUsuario != TipoUsuario.superAdmin && op.tipoUsuario != TipoUsuario.Administrador) {
            return false
        }
        if (objetivo.tipoUsuario == TipoUsuario.superAdmin && op.tipoUsuario != TipoUsuario.superAdmin) {
            return false
        }
        return true
    }

    private fun puedeEliminar(operador: Usuario?, objetivo: Usuario): Boolean {
        if (!puedeGestionar(operador, objetivo)) return false
        if (operador?.idUsuario == objetivo.idUsuario) return false
        if (objetivo.tipoUsuario == TipoUsuario.superAdmin) {
            return operador?.tipoUsuario == TipoUsuario.superAdmin && operador.idUsuario != objetivo.idUsuario
        }
        return true
    }

    fun actualizarDatosBasicos(
        objetivo: Usuario,
        nombre: String,
        apellidos: String,
        region: String,
        comuna: String,
        direccion: String
    ) {
        val operador = _uiState.value.operador
        if (!puedeGestionar(operador, objetivo)) {
            _uiState.update { it.copy(mensaje = "No tienes permisos para editar este usuario.") }
            return
        }

        val nombreTrim = nombre.trim()
        val apellidosTrim = apellidos.trim()
        if (nombreTrim.isEmpty() || apellidosTrim.isEmpty()) {
            _uiState.update { it.copy(mensaje = "Nombre y apellidos no pueden estar vacíos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            try {
                val actualizado = objetivo.copy(
                    nombre = nombreTrim,
                    apellidos = apellidosTrim,
                    region = region.trim(),
                    comuna = comuna.trim(),
                    direccion = direccion.trim()
                )
                repository.actualizarUsuario(actualizado)
                _uiState.update { it.copy(mensaje = "Usuario actualizado correctamente.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al actualizar usuario.") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun cambiarRolUsuario(objetivo: Usuario, nuevoRol: TipoUsuario) {
        val operador = _uiState.value.operador
        if (!puedeGestionar(operador, objetivo)) {
            _uiState.update { it.copy(mensaje = "No tienes permisos para cambiar el rol de este usuario.") }
            return
        }

        if (objetivo.tipoUsuario == nuevoRol) {
            _uiState.update { it.copy(mensaje = "El usuario ya tiene ese rol.") }
            return
        }

        if (nuevoRol == TipoUsuario.superAdmin && operador?.tipoUsuario != TipoUsuario.superAdmin) {
            _uiState.update { it.copy(mensaje = "Solo un superadmin puede asignar este rol.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            try {
                repository.actualizarTipoUsuario(objetivo.idUsuario, nuevoRol)
                _uiState.update { it.copy(mensaje = "Rol actualizado correctamente.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al cambiar el rol.") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun alternarBloqueoUsuario(objetivo: Usuario) {
        val operador = _uiState.value.operador
        if (!puedeGestionar(operador, objetivo)) {
            _uiState.update { it.copy(mensaje = "No tienes permisos para cambiar el estado de este usuario.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val nuevoEstado = !objetivo.estaBloqueado
            if (nuevoEstado && operador?.idUsuario == objetivo.idUsuario) {
                _uiState.update {
                    it.copy(
                        isActionInProgress = false,
                        mensaje = "No puedes bloquear tu propia cuenta."
                    )
                }
                return@launch
            }
            try {
                repository.actualizarEstadoBloqueo(objetivo.idUsuario, nuevoEstado)
                val mensaje = if (nuevoEstado) "Usuario bloqueado." else "Usuario desbloqueado."
                _uiState.update { it.copy(mensaje = mensaje) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al actualizar el bloqueo.") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }

    fun eliminarUsuario(objetivo: Usuario) {
        val operador = _uiState.value.operador
        if (!puedeEliminar(operador, objetivo)) {
            _uiState.update { it.copy(mensaje = "No puedes eliminar este usuario.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            try {
                repository.eliminarUsuario(objetivo.idUsuario)
                _uiState.update { it.copy(mensaje = "Usuario eliminado correctamente.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al eliminar usuario.") }
            } finally {
                _uiState.update { it.copy(isActionInProgress = false) }
            }
        }
    }
}

class UserManagementViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserManagementViewModel::class.java)) {
            return UserManagementViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
