package com.example.uniqueartifacts.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.uniqueartifacts.model.Producto

class DetalleProductoViewModel : ViewModel() {
    var productoSeleccionado = mutableStateOf<Producto?>(null)
}
