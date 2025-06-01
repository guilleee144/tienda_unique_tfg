package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: Int? = null,
    val categoria: String,
    val grupo: String,
    val subcategoria: String,
    val producto: String,
    val precio: Double,
    val stock: Int,
    val imagen: String,
    // Estos campos son opcionales y se usarán solo en figuras y funko pops
    val imagen2: String? = null,
    val imagen3: String? = null
)
