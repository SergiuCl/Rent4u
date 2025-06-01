package at.rent4u.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    var tool by remember { mutableStateOf(Tool()) }
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val creationSuccess by viewModel.creationSuccess.collectAsState()

    val scrollState = rememberScrollState()
    val isFormValid = tool.type.isNotBlank()
            && tool.brand.isNotBlank()
            && tool.availabilityStatus.isNotBlank()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Add New Tool", style = MaterialTheme.typography.headlineMedium)

                DropDownField(
                    "Availability Status",
                    listOf("Available", "Unavailable"),
                    tool.availabilityStatus
                ) { tool = tool.copy(availabilityStatus = it) }

                LabeledField("Type", tool.type) { tool = tool.copy(type = it) }
                LabeledField("Brand", tool.brand) { tool = tool.copy(brand = it) }
                LabeledField("Model Number", tool.modelNumber) { tool = tool.copy(modelNumber = it) }
                LabeledField("Description", tool.description) { tool = tool.copy(description = it) }
                LabeledField("Weight", tool.weight) { tool = tool.copy(weight = it) }
                LabeledField("Dimensions", tool.dimensions) { tool = tool.copy(dimensions = it) }

                DropDownField(
                    "Power Source",
                    listOf("Electric", "Battery", "Manual", "Hybrid", "Pneumatic", "Hydraulic", "Solar", "Mechanical"),
                    tool.powerSource)
                { tool = tool.copy(powerSource = it) }

                DropDownField(
                    "Fuel Type",
                    listOf("Gasoline", "Diesel", "Electric"),
                    tool.fuelType)
                { tool = tool.copy(fuelType = it) }

                LabeledField("Voltage", tool.voltage) { tool = tool.copy(voltage = it) }
                LabeledField("Rental Rate", tool.rentalRate) { tool = tool.copy(rentalRate = it) }
                LabeledField("Image URL", tool.image) { tool = tool.copy(image = it) }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.createTool(tool) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 24.dp),
                    enabled = !isLoading && isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Create")
                }
            }

            if (isLoading) {
                LoadingScreen()
            }

            toastMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearToastMessage()
            }


            LaunchedEffect(creationSuccess) {
                if (creationSuccess == true) {
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

@Composable
fun LabeledField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}