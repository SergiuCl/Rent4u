package at.rent4u.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.ToolListViewModel
import coil.compose.AsyncImage

@Composable
fun ToolDetailsScreen(
    toolId: String,
    navController: NavController,
    viewModel: ToolListViewModel = hiltViewModel()
) {
    val toolsState = viewModel.tools.collectAsState()
    val tool = toolsState.value.find { it.first == toolId }?.second
    val isLoading = toolsState.value.isEmpty()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            tool != null -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    AsyncImage(
                        model = tool.image,
                        contentDescription = "Tool Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    InfoRow("Model", tool.modelNumber)
                    InfoRow("Description", tool.description)
                    InfoRow("Availability", tool.availabilityStatus)
                    InfoRow("Power", tool.powerSource)
                    InfoRow("Type", tool.type)
                    InfoRow("Voltage", tool.voltage)
                    InfoRow("Fuel Type", tool.fuelType)
                    InfoRow("Weight", tool.weight)
                    InfoRow("Dimensions", tool.dimensions)
                    InfoRow("Rental rate", tool.rentalRate)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate(Screen.Booking.createRoute(toolId))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Book this Tool")
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tool not found", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier
        .padding(bottom = 16.dp)
        .fillMaxWidth()
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value)
    }
}