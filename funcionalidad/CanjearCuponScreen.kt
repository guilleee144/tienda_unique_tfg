package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanjearCuponScreen(
    titulo: String,
    imagen: String,
    descuento: String,
    carritoViewModel: CarritoViewModel,
    navController: NavController
) {
    val descuentoDecimal = when {
        descuento.contains("%") -> 1 - (descuento.filter { it.isDigit() }.toIntOrNull() ?: 0) / 100.0
        descuento.contains("2x1") -> 0.5
        else -> 1.0
    }

    var producto by remember { mutableStateOf<Producto?>(null) }

    LaunchedEffect(titulo) {
        val client = SupabaseClientProvider.getClient()
        val categorias = categoryToTable.values
        for (tabla in categorias) {
            val encontrados = try {
                client.from(tabla).select().decodeList<Producto>().filter { it.producto == titulo }
            } catch (e: Exception) {
                emptyList()
            }
            if (encontrados.isNotEmpty()) {
                producto = encontrados.first()
                break
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Canjear", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.atras),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { paddingValues ->
        producto?.let { prod ->
            val precioDescontado = (prod.precio * descuentoDecimal)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // ⬅️ Centrado vertical
            ) {
                // 📦 Tarjeta blanca con contenido del cupón
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imagen),
                        contentDescription = null,
                        modifier = Modifier
                            .height(130.dp)
                            .fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    "Descuento en la compra de productos,\nsólo a partir de pedidos de 60 €.",
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                Divider(color = Color.Gray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "SOLO APLICABLE UNA VEZ",
                    color = Color.White,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        carritoViewModel.agregarAlCarrito(prod.copy(precio = precioDescontado))
                        navController.navigate("carrito")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64949))
                ) {
                    Text("Reclamar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}
