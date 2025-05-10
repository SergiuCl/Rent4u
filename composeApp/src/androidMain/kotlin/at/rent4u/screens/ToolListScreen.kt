package at.rent4u.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import at.rent4u.model.Tool
import at.rent4u.presentation.ToolListViewModel
import coil.compose.AsyncImage

@Composable
fun ToolListScreen(navController: NavController) {

    val viewModel: ToolListViewModel = hiltViewModel()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val tools by viewModel.tools.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            if (isAdmin) {
                Button(
                    onClick = { navController.navigate(Screen.AdminToolEditor.route) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Tool")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Tool")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            tools.forEach { (id, tool) ->
                ToolListItem(tool = tool) {
                    navController.navigate(Screen.ToolDetails.createRoute(id))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ToolListItem(tool: Tool, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            AsyncImage(
                model = tool.image,
                contentDescription = "Tool Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.alignByBaseline()) {
                Text(tool.brand, style = MaterialTheme.typography.titleMedium)
                Text(tool.type, style = MaterialTheme.typography.bodyMedium)

                // Availability Row with colored indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = if (tool.availabilityStatus.equals("available", ignoreCase = true)) {
                        Color(0xFF4CAF50) // Green
                    } else {
                        Color(0xFFF44336) // Red
                    }

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tool.availabilityStatus, style = MaterialTheme.typography.bodyMedium)
                }

                val cleanPrice = tool.rentalRate.replace("€", "").trim()
                Text("€$cleanPrice", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
