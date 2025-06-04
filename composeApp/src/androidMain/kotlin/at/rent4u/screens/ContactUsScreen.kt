package at.rent4u.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.localization.LocalizedStringProvider
import at.rent4u.localization.StringResourceId
import at.rent4u.presentation.ContactUsViewModel

@Composable
fun ContactUsScreen(
    navController: NavController,
    viewModel: ContactUsViewModel = hiltViewModel()
) {
    // Setup localization
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val strings = remember(configuration) {
        LocalizedStringProvider(context)
    }

    val message by viewModel.message.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = strings.getString(StringResourceId.CONTACT_US),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            // Info message
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = strings.getString(StringResourceId.CONTACT_INFO),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }

            // Message box
            OutlinedTextField(
                value = message,
                onValueChange = { viewModel.updateMessage(it) },
                label = { Text(strings.getString(StringResourceId.YOUR_MESSAGE)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                singleLine = false,
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Send button
            Button(
                onClick = {
                    viewModel.sendEmail(context) {
                        navController.navigate(Screen.ToolList.route) {
                            popUpTo(Screen.ContactUs.route) { inclusive = true }
                        }
                    }
                },
                enabled = message.isNotBlank() && !isSending
            ) {
                Text(strings.getString(StringResourceId.SEND))
            }
        }

        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }
}
