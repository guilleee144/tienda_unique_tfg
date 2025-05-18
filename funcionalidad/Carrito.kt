package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Carrito(navController: NavController, carritoViewModel: CarritoViewModel = viewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val productosEnCarrito by carritoViewModel.productosEnCarrito.collectAsState()
    val totalProductos by carritoViewModel.totalProductos.collectAsState()
    val totalPrecio = carritoViewModel.obtenerPrecioTotal()
    var searchText by remember { mutableStateOf("") }
    var resultadosBusqueda by remember { mutableStateOf<List<Producto>>(emptyList()) }
    val categorias = categoryToTable.values.toList()
    val totalCount = totalProductos

    // Búsqueda en tiempo real
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            Row {
                DismissibleDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    drawerContainerColor = Color.Black
                ) {
                    MenuLateral(navController = navController) { scope.launch { drawerState.close() } }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { scope.launch { drawerState.close() } }
                )
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp)
                ) {
                    // Navbar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(top = 40.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var userData by remember { mutableStateOf<UserData?>(null) }

                                LaunchedEffect(Unit) {
                                    scope.launch {
                                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                                        if (userId != null) {
                                            val result = SupabaseClientProvider.getClient()
                                                .from("usuarios")
                                                .select {
                                                    filter {
                                                        eq("uid", userId)
                                                    }
                                                }
                                                .decodeSingle<UserData>()
                                            userData = result
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(0.1f))

                                val fotoPerfil = userData?.fotoPerfil
                                val imagenPerfil = if (fotoPerfil.isNullOrEmpty()) R.drawable.camara else null
                                val painter = if (imagenPerfil != null) {
                                    painterResource(id = imagenPerfil)
                                } else {
                                    rememberAsyncImagePainter(fotoPerfil)
                                }

                                Image(
                                    painter = painter,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(CircleShape)
                                        .clickable { scope.launch { drawerState.open() } },
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.weight(0.9f))
                                Image(
                                    painter = painterResource(id = R.drawable.logo_rojo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Box(contentAlignment = Alignment.TopEnd) {
                                    Image(
                                        painter = painterResource(id = R.drawable.cesta),
                                        contentDescription = "Carrito",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable { navController.navigate("carrito") }
                                    )
                                    if (totalCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .offset(x = 10.dp, y = (-6).dp)
                                                .size(20.dp) // ⬅️ Aumentamos el tamaño
                                                .clip(CircleShape)
                                                .background(Color.Red),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = totalCount.toString(), // Asegúrate de usar totalCount, no toda la lista
                                                color = Color.White,
                                                fontSize = 12.sp, // ⬅️ Más legible
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(0.1f))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Buscador clásico integrado (no superpuesto)
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
                                        .background(Color(0xFFC3C3C3))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "Productos Seleccionados",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )

                    productosEnCarrito.forEach { item ->
                        ProductoEnCarritoCard(
                            producto = item.producto,
                            cantidad = item.cantidad,
                            onIncrementar = { carritoViewModel.incrementarCantidad(item.producto.id!!) },
                            onDecrementar = { carritoViewModel.decrementarCantidad(item.producto.id!!) }
                        )
                    }


                    // ➕ Resumen total
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Total productos: $totalProductos",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Precio total: $totalPrecio €",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    ProductoEnCarritoPlaceholder()
                    if (productosEnCarrito.isNotEmpty()) {
                        Button(
                            onClick = {
                                navController.navigate("confirmarPedido")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3598DB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Confirmar pedido",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }

                // ⚫️ BARRA INFERIOR (igual que antes)
                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(Color.Black)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.Black)
                    ) {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route

                        val items = listOf(
                            Triple("HOME", if (currentRoute == "pantallaHome") R.drawable.logo_rojo else R.drawable.logo_gris, "pantallaHome"),
                            Triple("BUSCAR", if (currentRoute == "buscador") R.drawable.lupa_a else R.drawable.lupa, "buscador"),
                            Triple("OFERTAS", R.drawable.rebajas, "ofertas"),
                            Triple("GUARDADOS", R.drawable.guardado, "guardados"),
                            Triple("AJUSTES", R.drawable.ajustes, "ajustes")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProductoEnCarritoCard(
    producto: Producto,
    cantidad: Int,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF9A9E), Color(0xFFFF6B6B))
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = producto.imagen),
                contentDescription = producto.producto,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.producto,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%.2f €".format(producto.precio),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Cantidad", color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDecrementar,
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("-", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = cantidad.toString(),
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = onIncrementar,
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("+", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ProductoEnCarritoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "+", fontSize = 30.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
    }
}
