package at.rent4u.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.rent4u.model.Tool

@Composable
fun AdminToolForm(
    initialTool: Tool,
    title: String,
    isLoading: Boolean,
    isUpdate: Boolean = false,
    onSave: (Tool, String) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    
    // Form state
    var tool by remember { mutableStateOf(initialTool) }
    var rentalRateText by remember { 
        mutableStateOf(if (initialTool.rentalRate > 0) initialTool.rentalRate.toString() else "") 
    }
    
    // Validation
    val isFormValid = tool.type.isNotBlank() &&
            tool.brand.isNotBlank() &&
            tool.availabilityStatus.isNotBlank()
    
    Column(
        modifier = Modifier
            .padding(if (isUpdate) 16.dp else 24.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth(),
        horizontalAlignment = if (isUpdate) Alignment.Start else Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Availability Status - Dropdown
        DropDownField(
            label = "Availability Status",
            options = listOf("Available", "Unavailable"),
            selectedValue = tool.availabilityStatus,
            onChange = { newValue -> tool = tool.copy(availabilityStatus = newValue) }
        )
        
        // Type - Regular field
        LabeledField(
            label = "Type", 
            value = tool.type, 
            onChange = { newValue -> tool = tool.copy(type = newValue) }
        )
        
        // Brand - Regular field
        LabeledField(
            label = "Brand", 
            value = tool.brand, 
            onChange = { newValue -> tool = tool.copy(brand = newValue) }
        )
        
        // Model Number - Regular field
        LabeledField(
            label = "Model Number", 
            value = tool.modelNumber, 
            onChange = { newValue -> tool = tool.copy(modelNumber = newValue) }
        )
        
        // Description - Regular field
        LabeledField(
            label = "Description", 
            value = tool.description, 
            onChange = { newValue -> tool = tool.copy(description = newValue) }
        )
        
        // Weight - Regular field
        LabeledField(
            label = "Weight", 
            value = tool.weight, 
            onChange = { newValue -> tool = tool.copy(weight = newValue) }
        )
        
        // Dimensions - Regular field
        LabeledField(
            label = "Dimensions", 
            value = tool.dimensions, 
            onChange = { newValue -> tool = tool.copy(dimensions = newValue) }
        )
        
        // Power Source - Dropdown
        DropDownField(
            label = "Power Source",
            options = listOf("Electric", "Battery", "Manual", "Hybrid", "Pneumatic", "Hydraulic", "Solar", "Mechanical"),
            selectedValue = tool.powerSource,
            onChange = { newValue -> tool = tool.copy(powerSource = newValue) }
        )
        
        // Fuel Type - Dropdown
        DropDownField(
            label = "Fuel Type",
            options = listOf("Gasoline", "Diesel", "Electric"),
            selectedValue = tool.fuelType,
            onChange = { newValue -> tool = tool.copy(fuelType = newValue) }
        )
        
        // Voltage - Regular field
        LabeledField(
            label = "Voltage", 
            value = tool.voltage, 
            onChange = { newValue -> tool = tool.copy(voltage = newValue) }
        )
        
        // Rental Rate - Regular field
        OutlinedTextField(
            value = rentalRateText,
            onValueChange = { newValue -> rentalRateText = newValue },
            label = { Text("Rental Rate (€)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        
        // Image URL - Regular field
        LabeledField(
            label = "Image URL", 
            value = tool.image, 
            onChange = { newValue -> tool = tool.copy(image = newValue) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Buttons - different layout based on whether it's create or update
        if (isUpdate) {
            // Update screen has save and cancel buttons
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Log.d("AdminToolForm", "Save clicked with tool: $tool")
                        onSave(tool, rentalRateText)
                    },
                    enabled = !isLoading && isFormValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
            
            // Add delete button below save/cancel if we're in update mode
            if (onDelete != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Tool")
                }
            }
        } else {
            // Create screen has a single centered Create button
            Button(
                onClick = {
                    Log.d("AdminToolForm", "Create button clicked with tool: $tool")
                    onSave(tool, rentalRateText)
                },
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
    }
}
