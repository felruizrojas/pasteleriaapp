package com.example.pasteleriaapp.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario
import com.example.pasteleriaapp.ui.components.AppScaffold
import com.example.pasteleriaapp.ui.components.AppTopBarActions
import com.example.pasteleriaapp.ui.viewmodel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosAdminScreen(
    viewModel: UserManagementViewModel,
    currentUser: Usuario,
    onBackClick: () -> Unit,
    badgeCount: Int,
    isLoggedIn: Boolean,
    topBarActions: AppTopBarActions,
    onLogout: (() -> Unit)?
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(currentUser.idUsuario) {
        viewModel.configurarOperador(currentUser)
    }

    LaunchedEffect(state.mensaje, state.error) {
        val mensaje = state.mensaje ?: state.error
        if (!mensaje.isNullOrBlank()) {
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            viewModel.limpiarMensajes()
        }
    }

    var usuarioParaEditar by remember { mutableStateOf<Usuario?>(null) }
    var usuarioParaRol by remember { mutableStateOf<Usuario?>(null) }
    var usuarioParaBloqueo by remember { mutableStateOf<Usuario?>(null) }
    var usuarioParaEliminar by remember { mutableStateOf<Usuario?>(null) }

    AppScaffold(
        badgeCount = badgeCount,
        isLoggedIn = isLoggedIn,
        topBarActions = topBarActions,
        pageTitle = "Gestión de Usuarios",
        onLogout = onLogout
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            TextButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                Spacer(Modifier.width(8.dp))
                Text("Volver")
            }

            if (state.isActionInProgress) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Buscar por nombre, correo o RUN") },
                singleLine = true
            )

            RoleFilterRow(
                selectedRole = state.filtroRol,
                onRoleSelected = viewModel::onFiltroRolChange
            )

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.usuariosFiltrados.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se encontraron usuarios con los filtros aplicados.")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(state.usuariosFiltrados, key = { it.idUsuario }) { usuario ->
                            UsuarioCard(
                                usuario = usuario,
                                puedeGestionar = viewModel.puedeGestionarUsuario(usuario),
                                puedeEliminar = viewModel.puedeEliminarUsuario(usuario),
                                onEditar = { usuarioParaEditar = it },
                                onCambiarRol = { usuarioParaRol = it },
                                onCambiarBloqueo = { usuarioParaBloqueo = it },
                                onEliminar = { usuarioParaEliminar = it },
                                accionesHabilitadas = !state.isActionInProgress
                            )
                        }
                    }
                }
            }
        }
    }

    if (usuarioParaEditar != null) {
        EditarUsuarioDialog(
            usuario = usuarioParaEditar!!,
            onDismiss = { usuarioParaEditar = null },
            onConfirm = { actualizado ->
                viewModel.actualizarDatosBasicos(
                    objetivo = usuarioParaEditar!!,
                    nombre = actualizado.nombre,
                    apellidos = actualizado.apellidos,
                    region = actualizado.region,
                    comuna = actualizado.comuna,
                    direccion = actualizado.direccion
                )
                usuarioParaEditar = null
            },
            accionesDeshabilitadas = state.isActionInProgress
        )
    }

    if (usuarioParaRol != null) {
        CambiarRolDialog(
            usuario = usuarioParaRol!!,
            onDismiss = { usuarioParaRol = null },
            onConfirm = { nuevoRol ->
                viewModel.cambiarRolUsuario(usuarioParaRol!!, nuevoRol)
                usuarioParaRol = null
            },
            accionesDeshabilitadas = state.isActionInProgress
        )
    }

    if (usuarioParaBloqueo != null) {
        ConfirmacionDialogoSimple(
            titulo = if (usuarioParaBloqueo!!.estaBloqueado) "Desbloquear usuario" else "Bloquear usuario",
            mensaje = if (usuarioParaBloqueo!!.estaBloqueado) {
                "¿Deseas desbloquear a ${usuarioParaBloqueo!!.nombre}?"
            } else {
                "¿Deseas bloquear a ${usuarioParaBloqueo!!.nombre}?"
            },
            confirmLabel = if (usuarioParaBloqueo!!.estaBloqueado) "Desbloquear" else "Bloquear",
            onDismiss = { usuarioParaBloqueo = null },
            onConfirm = {
                viewModel.alternarBloqueoUsuario(usuarioParaBloqueo!!)
                usuarioParaBloqueo = null
            },
            accionesDeshabilitadas = state.isActionInProgress
        )
    }

    if (usuarioParaEliminar != null) {
        ConfirmacionDialogoSimple(
            titulo = "Eliminar usuario",
            mensaje = "Esta acción no se puede deshacer. ¿Eliminar a ${usuarioParaEliminar!!.nombre}?",
            confirmLabel = "Eliminar",
            onDismiss = { usuarioParaEliminar = null },
            onConfirm = {
                viewModel.eliminarUsuario(usuarioParaEliminar!!)
                usuarioParaEliminar = null
            },
            accionesDeshabilitadas = state.isActionInProgress
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleFilterRow(
    selectedRole: TipoUsuario?,
    onRoleSelected: (TipoUsuario?) -> Unit
) {
    val scrollState = rememberScrollState()
    val roles = listOf<TipoUsuario?>(null) + TipoUsuario.values().toList()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        roles.forEach { rol ->
            val label = rol?.toDisplayName() ?: "Todos"
            FilterChip(
                selected = selectedRole == rol,
                onClick = { onRoleSelected(if (rol == selectedRole) null else rol) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors()
            )
        }
    }
}

@Composable
private fun UsuarioCard(
    usuario: Usuario,
    puedeGestionar: Boolean,
    puedeEliminar: Boolean,
    accionesHabilitadas: Boolean,
    onEditar: (Usuario) -> Unit,
    onCambiarRol: (Usuario) -> Unit,
    onCambiarBloqueo: (Usuario) -> Unit,
    onEliminar: (Usuario) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${usuario.nombre} ${usuario.apellidos}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = usuario.correo,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (usuario.estaBloqueado) {
                    Text(
                        text = "Bloqueado",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(text = "RUN: ${usuario.run}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Rol: ${usuario.tipoUsuario.toDisplayName()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Región: ${usuario.region}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Comuna: ${usuario.comuna}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onEditar(usuario) },
                    enabled = puedeGestionar && accionesHabilitadas
                ) { Text("Editar") }

                OutlinedButton(
                    onClick = { onCambiarRol(usuario) },
                    enabled = puedeGestionar && accionesHabilitadas
                ) { Text("Cambiar rol") }

                OutlinedButton(
                    onClick = { onCambiarBloqueo(usuario) },
                    enabled = puedeGestionar && accionesHabilitadas
                ) {
                    Text(if (usuario.estaBloqueado) "Desbloquear" else "Bloquear")
                }

                OutlinedButton(
                    onClick = { onEliminar(usuario) },
                    enabled = puedeEliminar && accionesHabilitadas
                ) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun EditarUsuarioDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onConfirm: (Usuario) -> Unit,
    accionesDeshabilitadas: Boolean
) {
    val usuarioRecordado by rememberUpdatedState(usuario)

    var nombre by remember(usuarioRecordado) { mutableStateOf(usuarioRecordado.nombre) }
    var apellidos by remember(usuarioRecordado) { mutableStateOf(usuarioRecordado.apellidos) }
    var region by remember(usuarioRecordado) { mutableStateOf(usuarioRecordado.region) }
    var comuna by remember(usuarioRecordado) { mutableStateOf(usuarioRecordado.comuna) }
    var direccion by remember(usuarioRecordado) { mutableStateOf(usuarioRecordado.direccion) }

    AlertDialog(
        onDismissRequest = { if (!accionesDeshabilitadas) onDismiss() },
        title = { Text("Editar usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text("Apellidos") }
                )
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text("Región") }
                )
                OutlinedTextField(
                    value = comuna,
                    onValueChange = { comuna = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text("Comuna") }
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text("Dirección") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        usuarioRecordado.copy(
                            nombre = nombre,
                            apellidos = apellidos,
                            region = region,
                            comuna = comuna,
                            direccion = direccion
                        )
                    )
                },
                enabled = !accionesDeshabilitadas
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !accionesDeshabilitadas) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CambiarRolDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onConfirm: (TipoUsuario) -> Unit,
    accionesDeshabilitadas: Boolean
) {
    var rolSeleccionado by remember(usuario) { mutableStateOf(usuario.tipoUsuario) }

    AlertDialog(
        onDismissRequest = { if (!accionesDeshabilitadas) onDismiss() },
        title = { Text("Cambiar rol") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoUsuario.values().forEach { rol ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = rolSeleccionado == rol,
                            onClick = { rolSeleccionado = rol }
                        )
                        Text(text = rol.toDisplayName())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(rolSeleccionado) },
                enabled = !accionesDeshabilitadas
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !accionesDeshabilitadas) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ConfirmacionDialogoSimple(
    titulo: String,
    mensaje: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    accionesDeshabilitadas: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!accionesDeshabilitadas) onDismiss() },
        title = { Text(titulo) },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !accionesDeshabilitadas) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !accionesDeshabilitadas) {
                Text("Cancelar")
            }
        }
    )
}

private fun TipoUsuario.toDisplayName(): String = when (this) {
    TipoUsuario.superAdmin -> "Superadmin"
    TipoUsuario.Administrador -> "Administrador"
    TipoUsuario.Vendedor -> "Vendedor"
    TipoUsuario.Cliente -> "Cliente"
}
