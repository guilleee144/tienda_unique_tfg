package com.example.uniqueartifacts.views

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.model.UserData
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.File

fun uriToByteArray(context: Context, uri: Uri): ByteArray {
    val inputStream = context.contentResolver.openInputStream(uri)
    return inputStream?.readBytes() ?: ByteArray(0)
}

@Composable
fun EditarFotoScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userData by remember { mutableStateOf<UserData?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val galleryAndUploadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            scope.launch {
                try {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    val fileName = "perfil_$uid.jpg"
                    val imageBytes = uriToByteArray(context, it)

                    val supabase = SupabaseClientProvider.getClient()
                    supabase.storage.from("fotosusuarios").upload(
                        path = fileName,
                        data = imageBytes,
                        options = { upsert = true }
                    )

                    val publicUrl = supabase.storage.from("fotosusuarios").publicUrl(fileName)

                    supabase.from("usuarios").update(
                        mapOf("fotoPerfil" to publicUrl)
                    ) {
                        filter { eq("uid", uid) }
                    }

                    userData = userData?.copy(fotoPerfil = publicUrl)

                    snackbarHostState.showSnackbar("✅ Foto actualizada correctamente")
                } catch (e: Exception) {
                    e.printStackTrace()
                    snackbarHostState.showSnackbar("❌ Error al subir la foto")
                }
            }
        }
    }
    val tempImageFile = remember {
        File.createTempFile("profile_temp", ".jpg", context.cacheDir).apply { deleteOnExit() }
    }

    val imageUri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        tempImageFile
    )
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri = imageUri
            scope.launch {
                try {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    val fileName = "perfil_$uid.jpg"
                    val imageBytes = uriToByteArray(context, imageUri)

                    val supabase = SupabaseClientProvider.getClient()
                    supabase.storage.from("fotosusuarios").upload(
                        path = fileName,
                        data = imageBytes,
                        options = { upsert = true }
                    )

                    val publicUrl = supabase.storage.from("fotosusuarios").publicUrl(fileName)

                    supabase.from("usuarios").update(
                        mapOf("fotoPerfil" to publicUrl)
                    ) {
                        filter { eq("uid", uid) }
                    }

                    userData = userData?.copy(fotoPerfil = publicUrl)
                    snackbarHostState.showSnackbar("✅ Foto de cámara actualizada")
                } catch (e: Exception) {
                    e.printStackTrace()
                    snackbarHostState.showSnackbar("❌ Error al subir la foto de cámara")
                }
            }
        }
    }




    LaunchedEffect(Unit) {
        scope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val result = SupabaseClientProvider.getClient().from("usuarios")
                .select {
                    filter { eq("uid", uid) }
                }
                .decodeSingle<UserData>()
            userData = result
        }
    }
    if (showDialog && !userData?.fotoPerfil.isNullOrEmpty()) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Vista de Foto", fontWeight = FontWeight.Bold) },
            text = {
                Image(
                    painter = rememberAsyncImagePainter(userData?.fotoPerfil),
                    contentDescription = "Foto de perfil ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            },
            containerColor = Color.White
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.Start
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
                        modifier = Modifier.padding(top = 5.dp),
                        text = "Editar Foto",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val fotoPerfil = userData?.fotoPerfil
                    val painter = if (selectedImageUri != null) {
                        rememberAsyncImagePainter(selectedImageUri)
                    } else if (!fotoPerfil.isNullOrEmpty()) {
                        rememberAsyncImagePainter(fotoPerfil)
                    } else {
                        painterResource(id = R.drawable.camara)
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { galleryAndUploadLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mas),
                            contentDescription = "Agregar Foto",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BotonAccion("Hacer foto", R.drawable.camara) {
                        cameraLauncher.launch(imageUri)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    BotonAccion("Subir Foto", R.drawable.archivos) {
                        galleryAndUploadLauncher.launch("image/*")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    BotonAccion("Ver foto", R.drawable.ocultar) {
                        showDialog = true
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo_gris_grande),
                    contentDescription = "Logo_gris",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun BotonAccion(texto: String, icono: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .padding(vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = icono),
                contentDescription = texto,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = texto, color = Color.White, fontSize = 16.sp)
        }
    }
}
