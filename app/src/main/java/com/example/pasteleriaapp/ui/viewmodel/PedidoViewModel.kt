package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.EstadoPedido
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.domain.repository.PedidoRepository
import com.example.pasteleriaapp.domain.model.CarritoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

// --- ESTADO PARA LA LISTA DE PEDIDOS ---
data class MisPedidosUiState(
    val estaCargando: Boolean = false,
    val pedidos: List<Pedido> = emptyList(),
    val error: String? = null
)

// --- ESTADO PARA EL DETALLE DE UN PEDIDO ---
data class PedidoDetalleUiState(
    val estaCargando: Boolean = false,
    val pedido: Pedido? = null,
    val productos: List<PedidoProducto> = emptyList(),
    val error: String? = null
)

// --- ESTADO PARA EL CHECKOUT ---
data class CheckoutUiState(
    val estaCargando: Boolean = false,
    val fechaEntrega: String = "",
    val error: String? = null,
    val pedidoCreadoId: Long? = null,
    val numeroTarjeta: String = "",
    val nombreTitular: String = "",
    val mesExpiracion: String = "",
    val anioExpiracion: String = "",
    val cvv: String = ""
)

class PedidoViewModel(
    private val pedidoRepository: PedidoRepository,
    private val carritoRepository: CarritoRepository // Para limpiar el carrito
) : ViewModel() {

    // Estado para la pantalla "Mis Pedidos"
    private val _misPedidosState = MutableStateFlow(MisPedidosUiState())
    val misPedidosState: StateFlow<MisPedidosUiState> = _misPedidosState.asStateFlow()

    // Estado para la pantalla "Detalle del Pedido"
    private val _pedidoDetalleState = MutableStateFlow(PedidoDetalleUiState())
    val pedidoDetalleState: StateFlow<PedidoDetalleUiState> = _pedidoDetalleState.asStateFlow()

    // Estado para la pantalla "Checkout"
    private val _checkoutState = MutableStateFlow(CheckoutUiState())
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    // --- Lógica para "Mis Pedidos" ---
    fun cargarMisPedidos(idUsuario: Int) {
        viewModelScope.launch {
            _misPedidosState.update { it.copy(estaCargando = true) }
            pedidoRepository.obtenerPedidosPorUsuario(idUsuario)
                .catch { e ->
                    _misPedidosState.update { it.copy(estaCargando = false, error = e.message) }
                }
                .collect { pedidos ->
                    _misPedidosState.update { it.copy(estaCargando = false, pedidos = pedidos) }
                }
        }
    }

    // --- Lógica para "Detalle del Pedido" ---
    fun cargarDetallePedido(idPedido: Int) {
        viewModelScope.launch {
            _pedidoDetalleState.update { it.copy(estaCargando = true) }
            try {
                val (pedido, productos) = pedidoRepository.obtenerDetallePedido(idPedido)
                _pedidoDetalleState.update {
                    it.copy(estaCargando = false, pedido = pedido, productos = productos)
                }
            } catch (e: Exception) {
                _pedidoDetalleState.update { it.copy(estaCargando = false, error = e.message) }
            }
        }
    }

    // --- Lógica para "Checkout" ---
    fun onFechaEntregaChange(fecha: String) {
        _checkoutState.update { it.copy(fechaEntrega = fecha) }
    }

    fun onNumeroTarjetaChange(valor: String) {
        val digits = valor.filter { it.isDigit() }.take(16)
        _checkoutState.update { it.copy(numeroTarjeta = digits) }
    }

    fun onNombreTitularChange(valor: String) {
        val limpio = valor.replace(Regex("\\s+"), " ").trimStart()
        _checkoutState.update { it.copy(nombreTitular = limpio.take(40)) }
    }

    fun onMesExpiracionChange(valor: String) {
        val digits = valor.filter { it.isDigit() }.take(2)
        _checkoutState.update { it.copy(mesExpiracion = digits) }
    }

    fun onAnioExpiracionChange(valor: String) {
        val digits = valor.filter { it.isDigit() }.take(4)
        _checkoutState.update { it.copy(anioExpiracion = digits) }
    }

    fun onCvvChange(valor: String) {
        val digits = valor.filter { it.isDigit() }.take(3)
        _checkoutState.update { it.copy(cvv = digits) }
    }

    fun crearPedido(idUsuario: Int, items: List<CarritoItem>, total: Double) {
        val fechaEntrega = _checkoutState.value.fechaEntrega
        if (fechaEntrega.isBlank()) {
            _checkoutState.update { it.copy(error = "Debe seleccionar una fecha de entrega") }
            return
        }

        val errorPago = validarDatosPago()
        if (errorPago != null) {
            _checkoutState.update { it.copy(error = errorPago) }
            return
        }

        viewModelScope.launch {
            _checkoutState.update { it.copy(estaCargando = true, error = null) }
            try {
                // 1. Creamos el objeto Pedido
                val nuevoPedido = Pedido(
                    idUsuario = idUsuario,
                    fechaPedido = Date().time,
                    fechaEntregaPreferida = fechaEntrega,
                    estado = EstadoPedido.EN_PREPARACION,
                    total = total
                )

                // 2. 'items' ya es una lista de CarritoItem (modelo de dominio)
                val itemsDominio = items

                // 3. Llamamos al repositorio (que hace la transacción) y obtenemos el ID
                val idGenerado: Long = pedidoRepository.crearPedido(nuevoPedido, itemsDominio)

                // 4. Actualizamos el estado con el ID real del pedido
                carritoRepository.limpiarCarrito(idUsuario)
                _checkoutState.update {
                    it.copy(estaCargando = false, pedidoCreadoId = idGenerado)
                }

            } catch (e: Exception) {
                _checkoutState.update { it.copy(estaCargando = false, error = e.message) }
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutUiState()
    }

    private fun validarDatosPago(): String? {
        val state = _checkoutState.value
        if (state.numeroTarjeta.length != 16) {
            return "El número de tarjeta debe tener 16 dígitos."
        }
        if (state.nombreTitular.isBlank() || state.nombreTitular.any { it.isDigit() }) {
            return "Ingrese el nombre del titular tal como aparece en la tarjeta."
        }
        val mes = state.mesExpiracion.toIntOrNull()
        val anio = state.anioExpiracion.toIntOrNull()
        if (mes == null || mes !in 1..12) {
            return "Ingrese un mes de expiración válido."
        }
        val calendario = java.util.Calendar.getInstance()
        val anioActual = calendario.get(java.util.Calendar.YEAR)
        val mesActual = calendario.get(java.util.Calendar.MONTH) + 1
        if (anio == null || anio < anioActual || anio > anioActual + 15) {
            return "Ingrese un año de expiración válido."
        }
        if (anio == anioActual && mes < mesActual) {
            return "La tarjeta está vencida."
        }
        if (state.cvv.length != 3) {
            return "El CVV debe tener 3 dígitos."
        }
        return null
    }
}

// Factory para el PedidoViewModel
class PedidoViewModelFactory(
    private val pedidoRepository: PedidoRepository,
    private val carritoRepository: CarritoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidoViewModel::class.java)) {
            return PedidoViewModel(pedidoRepository, carritoRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}