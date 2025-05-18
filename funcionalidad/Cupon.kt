package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class Cupon(
    val titulo: String,
    val imagenUrl: String,
    val descuento: String
)
