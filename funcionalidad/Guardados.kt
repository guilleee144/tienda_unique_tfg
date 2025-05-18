package com.example.uniqueartifacts.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Guardados(navController: NavController, guardadosViewModel: GuardadosViewModel, carritoViewModel: CarritoViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var resultadosBusqueda by remember { mutableStateOf<List<Producto>>(emptyList()) }
    val categorias = categoryToTable.values.toList()
    val totalProductos by carritoViewModel.productosEnCarrito.collectAsState(initial = emptyList())
    val totalCount = totalProductos.size

    val productosGuardados by guardadosViewModel.productosGuardados.collectAsState()

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
                    allProducts.filter { it.producto.contains(searchText, ignoreCase = true) }
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
                Column(modifier = Modifier.fillMaxSize()) {
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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Elementos Guardados",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        val filas = productosGuardados.chunked(2)

                        filas.forEach { fila ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                fila.forEach { producto ->
                                    val estaGuardado by rememberUpdatedState(
                                        newValue = guardadosViewModel.productoEstaGuardado(producto)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .width(160.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .clickable {
                                                navController.navigate("detallesProducto")
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp)
                                                        .background(Color(0xFFEDEDED), RoundedCornerShape(8.dp))
                                                ) {
                                                    AsyncImage(
                                                        model = producto.imagen,
                                                        contentDescription = producto.producto,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Fit
                                                    )
                                                }
                                                Image(
                                                    painter = painterResource(
                                                        id = if (estaGuardado) R.drawable.guardar_activado else R.drawable.guardar_desactivado
                                                    ),
                                                    contentDescription = "Guardar",
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(6.dp)
                                                        .size(22.dp)
                                                        .clickable {
                                                            guardadosViewModel.toggleGuardado(producto)
                                                        }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = producto.producto,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp,
                                                color = Color.Black,
                                                textAlign = TextAlign.Center,
                                                maxLines = 2,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        Color(0xFFFF7F7F),
                                                        RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "${producto.precio} €",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                                if (fila.size == 1) {
                                    AddCardButton()
                                }
                            }
                        }

                        if (productosGuardados.size % 2 == 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                AddCardButton()
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
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
                            Triple("BUSCAR", if (currentRoute == "buscador") R.drawable.lupa else R.drawable.lupa_a, "buscador"),
                            Triple("OFERTAS", R.drawable.rebajas, "ofertas"),
                            Triple("GUARDADOS", if (currentRoute == "guardados") R.drawable.guardados_a else R.drawable.guardado, "guardados"),
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
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
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
        }
    )
}

@Composable
fun AddCardButton() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .width(160.dp)
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("+", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
