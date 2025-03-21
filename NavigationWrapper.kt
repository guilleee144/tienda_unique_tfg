package com.example.uniqueartifacts

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.uniqueartifacts.views.Home
import com.example.uniqueartifacts.views.PantallaCarga
import com.example.uniqueartifacts.views.PantallaFiguras
import com.example.uniqueartifacts.views.PantallaInicio

@Composable
fun NavigationWrapper (navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = "pantallaCarga") {

        composable ("pantallaCarga") {PantallaCarga(navHostController)}
        composable ("pantallaInicio") {PantallaInicio(navHostController)}
        composable ("pantallaHome") {Home(navHostController)}
        composable ("figuras") {PantallaFiguras(navHostController)}
       composable ("editarfoto") {PantallaEditarFoto(navHostController)}
        


    }
}
