package com.example.pasteleriaapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember // <-- ¡IMPORT AÑADIDO!
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.pasteleriaapp.domain.repository.CarritoRepository
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.domain.repository.ProductoRepository
import com.example.pasteleriaapp.ui.screen.home.HomeScreen
import com.example.pasteleriaapp.domain.repository.UsuarioRepository
import com.example.pasteleriaapp.ui.screen.auth.LoginScreen
import com.example.pasteleriaapp.ui.screen.auth.RegisterScreen
import com.example.pasteleriaapp.ui.screen.carrito.CarritoScreen
import com.example.pasteleriaapp.ui.screen.productos.CategoriasScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoDetalleScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoFormScreen
import com.example.pasteleriaapp.ui.screen.productos.ProductoListScreen
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModel
import com.example.pasteleriaapp.ui.viewmodel.CategoriaViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoDetalleViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModel
import com.example.pasteleriaapp.ui.viewmodel.CarritoViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoFormViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModel
import com.example.pasteleriaapp.ui.viewmodel.ProductoViewModelFactory
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModel
import com.example.pasteleriaapp.ui.viewmodel.AuthViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    categoriaRepository: CategoriaRepository,
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    usuarioRepository: UsuarioRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, startDestination = Rutas.HOME, modifier = modifier
    ) {
        // --- 1. RUTA HOME (Pantalla de Inicio) ---
        composable(Rutas.HOME) {
            HomeScreen(onCatalogoClick = {
                navController.navigate(Rutas.CATEGORIAS)
            }, onNosotrosClick = {
                navController.navigate(Rutas.NOSOTROS)
            }, onLoginClick = {
                navController.navigate(Rutas.AUTH_FLOW)
            }, onCarritoClick = {
                navController.navigate(Rutas.CARRITO)
            })
        }

        authGraph(navController, usuarioRepository)

        // --- 2. RUTA CATEGORIAS (Catálogo) ---
        composable(Rutas.CATEGORIAS) {
            val factory = CategoriaViewModelFactory(categoriaRepository)
            val viewModel: CategoriaViewModel = viewModel(factory = factory)

            CategoriasScreen(viewModel = viewModel, onCategoriaClick = { idCategoria ->
                navController.navigate(Rutas.obtenerRutaProductos(idCategoria))
            }, onCarritoClick = {
                navController.navigate(Rutas.CARRITO)
            })
        }

        // --- 3. RUTA LISTA DE PRODUCTOS ---
        composable(
            route = Rutas.PRODUCTOS_RUTA, arguments = listOf(navArgument(Rutas.ARG_ID_CATEGORIA) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idCategoria = backStackEntry.arguments?.getInt(Rutas.ARG_ID_CATEGORIA)
            requireNotNull(idCategoria) { "idCategoria no encontrado en la ruta" }

            val factory = ProductoViewModelFactory(productoRepository, idCategoria)
            val viewModel: ProductoViewModel = viewModel(
                key = "producto_$idCategoria", factory = factory
            )

            ProductoListScreen(viewModel = viewModel, onBackClick = {
                navController.popBackStack()
            }, onProductoClick = { idProducto ->
                navController.navigate(Rutas.obtenerRutaDetalleProducto(idProducto))
            }, onAddProductoClick = {
                navController.navigate(Rutas.obtenerRutaNuevoProducto(idCategoria))
            })
        }

        // --- 4. RUTA DETALLE DE PRODUCTO ---
        composable(
            route = Rutas.DETALLE_PRODUCTO_RUTA,
            arguments = listOf(navArgument(Rutas.ARG_ID_PRODUCTO) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getInt(Rutas.ARG_ID_PRODUCTO)
            requireNotNull(idProducto) { "idProducto no encontrado en la ruta" }

            val factory = ProductoDetalleViewModelFactory(
                productoRepository, carritoRepository, idProducto
            )
            val viewModel: ProductoDetalleViewModel = viewModel(
                key = "detalle_producto_$idProducto", factory = factory
            )

            ProductoDetalleScreen(viewModel = viewModel, onBackClick = {
                navController.popBackStack()
            }, onEditProductoClick = {
                navController.navigate(Rutas.obtenerRutaEditarProducto(it))
            })
        }

        // --- 5. RUTA FORMULARIO DE PRODUCTO (Añadir/Editar) ---
        composable(
            route = Rutas.FORMULARIO_PRODUCTO,
            arguments = listOf(navArgument(Rutas.ARG_ID_PRODUCTO) {
                type = NavType.IntType
                defaultValue = 0
            }, navArgument(Rutas.ARG_ID_CATEGORIA) {
                type = NavType.IntType
                defaultValue = 0
            })) { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getInt(Rutas.ARG_ID_PRODUCTO) ?: 0
            val idCategoria = backStackEntry.arguments?.getInt(Rutas.ARG_ID_CATEGORIA) ?: 0

            if (idProducto == 0 && idCategoria == 0) {
                throw IllegalArgumentException("Se requiere un idCategoria para crear un producto nuevo")
            }

            val factory = ProductoFormViewModelFactory(productoRepository, idProducto, idCategoria)
            val viewModel: ProductoFormViewModel = viewModel(
                key = "form_producto_$idProducto", factory = factory
            )

            ProductoFormScreen(
                viewModel = viewModel, onBackClick = {
                    navController.popBackStack()
                })
        }

        // --- 6. RUTA NOSOTROS (Placeholder) ---
        composable(Rutas.NOSOTROS) {
            PlaceholderScreen(texto = "Pantalla 'Nosotros'")
        }

        // --- 7. RUTA CARRITO ---
        composable(Rutas.CARRITO) {
            val factory = CarritoViewModelFactory(carritoRepository)
            val viewModel: CarritoViewModel = viewModel(factory = factory)

            CarritoScreen(
                viewModel = viewModel, onBackClick = {
                    navController.popBackStack()
                })
        }

        // --- 8. RUTA LOGIN (ELIMINADA) ---
        // Se elimina el 'composable(Rutas.LOGIN)' duplicado que estaba aquí
    }
}


// --- ¡NUEVA FUNCIÓN PARA EL GRAFO ANIDADO! ---
private fun NavGraphBuilder.authGraph(
    navController: NavHostController, usuarioRepository: UsuarioRepository
) {
    navigation(
        startDestination = Rutas.LOGIN, route = Rutas.AUTH_FLOW // Esta es la ruta "madre"
    ) {
        // --- Pantalla de Login (Reemplaza el placeholder) ---
        composable(Rutas.LOGIN) { navBackStackEntry -> // Renombrado 'it' para claridad
            // Compartimos el ViewModel en el grafo anidado
            val authFactory = AuthViewModelFactory(usuarioRepository)

            // --- ¡CORRECCIÓN DEL WARNING! ---
            // Recordamos la entrada del grafo padre (AUTH_FLOW)
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Rutas.AUTH_FLOW)
            }
            // Pasamos el 'parentEntry' recordado como el dueño del ViewModel
            val authViewModel: AuthViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = authFactory
            )

            LoginScreen(viewModel = authViewModel, onLoginSuccess = {
                // Limpia el stack de auth y vuelve a Home
                navController.navigate(Rutas.HOME) {
                    popUpTo(Rutas.HOME) { inclusive = true }
                }
            }, onNavigateToRegister = {
                navController.navigate(Rutas.REGISTRO)
            })
        }

        // --- Pantalla de Registro (Nueva) ---
        composable(Rutas.REGISTRO) { navBackStackEntry -> // Renombrado 'it'
            val authFactory = AuthViewModelFactory(usuarioRepository)

            // --- ¡CORRECCIÓN DEL WARNING! ---
            // Obtenemos el MISMO ViewModel compartido
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Rutas.AUTH_FLOW)
            }
            val authViewModel: AuthViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = authFactory
            )

            RegisterScreen(viewModel = authViewModel, onRegisterSuccess = {
                navController.popBackStack() // Vuelve a Login
            }, onBackClick = {
                navController.popBackStack() // Vuelve a Login
            })
        }
    }
}


/**
 * Una pantalla genérica temporal para que la navegación funcione.
 */
@Composable
private fun PlaceholderScreen(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = MaterialTheme.typography.headlineMedium)
    }
}