package com.example.uniqueartifacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import com.example.uniqueartifacts.views.DetalleProducto
import com.example.uniqueartifacts.views.Home
import com.example.uniqueartifacts.views.LoginScreen
import com.example.uniqueartifacts.views.PantallaCarga
import com.example.uniqueartifacts.views.PantallaInicio
import com.example.uniqueartifacts.views.PerfilScreen
import com.example.uniqueartifacts.views.RegistroScreen

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    guardadosViewModel: GuardadosViewModel,
    carritoViewModel: CarritoViewModel,
    detalleProductoViewModel: DetalleProductoViewModel
) {
    NavHost(navController = navHostController, startDestination = "pantallaCarga") {
        composable("pantallaCarga") { PantallaCarga(navHostController) }
        composable("pantallaInicio") { PantallaInicio(navHostController) }
        composable("loginScreen") { LoginScreen(navHostController) }
        composable("registroScreen") { RegistroScreen(navHostController) }
        composable("perfil") { PerfilScreen(navHostController) }
        composable("pantallaHome") {
            Home(
                navController = navHostController,
                guardadosViewModel = guardadosViewModel,
                carritoViewModel = carritoViewModel,
                detalleProductoViewModel = detalleProductoViewModel
            )
        }
        composable("detallesProducto") {
            detalleProductoViewModel.productoSeleccionado.value?.let { producto ->
                DetalleProducto(
                    navController = navHostController,
                    producto = producto,
                    guardadosViewModel = guardadosViewModel,
                    carritoViewModel = carritoViewModel
                )
            }
        }
    }
}

