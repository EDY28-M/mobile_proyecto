package com.example.avisonline.presentation.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.avisonline.presentation.screen.*

// Define las rutas
sealed class Screen(val route: String, val label: String, val icon: @Composable (() -> Unit)? = null) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")
    object Main : Screen("main", "Main")
    
    // Rutas para la barra de navegación inferior
    object Home : Screen("home", "Inicio", { Icon(Icons.Default.Home, contentDescription = "Inicio") })
    object CreateProduct : Screen("create_product", "Crear", { Icon(Icons.Default.AddCircle, contentDescription = "Crear") })
    object Account : Screen("account", "Cuenta", { Icon(Icons.Default.Person, contentDescription = "Cuenta") })
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.CreateProduct,
    Screen.Account
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        
        // El grafo principal que contiene la barra de navegación
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = screen.icon!!,
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
             FloatingActionButton(
                 onClick = { 
                     // Reemplaza con tu número de WhatsApp
                     val url = "https://api.whatsapp.com/send?phone=+51973601133"
                     val intent = Intent(Intent.ACTION_VIEW)
                     intent.data = Uri.parse(url)
                     context.startActivity(intent)
                 },
                 containerColor = Color(0xFF25D366) // Color de WhatsApp
             ) {
                 // Puedes usar un Icono de WhatsApp si lo agregas a tus recursos
                 Text(" W ", color = Color.White, fontWeight = FontWeight.Bold)
             }
        }
    ) { innerPadding ->
        AppNavHost(navController = navController, padding = innerPadding)
    }
}

@Composable
fun AppNavHost(navController: NavHostController, padding: PaddingValues) {
    NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(padding)) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.CreateProduct.route) { CreateProductScreen() }
        composable(Screen.Account.route) { AccountScreen() }
    }
}