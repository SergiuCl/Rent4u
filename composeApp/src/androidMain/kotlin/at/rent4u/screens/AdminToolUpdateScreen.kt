package at.rent4u.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.AdminToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminToolUpdateScreen(
    toolId: String,
    navController: NavController,
    viewModel: AdminToolViewModel = hiltViewModel()
) {
    Log.d("AdminToolUpdate", "Composing AdminToolUpdateScreen for toolId = $toolId")
    
    // When this composable is first shown (or toolId changes), ask ViewModel to load it
    LaunchedEffect(toolId) {
        Log.d("AdminToolUpdate", "LaunchedEffect: loading tool for id = $toolId")
        viewModel.loadTool(toolId)
    }

    // Observe the loaded tool from ViewModel
    val toolState by viewModel.editingTool.collectAsState()
    Log.d("AdminToolUpdate", "Collected editingTool: $toolState")
    
    // Track update success and loading state
    val creationSuccess by viewModel.creationSuccess.collectAsState()
    val deletionSuccess by viewModel.deletionSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // For toast messages
    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()
    
    // For delete confirmation dialog
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    // Show toast messages
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearToastMessage()
    }

    // Wrap everything in a Scaffold so we get the standard top‐app‐bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Tool") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (toolState == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Use shared form component
                AdminToolForm(
                    initialTool = toolState!!,
                    title = "Edit Tool Details",
                    isLoading = isLoading,
                    isUpdate = true,
                    onSave = { tool, rentalRateText ->
                        Log.d("AdminToolUpdate", "Save clicked with tool: $tool")
                        // Build a map of updated fields
                        val updatedData = mapOf(
                            "brand" to tool.brand,
                            "modelNumber" to tool.modelNumber,
                            "description" to tool.description,
                            "availabilityStatus" to tool.availabilityStatus,
                            "powerSource" to tool.powerSource,
                            "type" to tool.type,
                            "voltage" to tool.voltage,
                            "fuelType" to tool.fuelType,
                            "weight" to tool.weight,
                            "dimensions" to tool.dimensions,
                            "rentalRate" to (rentalRateText.toDoubleOrNull() ?: 0.0),
                            "image" to tool.image
                        )
                        Log.d("AdminToolUpdate", "Calling updateTool with id=${toolState!!.id} and data=$updatedData")
                        // Tell ViewModel to save
                        viewModel.updateTool(toolState!!.id, updatedData)
                    },
                    onCancel = {
                        // Cancel goes back without saving
                        navController.popBackStack()
                    },
                    onDelete = {
                        // Show delete confirmation dialog
                        showDeleteConfirmation = true
                    }
                )
                
                // Show delete confirmation dialog if needed
                if (showDeleteConfirmation) {
                    DeleteConfirmationDialog(
                        toolName = "${toolState!!.brand} ${toolState!!.modelNumber}",
                        onConfirm = {
                            viewModel.deleteTool(toolState!!.id)
                            showDeleteConfirmation = false
                        },
                        onDismiss = {
                            showDeleteConfirmation = false
                        }
                    )
                }
            }
        }
    }
    
    // Handle successful update navigation
    LaunchedEffect(creationSuccess) {
        if (creationSuccess == true) {
            Log.d("AdminToolUpdate", "Update successful, setting refresh flag and navigating back")
            
            // Set the refresh flag on the previous backstack entry BEFORE navigating back
            navController.previousBackStackEntry?.savedStateHandle?.set("REFRESH_TOOL", toolId)
            navController.popBackStack()
            
            // Clear the success state
            viewModel.clearCreationSuccess()
        }
    }
    
    // Handle successful deletion navigation
    LaunchedEffect(deletionSuccess) {
        if (deletionSuccess == true) {
            Log.d("AdminToolUpdate", "Delete successful, navigating to tool list")
            
            // Set refresh flag in the tool list screen's saved state
            // Find the ToolList entry in the backstack
            navController.getBackStackEntry(Screen.ToolList.route).savedStateHandle.set(
                "REFRESH_TOOLS_LIST", true
            )
            
            // After deletion, navigate back to tool list
            navController.navigate(Screen.ToolList.route) {
                // Pop up to tool list including the tool list screen (will recreate it)
                popUpTo(Screen.ToolList.route) { inclusive = true }
            }
            
            // Clear the success state
            viewModel.clearDeletionSuccess()
        }
    }
}
