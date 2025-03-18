package com.example.uniqueartifacts.crud

data class Producto(
    var id: String? = null,   // Identificador asignado por Firestore
    var nombre: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0,
    var imagen: String = "",
    var imagen2: String? = null,
    var imagen3: String? = null
)
