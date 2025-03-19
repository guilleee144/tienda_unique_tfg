package com.example.login


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.login.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.MAIN_LOGIN) {
        composable(Routes.MAIN_LOGIN) { MainLoginScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) } // Asegurar que HomeScreen reciba el navController si lo necesita
    }
}

// Definir rutas en un objeto para evitar errores de escritura
object Routes {
    const val MAIN_LOGIN = "main_login"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}
