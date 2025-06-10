package com.example.uniqueartifacts.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.uniqueartifacts.model.Oferta
import com.example.uniqueartifacts.model.OfertasGenerales
import com.example.uniqueartifacts.model.OfertasGeneralesRaw
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.model.ProductoOferta
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfertasScreen(navController: NavController,carritoViewModel: CarritoViewModel, detalleProductoViewModel: DetalleProductoViewModel, guardadosViewModel: GuardadosViewModel) {
    val scope = rememberCoroutineScope()
    var productosOferta by remember { mutableStateOf<List<Oferta>>(emptyList()) }

    var fechaGeneracion by remember { mutableStateOf<Date?>(null) }
    var searchText by remember { mutableStateOf("") }
    var resultadosBusqueda by remember { mutableStateOf<List<Producto>>(emptyList()) }
    val categorias = listOf(
        "productos_cartas",
        "productos_figuras",
        "productos_funkos",
        "productos_camisetas",
        "productos_tazas"
    )
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val totalProductos by carritoViewModel.productosEnCarrito.collectAsState(initial = emptyList())
    val totalCount = totalProductos.size



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



    LaunchedEffect(Unit) {
        val client = SupabaseClientProvider.getClient()
        val fechaActual = Date()

        productosOferta = withContext(Dispatchers.IO) {
            // 1. Intentar obtener ofertas existentes
            val data = try {
                client.from("ofertas_generales").select().decodeList<OfertasGeneralesRaw>()
            } catch (e: Exception) {
                println("❌ ERROR al obtener ofertas: ${e.message}")
                emptyList()
            }

            // 2. Si hay ofertas y no han expirado, usar esas
            if (data.isNotEmpty()) {
                val ultimaFecha = Date.from(Instant.parse(data.first().fecha_generacion))
                fechaGeneracion = ultimaFecha // 🟢 Esto habilita el contador de tiempo

                val dias = ChronoUnit.DAYS.between(ultimaFecha.toInstant(), fechaActual.toInstant())
                if (dias < 7) {
                    println("✅ Ofertas existentes cargadas")
                    return@withContext data.first().ofertas
                }
            }


            // 3. Generar nuevas ofertas
            val tablas = listOf("productos_figuras", "productos_funkos", "productos_cartas", "productos_camisetas", "productos_tazas")
            val nuevasOfertas = tablas.flatMap { tabla ->
                try {
                    client.from(tabla).select().decodeList<Producto>().shuffled().take(2).map { prod ->
                        val descuento = (10..30).random()
                        val precioOriginal = prod.precio
                        val precioFinal = precioOriginal - (precioOriginal * descuento / 100.0)
                        Oferta(
                            titulo = prod.producto,
                            imagen = prod.imagen,
                            precio = "%.2f€".format(precioFinal)
                        )
                    }
                } catch (e: Exception) {
                    println("❌ Error obteniendo productos de $tabla: ${e.message}")
                    emptyList()
                }
            }

            // 4. Guardar nuevas ofertas en la base de datos
            try {
                val registro = OfertasGeneralesRaw(
                    id = 1,
                    fecha_generacion = Instant.now().toString(),
                    ofertas = nuevasOfertas
                )

                println("✅ Guardando nuevas ofertas: ${Json.encodeToString(registro)}")

                client.from("ofertas_generales").delete {
                    filter { eq("id", 1) }
                }
                client.from("ofertas_generales").insert(registro)
            } catch (e: Exception) {
                println("❌ ERROR al guardar ofertas generales: ${e.message}")
            }

            nuevasOfertas
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

                    Text(
                        text = "Ofertas de la Semana",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    if (fechaGeneracion != null) {
                        val now = rememberUpdatedState(Date())
                        val millisRestantes = fechaGeneracion!!.time + 7 * 24 * 60 * 60 * 1000 - now.value.time
                        val dias = (millisRestantes / (1000 * 60 * 60 * 24)).toInt()
                        val horas = ((millisRestantes / (1000 * 60 * 60)) % 24).toInt()
                        val minutos = ((millisRestantes / (1000 * 60)) % 60).toInt()

                        Text(
                            text = "⏳ Quedan $dias días, $horas h, $minutos min",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
                        )
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .weight(1f) // 🟢 Permite que la barra inferior se vea
                            .padding(bottom = 10.dp) // 🟢 Espacio para barra inferior
                    ) {
                        itemsIndexed(productosOferta.chunked(2)) { _, fila ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                fila.forEach { producto ->
                                    val productoCompleto = Producto(
                                        id = 0,
                                        producto = producto.titulo,
                                        imagen = producto.imagen,
                                        precio = producto.precio.replace("€", "").toDoubleOrNull() ?: 0.0,
                                        categoria = "ofertas_generales",
                                        grupo = "",
                                        subcategoria = "",
                                        stock = 0
                                    )
                                    val estaGuardado = guardadosViewModel.productoEstaGuardado(productoCompleto)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .clickable {
                                                detalleProductoViewModel.productoSeleccionado.value = productoCompleto
                                                navController.navigate("detallesProducto")
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                AsyncImage(
                                                    model = producto.imagen,
                                                    contentDescription = producto.titulo,
                                                    modifier = Modifier
                                                        .height(120.dp)
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )

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
                                                            guardadosViewModel.toggleGuardado(productoCompleto)
                                                        }
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(producto.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                                            Text(producto.precio, fontSize = 14.sp, color = Color.Red)
                                        }
                                    }

                                }
                            }
                        }
                    }
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






