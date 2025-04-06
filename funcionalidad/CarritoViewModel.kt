package com.example.uniqueartifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniqueartifacts.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.util.Locale

class CarritoViewModel : ViewModel() {

    private val _productosEnCarrito = MutableStateFlow<List<Producto>>(emptyList())
    val productosEnCarrito = _productosEnCarrito

    val totalProductos: StateFlow<Int> = productosEnCarrito
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun agregarAlCarrito(producto: Producto) {
        val listaActual = _productosEnCarrito.value.toMutableList()
        listaActual.add(producto)
        _productosEnCarrito.value = listaActual
    }

    fun obtenerCantidadTotal(): Int {
        return _productosEnCarrito.value.size
    }

    fun obtenerPrecioTotal(): String {
        val total = _productosEnCarrito.value.sumOf { it.precio }
        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        return nf.format(total)
    }
}
