package com.example.uniqueartifacts.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.crud.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun PantallaFiguras(navController: NavController) {
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Carga de todas las figuras de todos los animes
    LaunchedEffect(Unit) {
        try {
            val db = FirebaseFirestore.getInstance()
            // Se asume que en "FIGURAS/Anime/productos" hay documentos de cada anime
            val querySnapshot = db.collection("FIGURAS")
                .document("Anime")
                .collection("productos")
                .get()
                .await()
            Log.d("PantallaFiguras", "Query returned ${querySnapshot.size()} documents")
            val allFigures = mutableListOf<Producto>()
            // Para cada documento (cada anime)
            for (document in querySnapshot.documents) {
                val animeName = document.id // Ejemplo: "Bleach", "Naruto", etc.
                val data = document.data
                if (data != null) {
                    // Cada campo del documento es una figura
                    for ((figureName, value) in data) {
                        if (value is Map<*, *>) {
                            val precio = value["precio"] as? Number ?: 0.0
                            val stock = value["stock"] as? Number ?: 0
                            val imagen = value["imagen"] as? String ?: ""
                            val imagen2 = value["imagen2"] as? String
                            val imagen3 = value["imagen3"] as? String

                            allFigures.add(
                                Producto(
                                    id = null,
                                    // Se combina el nombre del anime y el de la figura para identificarla
                                    nombre = "$animeName: $figureName",
                                    precio = precio.toDouble(),
                                    stock = stock.toInt(),
                                    imagen = imagen,
                                    imagen2 = imagen2,
                                    imagen3 = imagen3
                                )
                            )
                        }
                    }
                }
            }
            productos = allFigures
        } catch (e: Exception) {
            Log.e("PantallaFiguras", "Error loading figures", e)
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Indicador de carga mientras se consultan los datos
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Cargando figuras...", fontSize = 16.sp, color = Color.Gray)
            }
        } else {
            if (errorMessage != null) {
                // Mostrar mensaje de error si hubo algún problema
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 16.sp)
                }
            } else {
                if (productos.isEmpty()) {
                    // Si no se encontraron figuras
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No se encontraron figuras", fontSize = 16.sp)
                    }
                } else {
                    // Mostrar todas las figuras en una lista vertical
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(productos) { producto ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Aquí podrías navegar al detalle del producto, si lo deseas
                                        // navController.navigate("detalle/${producto.id}")
                                    }
                                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(producto.imagen),
                                    contentDescription = producto.nombre,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = producto.nombre,
                                        fontSize = 16.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                    Text(
                                        text = "Precio: ${producto.precio}",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "Stock: ${producto.stock}",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
