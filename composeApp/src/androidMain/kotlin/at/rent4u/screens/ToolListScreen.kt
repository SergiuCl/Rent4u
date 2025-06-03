package at.rent4u.screens

import android.util.Log

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.localization.LocalizedStringProvider
import at.rent4u.localization.StringResourceId
import at.rent4u.model.Tool
import at.rent4u.presentation.ToolFilter
import at.rent4u.presentation.ToolListViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ToolListScreen(navController: NavController) {
    Log.d("ToolListScreen", "Composing ToolListScreen")

    // Setup localization
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val strings = remember(configuration) {
        LocalizedStringProvider(context)
    }

    val viewModel: ToolListViewModel = hiltViewModel()
    val isAdmin by viewModel.isAdmin.collectAsState()
    
    val tools by viewModel.filteredTools.collectAsState()
    val listState = rememberLazyListState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()
    val hasMoreTools by viewModel.hasMoreTools.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }

    // Detect when user has scrolled to the bottom and load more tools
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            
            // Check if we're near the end of the list (last 2 items)
            lastVisibleItemIndex > (totalItemsNumber - 2) && totalItemsNumber > 0
        }
        .distinctUntilChanged()
        .collect { isNearBottom ->
            if (isNearBottom && hasMoreTools && !isLoadingMore) {
                Log.d("ToolListScreen", "Near bottom, loading more tools")
                viewModel.loadMoreTools()
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AdminToolCreate.route) },
                    containerColor = Color.LightGray,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = strings.getString(StringResourceId.ADD_TOOL))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Show loading spinner during initial load
            if (isInitialLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.getString(StringResourceId.LOADING_TOOLS),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clickable { showFilters = !showFilters }
                                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (showFilters)
                                        strings.getString(StringResourceId.HIDE_FILTERS)
                                    else
                                        strings.getString(StringResourceId.SHOW_FILTERS),
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        AnimatedVisibility(
                            visible = showFilters,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                FilterSection(viewModel, strings)
                                Button(
                                    onClick = { viewModel.updateFilter { ToolFilter() } },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(strings.getString(StringResourceId.RESET_FILTERS))
                                }
                            }
                        }
                    }
                    
                    if (tools.isEmpty() && !isLoadingMore && !isInitialLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    strings.getString(StringResourceId.NO_TOOLS_FOUND),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        itemsIndexed(tools) { index, (id, tool) ->
                            ToolListItem(
                                tool = tool,
                                isLastItem = index == tools.lastIndex && !hasMoreTools,
                                onClick = {
                                    navController.navigate(Screen.ToolDetails.createRoute(id))
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Show loading indicator at bottom when loading more tools
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(viewModel: ToolListViewModel, strings: LocalizedStringProvider) {
    val filters by viewModel.filters.collectAsState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = filters.brand,
            onValueChange = { viewModel.updateFilter { copy(brand = it) } },
            label = { Text(strings.getString(StringResourceId.BRAND)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = filters.type,
            onValueChange = { viewModel.updateFilter { copy(type = it) } },
            label = { Text(strings.getString(StringResourceId.TYPE)) },
            modifier = Modifier.fillMaxWidth()
        )

        DropDownField(
            label = strings.getString(StringResourceId.AVAILABILITY),
            options = listOf("", "available", "unavailable"),
            selectedValue = filters.availabilityStatus,
            onChange = { viewModel.updateFilter { copy(availabilityStatus = it) } }
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = filters.minPriceText,
                onValueChange = { viewModel.updateFilter { copy(minPriceText = it) } },
                label = { Text(strings.getString(StringResourceId.MIN_PRICE)) },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )

            OutlinedTextField(
                value = filters.maxPriceText,
                onValueChange = { viewModel.updateFilter { copy(maxPriceText = it) } },
                label = { Text(strings.getString(StringResourceId.MAX_PRICE)) },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun ToolListItem(
    tool: Tool,
    isLastItem: Boolean = false,
    onClick: () -> Unit
) {
    // Get the localized string provider
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val strings = remember(configuration) {
        LocalizedStringProvider(context)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp)
                .padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = tool.image,
                    contentDescription = strings.getString(StringResourceId.TOOL_IMAGE),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.alignByBaseline()) {
                    Text(tool.brand, style = MaterialTheme.typography.titleMedium)
                    Text(tool.type, style = MaterialTheme.typography.bodyMedium)

                    // Availability Row with colored indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val statusColor = if (tool.availabilityStatus.equals("available", ignoreCase = true)) {
                            Color(0xFF4CAF50) // Green
                        } else {
                            Color(0xFFF44336) // Red
                        }

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(tool.availabilityStatus, style = MaterialTheme.typography.bodyMedium)
                    }

                    val cleanPrice = tool.rentalRate
                    // Format with localized currency symbol and rental rate
                    Text("€$cleanPrice", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLastItem) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )
        }
    }
}
