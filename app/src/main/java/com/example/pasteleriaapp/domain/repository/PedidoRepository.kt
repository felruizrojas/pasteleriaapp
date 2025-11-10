package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Pedido
import com.example.pasteleriaapp.domain.model.PedidoProducto
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {

    suspend fun crearPedido(pedido: Pedido, items: List<CarritoItem>): Long

    fun obtenerPedidosPorUsuario(idUsuario: Int): Flow<List<Pedido>>

    suspend fun obtenerDetallePedido(idPedido: Int): Pair<Pedido?, List<PedidoProducto>>
}