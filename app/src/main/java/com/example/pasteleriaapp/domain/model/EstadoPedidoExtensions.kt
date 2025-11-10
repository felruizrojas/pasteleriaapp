package com.example.pasteleriaapp.domain.model

private val TRACKING_STATES = listOf(
    EstadoPedido.PENDIENTE,
    EstadoPedido.EN_PREPARACION,
    EstadoPedido.EN_REPARTO,
    EstadoPedido.ENTREGADO
)

fun EstadoPedido.displayName(): String = when (this) {
    EstadoPedido.PENDIENTE -> "Pedido recibido"
    EstadoPedido.EN_PREPARACION -> "En preparación"
    EstadoPedido.EN_REPARTO -> "En reparto"
    EstadoPedido.ENTREGADO -> "Entregado"
    EstadoPedido.CANCELADO -> "Cancelado"
}

fun EstadoPedido.descripcion(): String = when (this) {
    EstadoPedido.PENDIENTE -> "Hemos recibido tu pedido y lo estamos validando."
    EstadoPedido.EN_PREPARACION -> "Nuestro equipo está preparando tu pedido con dedicación."
    EstadoPedido.EN_REPARTO -> "Tu pedido salió a reparto, pronto lo recibirás."
    EstadoPedido.ENTREGADO -> "Pedido entregado con éxito. ¡Que lo disfrutes!"
    EstadoPedido.CANCELADO -> "El pedido fue cancelado. Si tienes dudas contáctanos."
}

fun EstadoPedido.progressStep(): Int {
    val index = TRACKING_STATES.indexOf(this)
    return when {
        index >= 0 -> index
        this == EstadoPedido.CANCELADO -> 0
        else -> 0
    }
}

fun EstadoPedido.progressFraction(): Float {
    if (this == EstadoPedido.CANCELADO) return 0f
    val totalSteps = (TRACKING_STATES.size - 1).coerceAtLeast(1)
    val current = progressStep().coerceIn(0, totalSteps)
    return current.toFloat() / totalSteps.toFloat()
}

fun trackingEstados(): List<EstadoPedido> = TRACKING_STATES