package com.example.pasteleriaapp.ui.screen.productos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    viewModel: ProductoDetalleViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.producto?.nombreProducto ?: "Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.estaCargando -> CircularProgressIndicator()
                state.error != null -> Text("Error: ${state.error}")
                state.producto != null -> ProductoDetalle(state.producto!!)
                else -> Text("Producto no encontrado.")
            }
        }
    }
}

@Composable
private fun ProductoDetalle(producto: Producto) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        //  (Aquí iría la imagen del producto)
        Spacer(Modifier.height(16.dp))

        Text(
            text = producto.nombreProducto,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = "$${producto.precioProducto}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = producto.descripcionProducto,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Stock disponible: ${producto.stockProducto} unidades",
            style = MaterialTheme.typography.bodySmall
        )
    }
}