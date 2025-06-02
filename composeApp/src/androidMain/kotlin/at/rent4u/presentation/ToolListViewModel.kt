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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val toolRepository: ToolRepository
) : ViewModel() {

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    // Backing flow for all tools observed from Firestore
    private val _allTools = MutableStateFlow<List<Pair<String, Tool>>>(emptyList())
    // Expose allTools if needed; otherwise use filteredTools only
    // val allTools: StateFlow<List<Pair<String, Tool>>> = _allTools

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
        // 1. Start collecting the repo’s Flow of all tools
        viewModelScope.launch {
            toolRepository.observeAllTools()
                .collectLatest { toolsList ->
                    _allTools.value = toolsList
                    applyFilters(toolsList) // pass the list into applyFilters
                }
        }
        // 2) Immediately check “am I admin?” and keep it up to date
        //    If `userRepository.isCurrentUserAdmin()` is itself a Flow, collect it.
        //    If it’s a suspend function, call it once.
        viewModelScope.launch {
            val adminStatus = userRepository.isCurrentUserAdmin()
            _isAdmin.value = adminStatus
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

            // If needed, update _allTools here or trigger filtering
            // _allTools.value = allTools
            // applyFilters(allTools)

            _isLoadingMore.value = false
        }
    }

    fun updateFilter(update: ToolFilter.() -> ToolFilter) {
        _filters.value = _filters.value.update()
        applyFilters(_allTools.value)
    }

    private fun applyFilters(toolsList: List<Pair<String, Tool>>) {
        val minPrice = _filters.value.minPriceText.toDoubleOrNull()
        val maxPrice = _filters.value.maxPriceText.toDoubleOrNull()

        _filteredTools.value = toolsList.filter { (_, tool) ->
            val price = tool.rentalRate
            (_filters.value.brand.isBlank() || tool.brand.contains(
                _filters.value.brand,
                ignoreCase = true
            ))
                    && (_filters.value.type.isBlank() || tool.type.contains(
                _filters.value.type,
                ignoreCase = true
            ))
                    && (_filters.value.availabilityStatus.isBlank()
                    || tool.availabilityStatus.equals(
                _filters.value.availabilityStatus,
                ignoreCase = true
            ))
                    && (minPrice == null || price >= minPrice)
                    && (maxPrice == null || price <= maxPrice)
        }
    }

    fun fetchToolById(toolId: String, forceRefresh: Boolean = false) {
        val alreadyLoaded = _allTools.value.find { it.first == toolId }
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
                val updatedTools = _allTools.value.map {
                    if (it.first == toolId) toolId to tool else it
                }.toMutableList()

                // Add the tool if it wasn't in our list
                if (!updatedTools.any { it.first == toolId }) {
                    updatedTools.add(toolId to tool)
                }

                _allTools.value = updatedTools
                _filteredTools.value = listOf(toolId to tool)
            } else {
                Log.d("ToolListVM", "Tool not found with ID: $toolId")
                _filteredTools.value = emptyList()
            }
            _isFetchingTool.value = false
        }
    }

    fun refreshTools() {
        viewModelScope.launch {
            toolRepository.observeAllTools()
                .collectLatest { toolsList ->
                    _allTools.value = toolsList
                    applyFilters(toolsList)
                }
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
