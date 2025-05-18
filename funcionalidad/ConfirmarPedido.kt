package com.example.uniqueartifacts.views

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ConfirmarPedido(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    detalleProductoViewModel: DetalleProductoViewModel
) {
    val productosEnCarrito by carritoViewModel.productosEnCarrito.collectAsState()
    var selectedEnvio by remember { mutableStateOf("Envío estandar 24 - 72 h") }
    var selectedPago by remember { mutableStateOf("Pago con Tarjeta (Seguro)") }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val totalProductos by carritoViewModel.productosEnCarrito.collectAsState(initial = emptyList())
    val totalCount = totalProductos.size

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var resultadosBusqueda by remember { mutableStateOf<List<Producto>>(emptyList()) }
    val categorias = categoryToTable.values.toList()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

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

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Confirmar Pedido",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            productosEnCarrito.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFCDD2))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = item.producto.imagen,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.producto.producto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("x ${item.cantidad}", fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "+ ${item.producto.precio.toInt() * 100} ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Image(
                                painter = painterResource(id = R.drawable.logo_blanco),
                                contentDescription = "Puntos",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 4.dp)
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Forma de Envio", fontWeight = FontWeight.Bold)
            listOf("Envío estandar 24 - 72 h", "Envío Premium 24h").forEach { envio ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedEnvio = envio }
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedEnvio == envio) Color(0xFFFFCDD2) else Color.Transparent)
                        .padding(horizontal = 12.dp)
                ) {
                    RadioButton(
                        selected = selectedEnvio == envio,
                        onClick = { selectedEnvio = envio }
                    )
                    Text(envio, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Método de Pago", fontWeight = FontWeight.Bold)
            listOf("Pago con Tarjeta (Seguro)", "PayPal").forEach { metodo ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPago = metodo }
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedPago == metodo) Color(0xFFFFCDD2) else Color.Transparent)
                        .padding(horizontal = 12.dp)
                ) {
                    RadioButton(
                        selected = selectedPago == metodo,
                        onClick = { selectedPago = metodo }
                    )
                    Text(metodo, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("formularioPedido") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A80)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Siguiente")
            }
        }

            Spacer(modifier = Modifier.height(120.dp))
        }

                        Box(modifier = Modifier.fillMaxSize()) {


                            // ⚫️ BARRA INFERIOR (igual que antes)
                            Column(
                                modifier = Modifier.align(Alignment.BottomCenter) // ✅ ahora es válido
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
            }
        )
    }
}

