package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class PuntosDinamicos(
    val uid: String,
    val ofertas: String,
    val cupones: String,
    val timestamp: String
)
