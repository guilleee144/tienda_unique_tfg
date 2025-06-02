package com.example.uniqueartifacts

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.uniqueartifacts.supabase.SupabaseClientProvider
import com.example.uniqueartifacts.viewmodel.NotificacionesViewModel
import com.example.uniqueartifacts.views.CanjearCuponScreen
import com.example.uniqueartifacts.views.ConfirmarPedido
import com.example.uniqueartifacts.views.FormularioCompra
import com.example.uniqueartifacts.views.Guardados
import com.example.uniqueartifacts.views.Notificaciones
import com.example.uniqueartifacts.views.OfertasScreen
import com.example.uniqueartifacts.views.Premium


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    guardadosViewModel: GuardadosViewModel,
    carritoViewModel: CarritoViewModel,
    detalleProductoViewModel: DetalleProductoViewModel,
    notificacionesViewModel : NotificacionesViewModel
) {
    NavHost(navController = navHostController, startDestination = "pantallaCarga") {
        composable("pantallaCarga") { PantallaCarga(navHostController) }
        composable("pantallaInicio") { PantallaInicio(navHostController) }
        composable("loginScreen") { LoginScreen(navHostController) }
        composable("registroScreen") { RegistroScreen(navHostController) }
        composable("perfil") { PerfilScreen(navHostController) }
        composable(route = "editarfoto") { EditarFotoScreen(navHostController) }
        composable(route = "buscador") { Buscador(navHostController, carritoViewModel) }
        composable(route = "ajustes") {Ajustes(navHostController) }
        composable(route = "ofertas") { OfertasScreen(navHostController, carritoViewModel,detalleProductoViewModel,guardadosViewModel ) }
        composable("guardados") {
            Guardados(
                navController = navHostController,
                guardadosViewModel = guardadosViewModel,
                carritoViewModel = carritoViewModel,
                notificacionesViewModel = notificacionesViewModel // ✅ Añade esto
            )
        }
        composable("notificaciones") {
            Notificaciones(
                navController = navHostController,
                viewModel = notificacionesViewModel
            )
        }

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
                detalleProductoViewModel = detalleProductoViewModel,
                notificacionesViewModel = notificacionesViewModel
            )
        }
        composable(route = "premium") {
            Premium(
                navController = navHostController,
                supabase = SupabaseClientProvider.getClient(),
                viewModel = notificacionesViewModel,
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

