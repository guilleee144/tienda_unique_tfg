package com.example.uniqueartifacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.ui.theme.UniqueArtifactsTheme
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import com.example.uniqueartifacts.NavigationWrapper
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.uniqueartifacts.viewmodel.NotificacionesViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val supabaseClient = SupabaseClientProvider.getClient()

        setContent {
            UniqueArtifactsTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val guardadosViewModel: GuardadosViewModel = viewModel()
                    val carritoViewModel: CarritoViewModel = viewModel()
                    val detalleProductoViewModel: DetalleProductoViewModel = viewModel()
                    val notificacionesViewModel = viewModel<NotificacionesViewModel>()


                    // 🔁 Cargar productos guardados al arrancar la app
                    LaunchedEffect(Unit) {
                        guardadosViewModel.cargarProductosGuardadosDesdeReferencias()
                    }

                    NavigationWrapper(
                        navHostController = navController,
                        guardadosViewModel = guardadosViewModel,
                        carritoViewModel = carritoViewModel,
                        detalleProductoViewModel = detalleProductoViewModel,
                        notificacionesViewModel
                    )
                }
            }
        }
    }
}

