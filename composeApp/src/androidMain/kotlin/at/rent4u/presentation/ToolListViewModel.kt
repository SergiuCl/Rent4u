package at.rent4u.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.rent4u.data.ToolRepository
import at.rent4u.data.UserRepository
import at.rent4u.model.Tool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val toolRepository: ToolRepository
) : ViewModel() {

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin
    private val _tools = MutableStateFlow<List<Pair<String, Tool>>>(emptyList())
    val tools: StateFlow<List<Pair<String, Tool>>> = _tools.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private var hasMoreItems = true

    init {
        viewModelScope.launch {
            loadMoreTools()
        }
    }

    fun loadMoreTools() {
        if (_isLoadingMore.value || !hasMoreItems) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val newTools = toolRepository.getToolsPaged()

            if (newTools.isEmpty()) {
                hasMoreItems = false
            } else {
                // Use Firestore document ID as key to prevent duplicates
                _tools.value = (_tools.value + newTools)
                    .distinctBy { it.first }
            }

            _isAdmin.value = userRepository.isCurrentUserAdmin()
            _isLoadingMore.value = false
        }
    }
}