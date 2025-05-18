// Importaciones necesarias
package com.example.uniqueartifacts.views

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.model.PuntosDinamicos
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.datetime.*
import com.example.uniqueartifacts.model.Cupon
import com.example.uniqueartifacts.model.Oferta



data class Oferta(val titulo: String, val imagen: String, val precio: String)
data class Cupon(val titulo: String, val imagenUrl: String, val descuento: String)
val tablasProductos = listOf(
    "productos_figuras",
    "productos_tazas",
    "productos_camisetas",
    "productos_funkos",
    "productos_cartas"
)
@Composable
fun Puntos_Ofertas(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Ofertas") }
    val puntosState = remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var ultimaActualizacion by remember { mutableStateOf<Instant?>(null) }

    fun tiempoRestante(timestamp: Instant): String {
        val ahora = Clock.System.now()
        val restante = 86_400_000 - (ahora.toEpochMilliseconds() - timestamp.toEpochMilliseconds())
        val horas = (restante / (1000 * 60 * 60)).toInt()
        val minutos = ((restante / (1000 * 60)) % 60).toInt()
        return if (horas > 0) "$horas h $minutos min" else "$minutos min"
    }


    val categorias = categoryToTable.values.toList()
    var ofertasDinamicas by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var cuponesDinamicos by remember { mutableStateOf<List<Cupon>>(emptyList()) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val client = SupabaseClientProvider.getClient()

            // Obtener puntos del usuario
            try {
                val result = client.from("usuarios").select().decodeList<UserData>()
                val currentUser = result.firstOrNull { it.uid == userId }
                if (currentUser != null) {
                    puntosState.value = currentUser.puntos
                }
            } catch (e: Exception) {
                Log.e("SupabaseError", "Error al obtener puntos: ${e.localizedMessage}")
            }

            try {
                val rows = client.from("puntos_dinamicos")
                    .select()
                    .decodeList<PuntosDinamicos>()
                    .filter { it.uid == userId }

                val datos = rows.firstOrNull()
                val now = System.currentTimeMillis()

                if (datos != null) {
                    val lastTime = kotlinx.datetime.Instant.parse(datos.timestamp).toEpochMilliseconds()
                    val difference = now - lastTime

                    if (difference < 86_400_000) { // < 1 día
                        ultimaActualizacion = kotlinx.datetime.Instant.fromEpochMilliseconds(lastTime)
                        ofertasDinamicas = Json.decodeFromString(datos.ofertas)
                        cuponesDinamicos = Json.decodeFromString(datos.cupones)
                        loading = false
                        return@LaunchedEffect
                    }
                }
            } catch (e: Exception) {
                Log.e("SupabaseError", "Error al consultar puntos_dinamicos: ${e.localizedMessage}")
            }

            // Si no existen o han expirado, generamos nuevos
            try {
                val categorias = tablasProductos

                val allProducts = categorias.flatMap { tabla ->
                    try {
                        client.from(tabla).select().decodeList<Producto>()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }.shuffled()

                val nuevasOfertas = allProducts.take(3)
                val posiblesDescuentos = listOf("10%", "20%", "30%", "2x1", "50%", "15%")
                val nuevosCupones = allProducts.shuffled().take(4).map {
                    Cupon(
                        titulo = it.producto,
                        imagenUrl = it.imagen,
                        descuento = posiblesDescuentos.random()
                    )
                }

                val timestampNow = kotlinx.datetime.Clock.System.now()
                ultimaActualizacion = timestampNow

                client.from("puntos_dinamicos").insert(
                    mapOf(
                        "uid" to userId,
                        "timestamp" to timestampNow.toString(),
                        "ofertas" to Json.encodeToString(nuevasOfertas),
                        "cupones" to Json.encodeToString(nuevosCupones)
                    )
                )

                ofertasDinamicas = nuevasOfertas
                cuponesDinamicos = nuevosCupones
            } catch (e: Exception) {
                Log.e("SupabaseError", "Error generando o guardando datos nuevos: ${e.localizedMessage}")
            } finally {
                loading = false
            }
        }
    }



    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        EncabezadoPuntos(navController, puntosState.value, loading)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF444444), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton("Ofertas", selectedTab) { selectedTab = "Ofertas" }
                TabButton("Cupones", selectedTab) { selectedTab = "Cupones" }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ultimaActualizacion?.let { timestamp ->
                Text(
                    text = "Ofertas disponibles por tiempo limitado ${tiempoRestante(timestamp)}",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            if (selectedTab == "Ofertas") {
                ListaDeOfertasDinamica(ofertasDinamicas, puntosState)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cuponesDinamicos) { cupon ->
                        CuponItemDinamico(cupon, puntosState, navController)
                    }
                }

            }
        }
    }
}

