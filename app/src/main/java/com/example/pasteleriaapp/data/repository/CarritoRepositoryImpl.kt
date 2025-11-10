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

    override fun obtenerItemsCarrito(): Flow<List<CarritoItem>> {
        return dao.obtenerItemsCarrito().map { entities ->
            entities.map { it.toCarritoItem() }
        }
    }

    override suspend fun agregarAlCarrito(producto: Producto, cantidad: Int, mensaje: String) {
        val mensajeNormalizado = mensaje.trim()
        val mensajeClave = mensajeNormalizado.takeIf { it.isNotBlank() } ?: ""
        val itemExistente = dao.obtenerItemPorProductoYMensaje(producto.idProducto, mensajeClave)

        if (itemExistente != null) {
            val itemActualizado = itemExistente.copy(
                cantidad = itemExistente.cantidad + cantidad,
                mensajePersonalizado = mensajeClave
            )
            dao.actualizarItem(itemActualizado)
        } else {
            val nuevoItem = CarritoItem(
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

    override suspend fun actualizarCantidadItem(item: CarritoItem, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarItem(item)
        } else {
            val itemActualizado = item.copy(cantidad = nuevaCantidad).toCarritoItemEntity()
            dao.actualizarItem(itemActualizado)
        }
    }

    override suspend fun eliminarItem(item: CarritoItem) {
        dao.eliminarItem(item.toCarritoItemEntity())
    }

    override suspend fun limpiarCarrito() {
        dao.limpiarCarrito()
    }

    override suspend fun actualizarMensajeItem(idCarrito: Int, nuevoMensaje: String) {
        val mensajeNormalizado = nuevoMensaje.trim()
        val mensajeClave = mensajeNormalizado.takeIf { it.isNotBlank() } ?: ""
        val item = dao.obtenerItemPorId(idCarrito) ?: return
        if (item.mensajePersonalizado == mensajeClave) return

        val potencialDuplicado = dao.obtenerItemPorProductoYMensaje(item.idProducto, mensajeClave)
        if (potencialDuplicado != null && potencialDuplicado.idCarrito != item.idCarrito) {
            val actualizado = potencialDuplicado.copy(cantidad = potencialDuplicado.cantidad + item.cantidad)
            dao.actualizarItem(actualizado)
            dao.eliminarItem(item)
        } else {
            dao.actualizarMensajeItem(idCarrito, mensajeClave)
        }
    }
}