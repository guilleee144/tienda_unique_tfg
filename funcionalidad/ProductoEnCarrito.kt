package com.example.uniqueartifacts.model


data class ProductoEnCarrito(
    val producto: Producto,
    var cantidad: Int = 1
)
