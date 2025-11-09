package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.state.ProductoDetalleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoDetalleViewModel(
    private val repository: ProductoRepository, // <-- ¡Importante! Usa la interfaz
    private val idProducto: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoDetalleUiState())
    val uiState: StateFlow<ProductoDetalleUiState> = _uiState.asStateFlow()

    init {
        cargarProducto()
    }

    private fun cargarProducto() {
        viewModelScope.launch {
            _uiState.value = ProductoDetalleUiState(estaCargando = true)
            try {
                // obtenerProductoPorId es 'suspend', no un Flow
                val producto = repository.obtenerProductoPorId(idProducto)
                _uiState.value = ProductoDetalleUiState(producto = producto)
            } catch (e: Exception) {
                _uiState.value = ProductoDetalleUiState(error = e.message ?: "Error desconocido")
            }
        }
    }
}

/**
 * Factory para crear el ProductoDetalleViewModel con sus dependencias.
 */
class ProductoDetalleViewModelFactory(
    private val repository: ProductoRepository,
    private val idProducto: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoDetalleViewModel::class.java)) {
            return ProductoDetalleViewModel(repository, idProducto) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}