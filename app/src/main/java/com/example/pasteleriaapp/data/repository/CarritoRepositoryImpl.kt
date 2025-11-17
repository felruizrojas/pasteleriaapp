package com.example.pasteleriaapp.data.repository

import com.example.pasteleriaapp.data.local.dao.CarritoDao
import com.example.pasteleriaapp.data.local.entity.toCarritoItem
import com.example.pasteleriaapp.data.local.entity.toCarritoItemEntity
import com.example.pasteleriaapp.domain.model.CarritoItem
import com.example.pasteleriaapp.domain.model.Producto
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarritoRepositoryImpl(
    private val dao: CarritoDao
) : CarritoRepository {

    override fun obtenerItemsCarrito(usuarioId: Int): Flow<List<CarritoItem>> {
        return dao.obtenerItemsCarrito(usuarioId).map { entities ->
            entities.map { it.toCarritoItem() }
        }
    }

    override suspend fun agregarAlCarrito(usuarioId: Int, producto: Producto, cantidad: Int, mensaje: String) {
        val mensajeNormalizado = mensaje.trim()
        val mensajeClave = mensajeNormalizado.takeIf { it.isNotBlank() } ?: ""
        val itemExistente = dao.obtenerItemPorProductoYMensaje(usuarioId, producto.idProducto, mensajeClave)

        if (itemExistente != null) {
            val itemActualizado = itemExistente.copy(
                cantidad = itemExistente.cantidad + cantidad,
                mensajePersonalizado = mensajeClave
            )
            dao.actualizarItem(itemActualizado)
        } else {
            val nuevoItem = CarritoItem(
                usuarioId = usuarioId,
                idProducto = producto.idProducto,
                nombreProducto = producto.nombreProducto,
                precioProducto = producto.precioProducto,
                imagenProducto = producto.imagenProducto,
                cantidad = cantidad,
                mensajePersonalizado = mensajeClave
            ).toCarritoItemEntity()
            dao.insertarItem(nuevoItem)
        }
    }

    override suspend fun actualizarCantidadItem(usuarioId: Int, item: CarritoItem, nuevaCantidad: Int) {
        if (item.usuarioId != usuarioId) return
        if (nuevaCantidad <= 0) {
            eliminarItem(usuarioId, item)
        } else {
            val itemActualizado = item.copy(cantidad = nuevaCantidad).toCarritoItemEntity()
            dao.actualizarItem(itemActualizado)
        }
    }

    override suspend fun eliminarItem(usuarioId: Int, item: CarritoItem) {
        if (item.usuarioId != usuarioId) return
        dao.eliminarItem(item.toCarritoItemEntity())
    }

    override suspend fun limpiarCarrito(usuarioId: Int) {
        dao.limpiarCarrito(usuarioId)
    }

    override suspend fun actualizarMensajeItem(usuarioId: Int, idCarrito: Int, nuevoMensaje: String) {
        val mensajeNormalizado = nuevoMensaje.trim()
        val mensajeClave = mensajeNormalizado.takeIf { it.isNotBlank() } ?: ""
        val item = dao.obtenerItemPorId(idCarrito) ?: return
        if (item.idUsuario != usuarioId) return
        if (item.mensajePersonalizado == mensajeClave) return

        val potencialDuplicado = dao.obtenerItemPorProductoYMensaje(usuarioId, item.idProducto, mensajeClave)
        if (potencialDuplicado != null && potencialDuplicado.idCarrito != item.idCarrito) {
            val actualizado = potencialDuplicado.copy(cantidad = potencialDuplicado.cantidad + item.cantidad)
            dao.actualizarItem(actualizado)
            dao.eliminarItem(item)
        } else {
            dao.actualizarMensajeItem(idCarrito, mensajeClave)
        }
    }
}