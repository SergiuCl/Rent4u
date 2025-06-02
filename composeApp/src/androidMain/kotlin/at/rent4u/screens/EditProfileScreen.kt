import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.presentation.UserViewModel
import at.rent4u.screens.BottomNavBar
import at.rent4u.screens.ChangeEmailDialog
import at.rent4u.screens.ChangePasswordDialog
import at.rent4u.screens.Screen
import at.rent4u.screens.DeleteConfirmationDialog
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: UserViewModel = hiltViewModel()
    var userId by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Read-only now
    var phone by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // New state variables for the email and password change dialogs
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        userId = viewModel.getCurrentUserId()
        userId?.let {
            val userDetails = viewModel.getUserDetails(it)
            username = userDetails.username
            firstName = userDetails.firstName
            lastName = userDetails.lastName
            email = userDetails.email
            phone = userDetails.phone
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState), // Added vertical scroll to make content scrollable
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // User Profile Data Section
            Text(
                text = "Profile Information",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(0.9f),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Email field (read-only)
            OutlinedTextField(
                value = email,
                onValueChange = { /* Read-only field */ },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(0.9f),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Profile Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            viewModel.updateUserProfileDetails(
                                userId!!, username, firstName, lastName, phone
                            )
                            viewModel.setToastMessage("Profile updated successfully")
                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMessage = "Failed to update profile: ${e.message}"
                            showErrorDialog = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save Profile")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.9f))
            Spacer(modifier = Modifier.height(16.dp))

            // Security Section
            Text(
                text = "Security Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(0.9f),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Change Email Button
            Button(
                onClick = { showChangeEmailDialog = true },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Change Email")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Change Password Button
            Button(
                onClick = { showChangePasswordDialog = true },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Change Password")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.9f))
            Spacer(modifier = Modifier.height(16.dp))

            // Danger Zone Section
            Text(
                text = "Danger Zone",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(0.9f),
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Account Button
            Button(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Delete Account")
            }

            // Extra space at the bottom to ensure scrolling works well
            Spacer(modifier = Modifier.height(80.dp))

            if (showDeleteConfirmation) {
                DeleteConfirmationDialog(
                    entityType = "user",
                    entityName = username,
                    onConfirm = {
                        coroutineScope.launch {
                            viewModel.deleteUser(userId!!)
                            showDeleteConfirmation = false
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onDismiss = { showDeleteConfirmation = false }
                )
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            // Show Change Email Dialog if requested
            if (showChangeEmailDialog) {
                ChangeEmailDialog(
                    currentEmail = email,
                    onDismiss = { showChangeEmailDialog = false },
                    onChangeEmail = { newEmail, password ->
                        coroutineScope.launch {
                            try {
                                // First update the email
                                viewModel.updateUserEmail(userId!!, newEmail, password)

                                // Set the toast message before navigating
                                viewModel.setToastMessage("Email updated. Please check your inbox to verify the new email.")

                                // Close dialog first to prevent any UI glitches
                                showChangeEmailDialog = false

                                // Important: Explicitly perform logout
                                viewModel.logout()

                                // Delay slightly to ensure logout completes before navigation
                                kotlinx.coroutines.delay(300)

                                // Navigate to login screen with backstack cleared
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Failed to update email: ${e.message}"
                                showErrorDialog = true
                                showChangeEmailDialog = false
                            }
                        }
                    }
                )
            }

            // Show Change Password Dialog if requested
            if (showChangePasswordDialog) {
                ChangePasswordDialog(
                    onDismiss = { showChangePasswordDialog = false },
                    onChangePassword = { currentPassword, newPassword ->
                        coroutineScope.launch {
                            try {
                                viewModel.updateUserPassword(userId!!, currentPassword, newPassword)

                                // Set toast message before navigating
                                viewModel.setToastMessage("Password updated successfully. Please log in with your new password.")

                                // Close dialog first to prevent UI glitches
                                showChangePasswordDialog = false

                                // Log the user out
                                viewModel.logout()

                                // Add delay to ensure logout completes
                                kotlinx.coroutines.delay(300)

                                // Navigate to login screen
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Failed to update password: ${e.message}"
                                showErrorDialog = true
                                showChangePasswordDialog = false
                            }
                        }
                    }
                )
            }
        }
    }
}
