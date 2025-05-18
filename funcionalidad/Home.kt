package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import com.google.firebase.auth.FirebaseAuth


// Mapeo de categorías a nombres de tabla en Supabase
val categoryToTable = mapOf(
    "Figuras" to "productos_figuras",
    "Funko Pops" to "productos_funkos",
    "Cartas" to "productos_cartas",
    "Camisetas" to "productos_camisetas",
    "Tazas" to "productos_tazas"
)

@Composable
fun Home(navController: NavController, guardadosViewModel: GuardadosViewModel, carritoViewModel: CarritoViewModel, detalleProductoViewModel: DetalleProductoViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val totalProductos by carritoViewModel.productosEnCarrito.collectAsState(initial = emptyList())
    val totalCount = totalProductos.size

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var resultadosBusqueda by remember { mutableStateOf<List<Producto>>(emptyList()) }
    val categorias = categoryToTable.values.toList()

    LaunchedEffect(selectedCategory) {
        productos = withContext(Dispatchers.IO) {
            val supabase = SupabaseClientProvider.getClient()
            if (selectedCategory == null) {
                val cartas = supabase.from("productos_cartas").select().decodeList<Producto>()
                val figuras = supabase.from("productos_figuras").select().decodeList<Producto>()
                val funkos = supabase.from("productos_funkos").select().decodeList<Producto>()
                val camisetas = supabase.from("productos_camisetas").select().decodeList<Producto>()
                val tazas = supabase.from("productos_tazas").select().decodeList<Producto>()
                (cartas + figuras + funkos + camisetas + tazas).shuffled().take(10)
            } else {
                val tableName = categoryToTable[selectedCategory]
                tableName?.let { supabase.from(it).select().decodeList<Producto>() } ?: emptyList()
            }
        }
    }

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
                                val imagenPerfil =
                                    if (fotoPerfil.isNullOrEmpty()) R.drawable.camara else null
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
                            var expanded by remember { mutableStateOf(false) }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column {
                                    OutlinedTextField(
                                        value = searchText,
                                        onValueChange = {
                                            searchText = it
                                            expanded =
                                                it.isNotBlank() && resultadosBusqueda.isNotEmpty()
                                        },
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
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFC3C3C3))
                                    )

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                    ) {
                                        resultadosBusqueda.take(5).forEach { producto ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    detalleProductoViewModel.productoSeleccionado.value =
                                                        producto
                                                    navController.navigate("detallesProducto")
                                                    searchText = ""
                                                    resultadosBusqueda = emptyList()
                                                    expanded = false
                                                },
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        AsyncImage(
                                                            model = producto.imagen,
                                                            contentDescription = producto.producto,
                                                            modifier = Modifier
                                                                .size(40.dp)
                                                                .clip(RoundedCornerShape(6.dp)),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                            text = producto.producto,
                                                            fontSize = 14.sp,
                                                            color = Color.Black,
                                                            maxLines = 1
                                                        )
                                                    }
                                                })
                                        }
                                    }
                                }
                            }
                        }
                    }
                        // Contenido scrollable
                    Box(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 100.dp)) {
                            // Aquí se integra el contenido directamente sin funciones extra
                            val categories = listOf("Figuras", "Funko Pops", "Cartas", "Camisetas", "Tazas")
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(categories) { category ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable { selectedCategory = category }
                                    ) {
                                        Image(
                                            painter = painterResource(id = when (category) {
                                                "Figuras" -> R.drawable.zoro
                                                "Funko Pops" -> R.drawable.pops
                                                "Cartas" -> R.drawable.cartas
                                                "Camisetas" -> R.drawable.camiseta
                                                "Tazas" -> R.drawable.taza
                                                else -> R.drawable.logo_rojo
                                            }),
                                            contentDescription = category,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.White)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = category,
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
                                Text(
                                    text = "Nueva Colección",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 5.dp)
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.banner_pokemon),
                                    contentDescription = "Banner Pokémon",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = selectedCategory ?: "Destacados",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                            )

                            val grouped = productos.groupBy { it.subcategoria }
                            grouped.forEach { (subcategoria, list) ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = subcategoria,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                                    )
                                    val productosGuardados by guardadosViewModel.productosGuardados.collectAsState()

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        itemsIndexed(list) { index, producto ->
                                            key("${producto.id ?: "null"}_$index") {
                                                val estaGuardado =
                                                    guardadosViewModel.productoEstaGuardado(producto)

                                                Box(
                                                    modifier = Modifier
                                                        .width(160.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(Color.White)
                                                        .clickable {
                                                            detalleProductoViewModel.productoSeleccionado.value =
                                                                producto
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
                                                                    .background(
                                                                        Color(0xFFEDEDED),
                                                                        RoundedCornerShape(8.dp)
                                                                    )
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
                                                                        guardadosViewModel.toggleGuardado(
                                                                            producto
                                                                        )
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
                                                                .padding(
                                                                    horizontal = 8.dp,
                                                                    vertical = 2.dp
                                                                )
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
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Barra inferior fija
                    Column(
                        modifier = Modifier.fillMaxWidth()
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
        }
    )
}


@Composable
fun CategoriaItem(titulo: String, imagenRes: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imagenRes),
            contentDescription = titulo,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = titulo,
            textAlign = TextAlign.Center,
            color = Color(0xFF8B8B8B),
            fontSize = 14.sp
        )
    }
}

@Composable
fun MenuLateral(navController: NavController, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            val fotoPerfil = userData?.fotoPerfil
            val imagenPerfil = if (fotoPerfil.isNullOrEmpty()) R.drawable.camara else null
            val painter = if (imagenPerfil != null) {
                painterResource(id = imagenPerfil)
            } else {
                rememberAsyncImagePainter(fotoPerfil)
            }
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Image(
                    painter = painterResource(id = R.drawable.editar_usuario),
                    contentDescription = "Icon Overlay",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(4.dp)
                        .clickable {
                            navController.navigate("editarfoto")
                            onClose()
                        }
                )
            }

        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "${userData?.nombre ?: ""} ${userData?.apellidos ?: ""}",
            fontSize = 24.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(70.dp))

        Button(
            onClick = { navController.navigate("perfil") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.usuario),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(35.dp))
                Text(
                    text = "Perfil",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { navController.navigate("puntosOfertas") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_blanco),
                    contentDescription = "Puntos",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(35.dp))
                Text(
                    text = "Puntos",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { navController.navigate("premium") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.premium),
                    contentDescription = "Premium",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(35.dp))
                Text(
                    text = "Premium",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { navController.navigate("notificaciones") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.campana),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(35.dp))
                Text(
                    text = "Notificaciones",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}