@Composable
fun EncabezadoPuntos(navController: NavController, puntos: Int, loading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Image(painter = painterResource(R.drawable.atras), contentDescription = "Volver", modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(105.dp))
            Text("Puntos", fontSize = 24.sp, color = Color.White, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text("UniquePoints disponibles", fontSize = 16.sp, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Text(if (loading) "Cargando..." else "$puntos", fontSize = 26.sp, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Image(painter = painterResource(R.drawable.logo_blanco), contentDescription = "Puntos", modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun TabButton(text: String, selectedTab: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(4.dp)) {
        Text(text, fontSize = 16.sp, color = if (text == selectedTab) Color.White else Color.Gray)
        if (text == selectedTab) Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.width(40.dp))
    }
}

@Composable
fun ListaDeOfertasDinamica(productos: List<Producto>, userPointsState: MutableState<Int>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(productos.size) { index ->
            val producto = productos[index]
            val precioEnPuntos = (producto.precio * 100).toInt()
            val oferta = Oferta(producto.producto, producto.imagen, precioEnPuntos.toString())
            OfertaItem(oferta = oferta, userPointsState = userPointsState)
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun OfertaItem(oferta: Oferta, userPointsState: MutableState<Int>) {
    val desbloqueada = userPointsState.value >= oferta.precio.toIntOrNull() ?: Int.MAX_VALUE
    var mostrarImagenAmpliada by remember { mutableStateOf(false) }

    if (mostrarImagenAmpliada) {
        Dialog(onDismissRequest = { mostrarImagenAmpliada = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = oferta.imagen),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { mostrarImagenAmpliada = false },
                    alignment = Alignment.Center
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (desbloqueada) R.drawable.candado_abierto else R.drawable.candado_negro),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                if (desbloqueada) {
                    Button(
                        onClick = {
                            canjearElemento(oferta.precio.toInt()) {
                                userPointsState.value = it
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Canjear", fontSize = 12.sp, color = Color.White)
                    }
                } else {
                    Text("Oferta Bloqueada", fontSize = 14.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imagen clicable
                Image(
                    painter = rememberAsyncImagePainter(model = oferta.imagen),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { mostrarImagenAmpliada = true }
                        .padding(end = 8.dp)
                )

                // Separador
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(Color.Black)
                )

                // Texto
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    Text(oferta.titulo, fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coste: ${oferta.precio} puntos", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}


@Composable
fun CuponItemDinamico(cupon: Cupon, userPointsState: MutableState<Int>, navController: NavController) {
    val puntosNecesarios = 1000
    val desbloqueado = userPointsState.value >= puntosNecesarios

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable {
                    navController.navigate("canjearCupon?titulo=${cupon.titulo}&imagen=${cupon.imagenUrl}&descuento=${cupon.descuento}")


            }
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            Image(
                painter = rememberAsyncImagePainter(cupon.imagenUrl),
                contentDescription = cupon.titulo,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
            Text(
                cupon.descuento,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.Red, shape = RoundedCornerShape(8.dp))
                    .padding(6.dp)
            )
        }
    }
}


fun canjearElemento(puntosNecesarios: Int, onExito: (Int) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val client = SupabaseClientProvider.getClient()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val result = client.from("usuarios").select().decodeList<UserData>()
            val user = result.firstOrNull { it.uid == userId }

            if (user != null && user.puntos >= puntosNecesarios) {
                val nuevosPuntos = user.puntos - puntosNecesarios
                client.from("usuarios").update(mapOf("puntos" to nuevosPuntos)) {
                    filter { eq("uid", userId) }
                }
                client.from("canjeos").insert(mapOf("uid" to userId, "puntos_utilizados" to puntosNecesarios))
                withContext(Dispatchers.Main) {
                    onExito(nuevosPuntos)
                }
            }
        } catch (e: Exception) {
            Log.e("CanjeoError", e.message ?: "Error desconocido")
        }
    }
}
