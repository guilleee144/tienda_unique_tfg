package com.example.uniqueartifacts.crud

import com.google.firebase.firestore.FirebaseFirestore

class ProductoRepository {

    private val db = FirebaseFirestore.getInstance()

    // Crear un producto en la colección indicada
    fun createProducto(
        collection: String,
        producto: Producto,
        onResult: (success: Boolean, documentId: String?) -> Unit
    ) {
        db.collection(collection)
            .add(producto)
            .addOnSuccessListener { documentReference ->
                onResult(true, documentReference.id)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    // Leer (obtener) un producto a partir de su ID
    fun readProducto(
        collection: String,
        documentId: String,
        onResult: (producto: Producto?) -> Unit
    ) {
        db.collection(collection)
            .document(documentId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val producto = documentSnapshot.toObject(Producto::class.java)
                    producto?.id = documentSnapshot.id
                    onResult(producto)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // Actualizar un producto existente. El objeto Producto debe tener asignado su ID.
    fun updateProducto(
        collection: String,
        producto: Producto,
        onResult: (success: Boolean) -> Unit
    ) {
        val docId = producto.id
        if (docId == null) {
            onResult(false)
            return
        }
        db.collection(collection)
            .document(docId)
            .set(producto)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Eliminar un producto a partir de su ID
    fun deleteProducto(
        collection: String,
        documentId: String,
        onResult: (success: Boolean) -> Unit
    ) {
        db.collection(collection)
            .document(documentId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Obtener todos los productos de una colección
    fun getAllProductos(
        collection: String,
        onResult: (List<Producto>) -> Unit
    ) {
        db.collection(collection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productos = mutableListOf<Producto>()
                for (document in querySnapshot.documents) {
                    val producto = document.toObject(Producto::class.java)
                    producto?.id = document.id
                    if (producto != null) {
                        productos.add(producto)
                    }
                }
                onResult(productos)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
