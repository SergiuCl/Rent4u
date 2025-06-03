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

    private val _isFetchingTool = MutableStateFlow(false)
    val isFetchingTool: StateFlow<Boolean> = _isFetchingTool

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    // Initial loading state
    private val _isInitialLoading = MutableStateFlow(true)
    val isInitialLoading: StateFlow<Boolean> = _isInitialLoading

    private val _filters = MutableStateFlow(ToolFilter())
    val filters: StateFlow<ToolFilter> = _filters.asStateFlow()

    // This is the flow your UI should collect from.
    private val _filteredTools = MutableStateFlow<List<Pair<String, Tool>>>(emptyList())
    val filteredTools: StateFlow<List<Pair<String, Tool>>> = _filteredTools.asStateFlow()
    
    // Pagination properties
    private val PAGE_SIZE = 5
    private var currentPage = 0
    private var allFilteredTools = listOf<Pair<String, Tool>>()
    
    private val _hasMoreTools = MutableStateFlow(true)
    val hasMoreTools: StateFlow<Boolean> = _hasMoreTools

    init {
        // 1. Start collecting the repo's Flow of all tools
        viewModelScope.launch {
            _isInitialLoading.value = true
            toolRepository.observeAllTools()
                .collectLatest { toolsList ->
                    _allTools.value = toolsList
                    applyFilters(toolsList) // pass the list into applyFilters
                    _isInitialLoading.value = false
                }
        }
        // 2) Immediately check "am I admin?" and keep it up to date
        viewModelScope.launch {
            val adminStatus = userRepository.isCurrentUserAdmin()
            _isAdmin.value = adminStatus
        }
    }

    fun loadMoreTools() {
        if (_isLoadingMore.value || !_hasMoreTools.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            
            // Add small delay to make loading indicator visible
            kotlinx.coroutines.delay(300)
            
            val nextPage = currentPage + 1
            val startIndex = currentPage * PAGE_SIZE
            val endIndex = nextPage * PAGE_SIZE
            
            // Make sure we don't go beyond the list bounds
            if (startIndex < allFilteredTools.size) {
                val nextBatch = allFilteredTools.subList(
                    startIndex, 
                    minOf(endIndex, allFilteredTools.size)
                )
                
                // Add this batch to the current displayed tools
                _filteredTools.value = _filteredTools.value + nextBatch
                currentPage = nextPage
                
                // Check if we've loaded all tools
                _hasMoreTools.value = endIndex < allFilteredTools.size
                
                Log.d("ToolListVM", "Loaded more tools: now showing ${_filteredTools.value.size}/${allFilteredTools.size}")
            } else {
                _hasMoreTools.value = false
            }
            
            _isLoadingMore.value = false
        }
    }

    fun updateFilter(update: ToolFilter.() -> ToolFilter) {
        _filters.value = _filters.value.update()
        // Reset pagination when filters change
        currentPage = 0
        applyFilters(_allTools.value)
    }

    private fun applyFilters(toolsList: List<Pair<String, Tool>>) {
        val minPrice = _filters.value.minPriceText.toDoubleOrNull()
        val maxPrice = _filters.value.maxPriceText.toDoubleOrNull()

        // Filter the complete list
        allFilteredTools = toolsList.filter { (_, tool) ->
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
        
        // Reset pagination
        currentPage = 0
        
        // Show only first page
        _filteredTools.value = allFilteredTools.take(PAGE_SIZE)
        
        // Set whether there are more tools to load
        _hasMoreTools.value = allFilteredTools.size > PAGE_SIZE
        
        Log.d("ToolListVM", "Applied filters: filtered to ${allFilteredTools.size} tools, showing initial ${_filteredTools.value.size}")
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
            _isInitialLoading.value = true
            currentPage = 0
            
            toolRepository.observeAllTools()
                .collectLatest { toolsList ->
                    _allTools.value = toolsList
                    applyFilters(toolsList)
                    _isInitialLoading.value = false
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
