package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class Oferta(
    val titulo: String,
    val imagen: String,
    val precio: String
)
