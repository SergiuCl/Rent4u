package at.rent4u.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.BookingViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingScreen(
    toolId: String,
    navController: NavController,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tool by viewModel.tool.collectAsState()
    val bookedDates by viewModel.bookedDates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val bookingSuccess by viewModel.bookingSuccessful.collectAsState()

    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchTool(toolId)
    }

    LaunchedEffect(bookingSuccess) {
        if (bookingSuccess) {
            navController.navigate(Screen.ToolList.route) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.clearBookingState()
        }
    }

    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearToastMessage()
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            tool?.let {
                LabelInputPair("Brand", it.brand)
                LabelInputPair("Model", it.modelNumber)
                LabelInputPair("Type", it.type)
                LabelInputPair("Rental Rate", "${it.rentalRate}€ / day")
            }

            Spacer(Modifier.height(16.dp))

            Text("Select a booking date", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(16.dp))

            CustomCalendarPicker(
                bookedDates = bookedDates,
                onDateRangeSelected = { start, end ->
                    selectedStartDate = start
                    selectedEndDate = end
                }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedStartDate != null && selectedEndDate != null) {
                        val rate = tool?.rentalRate?.replace("€", "")?.trim()?.toDoubleOrNull()
                        val days = ChronoUnit.DAYS.between(selectedStartDate, selectedEndDate) + 1
                        val totalAmount = if (rate != null) {
                            String.format("%.2f", rate * days).toDouble()
                        } else 0.0

                        viewModel.book(toolId, selectedStartDate!!, selectedEndDate!!, totalAmount)
                    }
                },
                enabled = selectedStartDate != null && selectedEndDate != null && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Booking")
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendarPicker(
    bookedDates: List<LocalDate>,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit
) {
    val context = LocalContext.current
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val days: List<LocalDate?> = remember(currentMonth) {
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDay = currentMonth.lengthOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

        (0 until ((firstDayOfWeek + lastDay + 6) / 7) * 7).map { index ->
            val dayOffset = index - firstDayOfWeek
            if (dayOffset in 0 until lastDay) currentMonth.atDay(dayOffset + 1) else null
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("<", modifier = Modifier.clickable { currentMonth = currentMonth.minusMonths(1) })
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(">", modifier = Modifier.clickable { currentMonth = currentMonth.plusMonths(1) })
        }

        // Weekday labels
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            DayOfWeek.values().forEach {
                Text(
                    text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier.height(300.dp).padding(8.dp)
        ) {
            itemsIndexed(days) { _, date ->
                val isPast = date != null && date.isBefore(LocalDate.now())
                val isBooked = date != null && bookedDates.contains(date)
                val isSelected = date != null && (date == startDate || date == endDate ||
                        (startDate != null && endDate != null &&
                                date.isAfter(startDate) && date.isBefore(endDate)))

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .background(
                            when {
                                isBooked || isPast -> Color.LightGray
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> Color.Transparent
                            }
                        )
                        .clickable(enabled = date != null && !isBooked && !isPast) {
                            date?.let {
                                if (startDate == null || (startDate != null && endDate != null)) {
                                    startDate = it
                                    endDate = null
                                } else if (it.isBefore(startDate)) {
                                    startDate = it
                                } else {
                                    // Validate range before accepting - should not be able to select
                                    // a range which contains already booked days
                                    val rangeStart = startDate!!
                                    val rangeEnd = it

                                    val isOverlapping = bookedDates.any { booked ->
                                        // Treat each booked day as its own 1-day range
                                        !rangeStart.isAfter(booked) && !rangeEnd.isBefore(booked)
                                    }

                                    if (isOverlapping) {
                                        Toast.makeText(
                                            context,
                                            "Selected range includes booked dates!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startDate = null
                                        endDate = null
                                    } else {
                                        endDate = it
                                        onDateRangeSelected(startDate!!, it)
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = date?.dayOfMonth?.toString() ?: "")
                }
            }
        }
    }
}
