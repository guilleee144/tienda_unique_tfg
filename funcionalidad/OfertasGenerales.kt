package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class OfertasGenerales(
    val id: Int,
    val fecha_generacion: String,
    val ofertas: List<Oferta>
)
