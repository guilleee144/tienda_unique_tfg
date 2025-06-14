package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.viewmodel.NotificacionesViewModel

// 🔹 Modelo de Notificación
data class Notificacion(
    val icono: Int,
    val titulo: String,
    val descripcion: String
)

@Composable
fun Notificaciones(navController: NavController, viewModel: NotificacionesViewModel = viewModel()) {
    val notificaciones = viewModel.notificaciones

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoNotificaciones(navController, notificaciones.size)

            Spacer(modifier = Modifier.height(20.dp))

            if (notificaciones.isNotEmpty()) {
                Column {
                    notificaciones.forEach { notificacion ->
                        NotificacionItem(notificacion) {
                            viewModel.eliminar(notificacion)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                Text(
                    text = "No hay más notificaciones",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo_gris_grande),
                contentDescription = "Logo_gris",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Composable
fun EncabezadoNotificaciones(navController: NavController, contador: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.atras),
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(80.dp))

            Text(
                text = "Notificaciones",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.campana),
                contentDescription = "Notificaciones",
                modifier = Modifier.size(25.dp)
            )

            if (contador > 0) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color.Red, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contador.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun NotificacionItem(notificacion: Notificacion, onEliminar: () -> Unit) {
    var isSeleccionada by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSeleccionada) Color(0xFFDDDDDD) else Color(0xFFBBBBBB))
            .clickable { isSeleccionada = !isSeleccionada }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = notificacion.icono),
                contentDescription = "Icono Notificación",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificacion.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = notificacion.descripcion,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            if (isSeleccionada) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEliminar() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.papelera_negra),
                        contentDescription = "Eliminar",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Eliminar", fontSize = 14.sp, color = Color.Black)
                }
            }
        }
    }
}

// 🔔 Función centralizada para agregar notificaciones desde cualquier parte
fun notificarEvento(tipo: String, viewModel: NotificacionesViewModel) {
    when (tipo) {
        "compra" -> viewModel.agregar(
            Notificacion(R.drawable.caja_icono, "Compra confirmada", "Tu compra ha sido registrada correctamente.")
        )
        "premium_activado" -> viewModel.agregar(
            Notificacion(R.drawable.campana, "¡Ya eres Premium!", "Gracias por unirte a Premium.")
        )
        "premium_cancelado" -> viewModel.agregar(
            Notificacion(R.drawable.alerta, "Premium cancelado", "Tu suscripción ha sido cancelada.")
        )
        "oferta_guardado" -> viewModel.agregar(
            Notificacion(R.drawable.descuento, "¡Descuento en productos!", "Hay nuevos productos en oferta para ti.")
        )
        "entregado" -> viewModel.agregar(
            Notificacion(R.drawable.caja_icono, "Pedido entregado", "Tu pedido llegó con éxito.")
        )
    }
}
