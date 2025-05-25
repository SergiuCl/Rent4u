package at.rent4u.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.model.Booking
import at.rent4u.presentation.MyBookingsViewModel
import coil.compose.AsyncImage
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val bookings = viewModel.userBookings.collectAsState()
    val tools = viewModel.bookedTools.collectAsState()
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()

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

            // only show active bookings, not the ones from the past
            val activeBookings = bookings.value.filter {
                val endDate = LocalDate.parse(it.endDate)
                !endDate.isBefore(LocalDate.now())
            }

            if (activeBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "You currently have no active bookings. Book a tool to get started!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { navController.navigate(Screen.ToolList.route) }) {
                            Text("Browse Tools")
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(activeBookings) { booking ->
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
                                            onClick = { bookingToCancel = booking },
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

            toastMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearToastMessage()
            }
        }

        bookingToCancel?.let { booking ->
            AlertDialog(
                onDismissRequest = { bookingToCancel = null },
                title = { Text("Confirm Cancellation") },
                text = { Text("Are you sure you want to cancel this booking?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.cancelBooking(booking)
                        bookingToCancel = null
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookingToCancel = null }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
