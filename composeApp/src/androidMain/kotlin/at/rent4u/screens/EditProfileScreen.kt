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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.localization.LocalizedStringProvider
import at.rent4u.localization.StringResourceId
import at.rent4u.presentation.UserViewModel
import at.rent4u.screens.BottomNavBar
import at.rent4u.screens.ChangeEmailDialog
import at.rent4u.screens.ChangePasswordDialog
import at.rent4u.screens.Screen
import at.rent4u.screens.DeleteConfirmationDialog
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold

@Composable
fun EditProfileScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    // Get localized strings
    val strings = remember(configuration) {
        LocalizedStringProvider(context)
    }

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
    var isRefreshingVerification by remember { mutableStateOf(false) }

    // New state variables for the email and password change dialogs
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Function to refresh verification status properly
    fun refreshVerificationStatus() {
        coroutineScope.launch {
            isRefreshingVerification = true
            try {
                // Use the suspending function that forces a refresh
                isEmailVerified = viewModel.isCurrentEmailVerifiedWithRefresh()
            } catch (e: Exception) {
                // If there's an error, assume not verified for safety
                isEmailVerified = false
            } finally {
                isRefreshingVerification = false
            }
        }
    }

    // Initial data loading
    LaunchedEffect(Unit) {
        isLoading = true // Set loading state to true when fetching data

        try {
            // Force refresh the user data to get the latest verification status
            viewModel.refreshCurrentUser()

            userId = viewModel.getCurrentUserId()
            userId?.let {
                val userDetails = viewModel.getUserDetails(it)
                username = userDetails.username
                firstName = userDetails.firstName
                lastName = userDetails.lastName
                email = userDetails.email
                phone = userDetails.phone

                // Get verification status after refreshing user data
                isEmailVerified = viewModel.isCurrentEmailVerifiedWithRefresh()
            }
        } finally {
            isLoading = false // Set loading state to false after data is fetched
        }
    }

    // Periodically refresh verification status when not loading
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000) // Check every 5 seconds
            if (!isLoading && !isRefreshingVerification) {
                refreshVerificationStatus()
            }
        }
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
                        text = strings.getString(StringResourceId.LOADING_PROFILE),
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

                Spacer(modifier = Modifier.height(16.dp))

                // User Profile Data Section
                Text(
                    text = strings.getString(StringResourceId.PROFILE_INFORMATION),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(strings.getString(StringResourceId.USERNAME)) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(strings.getString(StringResourceId.FIRST_NAME)) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(strings.getString(StringResourceId.LAST_NAME)) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(strings.getString(StringResourceId.PHONE_NUMBER)) },
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
                        label = { Text(strings.getString(StringResourceId.EMAIL)) },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = {
                            if (isEmailVerified) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = strings.getString(StringResourceId.EMAIL_VERIFIED),
                                    tint = Color.Green
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = strings.getString(StringResourceId.EMAIL_NOT_VERIFIED),
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
                                        errorMessage =
                                            e.message ?: strings.getString(StringResourceId.ERROR)
                                        showErrorDialog = true
                                    }
                                }
                            }
                        ) {
                            Text(strings.getString(StringResourceId.VERIFY))
                        }
                    }
                }

                // Email verification status text
                if (!isEmailVerified) {
                    Text(
                        text = strings.getString(StringResourceId.EMAIL_VERIFICATION_NEEDED),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = strings.getString(StringResourceId.EMAIL_VERIFIED_MESSAGE),
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
                                viewModel.setToastMessage(strings.getString(StringResourceId.PROFILE_UPDATE_SUCCESS))
                                navController.popBackStack()
                            } catch (e: Exception) {
                                errorMessage =
                                    strings.getString(StringResourceId.PROFILE_UPDATE_ERROR) + ": ${e.message}"
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
                    Text(strings.getString(StringResourceId.SAVE_PROFILE))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Divider(modifier = Modifier.fillMaxWidth(0.9f))
                Spacer(modifier = Modifier.height(16.dp))

                // Security Section
                Text(
                    text = strings.getString(StringResourceId.SECURITY_SETTINGS),
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
                    Text(strings.getString(StringResourceId.CHANGE_EMAIL))
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
                    Text(strings.getString(StringResourceId.CHANGE_PASSWORD))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Divider(modifier = Modifier.fillMaxWidth(0.9f))
                Spacer(modifier = Modifier.height(16.dp))

                // Danger Zone Section
                Text(
                    text = strings.getString(StringResourceId.DANGER_ZONE),
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
                    Text(strings.getString(StringResourceId.DELETE_ACCOUNT))
                }

                // Extra space at the bottom to ensure scrolling works well
                Spacer(modifier = Modifier.height(80.dp))

                if (showDeleteConfirmation) {
                    DeleteConfirmationDialog(
                        entityType = strings.getString(StringResourceId.USER),
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
                        title = { Text(strings.getString(StringResourceId.ERROR)) },
                        text = { Text(errorMessage) },
                        confirmButton = {
                            TextButton(onClick = { showErrorDialog = false }) {
                                Text(strings.getString(StringResourceId.OK))
                            }
                        }
                    )
                }

                if (showVerificationSentDialog) {
                    AlertDialog(
                        onDismissRequest = { showVerificationSentDialog = false },
                        title = { Text(strings.getString(StringResourceId.VERIFICATION_EMAIL_SENT)) },
                        text = { Text(strings.getString(StringResourceId.VERIFICATION_EMAIL_MESSAGE)) },
                        confirmButton = {
                            TextButton(onClick = { showVerificationSentDialog = false }) {
                                Text(strings.getString(StringResourceId.OK))
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
                                    // Update the email in both auth and database
                                    viewModel.updateUserEmail(userId!!, newEmail, password)

                                    // Update the local email variable
                                    email = newEmail

                                    // After email change, explicitly set verification to false
                                    // This ensures the UI shows correct status immediately
                                    isEmailVerified = false

                                    // Set the toast message
                                    viewModel.setToastMessage(strings.getString(StringResourceId.EMAIL_UPDATE_SUCCESS))

                                    // Close dialog
                                    showChangeEmailDialog = false

                                    // Show verification message
                                    showVerificationSentDialog = true

                                    // Trigger background refresh of verification status
                                    refreshVerificationStatus()

                                } catch (e: Exception) {
                                    errorMessage =
                                        strings.getString(StringResourceId.EMAIL_UPDATE_ERROR) + ": ${e.message}"
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
                                    // First, check if email is verified with a forced refresh
                                    val verified = viewModel.isCurrentEmailVerifiedWithRefresh()

                                    if (!verified) {
                                        errorMessage =
                                            strings.getString(StringResourceId.EMAIL_VERIFICATION_REQUIRED)
                                        showErrorDialog = true
                                        showChangePasswordDialog = false
                                        return@launch
                                    }

                                    viewModel.updateUserPassword(
                                        userId!!,
                                        currentPassword,
                                        newPassword
                                    )

                                    // Set toast message before navigating
                                    viewModel.setToastMessage(strings.getString(StringResourceId.PASSWORD_UPDATE_SUCCESS))

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
                                    errorMessage =
                                        strings.getString(StringResourceId.PASSWORD_UPDATE_ERROR) + ": ${e.message}"
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
