package com.example.login.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "UNIQUE",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Registrarse",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampoTexto(label = "Nombre", value = name, onValueChange = { name = it })
        CampoTexto(label = "Correo Electrónico", value = email, onValueChange = { email = it })
        CampoTexto(label = "Número de Teléfono", value = phoneNumber, onValueChange = { phoneNumber = it })
        CampoTexto(label = "Fecha de Nacimiento", value = birthDate, onValueChange = { birthDate = it })
        CampoTexto(label = "Contraseña", value = password, onValueChange = { password = it }, esPassword = true)
        CampoTexto(label = "Confirmar Contraseña", value = confirmPassword, onValueChange = { confirmPassword = it }, esPassword = true)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || birthDate.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val user = hashMapOf(
                                        "id" to userId,
                                        "name" to name,
                                        "email" to email,
                                        "phone" to phoneNumber,
                                        "birthDate" to birthDate,
                                        "password" to password // Guardamos la contraseña en Firestore
                                    )
                                    db.collection("users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login") {
                                                popUpTo("main_login") { inclusive = false }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84343)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "CONTINUAR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CampoTexto(label: String, value: String, onValueChange: (String) -> Unit, esPassword: Boolean = false) {
    var ocultarTexto by remember { mutableStateOf(esPassword) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                visualTransformation = if (ocultarTexto) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            IconButton(onClick = { ocultarTexto = !ocultarTexto }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.Gray
                )
            }
        }

        Divider(color = Color.Gray, thickness = 1.dp)
    }
}
