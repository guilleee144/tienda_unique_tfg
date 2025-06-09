package com.example.uniqueartifacts.views

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Buscador(navController: NavController, carritoViewModel: CarritoViewModel, detalleProductoViewModel: DetalleProductoViewModel) {
    val scope = rememberCoroutineScope()
    var productosSugeridos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var resultadosBusqueda by remember { mutableStateOf<List<Producto>>(emptyList()) }

    val categorias = listOf(
        "productos_figuras",
        "productos_funkos",
        "productos_cartas",
        "productos_camisetas",
        "productos_tazas"
    )

    // Productos sugeridos al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            productosSugeridos = withContext(Dispatchers.IO) {
                val client = SupabaseClientProvider.getClient()
                val allProducts = categorias.flatMap { tabla ->
                    try {
                        client.from(tabla).select().decodeList<Producto>()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                allProducts.shuffled().take(3)
            }
        }
    }

    // Buscar productos conforme se escribe
    LaunchedEffect(searchText) {
        if (searchText.isBlank()) {
            resultadosBusqueda = emptyList()
        } else {
            scope.launch {
                resultadosBusqueda = withContext(Dispatchers.IO) {
                    val client = SupabaseClientProvider.getClient()
                    val allProducts = categorias.flatMap { tabla ->
                        try {
                            client.from(tabla).select().decodeList<Producto>()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                    allProducts.filter {
                        it.producto.contains(searchText, ignoreCase = true)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
                .background(Color.Black)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

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
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFC3C3C3)),
            )

            Spacer(modifier = Modifier.height(25.dp))

            if (searchText.isBlank()) {
                Text("Sugerencias", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(productosSugeridos) { producto ->
                        SugerenciaItem(producto = producto, navController = navController, carritoViewModel = carritoViewModel, detalleProductoViewModel= detalleProductoViewModel)
                    }

                }
            } else {
                Text("Resultados", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(resultadosBusqueda) { producto ->
                        SugerenciaItem(producto = producto, navController = navController, carritoViewModel = carritoViewModel, detalleProductoViewModel= detalleProductoViewModel)
                    }

                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Black)
                .align(Alignment.BottomCenter)
        ) {
            val currentRoute = "buscar"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val items = listOf(
                    Triple("HOME", if (currentRoute == "pantallaHome") R.drawable.logo_rojo else R.drawable.logo_gris, "pantallaHome"),
                    Triple("BUSCAR", if (currentRoute == "buscar") R.drawable.lupa else R.drawable.lupa_a, "buscar"),
                    Triple("OFERTAS", R.drawable.rebajas, "ofertas"),
                    Triple("GUARDADOS", R.drawable.guardado, "guardados"),
                    Triple("AJUSTES", R.drawable.ajustes, "ajustes")
                )

                items.forEach { (label, icon, route) ->
                    Button(
                        onClick = {
                            if (route != currentRoute) {
                                navController.navigate(route) {
                                    popUpTo("pantallaHome") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = icon),
                                contentDescription = label,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (currentRoute == route) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentRoute == route) Color.White else Color.Gray,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SugerenciaItem(
    producto: Producto,
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    detalleProductoViewModel: DetalleProductoViewModel

) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    detalleProductoViewModel.productoSeleccionado.value = producto
                    navController.navigate("detallesProducto")
                }


                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.producto,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(producto.producto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(producto.categoria ?: "", color = Color.Gray, fontSize = 14.sp)
            }

            Button(
                onClick = { carritoViewModel.agregarAlCarrito(producto) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                shape = RoundedCornerShape(50.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Añadir al Carrito", color = Color.White, fontSize = 13.sp)
            }
        }

        Divider(color = Color.Gray.copy(alpha = 0.4f), thickness = 1.dp)
    }
}

