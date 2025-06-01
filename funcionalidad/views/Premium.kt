package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.viewmodel.NotificacionesViewModel
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun Premium(
    navController: NavController,
    supabase: SupabaseClient,
    viewModel: NotificacionesViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var isPremium by remember { mutableStateOf(false) }
    var cuentaBancaria by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var nuevaCuenta by remember { mutableStateOf("") }

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        if (userId != null) {
            try {
                val usuario = supabase.from("usuarios")
                    .select {
                        filter { eq("uid", userId) }
                    }
                    .decodeSingleOrNull<UserData>()

                if (usuario != null) {
                    isPremium = usuario.premium
                    cuentaBancaria = usuario.cuentaBancaria
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Image(
                    painter = painterResource(id = R.drawable.atras),
                    contentDescription = "Volver",
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(90.dp))

            Text(
                text = "Premium",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Siente la diferencia",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Descubre las ventajas de nuestro Plan Premium",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF222222), shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Qué incluye", color = Color.White, fontSize = 14.sp)
                Row {
                    Text(text = "FREE", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.premium),
                        contentDescription = "Premium",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Divider(color = Color.Gray, thickness = 1.dp)

            BeneficioItem("Envío de 24 - 48 horas", true)
            BeneficioItem("Sorteos Exclusivos", true)
            BeneficioItem("Artículos únicos", true)
            BeneficioItem("Unidades Reservadas", true)
        }

        Spacer(modifier = Modifier.height(50.dp))



        Button(
            onClick = {
                if (userId == null) return@Button

                if (isPremium) {
                    showDialog = true
                } else {
                    if (!cuentaBancaria.isNullOrEmpty()) {
                        coroutineScope.launch {
                            supabase.from("usuarios").update({
                                set("premium", true)
                            }) {
                                filter { eq("uid", userId) }
                            }
                            isPremium = true
                            notificarEvento("premium_activado", viewModel)
                        }
                    } else {
                        showDialog = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPremium) Color.Gray else Color(0xFFE64949)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
        ) {
            Text(
                text = if (isPremium) "Cancelar suscripción" else "Comprar ahora",
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "2,40€ / mes", color = Color.White, fontSize = 14.sp)
        }

        cuentaBancaria?.let {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Cuenta bancaria registrada:",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Text(
                text = it,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_gris_grande),
            contentDescription = "Logo_gris",
            modifier = Modifier.size(120.dp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        if (userId != null) {
                            supabase.from("usuarios").update({
                                set("premium", false)
                            }) {
                                filter { eq("uid", userId) }
                            }
                            isPremium = false
                            showDialog = false
                            notificarEvento("premium_cancelado", viewModel)
                        }
                    }
                }) {
                    Text("Sí, cancelar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No", color = Color.Gray)
                }
            },
            title = { Text("¿Cancelar Premium?", color = Color.White) },
            text = { Text("Perderás todos los beneficios exclusivos si cancelas tu suscripción.", color = Color.LightGray) },
            containerColor = Color.DarkGray
        )
    }
}

@Composable
fun BeneficioItem(texto: String, esPremium: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, color = Color.White, fontSize = 14.sp)
        Row {
            Text(text = "--", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Check",
                modifier = Modifier.size(18.dp)
            )
        }
    }
    Divider(color = Color.Gray, thickness = 1.dp)
}
