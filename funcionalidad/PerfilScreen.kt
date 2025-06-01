package com.example.uniqueartifacts.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.model.UserData
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userData by remember { mutableStateOf<UserData?>(null) }
    var isBankAccountVisible by remember { mutableStateOf(false) }
    var nuevaCuenta by remember { mutableStateOf("") }

    // Estados editables para cada campo
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val result = SupabaseClientProvider.getClient()
                    .from("usuarios")
                    .select {
                        filter {
                            eq("uid", userId)
                        }
                    }
                    .decodeSingle<UserData>()

                userData = result
                nuevaCuenta = result.cuentaBancaria ?: ""
                nombre = result.nombre ?: ""
                apellidos = result.apellidos ?: ""
                correo = result.correo ?: ""
                telefono = result.telefono ?: ""
                fechaNacimiento = result.fechaNacimiento ?: ""
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
                .padding(top = 60.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.navigate("pantallaHome") }) {
                Image(
                    painter = painterResource(id = R.drawable.atras),
                    contentDescription = "Volver",
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = "Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = {
                scope.launch {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("pantallaLogin")
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.log_out),
                    contentDescription = "Logout",
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        val fotoPerfil = userData?.fotoPerfil
        val imagenPerfil = if (fotoPerfil.isNullOrEmpty()) R.drawable.camara else null
        val painter = if (imagenPerfil != null) {
            painterResource(id = imagenPerfil)
        } else {
            rememberAsyncImagePainter(fotoPerfil)
        }

        Image(
            painter = painter,
            contentDescription = "Profile",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Text(
            text = "$nombre $apellidos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
        )

        EditableCampo("Nombre", nombre) { nombre = it }
        EditableCampo("Apellidos", apellidos) { apellidos = it }
        EditableCampo("Correo electrónico", correo) { correo = it }
        EditableCampo("Número de Teléfono", telefono) { telefono = it }
        EditableCampo("Fecha Nacimiento", fechaNacimiento) { fechaNacimiento = it }

        Spacer(modifier = Modifier.height(15.dp))

        // Cuenta bancaria
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column {
                Text("Cuenta Bancaria", fontSize = 14.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBankAccountVisible) nuevaCuenta else "********************",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { isBankAccountVisible = !isBankAccountVisible }) {
                        Image(
                            painter = painterResource(id = R.drawable.ocultar),
                            contentDescription = "Mostrar/Ocultar Cuenta",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                if (isBankAccountVisible) {
                    BasicTextField(
                        value = nuevaCuenta,
                        onValueChange = { nuevaCuenta = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }

        Button(
            onClick = {
                scope.launch {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        SupabaseClientProvider.getClient().from("usuarios")
                            .update(
                                mapOf(
                                    "nombre" to nombre,
                                    "apellidos" to apellidos,
                                    "correo" to correo,
                                    "telefono" to telefono,
                                    "fechaNacimiento" to fechaNacimiento,
                                    "cuenta_bancaria" to nuevaCuenta
                                )
                            ) {
                                filter {
                                    eq("uid", userId)
                                }
                            }
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Guardar Cambios", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.logo_gris_grande),
            contentDescription = "Logo_gris_grande",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun EditableCampo(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}
