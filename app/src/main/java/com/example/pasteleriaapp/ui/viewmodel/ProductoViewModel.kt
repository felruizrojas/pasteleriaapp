package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.state.ProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductoViewModel (
    private val repository: ProductoRepository,
    private val idCategoria: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estaCargando = true)

            repository.obtenerProductosPorCategoria(idCategoria)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
                .collect { productos ->
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        productos = productos,
                        error = null
                    )
                }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.insertarProducto(producto)
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }
}
/**
 * Factory para crear el ProductoViewModel, ya que necesita
 * que le inyectemos el RepositorioProductos.
 */
    class ProductoViewModelFactory(
        private val repository: ProductoRepository,
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
