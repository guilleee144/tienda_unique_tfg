package com.example.uniqueartifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.ProductoGuardadoInsert
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.atomicfu.TraceBase.None.append
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuardadosViewModel : ViewModel() {

    private val _productosGuardados = MutableStateFlow<List<Producto>>(emptyList())
    val productosGuardados: StateFlow<List<Producto>> = _productosGuardados

    private val supabase = SupabaseClientProvider.getClient()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid

    fun toggleGuardado(producto: Producto) {
        val listaActual = _productosGuardados.value.toMutableList()
        val yaGuardado = listaActual.any { it.id == producto.id }

        if (yaGuardado) {
            listaActual.removeAll { it.id == producto.id }
            currentUid?.let { eliminarProducto(it, producto.id) }
        } else {
            listaActual.add(producto)
            currentUid?.let { guardarProducto(it, producto) }
        }

        _productosGuardados.value = listaActual
    }

    fun cargarProductosGuardadosDesdeReferencias() {
        currentUid?.let { uid ->
            viewModelScope.launch {
                val productos = withContext(Dispatchers.IO) {
                    try {
                        supabase.from("productos_guardados")
                            .select {
                                append(Columns.raw("uid"), "eq.$uid")
                            }
                            .decodeList<Producto>()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                _productosGuardados.value = productos
            }
        }
    }

    fun productoEstaGuardado(producto: Producto): Boolean {
        return _productosGuardados.value.any { it.id == producto.id }
    }

    fun guardarProducto(uid: String, producto: Producto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (producto.id == null) return@launch

                val insertData = ProductoGuardadoInsert(
                    uid = uid,
                    producto_id = producto.id,
                    categoria = producto.categoria,
                    grupo = producto.grupo,
                    subcategoria = producto.subcategoria,
                    producto = producto.producto,
                    precio = producto.precio,
                    stock = producto.stock,
                    imagen = producto.imagen,
                    imagen2 = producto.imagen2,
                    imagen3 = producto.imagen3
                )

                supabase.from("productos_guardados").insert(insertData)
            } catch (e: Exception) {
                println("🔥 Error insertando producto guardado: ${e.message}")
            }
        }
    }



    fun eliminarProducto(uid: String, productoId: Int?) {
        if (productoId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                supabase.from("productos_guardados")
                    .delete {
                        append(Columns.raw("uid"), "eq.$uid")
                        append(Columns.raw("producto_id"), "eq.$productoId")
                    }
            } catch (_: Exception) {}
        }
    }
}
