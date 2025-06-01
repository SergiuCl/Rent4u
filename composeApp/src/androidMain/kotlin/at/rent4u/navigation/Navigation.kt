package at.rent4u.navigation

import android.os.Build
import android.util.Log
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
import at.rent4u.screens.AdminToolCreateScreen
import at.rent4u.screens.ContactUsScreen
import at.rent4u.screens.LoginScreen
import at.rent4u.screens.MyBookingsScreen
import at.rent4u.screens.ProfileScreen
import at.rent4u.screens.RegisterScreen
import at.rent4u.screens.AdminToolUpdateScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Rent4uNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.ToolList.route) {
            Log.d("NavGraph", "Navigated to ToolList")
            ToolListScreen(navController)
        }

        composable(
            route = Screen.ToolDetails.route,
            arguments = listOf(navArgument(ARG_TOOL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            // ← This is where you extract toolId from nav args:
            val toolId = backStackEntry.arguments
                ?.getString(ARG_TOOL_ID)
                ?: return@composable

            Log.d("NavGraph", "Navigated to ToolDetails with toolId = $toolId")
            ToolDetailsScreen(
                toolId = toolId,
                navController = navController,
                onEditClick = { tool ->
                    Log.d("NavGraph", "Edit clicked for toolId = $toolId")
                    navController.navigate(Screen.AdminToolUpdate.createRoute(toolId))
                }
            )
        }

        composable(
            Screen.Booking.route,
            arguments = listOf(navArgument(ARG_TOOL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString(ARG_TOOL_ID) ?: ""
            Log.d("NavGraph", "Navigated to Booking with toolId = $toolId")
            BookingScreen(toolId = toolId, navController = navController)
        }

        composable(Screen.AdminToolCreate.route) {
            Log.d("NavGraph", "Navigated to AdminToolCreate")
            AdminToolCreateScreen(navController = navController)
        }

        composable(
            route = Screen.AdminToolUpdate.route,  // must be "admin_tool_update/{toolId}"
            arguments = listOf(navArgument(ARG_TOOL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(ARG_TOOL_ID) ?: return@composable
            Log.d("NavGraph", "Navigated to AdminToolUpdate with toolId = $id")
            AdminToolUpdateScreen(toolId = id, navController = navController)
        }

        composable(Screen.MyBookings.route) {
            Log.d("NavGraph", "Navigated to MyBookings")
            MyBookingsScreen(navController)
        }

        composable(Screen.Login.route) {
            Log.d("NavGraph", "Navigated to Login")
            LoginScreen(navController)
        }

        composable(Screen.Profile.route) {
            Log.d("NavGraph", "Navigated to Profile")
            ProfileScreen(navController)
        }

        composable(Screen.Register.route) {
            Log.d("NavGraph", "Navigated to Register")
            RegisterScreen(navController)
        }

        composable(Screen.Register.route) {
            Log.d("NavGraph", "Navigated to Register")
            RegisterScreen(navController)
        }

        composable(Screen.ContactUs.route) {
            Log.d("NavGraph", "Navigated to ContactUs")
            ContactUsScreen(navController)
        }
    }
}