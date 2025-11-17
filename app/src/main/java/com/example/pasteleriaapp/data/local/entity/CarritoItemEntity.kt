package com.example.pasteleriaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.CarritoItem

@Entity(
    tableName = "carrito",
    indices = [Index(value = ["idUsuario", "idProducto", "mensajePersonalizado"], unique = true)]
)
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val idCarrito: Int = 0,
    val idUsuario: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val precioProducto: Double,
    val imagenProducto: String,
    val cantidad: Int,
    @ColumnInfo(defaultValue = "")
    val mensajePersonalizado: String
)

fun CarritoItemEntity.toCarritoItem() = CarritoItem(
    idCarrito = idCarrito,
    usuarioId = idUsuario,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)

fun CarritoItem.toCarritoItemEntity() = CarritoItemEntity(
    idCarrito = idCarrito,
    idUsuario = usuarioId,
    idProducto = idProducto,
    nombreProducto = nombreProducto,
    precioProducto = precioProducto,
    imagenProducto = imagenProducto,
    cantidad = cantidad,
    mensajePersonalizado = mensajePersonalizado
)