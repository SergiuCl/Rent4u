package at.rent4u.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.rent4u.localization.LocalizedStringProvider
import at.rent4u.localization.StringResourceId
import at.rent4u.presentation.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    // Setup localization
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val strings = remember(configuration) {
        LocalizedStringProvider(context)
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isEmailValid = validateEmail(email)
    val isFormValid = email.isNotBlank() && password.isNotBlank()

    val viewModel: UserViewModel = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    var keepLoggedIn by remember { mutableStateOf(false) }

    // Check if user is already logged in, if so navigate directly to ToolList
    LaunchedEffect(Unit) {
        if (viewModel.isUserLoggedIn()) {
            Log.d("LoginScreen", "User already logged in, navigating to ToolList")
            navController.navigate(Screen.ToolList.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(strings.getString(StringResourceId.WELCOME_BACK), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(strings.getString(StringResourceId.EMAIL)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(strings.getString(StringResourceId.PASSWORD)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Keep me logged in checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = keepLoggedIn,
                    onCheckedChange = { keepLoggedIn = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.getString(StringResourceId.STAY_LOGGED_IN))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(enabled = !isLoading && isFormValid, onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!isEmailValid) {
                        viewModel.setToastMessage("Invalid email address")
                        return@launch
                    }

                    val success = viewModel.login(email, password)

                    if (success) {
                        // Set the keep logged in preference
                        viewModel.setKeepLoggedIn(keepLoggedIn)
                        Log.d("LoginScreen", "Login succeeded → ToolList")
                        navController.navigate(Screen.ToolList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        viewModel.setToastMessage("Login failed. Please try again.")
                    }
                }
            }) {
                Text(strings.getString(StringResourceId.LOGIN))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(enabled = !isLoading, onClick = {
                navController.navigate(Screen.Register.route)
            }) {
                Text(strings.getString(StringResourceId.DONT_HAVE_ACCOUNT))
            }
        }
    }
}

