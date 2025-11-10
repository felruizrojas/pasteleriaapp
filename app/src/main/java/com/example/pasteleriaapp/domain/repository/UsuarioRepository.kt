package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    suspend fun login(correo: String, contrasena: String): Usuario?
    suspend fun registrarUsuario(usuario: Usuario)

    suspend fun actualizarUsuario(usuario: Usuario)
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario?
    suspend fun obtenerUsuarioPorId(idUsuario: Int): Usuario?

    fun observarUsuarios(): Flow<List<Usuario>>
    suspend fun actualizarTipoUsuario(idUsuario: Int, tipoUsuario: TipoUsuario)
    suspend fun actualizarEstadoBloqueo(idUsuario: Int, estaBloqueado: Boolean)
    suspend fun eliminarUsuario(idUsuario: Int)
}