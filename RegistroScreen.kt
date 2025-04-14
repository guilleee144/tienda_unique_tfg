package com.example.uniqueartifacts.views

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

// Función para convertir la fecha del formato "d/M/yyyy" a "yyyy-MM-dd"
@RequiresApi(Build.VERSION_CODES.O)
fun convertirFormatoFecha(fechaEntrada: String): String? {
    return try {
        val formatoEntrada = DateTimeFormatter.ofPattern("d/M/yyyy")
        val date = LocalDate.parse(fechaEntrada, formatoEntrada)
        val formatoSalida = DateTimeFormatter.ISO_LOCAL_DATE
        formatoSalida.format(date)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroScreen(navController: NavController) {
    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    // Mostrar el DatePickerDialog cuando se requiera
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Se formatea la fecha en formato "d/M/yyyy" (mes es 0-indexado)
                fechaNacimiento = "$dayOfMonth/${month + 1}/$year"
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título y subtítulo
            Text(
                text = "UNIQUE",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Registrarse",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo: Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Nombre", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Apellidos
            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                placeholder = { Text("Apellidos", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                placeholder = { Text("Contraseña", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Confirmar Contraseña
            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { confirmarContrasena = it },
                placeholder = { Text("Confirmar Contraseña", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Correo electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("Correo electrónico", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Número de Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("Número de Teléfono", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Fecha de Nacimiento (solo lectura; clic para abrir el DatePicker)
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { /* No se permite editar manualmente */ },
                placeholder = { Text("Fecha de Nacimiento (dd/mm/yyyy)", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz),
                        contentDescription = "Lápiz",
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
            Spacer(modifier = Modifier.height(32.dp))

            var errorMessage by remember { mutableStateOf("") }
            // Botón: CONTINUAR (registro)
            // Dentro de tu función RegistroScreen, en la parte del botón CONTINUAR:
            Button(
                onClick = {
                    if (contrasena != confirmarContrasena) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (nombre.isBlank() || apellidos.isBlank() || correo.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    auth.createUserWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        // Convertir la fecha al formato ISO ("yyyy-MM-dd")
                                        val fechaFormateada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            convertirFormatoFecha(fechaNacimiento) ?: fechaNacimiento
                                        } else {
                                            fechaNacimiento
                                        }
                                        // Crear la instancia de UserData
                                        val userData = UserData(
                                            uid = auth.currentUser?.uid,
                                            nombre = nombre,
                                            apellidos = apellidos,
                                            correo = correo,
                                            telefono = telefono,
                                            fechaNacimiento = fechaFormateada,
                                            puntos = 0
                                        )
                                        try {
                                            // Inserta los datos en Supabase.
                                            // Nota: Insert espera una lista; por ello ponemos listOf(userData)
                                            SupabaseClientProvider.client
                                                .from("usuarios")
                                                .insert(listOf(userData))
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "Error al insertar datos en Supabase: ${e.localizedMessage}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    navController.navigate("loginScreen")
                                }


                            } else {
                                val errorMessage = task.exception?.localizedMessage ?: "Error al registrar"
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
                    text = "CONTINUAR",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}
