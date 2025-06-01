package at.rent4u.screens

import android.util.Log

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.model.Tool
import at.rent4u.presentation.ToolListViewModel
import coil.compose.AsyncImage

@Composable
fun ToolDetailsScreen(
    toolId: String,
    navController: NavController,
    viewModel: ToolListViewModel = hiltViewModel(),
    onEditClick: (Tool) -> Unit
) {
    // 1) As soon as this screen appears, ask the VM to load exactly this one tool
    LaunchedEffect(toolId) {
        Log.d("ToolDetails", "LaunchedEffect: fetching tool for id = $toolId")
        viewModel.fetchToolById(toolId, forceRefresh = true) // Always force refresh when entering details screen
    }

    // Check if we need to refresh the tool when returning from the edit screen
    LaunchedEffect(Unit) {
        val refreshToolId = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<String>("REFRESH_TOOL")

        if (refreshToolId == toolId) {
            Log.d("ToolDetails", "Detected need to refresh tool ID: $toolId")
            viewModel.refreshTool(toolId)
        }
    }

    // 2) Observe the filteredTools flow, which will contain exactly [toolId -> Tool] once fetch completes
    val tools by viewModel.filteredTools.collectAsState()
    Log.d("ToolDetails", "Collected filteredTools: size = ${tools.size}, contents = $tools")
    val isFetchingTool by viewModel.isFetchingTool.collectAsState()
    Log.d("ToolDetails", "isFetchingTool = $isFetchingTool")

    // 3) Extract the actual Tool object (or null if not found)
    val tool: Tool? = tools.find { it.first == toolId }?.second
    Log.d("ToolDetails", "Resolved tool = $tool")

    // 4) Determine loading state
    val isLoading = tools.isEmpty() && isFetchingTool
    Log.d("ToolDetails", "isLoading = $isLoading")

    // 5) Check admin permission
    val isAdmin by viewModel.isAdmin.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        when {
            // 6a) Still loading from Firestore?
            isLoading -> {
                Log.d("ToolDetails", "Displaying loading spinner")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 6b) Tool found → render details
            tool != null -> {
                Log.d("ToolDetails", "Displaying tool details for id = $toolId")
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
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
                    LabelInputPair("Rental rate", "${tool.rentalRate}€ / day")

                    Spacer(modifier = Modifier.height(16.dp))

                    if (tool.availabilityStatus.equals("Available", ignoreCase = true)) {
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

                    if (isAdmin) {
                        Button(onClick = {
                            Log.d("ToolDetails", "Edit clicked for toolId = $toolId")
                            navController.navigate(Screen.AdminToolUpdate.createRoute(toolId))
                        }) {
                            Text("Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // 6c) Neither loading nor tool found ⇒ show "not found"
            else -> {
                Log.d("ToolDetails", "Tool not found for id = $toolId")
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
