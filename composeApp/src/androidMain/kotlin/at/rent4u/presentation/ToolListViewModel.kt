package at.rent4u.presentation

import android.util.Log
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

    // We no longer expose _tools directly; instead we filter them immediately
    private val _tools = MutableStateFlow<List<Pair<String, Tool>>>(emptyList())

    private val _isFetchingTool = MutableStateFlow(false)
    val isFetchingTool: StateFlow<Boolean> = _isFetchingTool

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _filters = MutableStateFlow(ToolFilter())
    val filters: StateFlow<ToolFilter> = _filters.asStateFlow()

    // This is the flow your UI should collect from.
    private val _filteredTools = MutableStateFlow<List<Pair<String, Tool>>>(emptyList())
    val filteredTools: StateFlow<List<Pair<String, Tool>>> = _filteredTools.asStateFlow()

    init {
        viewModelScope.launch {
            // Check if the current user is an admin
            _isAdmin.value = userRepository.isCurrentUserAdmin()
            Log.d("ToolListVM", "User admin status: ${_isAdmin.value}")

            loadMoreTools()
        }
    }

    fun loadMoreTools() {
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            Log.d("ToolListVM", "Fetching all tools")

            // Fetch all tools in one go
            val allTools = toolRepository.getToolsPaged(null)
            Log.d("ToolListVM", "Repository returned ${allTools.size} tools")

            // Replace the tools list and apply filters
            _tools.value = allTools
            applyFilters()

            Log.d("ToolListVM", "After loading, tools.size = ${_tools.value.size}, filteredTools.size = ${_filteredTools.value.size}")

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
            val price = tool.rentalRate

            (_filters.value.brand.isBlank() || tool.brand.contains(_filters.value.brand, ignoreCase = true))
                    && (_filters.value.type.isBlank() || tool.type.contains(_filters.value.type, ignoreCase = true))
                    && (_filters.value.availabilityStatus.isBlank()
                    || tool.availabilityStatus.equals(_filters.value.availabilityStatus, ignoreCase = true))
                    && (minPrice == null || price >= minPrice)
                    && (maxPrice == null || price <= maxPrice)
        }
    }

    fun fetchToolById(toolId: String, forceRefresh: Boolean = false) {
        val alreadyLoaded = _tools.value.find { it.first == toolId }
        if (alreadyLoaded != null && !forceRefresh) {
            // If we already have it in the current list and no refresh is needed, emit it immediately
            _filteredTools.value = listOf(alreadyLoaded)
            return
        }

        viewModelScope.launch {
            _isFetchingTool.value = true
            Log.d("ToolListVM", "Fetching tool with ID: $toolId, forceRefresh=$forceRefresh")
            
            val tool = toolRepository.getToolById(toolId)
            if (tool != null) {
                Log.d("ToolListVM", "Retrieved tool: $tool")
                
                // Update the tool in our local cache
                val updatedTools = _tools.value.map {
                    if (it.first == toolId) toolId to tool else it
                }.toMutableList()
                
                // Add the tool if it wasn't in our list
                if (!updatedTools.any { it.first == toolId }) {
                    updatedTools.add(toolId to tool)
                }
                
                _tools.value = updatedTools
                _filteredTools.value = listOf(toolId to tool)
            } else {
                Log.d("ToolListVM", "Tool not found with ID: $toolId")
                _filteredTools.value = emptyList()
            }
            _isFetchingTool.value = false
        }
    }

    fun refreshTool(toolId: String) {
        Log.d("ToolListVM", "Refreshing tool with ID: $toolId")
        fetchToolById(toolId, forceRefresh = true)
    }
}

data class ToolFilter(
    val brand: String = "",
    val type: String = "",
    val availabilityStatus: String = "",
    val minPriceText: String = "",
    val maxPriceText: String = ""
)
