package at.rent4u.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import at.rent4u.screens.ARG_TOOL_ID
import at.rent4u.screens.Screen
import at.rent4u.screens.ToolDetailsScreen
import at.rent4u.screens.ToolListScreen
import at.rent4u.screens.BookingScreen
import at.rent4u.screens.AdminToolEditorScreen
import at.rent4u.screens.ContactUsScreen
import at.rent4u.screens.LoginScreen
import at.rent4u.screens.MyBookingsScreen
import at.rent4u.screens.ProfileScreen
import at.rent4u.screens.RegisterScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Rent4uNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.ToolList.route) {
            ToolListScreen(navController)
        }

        composable(
            Screen.ToolDetails.route,
            arguments = listOf(navArgument(ARG_TOOL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString(ARG_TOOL_ID) ?: ""
            ToolDetailsScreen(toolId = toolId, navController = navController)
        }

        composable(
            Screen.Booking.route,
            arguments = listOf(navArgument(ARG_TOOL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString(ARG_TOOL_ID) ?: ""
            BookingScreen(toolId = toolId, navController = navController)
        }

        composable(Screen.MyBookings.route) {
            MyBookingsScreen(navController)
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

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.ContactUs.route) {
            ContactUsScreen(navController)
        }
    }
}