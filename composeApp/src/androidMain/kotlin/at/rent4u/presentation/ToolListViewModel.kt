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

    init {
        viewModelScope.launch {
            _isAdmin.value = userRepository.isCurrentUserAdmin()
            _tools.value = toolRepository.getTools()
        }
    }
}