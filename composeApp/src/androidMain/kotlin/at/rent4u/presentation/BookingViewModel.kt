package at.rent4u.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.rent4u.data.ToolRepository
import at.rent4u.model.Booking
import at.rent4u.model.Tool
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: ToolRepository
) : ViewModel() {

    private val _tool = MutableStateFlow<Tool?>(null)
    val tool: StateFlow<Tool?> = _tool

    private val _bookedDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val bookedDates: StateFlow<List<LocalDate>> = _bookedDates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _bookingSuccessful = MutableStateFlow(false)
    val bookingSuccessful: StateFlow<Boolean> = _bookingSuccessful

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTool(toolId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _tool.value = repository.getToolById(toolId)
            _bookedDates.value = repository.getBookedDates(toolId)
            _isLoading.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun book(toolId: String, startDate: LocalDate, endDate: LocalDate, totalAmount: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _toastMessage.value = "You must be logged in to book"
            return
        }

        val booking = Booking(
            toolId = toolId,
            userId = userId,
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            totalAmount = totalAmount
        )

        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.bookTool(booking)
            _isLoading.value = false

            if (success) {
                _toastMessage.value = "Booking confirmed"
                _bookingSuccessful.value = true
            } else {
                _toastMessage.value = "Booking failed"
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearBookingState() {
        _bookingSuccessful.value = false
    }
}
