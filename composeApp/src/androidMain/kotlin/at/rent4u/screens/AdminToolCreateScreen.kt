package at.rent4u.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.model.Tool
import at.rent4u.presentation.AdminToolViewModel

@Composable
fun AdminToolCreateScreen(
    navController: NavController
) {
    val viewModel: AdminToolViewModel = hiltViewModel()
    val context = LocalContext.current

    Log.d("AdminToolCreate", "Composing AdminToolCreateScreen")

    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val creationSuccess by viewModel.creationSuccess.collectAsState()

    Log.d("AdminToolCreate", "State - isLoading: $isLoading, toastMessage: $toastMessage, creationSuccess: $creationSuccess")

    // Show toast messages
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearToastMessage()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Use shared form component
            AdminToolForm(
                initialTool = Tool(),
                title = "Add New Tool",
                isLoading = isLoading,
                isUpdate = false,
                onSave = { tool, rentalRateText ->
                    // Parse rentalRateText into a Double; default to 0.0 if invalid
                    val parsedRate = rentalRateText.toDoubleOrNull() ?: 0.0
                    val updatedTool = tool.copy(rentalRate = parsedRate)
                    viewModel.createTool(updatedTool)
                },
                onCancel = {
                    navController.popBackStack()
                }
            )

            if (isLoading) {
                LoadingScreen()
            }

            LaunchedEffect(creationSuccess) {
                Log.d("AdminToolCreate", "creationSuccess changed: $creationSuccess")
                if (creationSuccess == true) {
                    Log.d("AdminToolCreate", "Navigating to ToolList because creation succeeded")
                    navController.navigate(Screen.ToolList.route) {
                        popUpTo(Screen.AdminToolCreate.route) { inclusive = true }
                    }
                    viewModel.clearToastMessage()
                    viewModel.clearCreationSuccess()
                }
            }
        }
    }
}
