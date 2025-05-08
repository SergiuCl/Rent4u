package at.rent4u.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ToolListScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Tool List Screen")
    }
    // Example: Navigate on click
    // Button(onClick = { navController.navigate(Screen.ToolDetails("tool123")) }) { Text("Go to Details") }
}