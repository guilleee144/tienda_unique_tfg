package com.example.uniqueartifacts.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uniqueartifacts.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GuardadosViewModel : ViewModel() {

    // 🔁 Lista mutable de productos guardados (estado interno)
    private val _productosGuardados = MutableStateFlow<List<Producto>>(emptyList())

    // ✅ Estado observable desde las pantallas
    val productosGuardados: StateFlow<List<Producto>> = _productosGuardados

    fun toggleGuardado(producto: Producto) {
        val listaActual = _productosGuardados.value.toMutableList()
        val index = listaActual.indexOfFirst { it.producto == producto.producto }

        if (index >= 0) {
            listaActual.removeAt(index)
        } else {
            listaActual.add(producto)
        }

        _productosGuardados.value = listaActual
    }
}
