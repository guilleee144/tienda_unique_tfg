package com.example.uniqueartifacts.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uniqueartifacts.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    // Para mostrar u ocultar la contraseña
    var isPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "UNIQUE",
                fontSize = 30.sp,
                color = Color.White,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Subtítulo: "Iniciar Sesión"
            Text(
                text = "Iniciar Sesión",
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo: Correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo: Contraseña con ícono de visibilidad
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    // Aquí se alterna el ícono para mostrar/ocultar contraseña.
                    // Puedes cambiar R.drawable.ocultar por dos íconos distintos (por ejemplo: R.drawable.ic_eye y R.drawable.ic_eye_off)
                    val iconRes = if (isPasswordVisible) R.drawable.ocultar else R.drawable.ocultar
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Toggle Password Visibility",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { isPasswordVisible = !isPasswordVisible }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón INICIAR SESIÓN
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Completa todos los campos"
                        return@Button
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate("pantallaHome")
                            } else {
                                errorMessage = task.exception?.localizedMessage ?: "Error al iniciar sesión"
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444))
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón "Continuar con Google" (solo UI; se debe implementar la lógica)
            Button(
                onClick = {
                    errorMessage = "Lógica de Google Sign-In pendiente"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continuar con Google",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Texto "¿No tienes cuenta? Regístrate"
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate",
                    color = Color(0xFFFF4444),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("registro")
                    }
                )
            }
        }
    }
}
