package com.example.login.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import com.example.login.R

@Composable
fun MainLoginScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Texto principal
            Text(
                text = "UNIQUE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp)) // Espaciado entre texto y botones

            // Botones de inicio de sesión y registro
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("login") }, // Navegar a LoginScreen
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp)
                ) {
                    Text(text = "INICIAR SESIÓN", color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp)) // Espaciado entre botones

                Button(
                    onClick = { navController.navigate("register") }, // Navegar a RegisterScreen
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp)
                ) {
                    Text(text = "REGISTRARSE", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Espaciado entre botones y logo

            // Imagen del logo en la parte inferior
            Image(
                painter = painterResource(id = R.drawable.logo), // Agregar logo en drawable
                contentDescription = "Logo",
                modifier = Modifier
                    .size(110.dp)
            )
        }
    }
}
