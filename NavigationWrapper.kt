package com.example.uniqueartifacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.uniqueartifacts.views.EditarFotoScreen
import com.example.uniqueartifacts.views.Home
import com.example.uniqueartifacts.views.MenuLateral
import com.example.uniqueartifacts.views.PantallaCarga
import com.example.uniqueartifacts.views.PantallaInicio

@Composable
fun NavigationWrapper (navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = "pantallaHome") {

        composable ("pantallaCarga") {PantallaCarga(navHostController)}
        composable ("pantallaInicio") {PantallaInicio(navHostController)}
        composable ("pantallaHome") {Home(navHostController)}
        composable ("editarfoto") {EditarFotoScreen(navHostController)}

    }
}

