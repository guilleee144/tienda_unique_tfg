package com.example.uniqueartifacts.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductoPedido(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)
@Serializable
data class PedidoSerializable(
    val uid: String,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val tarjeta: String,
    val fecha: String,
    val cvv: String,
    val total: Double,
    val productos: List<ProductoPedido>
)
