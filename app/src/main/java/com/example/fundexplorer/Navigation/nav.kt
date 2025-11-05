package com.example.fundexplorer.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fundexplorer.presentation.Screens.DetailScreen
import com.example.fundexplorer.presentation.Screens.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{schemeCode}") {
        fun createRoute(schemeCode: String) = "detail/$schemeCode"
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onFundClick = { schemeCode ->
                    navController.navigate(Screen.Detail.createRoute(schemeCode))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("schemeCode") { type = NavType.StringType }
            )
        ) {
            DetailScreen(
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
