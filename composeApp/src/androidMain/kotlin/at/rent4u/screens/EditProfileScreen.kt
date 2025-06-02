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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
    var isEmailVerified by remember { mutableStateOf(false) }
    var showVerificationSentDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) } // Add loading state

    // New state variables for the email and password change dialogs
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Check if email is verified
    LaunchedEffect(Unit) {
        isLoading = true // Set loading state to true when fetching data
        userId = viewModel.getCurrentUserId()
        userId?.let {
            val userDetails = viewModel.getUserDetails(it)
            username = userDetails.username
            firstName = userDetails.firstName
            lastName = userDetails.lastName
            email = userDetails.email
            phone = userDetails.phone

            // Refresh user data to get the latest verification status
            try {
                viewModel.refreshCurrentUser()
                isEmailVerified = viewModel.isCurrentEmailVerified()
            } catch (e: Exception) {
                // If we can't get verification status, assume it's not verified
                isEmailVerified = false
            }
        }
        isLoading = false // Set loading state to false after data is fetched
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }) { innerPadding ->
        if (isLoading) {
            // Show loading indicator when data is being fetched
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading profile...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // Show profile content when data is loaded
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState), // Added vertical scroll to make content scrollable
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User banner at the top showing logged in username
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Logged in as $username",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

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

                // Email field with verification status indicator
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Email field (read-only)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { /* Read-only field */ },
                        label = { Text("Email") },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = {
                            if (isEmailVerified) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color.Green
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Not Verified",
                                    tint = Color.Red
                                )
                            }
                        }
                    )

                    if (!isEmailVerified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        viewModel.sendVerificationEmail()
                                        showVerificationSentDialog = true
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to send verification email: ${e.message}"
                                        showErrorDialog = true
                                    }
                                }
                            }
                        ) {
                            Text("Verify")
                        }
                    }
                }

                // Email verification status text
                if (!isEmailVerified) {
                    Text(
                        text = "Your email is not verified. Please check your inbox or click 'Verify' to receive a new verification link.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "Your email is verified",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 4.dp)
                    )
                }

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

                if (showVerificationSentDialog) {
                    AlertDialog(
                        onDismissRequest = { showVerificationSentDialog = false },
                        title = { Text("Verification Email Sent") },
                        text = { Text("A verification email has been sent to your email address. Please check your inbox and follow the link to verify your email.") },
                        confirmButton = {
                            TextButton(onClick = { showVerificationSentDialog = false }) {
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
}
