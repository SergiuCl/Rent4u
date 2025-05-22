package at.rent4u.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.rent4u.data.ToolRepository
import at.rent4u.data.UserRepository
import at.rent4u.model.Booking
import at.rent4u.model.Tool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val repository: ToolRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userBookings = MutableStateFlow<List<Booking>>(emptyList())
    val userBookings: StateFlow<List<Booking>> = _userBookings

    private val _bookedTools = MutableStateFlow<Map<String, Tool>>(emptyMap())
    val bookedTools: StateFlow<Map<String, Tool>> = _bookedTools

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchUserBookings() {
        val userId = userRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            val bookings = repository.getBookingsForUser(userId)
            _userBookings.value = bookings

            val toolIds = bookings.map { it.toolId }.toSet()
            val tools = repository.getToolsByIds(toolIds)
            _bookedTools.value = tools.associate { it.first to it.second }  // toolId -> Tool
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cancelBooking(booking: Booking) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
            fetchUserBookings()
        }
    }
}
