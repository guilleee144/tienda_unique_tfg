package com.example.uniqueartifacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uniqueartifacts.model.Producto
import com.example.uniqueartifacts.viewmodel.CarritoViewModel
import com.example.uniqueartifacts.viewmodel.DetalleProductoViewModel
import com.example.uniqueartifacts.viewmodel.GuardadosViewModel
import com.example.uniqueartifacts.views.Ajustes
import com.example.uniqueartifacts.views.Buscador
import com.example.uniqueartifacts.views.Carrito
import com.example.uniqueartifacts.views.DetalleProducto
import com.example.uniqueartifacts.views.EditarFotoScreen
import com.example.uniqueartifacts.views.Home
import com.example.uniqueartifacts.views.LoginScreen
import com.example.uniqueartifacts.views.PantallaCarga
import com.example.uniqueartifacts.views.PantallaInicio
import com.example.uniqueartifacts.views.PerfilScreen
import com.example.uniqueartifacts.views.Puntos_Ofertas
import com.example.uniqueartifacts.views.RegistroScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.uniqueartifacts.views.CanjearCuponScreen
import com.example.uniqueartifacts.views.ConfirmarPedido
import com.example.uniqueartifacts.views.FormularioCompra
import com.example.uniqueartifacts.views.Guardados


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
        composable(route = "editarfoto") { EditarFotoScreen(navHostController) }
        composable(route = "buscador") { Buscador(navHostController) }
        composable(route = "ajustes") {Ajustes(navHostController) }
        composable(route = "guardados") { Guardados(navHostController, guardadosViewModel, carritoViewModel) }
        composable("puntosOfertas") {
            Puntos_Ofertas(navController = navHostController)
        }
        composable("confirmarPedido") {
            ConfirmarPedido(
                navController = navHostController,
                carritoViewModel = carritoViewModel,
                detalleProductoViewModel = detalleProductoViewModel
            )
        }
        composable("formularioPedido") {
            FormularioCompra(
                navController = navHostController,
                carritoViewModel = carritoViewModel,
                detalleProductoViewModel = detalleProductoViewModel
            )
        }

        composable(
            route = "canjearCupon?titulo={titulo}&imagen={imagen}&descuento={descuento}",
            arguments = listOf(
                navArgument("titulo") { type = NavType.StringType },
                navArgument("imagen") { type = NavType.StringType },
                navArgument("descuento") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val titulo = backStackEntry.arguments?.getString("titulo") ?: ""
            val imagen = backStackEntry.arguments?.getString("imagen") ?: ""
            val descuento = backStackEntry.arguments?.getString("descuento") ?: ""

            CanjearCuponScreen(
                titulo = titulo,
                imagen = imagen,
                descuento = descuento,
                carritoViewModel = carritoViewModel,
                navController = navHostController
            )
        }

        composable(route = "carrito") {
            Carrito(
                navController = navHostController,
                carritoViewModel = carritoViewModel
            )
        }

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

