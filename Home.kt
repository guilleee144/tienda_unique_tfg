package com.example.uniqueartifacts.views

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.uniqueartifacts.Producto
import com.example.uniqueartifacts.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun Home(navController: NavController) {
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
                            .height(56.dp) // Altura ajustada a un tamaño más preciso
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
                                    modifier = Modifier.size(24.dp) // Tamaño ajustado
                                )
                            },
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.micro),
                                    contentDescription = "Micrófono",
                                    modifier = Modifier.size(24.dp) // Tamaño ajustado
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.92f) // Ajuste exacto del ancho
                                .height(50.dp) // Altura exacta
                                .clip(RoundedCornerShape(12.dp)) // Bordes redondeados exactos
                                .background(Color(0xFFC3C3C3)), // Color de fondo exacto
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

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
                }// Segunda categoría
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
                            fontSize = 14.sp,
                        )
                    }
                }

                // Tercera categoría
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
                    color = androidx.compose.ui.graphics.Color.Black,
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 5.dp)
                )

                // Imagen del Banner
                Image(
                    painter = painterResource(id = R.drawable.banner_pokemon),
                    contentDescription = "Banner Pokémon",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp) // Altura ajustada al diseño original
                )
            }
        }

        // 🔹 Barra de navegación siempre pegada abajo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Altura de la barra
                .background(Color.Black)
                .align(Alignment.BottomCenter) // Asegura que esté pegada abajo
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







