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
    val tool = viewModel.tools.collectAsState().value.find { it.first == toolId }?.second

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        tool?.let {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                // Image at top
                AsyncImage(
                    model = it.image,
                    contentDescription = "Tool Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Left-aligned properties
                InfoRow("Model", it.modelNumber)
                InfoRow("Description", it.description)
                InfoRow("Availability", it.availabilityStatus)
                InfoRow("Power", it.powerSource)
                InfoRow("Type", it.type)
                InfoRow("Voltage", it.voltage)
                InfoRow("Fuel Type", it.fuelType)
                InfoRow("Weight", it.weight)
                InfoRow("Dimensions", it.dimensions)
                InfoRow("Rental rate", it.rentalRate)

                // Only show button if available
                if (it.availabilityStatus.equals("available", ignoreCase = true)) {
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

                    Spacer(modifier = Modifier.height(80.dp)) // padding above nav bar
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tool not found", style = MaterialTheme.typography.bodyLarge)
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