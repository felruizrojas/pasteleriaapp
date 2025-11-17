package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.ui.state.CarritoUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CarritoViewModel(
    private val repository: CarritoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState.asStateFlow()

    private var usuarioActualId: Int? = null
    private var observacionJob: Job? = null

    fun observarCarrito(usuarioId: Int?) {
        if (usuarioActualId == usuarioId) return
        usuarioActualId = usuarioId
        observacionJob?.cancel()

        if (usuarioId == null) {
            observacionJob = null
            _uiState.value = CarritoUiState(
                estaCargando = false,
                items = emptyList(),
                subtotal = 0.0,
                requiereAutenticacion = true,
                error = null
            )
            return
        }

        observacionJob = viewModelScope.launch {
            _uiState.value = CarritoUiState(
                estaCargando = true,
                requiereAutenticacion = false
            )
            repository.obtenerItemsCarrito(usuarioId)
                .map { items ->
                    val subtotal = items.sumOf { it.precioProducto * it.cantidad }
                    CarritoUiState(
                        estaCargando = false,
                        items = items,
                        subtotal = subtotal,
                        requiereAutenticacion = false,
                        error = null
                    )
                }
                .catch { e ->
                    _uiState.value = CarritoUiState(
                        estaCargando = false,
                        items = emptyList(),
                        subtotal = 0.0,
                        requiereAutenticacion = false,
                        error = e.message
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun actualizarCantidad(item: CarritoItem, nuevaCantidad: Int) {
        val usuarioId = usuarioActualId ?: return
        viewModelScope.launch {
            repository.actualizarCantidadItem(usuarioId, item, nuevaCantidad)
        }
    }

    fun eliminarItem(item: CarritoItem) {
        val usuarioId = usuarioActualId ?: return
        viewModelScope.launch {
            repository.eliminarItem(usuarioId, item)
        }
    }

    fun limpiarCarrito() {
        val usuarioId = usuarioActualId ?: return
        viewModelScope.launch {
            repository.limpiarCarrito(usuarioId)
        }
    }

    fun actualizarMensaje(item: CarritoItem, nuevoMensaje: String) {
        val usuarioId = usuarioActualId ?: return
        viewModelScope.launch {
            repository.actualizarMensajeItem(usuarioId, item.idCarrito, nuevoMensaje)
        }
    }

    override fun onCleared() {
        super.onCleared()
        observacionJob?.cancel()
    }
}

class CarritoViewModelFactory(
    private val repository: CarritoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            return CarritoViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}