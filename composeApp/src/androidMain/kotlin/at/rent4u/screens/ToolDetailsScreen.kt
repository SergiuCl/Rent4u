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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
    val fallbackToolState = viewModel.singleTool.collectAsState()

    // will be true if the viewModel is loading one tool from the database
    // when the fetchToolIById method is executed
    val isFetchingTool = viewModel.isFetchingTool.collectAsState()

    // this will be set if the tool was already loaded before
    val toolFromList = toolsState.value.find { it.first == toolId }?.second

    // this will be used if the tool was not already loaded
    val fallbackTool = fallbackToolState.value?.takeIf { it.first == toolId }?.second

    val tool = toolFromList ?: fallbackTool
    val isLoading = toolsState.value.isEmpty() && fallbackTool == null

    // trigger fetch if needed
    LaunchedEffect(toolId) {
        if (toolFromList == null && fallbackTool == null) {
            viewModel.fetchToolIById(toolId)
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        when {
            isLoading || isFetchingTool.value -> {
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

                    LabelInputPair("Brand", tool.brand)
                    LabelInputPair("Model", tool.modelNumber)
                    LabelInputPair("Description", tool.description)
                    LabelInputPair("Availability", tool.availabilityStatus)
                    LabelInputPair("Power", tool.powerSource)
                    LabelInputPair("Type", tool.type)
                    LabelInputPair("Voltage", tool.voltage)
                    LabelInputPair("Fuel Type", tool.fuelType)
                    LabelInputPair("Weight", tool.weight)
                    LabelInputPair("Dimensions", tool.dimensions)
                    LabelInputPair("Rental rate", tool.rentalRate)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (tool.availabilityStatus == "Available") {
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