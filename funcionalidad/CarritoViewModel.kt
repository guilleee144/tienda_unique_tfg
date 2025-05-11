package com.example.uniqueartifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.ProductoEnCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.util.Locale

class CarritoViewModel : ViewModel() {

    private val _productosEnCarrito = MutableStateFlow<List<ProductoEnCarrito>>(emptyList())
    val productosEnCarrito: StateFlow<List<ProductoEnCarrito>> = _productosEnCarrito

    val totalProductos: StateFlow<Int> = productosEnCarrito
        .map { it.sumOf { item -> item.cantidad } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun agregarAlCarrito(producto: Producto) {
        val listaActual = _productosEnCarrito.value.toMutableList()
        val existente = listaActual.find { it.producto.id == producto.id }

        if (existente != null) {
            existente.cantidad += 1
        } else {
            listaActual.add(ProductoEnCarrito(producto))
        }

        _productosEnCarrito.value = listaActual
    }

    fun incrementarCantidad(productoId: Int) {
        _productosEnCarrito.value = _productosEnCarrito.value.map {
            if (it.producto.id == productoId) it.copy(cantidad = it.cantidad + 1) else it
        }
    }

    fun decrementarCantidad(productoId: Int) {
        _productosEnCarrito.value = _productosEnCarrito.value.mapNotNull {
            when {
                it.producto.id == productoId && it.cantidad > 1 -> it.copy(cantidad = it.cantidad - 1)
                it.producto.id == productoId && it.cantidad == 1 -> null
                else -> it
            }
        }
    }

    fun obtenerPrecioTotal(): String {
        val total = _productosEnCarrito.value.sumOf { it.producto.precio * it.cantidad }
        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        return nf.format(total)
    }
}

