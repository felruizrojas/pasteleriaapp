package com.example.pasteleriaapp.domain.model

data class PedidoProducto(
    val idPedidoProducto: Int = 0,
    val idPedido: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    val mensajePersonalizado: String
)