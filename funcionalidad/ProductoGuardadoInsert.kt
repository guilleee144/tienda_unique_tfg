package com.example.uniqueartifacts.model
import kotlinx.serialization.Serializable
@Serializable
data class ProductoGuardadoInsert(
    val uid: String,
    val producto_id: Int,
    val categoria: String,
    val grupo: String,
    val subcategoria: String,
    val producto: String,
    val precio: Double,
    val stock: Int,
    val imagen: String,
    val imagen2: String? = null,
    val imagen3: String? = null
)
