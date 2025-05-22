package at.rent4u.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.MyBookingsViewModel
import coil.compose.AsyncImage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val bookings = viewModel.userBookings.collectAsState()
    val tools = viewModel.bookedTools.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserBookings()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("My Bookings", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(bookings.value) { booking ->
                    val tool = tools.value[booking.toolId]
                    if (tool != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                AsyncImage(
                                    model = tool.image,
                                    contentDescription = "Tool Image",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            navController.navigate(Screen.ToolDetails.createRoute(booking.toolId))
                                        }
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${tool.type} (${tool.brand})", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Model: ${tool.modelNumber}")
                                    Text(text = "Rental: ${tool.rentalRate}")
                                    Text(
                                        text = "From: ${booking.startDate} To: ${booking.endDate}",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = { viewModel.cancelBooking(booking) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        )
                                    ) {
                                        Text("Cancel Booking")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
