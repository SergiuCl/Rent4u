package at.rent4u.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.ContactUsViewModel

@Composable
fun ContactUsScreen(
    navController: NavController,
    viewModel: ContactUsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val message by viewModel.message.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Contact Us", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { viewModel.updateMessage(it) },
                label = { Text("Your Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 10,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.sendEmail(context) {
                        navController.navigate(Screen.ToolList.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                enabled = message.isNotBlank() && !isSending
            ) {
                Text("Send")
            }
        }
    }

    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearToastMessage()
    }
}