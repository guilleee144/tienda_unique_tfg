package com.example.uniqueartifacts.views

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.uniqueartifacts.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "ajustes_usuario")

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Ajustes(navController: NavController) {
    val offsetY = remember { Animatable(1000f) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }

    val scrollState = rememberScrollState()
    val permisoArchivos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = offsetY.value.dp)
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color.Black)
            )

            CabeceraAjustes(onCerrar = {
                navController.navigate("pantallaHome") {
                    popUpTo("pantallaHome") { inclusive = false }
                    launchSingleTop = true
                }
            })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "AJUSTES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                AjustePersistente("Tema")

                Text(
                    text = "PERMISOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )

                PermisoItem("Cámara", Manifest.permission.CAMERA)
                PermisoItem("Micrófono", Manifest.permission.RECORD_AUDIO)
                PermisoItem("Archivos", permisoArchivos) // 👈 USA LA VARIABLE AQUÍ
                PermisoItem("Ubicación", Manifest.permission.ACCESS_FINE_LOCATION)
                PermisoItem("Notificaciones", Manifest.permission.POST_NOTIFICATIONS)

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "©2025 UNIQUE ARTIFACTS STORE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AjustePersistente(titulo: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val key = booleanPreferencesKey(titulo)

    val isCheckedFlow = context.dataStore.data.map { it[key] ?: false }
    val isChecked = isCheckedFlow.collectAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        BotonActivarNotis(checked = isChecked.value) {
            scope.launch {
                context.dataStore.edit { settings ->
                    settings[key] = it
                }
            }
        }
    }
}

@Composable
fun PermisoItem(nombre: String, permiso: String) {
    val context = LocalContext.current
    val activity = remember { context as? android.app.Activity }
    var permisoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permisoConcedido = isGranted
    }

    fun solicitarPermiso() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permiso)) {
            // Si ya lo denegó antes y no se puede volver a mostrar automáticamente
            activity.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", context.packageName, null)
            })
        } else {
            launcher.launch(permiso)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        BotonActivarNotis(checked = permisoConcedido) {
            solicitarPermiso()
        }
    }
}


@Composable
fun CabeceraAjustes(onCerrar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFF7F6F3))
                .height(60.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCerrar) {
                    Icon(
                        painter = painterResource(id = R.drawable.cerrar),
                        contentDescription = "Cerrar",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Configuración",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .offset(x = (-16).dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BotonActivarNotis(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val transition = updateTransition(targetState = checked, label = "switch")
    val offset by transition.animateDp(label = "offset") {
        if (it) 34.dp else 4.dp
    }

    val backgroundColor by transition.animateColor(label = "background") {
        if (it) Color(0xFF3598DB) else Color(0xFFDCDCDC)
    }

    Box(
        modifier = Modifier
            .width(60.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = offset)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
