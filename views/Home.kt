package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.crud.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun Home(navController: NavController) {
    // Estado para almacenar los productos (figuras de Bleach)
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }

    // Se carga la ruta FIGURAS -> Anime -> productos -> Bleach
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("FIGURAS")
            .document("Anime")
            .collection("productos")
            .document("Bleach")

        try {
            val documentSnapshot = docRef.get().await()
            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data // Mapa con todas las figuras
                if (data != null) {
                    val figureList = mutableListOf<Producto>()
                    for ((key, value) in data) {
                        // key = nombre de la figura (p. ej. "Aizen (Exclusiva Cara)")
                        // value = {precio=..., stock=..., imagen=..., ...}
                        if (value is Map<*, *>) {
                            val precio = value["precio"] as? Number ?: 0.0
                            val stock = value["stock"] as? Number ?: 0
                            val imagen = value["imagen"] as? String ?: ""
                            val imagen2 = value["imagen2"] as? String
                            val imagen3 = value["imagen3"] as? String

                            figureList.add(
                                Producto(
                                    id = null,           // No hay ID de documento individual
                                    nombre = key,        // Nombre de la figura
                                    precio = precio.toDouble(),
                                    stock = stock.toInt(),
                                    imagen = imagen,
                                    imagen2 = imagen2,
                                    imagen3 = imagen3
                                )
                            )
                        }
                    }
                    productos = figureList
                }
            }
        } catch (e: Exception) {
            // Manejo de errores
            productos = emptyList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp) // Espacio para la barra de navegación
        ) {
            // Navbar con fondo negro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(top = 40.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column {
                    // Primera fila (Perfil, Logo, Carrito)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen de perfil
                        Image(
                            painter = painterResource(id = R.drawable.grefg),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // Logo en el centro
                        Image(
                            painter = painterResource(id = R.drawable.logo_rojo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(50.dp)
                        )

                        // Icono del carrito
                        Image(
                            painter = painterResource(id = R.drawable.cesta),
                            contentDescription = "Carrito",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Barra de búsqueda
                    var searchText by remember { mutableStateOf("") }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Buscar ...", color = Color.Gray) },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.lupa_negra),
                                    contentDescription = "Buscar",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.micro),
                                    contentDescription = "Micrófono",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFC3C3C3)),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Fila de categorías
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // Primera categoría
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.zoro),
                            contentDescription = "Figuras",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Figuras",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B8B8B),
                            fontSize = 14.sp
                        )
                    }
                }

                // Segunda categoría
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.pops),
                            contentDescription = "Funko Pops",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Funko Pops",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B8B8B),
                            fontSize = 14.sp
                        )
                    }
                }

                // Tercera categoría
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.cartas),
                            contentDescription = "Cartas",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cartas",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B8B8B),
                            fontSize = 14.sp
                        )
                    }
                }

                // Cuarta categoría
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.camiseta),
                            contentDescription = "Camisetas",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Funko Pops",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B8B8B),
                            fontSize = 14.sp
                        )
                    }
                }

                // Quinta categoría
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.taza),
                            contentDescription = "Tazas",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cartas",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B8B8B),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Título "Nueva Colección"
                Text(
                    text = "Nueva Colección",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 5.dp)
                )

                // Imagen del Banner
                Image(
                    painter = painterResource(id = R.drawable.banner_pokemon),
                    contentDescription = "Banner Pokémon",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                // Sección adicional: Mostrar productos cargados de Firestore (Figuras de Bleach)
                if (productos.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(productos) { producto ->
                            Column(
                                modifier = Modifier
                                    .width(120.dp)
                                    .clickable {
                                        // Aquí podrías navegar al detalle del producto
                                        // navController.navigate("detalle/${producto.id}")
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(producto.imagen),
                                    contentDescription = producto.nombre,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = producto.nombre,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Barra de navegación siempre pegada abajo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Black)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var selectedItem by remember { mutableStateOf("HOME") }

                listOf(
                    "HOME" to R.drawable.logo_rojo,
                    "BUSCAR" to R.drawable.lupa,
                    "OFERTAS" to R.drawable.rebajas,
                    "GUARDADOS" to R.drawable.guardado,
                    "AJUSTES" to R.drawable.ajustes
                ).forEach { (label, iconRes) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(top = 5.dp)
                            .weight(1f)
                            .clickable { selectedItem = label }
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (selectedItem == label) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedItem == label) Color.White else Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
