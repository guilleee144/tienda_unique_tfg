package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uniqueartifacts.R

@Composable
fun PantallaInicio(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texto "UNIQUE"
            Text(
                text = "U N I Q U E",
                fontSize = 32.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(200.dp))

            // Botón de Iniciar Sesión
            Button(
                onClick = { navController.navigate("pantallaHome") }, // Reemplaza "login" con la ruta correcta
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F), // Rojo fuerte
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(text = "INICIAR SESIÓN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Registrarse
            Button(
                onClick = { navController.navigate("pantallaHome") }, // Reemplaza "register" con la ruta correcta
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD), // Gris claro
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(text = "REGISTRARSE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(150.dp))

            // Logo en la parte inferior
            Image(
                painter = painterResource(id = R.drawable.logo_gris_grande),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clickable { /* Puedes agregar una acción si lo deseas */ }
            )
        }
    }
}