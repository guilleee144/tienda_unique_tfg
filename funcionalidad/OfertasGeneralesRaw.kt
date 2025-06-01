package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OfertasGeneralesRaw(
    val id: Int,
    val fecha_generacion: String,
    val ofertas: List<Oferta>
)
