package com.example.uniqueartifacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uniqueartifacts.ui.theme.UniqueArtifactsTheme
import com.example.uniqueartifacts.views.Home
import com.example.uniqueartifacts.views.PantallaCarga
import com.example.uniqueartifacts.views.PantallaFiguras
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            UniqueArtifactsTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "pantallaCarga",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("pantallaCarga") {
                        PantallaCarga(navController = navController)
                    }
                    composable("home") {
                        Home(navController = navController)
                    }
                    // Nueva ruta para la pantalla de figuras
                    composable("figuras") {
                        PantallaFiguras(navController = navController)
                    }
                    composable ("editarfoto") {
                        EditarFotoScreen(navHostController)
                    }
                }
            }
        }
    }
}
