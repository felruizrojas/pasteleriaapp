package com.example.pasteleriaapp.ui.navigation

// Imports existentes
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType // Import necesario
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument // Import necesario
// Import (probablemente) innecesario, lo quitamos
// import com.example.pasteleriaapp.data.repository.ProductoRepositoryImpl
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.screen.home.HomeScreen
import com.example.pasteleriaapp.ui.screen.productos.CategoriasScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoListScreen
// --- Imports NUEVOS para la pantalla de detalle ---
import com.example.pasteleriaapp.ui.screen.productos.ProductoDetalleScreen
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModelFactory
// --- Fin de imports nuevos ---
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoriaRepository: CategoriaRepository,
    productoRepository: ProductoRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.HOME,
        modifier = modifier
    ) {
        composable(Rutas.HOME) {
            HomeScreen(
                onVerCategoriasClick = {
                    navController.navigate(Rutas.CATEGORIAS)
                }
            )
        }

        composable(Rutas.CATEGORIAS) {
            val factory = CategoriaViewModelFactory(categoriaRepository)
            val viewModel: CategoriaViewModel = viewModel(factory = factory)

            CategoriasScreen(
                viewModel = viewModel,
                onCategoriaClick = { idCategoria ->
                    navController.navigate(Rutas.obtenerRutaProductos(idCategoria))
                }
            )
        }

        composable(
            route = Rutas.PRODUCTOS_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_CATEGORIA) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idCategoria = backStackEntry.arguments?.getInt(Rutas.ARG_ID_CATEGORIA)
            requireNotNull(idCategoria) { "idCategoria no encontrado en la ruta" }

            // Tus correcciones anteriores ya están aquí (¡perfecto!)
            val factory = ProductoViewModelFactory(productoRepository, idCategoria)
            val viewModel: ProductoViewModel = viewModel(
                key = "producto_$idCategoria",
                factory = factory
            )

            ProductoListScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                // --- CAMBIO AQUÍ ---
                // Se implementa la navegación a detalle
                onProductoClick = { idProducto ->
                    navController.navigate(Rutas.obtenerRutaDetalleProducto(idProducto))
                }
            )
        }

        // --- NUEVO COMPOSABLE AÑADIDO ---
        // Ruta para la pantalla de detalle del producto
        composable(
            route = Rutas.DETALLE_PRODUCTO_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_PRODUCTO) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getInt(Rutas.ARG_ID_PRODUCTO)
            requireNotNull(idProducto) { "idProducto no encontrado en la ruta" }

            // Factory y ViewModel para la pantalla de detalle
            val factory = ProductoDetalleViewModelFactory(productoRepository, idProducto)
            val viewModel: ProductoDetalleViewModel = viewModel(
                key = "detalle_producto_$idProducto",
                factory = factory
            )

            ProductoDetalleScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}