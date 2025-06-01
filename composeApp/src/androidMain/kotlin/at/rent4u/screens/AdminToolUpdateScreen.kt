package at.rent4u.screens

import android.util.Log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.AdminToolViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

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

    // Local state for each field so we can prefill and then edit
    var brand by remember { mutableStateOf("") }
    var modelNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var availabilityStatus by remember { mutableStateOf("") }
    var powerSource by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var voltage by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var dimensions by remember { mutableStateOf("") }
    var rentalRate by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Once toolState is non-null, copy its values into our local vars
    LaunchedEffect(toolState) {
        toolState?.let { tool ->
            Log.d("AdminToolUpdate", "Populating fields with tool = $tool")
            brand = tool.brand
            modelNumber = tool.modelNumber
            description = tool.description
            availabilityStatus = tool.availabilityStatus
            powerSource = tool.powerSource
            type = tool.type
            voltage = tool.voltage
            fuelType = tool.fuelType
            weight = tool.weight
            dimensions = tool.dimensions
            rentalRate = tool.rentalRate.toString()
            imageUrl = tool.image
        }
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
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    LabeledField("Brand", brand) { brand = it }
                    LabeledField("Model Number", modelNumber) { modelNumber = it }
                    LabeledField("Description", description) { description = it }
                    LabeledField("Availability Status", availabilityStatus) { availabilityStatus = it }
                    LabeledField("Power Source", powerSource) { powerSource = it }
                    LabeledField("Type", type) { type = it }
                    LabeledField("Voltage", voltage) { voltage = it }
                    LabeledField("Fuel Type", fuelType) { fuelType = it }
                    LabeledField("Weight", weight) { weight = it }
                    LabeledField("Dimensions", dimensions) { dimensions = it }
                    LabeledField("Rental Rate (€)", rentalRate) { rentalRate = it }
                    LabeledField("Image URL", imageUrl) { imageUrl = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            Log.d("AdminToolUpdate", "Save clicked; current field values: brand=$brand, modelNumber=$modelNumber, description=$description, availabilityStatus=$availabilityStatus, powerSource=$powerSource, type=$type, voltage=$voltage, fuelType=$fuelType, weight=$weight, dimensions=$dimensions, rentalRate=$rentalRate, imageUrl=$imageUrl")
                            // Build a map of updated fields
                            val updatedData = mapOf(
                                "brand" to brand,
                                "modelNumber" to modelNumber,
                                "description" to description,
                                "availabilityStatus" to availabilityStatus,
                                "powerSource" to powerSource,
                                "type" to type,
                                "voltage" to voltage,
                                "fuelType" to fuelType,
                                "weight" to weight,
                                "dimensions" to dimensions,
                                "rentalRate" to (rentalRate.toDoubleOrNull() ?: 0.0),
                                "image" to imageUrl
                            )
                            Log.d("AdminToolUpdate", "Calling updateTool with id=${toolState!!.id} and data=$updatedData")
                            // Tell ViewModel to save
                            viewModel.updateTool(toolState!!.id, updatedData)
                            // After saving, pop back to detail screen
                            navController.popBackStack()
                            Log.d("AdminToolUpdate", "Navigated back after update")
                        }) {
                            Text("Save")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(onClick = {
                            // Cancel goes back without saving
                            navController.popBackStack()
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}