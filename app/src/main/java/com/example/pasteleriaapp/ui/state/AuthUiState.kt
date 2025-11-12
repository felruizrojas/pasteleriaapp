package com.example.pasteleriaapp.ui.state

import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import android.net.Uri

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val usuarioActual: Usuario? = null,
    val logoutSuccess: Boolean = false,
    val updateSuccess: Boolean = false,

    val fotoUri: Uri? = null,
    val loginCorreo: String = "",
    val loginContrasena: String = "",

    val regRun: String = "",
    val regNombre: String = "",
    val regApellidos: String = "",
    val regCorreo: String = "",
    val regFechaNacimiento: String = "",
    val regRegion: String = "",
    val regComuna: String = "",
    val regDireccion: String = "",
    val regContrasena: String = "",
    val regRepetirContrasena: String = "",
    val regCodigoPromo: String = "",

    val regRunError: String? = null,
    val regNombreError: String? = null,
    val regApellidosError: String? = null,
    val regCorreoError: String? = null,
    val regFechaNacimientoError: String? = null,
    val regRegionError: String? = null,
    val regComunaError: String? = null,
    val regDireccionError: String? = null,
    val regContrasenaError: String? = null,
    val regRepetirContrasenaError: String? = null,
    val regCodigoPromoError: String? = null,

    val profNombre: String = "",
    val profApellidos: String = "",
    val profRegion: String = "",
    val profComuna: String = "",
    val profDireccion: String = ""
)