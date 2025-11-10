package com.example.pasteleriaapp.ui.screen.productos

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
// --- NUEVOS IMPORTS ---
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.OutlinedTextField
// --- FIN NUEVOS IMPORTS ---
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    viewModel: CategoriaViewModel,
    onCategoriaClick: (Int) -> Unit,
    onCarritoClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // --- MODIFICADO: TopAppBar ahora es una Columna ---
            Column {
                TopAppBar(
                    title = { Text("Catálogo de Productos") },
                    actions = {
                        IconButton(onClick = onCarritoClick) {
                            Icon(Icons.Default.ShoppingCart, "Ver carrito")
                        }
                    }
                )

                // --- BARRA DE BÚSQUEDA AÑADIDA ---
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar categoría... (ej. Tortas)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, "Buscar")
                    },
                    trailingIcon = {
                        // Icono para limpiar la búsqueda
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, "Limpiar")
                            }
                        }
                    },
                    singleLine = true
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.estaCargando -> {
                    CircularProgressIndicator()
                }
                state.error != null -> {
                    Text(text = "Error: ${state.error}")
                }

                // Caso 1: Hay categorías (la lista filtrada no está vacía)
                state.hayCategorias -> {
                    CategoriasGrid(
                        categorias = state.categorias, // Muestra la lista filtrada
                        onCategoriaClick = onCategoriaClick
                    )
                }

                // Caso 2: No hay categorías POR EL FILTRO
                !state.hayCategorias && state.searchQuery.isNotEmpty() -> {
                    Text(text = "No se encontraron resultados para \"${state.searchQuery}\"")
                }

                // Caso 3: No hay categorías EN LA BD
                else -> {
                    Text(text = "No hay categorías disponibles.")
                }
            }
        }
    }
}

/**
 * Muestra la grilla de categorías.
 * (Sin cambios)
 */
@Composable
private fun CategoriasGrid(
    categorias: List<Categoria>,
    onCategoriaClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categorias) { categoria ->
            CategoriaCard(
                categoria = categoria,
                onClick = { onCategoriaClick(categoria.idCategoria) }
            )
        }
    }
}

/**
 * Representa una sola Card de categoría.
 * (Sin cambios)
 */
@Composable
private fun CategoriaCard(
    categoria: Categoria,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = painterResourceFromName(context, categoria.imagenCategoria)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen de ${categoria.nombreCategoria}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = categoria.nombreCategoria,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Función auxiliar para obtener un ID de drawable a partir de su nombre (String).
 * (Sin cambios)
 */
@DrawableRes
@Composable
private fun painterResourceFromName(context: Context, resName: String): Int {
    return try {
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
        if (resId == 0) {
            R.drawable.ic_launcher_background
        } else {
            resId
        }
    } catch (e: Exception) {
        R.drawable.ic_launcher_background
    }
}