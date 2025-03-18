package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
import kotlinx.coroutines.delay

@Composable
fun PantallaCarga(navController: NavController) {
    var visibleText by remember { mutableStateOf("") }
    val fullText = "U N I Q U E"

    // Animación: Cada letra aparece con un pequeño retraso
    LaunchedEffect(Unit) {
        for (i in fullText.indices) {
            delay(150L)  // Espera 300ms antes de mostrar la siguiente letra
            visibleText = fullText.substring(0, i + 1)
        }
        delay(1500L) // Espera 1.5 segundos antes de navegar
        navController.navigate("home") // Cambia esto a la pantalla que quieres mostrar
    }

    // UI de la pantalla de carga
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_grande),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = visibleText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White
            )
        }
    }
}