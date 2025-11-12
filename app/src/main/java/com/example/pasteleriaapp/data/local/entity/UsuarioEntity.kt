package com.example.pasteleriaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pasteleriaapp.domain.model.TipoUsuario
import com.example.pasteleriaapp.domain.model.Usuario

@Entity(
    tableName = "usuario",
    indices = [
        Index(value = ["run"], unique = true),
        Index(value = ["correo"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,
    val run: String,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val fechaNacimiento: String,
    val tipoUsuario: TipoUsuario,
    val region: String,
    val comuna: String,
    val direccion: String,
    val contrasena: String,

    @ColumnInfo(defaultValue = "0")
    val tieneDescuentoEdad: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val tieneDescuentoCodigo: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val esEstudianteDuoc: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val estaBloqueado: Boolean = false,

    @ColumnInfo(defaultValue = "NULL")
    val fotoUrl: String? = null
)

fun UsuarioEntity.toUsuario() = Usuario(
    idUsuario = idUsuario,
    run = run,
    nombre = nombre,
    apellidos = apellidos,
    correo = correo,
    fechaNacimiento = fechaNacimiento,
    tipoUsuario = tipoUsuario,
    region = region,
    comuna = comuna,
    direccion = direccion,
    contrasena = contrasena,
    tieneDescuentoEdad = tieneDescuentoEdad,
    tieneDescuentoCodigo = tieneDescuentoCodigo,
    esEstudianteDuoc = esEstudianteDuoc,
    fotoUrl = fotoUrl,
    estaBloqueado = estaBloqueado
)

fun Usuario.toUsuarioEntity() = UsuarioEntity(
    idUsuario = idUsuario,
    run = run,
    nombre = nombre,
    apellidos = apellidos,
    correo = correo,
    fechaNacimiento = fechaNacimiento,
    tipoUsuario = tipoUsuario,
    region = region,
    comuna = comuna,
    direccion = direccion,
    contrasena = contrasena,
    tieneDescuentoEdad = tieneDescuentoEdad,
    tieneDescuentoCodigo = tieneDescuentoCodigo,
    esEstudianteDuoc = esEstudianteDuoc,
    estaBloqueado = estaBloqueado,
    fotoUrl = fotoUrl
)