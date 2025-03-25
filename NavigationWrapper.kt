package com.example.uniqueartifacts

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import com.example.uniqueartifacts.views.Buscador
import com.example.uniqueartifacts.views.EditarFotoScreen
import com.example.uniqueartifacts.views.Guardados
import com.example.uniqueartifacts.views.Home
import com.example.uniqueartifacts.views.Notificaciones
import com.example.uniqueartifacts.views.PantallaCarga
import com.example.uniqueartifacts.views.PantallaInicio
import com.example.uniqueartifacts.views.Perfil
import com.example.uniqueartifacts.views.Premium
import com.example.uniqueartifacts.views.Puntos_Ofertas

@Composable
fun NavigationWrapper(navHostController: NavHostController, guardadosViewModel: GuardadosViewModel) {

    NavHost(navController = navHostController, startDestination = "pantallaCarga") {
        composable(route = "pantallaCarga") { PantallaCarga(navHostController) }
        composable(route = "pantallaInicio") { PantallaInicio(navHostController) }
        composable(route = "pantallaHome") { Home(navHostController, guardadosViewModel) }
        composable(route = "editarfoto") { EditarFotoScreen(navHostController) }
        composable(route = "perfil") { Perfil(navHostController) }
        composable(route = "puntosOfertas") { Puntos_Ofertas(navHostController) }
        composable(route = "premium") { Premium(navHostController) }
        composable(route = "notificaciones") { Notificaciones(navHostController) }
        composable(route = "buscador") { Buscador(navHostController) }
        composable(route = "guardados") { Guardados(navHostController, guardadosViewModel) }
    }
}
