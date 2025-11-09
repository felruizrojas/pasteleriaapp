package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.ProductoRepository // <-- Asegúrate que sea la interfaz
import com.example.pasteleriaapp.ui.state.ProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoViewModel (
    private val repository: ProductoRepository, // <-- Usando la interfaz (corregido de antes)
    private val idCategoria: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    // --- NUEVO: Lista interna para guardar todos los productos ---
    private var listaCompletaProductos: List<Producto> = emptyList()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            repository.obtenerProductosPorCategoria(idCategoria)
                .catch { exception ->
                    _uiState.update {
                        it.copy(estaCargando = false, error = exception.message ?: "Error desconocido")
                    }
                }
                .collect { productos ->
                    // Guardamos la lista completa internamente
                    listaCompletaProductos = productos

                    // Actualizamos la UI (inicialmente sin filtro)
                    _uiState.update {
                        it.copy(
                            estaCargando = false,
                            productos = productos, // La lista filtrada es igual a la completa al inicio
                            error = null
                        )
                    }
                }
        }
    }

    // --- FUNCIÓN NUEVA: Se llama cada vez que el usuario escribe ---
    fun onSearchQueryChange(query: String) {
        // 1. Actualiza el texto en la UI
        _uiState.update { it.copy(searchQuery = query) }

        // 2. Filtra la lista
        val listaFiltrada = if (query.isBlank()) {
            listaCompletaProductos // Si no hay búsqueda, muestra todo
        } else {
            // Filtra por nombre de producto (ignora mayúsculas/minúsculas)
            listaCompletaProductos.filter { producto ->
                producto.nombreProducto.contains(query, ignoreCase = true)
            }
        }

        // 3. Actualiza la lista de productos que ve el usuario
        _uiState.update { it.copy(productos = listaFiltrada) }
    }
}

/**
 * Factory (Corregida de nuestros pasos anteriores)
 */
class ProductoViewModelFactory(
    private val repository: ProductoRepository, // <-- Usando la interfaz
    private val idCategoria: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(
                repository,
                idCategoria = idCategoria
            ) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}