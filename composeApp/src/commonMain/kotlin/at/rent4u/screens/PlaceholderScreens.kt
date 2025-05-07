package at.rent4u.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ToolListScreen(navController: NavController) {
    Text("Tool List Screen")
    // Example: Navigate on click
    // Button(onClick = { navController.navigate(Screen.ToolDetails("tool123")) }) { Text("Go to Details") }
}

@Composable
fun ToolDetailsScreen(toolId: String = "-1") {
    Text("Tool Details Screen for Tool ID: $toolId")
}

@Composable fun BookingScreen() { Text("Booking Screen") }
@Composable fun AdminToolEditorScreen() { Text("Admin Tool Editor Screen") }
@Composable fun LoginScreen() { Text("Login Screen") }
@Composable fun ProfileScreen() { Text("Profile Screen") }