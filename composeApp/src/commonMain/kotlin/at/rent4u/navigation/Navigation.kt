package at.rent4u.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.rent4u.screens.Screen
import at.rent4u.screens.ToolDetailsScreen
import at.rent4u.screens.ToolListScreen
import at.rent4u.screens.BookingScreen
import at.rent4u.screens.AdminToolEditorScreen
import at.rent4u.screens.LoginScreen
import at.rent4u.screens.ProfileScreen
import at.rent4u.screens.RegisterScreen

@Composable
fun Rent4uNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.ToolList.route) {
            ToolListScreen(navController)
        }

        composable(Screen.ToolDetails.route) {
            ToolDetailsScreen(navController)
        }

        composable(Screen.Booking.route) {
            BookingScreen(navController)
        }

        composable(Screen.AdminToolEditor.route) {
            AdminToolEditorScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
    }
}