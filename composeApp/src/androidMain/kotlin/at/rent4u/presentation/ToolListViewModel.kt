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

    private val _singleTool = MutableStateFlow<Pair<String, Tool>?>(null)
    val singleTool: StateFlow<Pair<String, Tool>?> = _singleTool

    private val _isFetchingTool = MutableStateFlow(false)
    val isFetchingTool: StateFlow<Boolean> = _isFetchingTool

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _filters = MutableStateFlow(ToolFilter())
    val filters = _filters.asStateFlow()

    private val _filteredTools = MutableStateFlow<List<Pair<String, Tool>>>(emptyList())
    val filteredTools = _filteredTools.asStateFlow()

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

            applyFilters()

            _isAdmin.value = userRepository.isCurrentUserAdmin()
            _isLoadingMore.value = false
        }
    }

    fun updateFilter(update: ToolFilter.() -> ToolFilter) {
        _filters.value = _filters.value.update()
        applyFilters()
    }

    private fun applyFilters() {
        val minPrice = _filters.value.minPriceText.toDoubleOrNull()
        val maxPrice = _filters.value.maxPriceText.toDoubleOrNull()

        _filteredTools.value = _tools.value.filter { (_, tool) ->
            val price = tool.rentalRate.replace("€", "").toDoubleOrNull() ?: return@filter false

            (_filters.value.brand.isBlank() || tool.brand.contains(_filters.value.brand, true)) &&
                    (_filters.value.type.isBlank() || tool.type.contains(_filters.value.type, true)) &&
                    (_filters.value.availabilityStatus.isBlank() || tool.availabilityStatus.equals(_filters.value.availabilityStatus, true)) &&
                    (minPrice == null || price >= minPrice) &&
                    (maxPrice == null || price <= maxPrice)
        }
    }

    fun fetchToolIById(toolId: String) {
        val alreadyLoaded = _tools.value.find { it.first == toolId }
        if (alreadyLoaded != null) {
            _singleTool.value = alreadyLoaded
            return
        }

        viewModelScope.launch {
            _isFetchingTool.value = true
            val tool = toolRepository.getToolById(toolId)
            if (tool != null) {
                _singleTool.value = toolId to tool
            } else {
                _singleTool.value = null
            }
            _isFetchingTool.value = false
        }
    }
}

data class ToolFilter(
    val brand: String = "",
    val type: String = "",
    val availabilityStatus: String = "",
    val minPriceText: String = "",
    val maxPriceText: String = ""
)