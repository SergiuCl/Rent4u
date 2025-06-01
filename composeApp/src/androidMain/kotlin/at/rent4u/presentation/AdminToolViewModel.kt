package at.rent4u.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
    private val toolRepository: ToolRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _creationSuccess = MutableStateFlow<Boolean?>(null)
    val creationSuccess: StateFlow<Boolean?> = _creationSuccess

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _tool = MutableStateFlow<Tool?>(null)
    val tool: StateFlow<Tool?> = _tool

    private val _deletionSuccess = MutableStateFlow<Boolean?>(null)
    val deletionSuccess: StateFlow<Boolean?> = _deletionSuccess

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearCreationSuccess() {
        _creationSuccess.value = null
    }

    fun clearDeletionSuccess() {
        _deletionSuccess.value = null
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

        if (tool.rentalRate < 0.0) {
            _toastMessage.value = "Please enter a valid number for the rental rate."
            return
        }

        val toolWithTimestamp = tool.copy(createdAt = System.currentTimeMillis())

        viewModelScope.launch {
            _isLoading.value = true
            val (success, error) = toolRepository.addTool(toolWithTimestamp)
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

            val (success, error) = toolRepository.updateTool(toolId, updatedData)

            if (success) {
                // Force reload the tool data from Firestore to ensure we have the latest
                val updatedTool = toolRepository.getToolById(toolId)
                _editingTool.value = updatedTool
                _tool.value = updatedTool
                
                Log.d("AdminToolViewModel", "Tool updated successfully: $updatedTool")
                _toastMessage.value = "Tool updated successfully!"
                _creationSuccess.value = true
            } else {
                Log.e("AdminToolViewModel", "Failed to update tool: $error")
                _toastMessage.value = "Failed to update tool: ${error ?: "Unknown error"}"
                _creationSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun deleteTool(toolId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val isAdmin = userRepository.isCurrentUserAdmin()
            if (!isAdmin) {
                _toastMessage.value = "Permission denied: Not an admin"
                _deletionSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            try {
                val result = toolRepository.deleteTool(toolId)
                if (result) {
                    Log.d("AdminToolViewModel", "Tool deleted successfully: $toolId")
                    _toastMessage.value = "Tool deleted successfully!"
                    _deletionSuccess.value = true
                } else {
                    Log.e("AdminToolViewModel", "Failed to delete tool: $toolId")
                    _toastMessage.value = "Failed to delete tool."
                    _deletionSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("AdminToolViewModel", "Error deleting tool: ${e.message}", e)
                _toastMessage.value = "Error deleting tool: ${e.message}"
                _deletionSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _editingTool = MutableStateFlow<Tool?>(null)
    val editingTool: StateFlow<Tool?> = _editingTool

    fun loadTool(toolId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val tool = toolRepository.getToolById(toolId)
                _editingTool.value = tool
                Log.d("AdminToolViewModel", "Loaded tool: $tool")
            } catch(e: Exception) {
                Log.e("AdminToolViewModel", "Error loading tool: ${e.message}", e)
                _toastMessage.value = "Failed to load tool: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
