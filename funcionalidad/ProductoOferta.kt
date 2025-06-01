package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductoOferta(
    val id: Int,
    val nombre: String,
    val imagen: String,
    val precio_original: Double,
    val precio_oferta: Double,
    val tabla: String
)
