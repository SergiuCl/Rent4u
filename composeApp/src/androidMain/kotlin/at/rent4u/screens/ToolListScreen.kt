package at.rent4u.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import at.rent4u.presentation.ToolListViewModel

@Composable
fun ToolListScreen(navController: NavController) {

    val viewModel: ToolListViewModel = hiltViewModel()
    //val auth = UserAuth()
    //val user = auth.getCurrentUser()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Tool List Screen")
        }
    }
}