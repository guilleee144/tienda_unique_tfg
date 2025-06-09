package com.example.uniqueartifacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.ProductoGuardadoDTO
import com.example.uniqueartifacts.model.ProductoGuardadoInsert
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.atomicfu.TraceBase.None.append
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuardadosViewModel : ViewModel() {

    private val _productosGuardados = MutableStateFlow<List<Producto>>(emptyList())
    val productosGuardados: StateFlow<List<Producto>> = _productosGuardados.asStateFlow()

    private val supabase = SupabaseClientProvider.getClient()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid

    fun toggleGuardado(producto: Producto) {
        val listaActual = _productosGuardados.value.toMutableList()
        val yaGuardado = listaActual.any { it.id == producto.id }

        if (yaGuardado) {
            listaActual.removeAll { it.id == producto.id }
            producto.id?.let { id ->
                currentUid?.let { eliminarProducto(it, id) }
            }
        } else {
            listaActual.add(producto)
            currentUid?.let { guardarProducto(it, producto) }


        }
        _productosGuardados.value = listaActual
        cargarProductosGuardadosDesdeReferencias() // 🔄 Fuerza recarga
    }

    fun cargarProductosGuardadosDesdeReferencias() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val listaDTO = withContext(Dispatchers.IO) {
                    supabase.from("productos_guardados")
                        .select {
                            filter { eq("uid", uid) }
                        }
                        .decodeList<ProductoGuardadoDTO>()
                }

                val productos = listaDTO.map { dto ->
                    Producto(
                        id = dto.producto_id,
                        producto = dto.producto,
                        precio = dto.precio,
                        imagen = dto.imagen,
                        imagen2 = dto.imagen2,
                        imagen3 = dto.imagen3,
                        stock = dto.stock,
                        categoria = dto.categoria,
                        subcategoria = dto.subcategoria,
                        grupo = dto.grupo
                    )
                }

                _productosGuardados.value = productos
            } catch (e: Exception) {
                println("❌ Error al cargar productos guardados: ${e.message}")
                _productosGuardados.value = emptyList()
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
    fun limpiarGuardados() {
        _productosGuardados.value = emptyList()
    }



    fun eliminarProducto(uid: String, productoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                supabase.from("productos_guardados")
                    .delete {
                        filter {
                            eq("uid", uid)
                            eq("producto_id", productoId)
                        }
                    }
                // Opcional: recargar productos después de eliminar
                cargarProductosGuardadosDesdeReferencias()
            } catch (e: Exception) {
                println("🔥 Error al eliminar producto guardado: ${e.message}")
            }
        }
    }

}
