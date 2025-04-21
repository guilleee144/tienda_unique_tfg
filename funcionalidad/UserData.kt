package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val uid: String? = null,
    val nombre: String,
    val apellidos: String,
    val telefono: String,
    val fechaNacimiento: String,
    val correo: String,
    val puntos: Int = 0,
    val cuentaBancaria: String? = null,
    val fotoPerfil: String? = null
)
