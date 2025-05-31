package at.rent4u.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.rent4u.data.ToolRepository
import at.rent4u.data.UserRepository
import at.rent4u.model.Tool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminToolViewModel @Inject constructor(
    private val repository: ToolRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _creationSuccess = MutableStateFlow<Boolean?>(null)
    val creationSuccess: StateFlow<Boolean?> = _creationSuccess

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearCreationSuccess() {
        _creationSuccess.value = null
    }

    fun createTool(tool: Tool) {
        if (tool.type.isBlank()) {
            _toastMessage.value = "Please enter the type"
            return
        }
        if (tool.brand.isBlank()) {
            _toastMessage.value = "Please enter the brand"
            return
        }
        if (tool.availabilityStatus.isBlank()) {
            _toastMessage.value = "Please enter the availability status"
            return
        }

        val rentalRateString = tool.rentalRate.replace("€", "").trim()
        val isNumeric = rentalRateString.toDoubleOrNull() != null
        if (!isNumeric) {
            _toastMessage.value = "Please enter a valid number for the rental rate."
            return
        }

        val toolWithTimestamp = tool.copy(createdAt = System.currentTimeMillis())

        viewModelScope.launch {
            _isLoading.value = true
            val (success, error) = repository.addTool(toolWithTimestamp)
            _isLoading.value = false

            if (success) {
                _toastMessage.value = "Tool created successfully!"
                _creationSuccess.value = true
            } else {
                _toastMessage.value = "Unable to create tool."
                _creationSuccess.value = false
            }
        }
    }

    fun updateTool(toolId: String, updatedData: Map<String, Any>) {
        viewModelScope.launch {
            _isLoading.value = true

            val isAdmin = userRepository.isCurrentUserAdmin()
            if (!isAdmin) {
                _toastMessage.value = "Permission denied: Not an admin"
                _creationSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val (success, error) = repository.updateTool(toolId, updatedData)
            _isLoading.value = false

            if (success) {
                _toastMessage.value = "Tool updated successfully!"
                _creationSuccess.value = true
            } else {
                _toastMessage.value = "Failed to update tool."
                _creationSuccess.value = false
            }
        }
    }

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    init {
        viewModelScope.launch {
            _isAdmin.value = userRepository.isCurrentUserAdmin()
        }
    }

}