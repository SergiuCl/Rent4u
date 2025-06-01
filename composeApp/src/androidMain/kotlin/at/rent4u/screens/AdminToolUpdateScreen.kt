package at.rent4u.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.AdminToolViewModel


@Composable
fun AdminToolUpdateScreen(
    toolId: String,
    navController: NavController,
    viewModel: AdminToolViewModel = hiltViewModel()
) {
    val tool by viewModel.editingTool.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(toolId) {
        viewModel.loadTool(toolId)
    }

    if (isLoading || tool == null) {
        CircularProgressIndicator()
    } else {
        var type by remember { mutableStateOf(tool!!.type) }
        var brand by remember { mutableStateOf(tool!!.brand) }
        var rate by remember { mutableStateOf(tool!!.rentalRate) }

        Column(modifier = Modifier.padding(16.dp)) {
            TextField(value = type, onValueChange = { type = it }, label = { Text("Type") })
            TextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") })
            TextField(value = rate, onValueChange = { rate = it }, label = { Text("Rental Rate") })

            Row {
                Button(onClick = {
                    viewModel.updateTool(
                        toolId = toolId,
                        updatedData = mapOf(
                            "type" to type,
                            "brand" to brand,
                            "rentalRate" to rate
                        )
                    )
                    navController.popBackStack()
                }) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }
            }
        }
    }
}
