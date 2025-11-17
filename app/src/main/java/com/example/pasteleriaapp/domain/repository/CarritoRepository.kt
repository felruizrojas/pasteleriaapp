package com.example.pasteleriaapp.domain.repository

import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface CarritoRepository {
    fun obtenerItemsCarrito(usuarioId: Int): Flow<List<CarritoItem>>

    suspend fun agregarAlCarrito(usuarioId: Int, producto: Producto, cantidad: Int, mensaje: String)

    suspend fun actualizarCantidadItem(usuarioId: Int, item: CarritoItem, nuevaCantidad: Int)

    suspend fun eliminarItem(usuarioId: Int, item: CarritoItem)

    suspend fun limpiarCarrito(usuarioId: Int)

    suspend fun actualizarMensajeItem(usuarioId: Int, idCarrito: Int, nuevoMensaje: String)
}