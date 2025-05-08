package at.rent4u.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import at.rent4u.auth.UserAuth
import at.rent4u.logging.logMessage
import at.rent4u.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showToastMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = email.isNotBlank()
            && password.isNotBlank()
            && username.isNotBlank()
            && confirmPassword.isNotBlank()

    val isPhoneNumberValid = phoneNumber.isNotBlank() && phoneNumber.all { it.isDigit() }
    val isEmailValid = validateEmail(email)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create Account", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(enabled = !isLoading && isFormValid, onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    if (password != confirmPassword) {
                        showToastMessage = "Passwords do not match"
                        return@launch
                    }
                    if (!isPhoneNumberValid) {
                        showToastMessage = "Invalid phone number"
                        return@launch
                    }
                    if (!isEmailValid) {
                        showToastMessage = "Invalid email address"
                        return@launch
                    }

                    isLoading = true

                    val userAuth = UserAuth()
                    val (success, errorMessage) = userAuth.registerUser(
                        email = email,
                        password = password,
                        username = username,
                        firstName = firstName,
                        lastName = lastName,
                        phone = phoneNumber
                    )

                    isLoading = false

                    logMessage("Registration", "Registration success: $success")
                    if (success) {
                        navController.navigate(Screen.ToolList.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    } else {
                        // Update the state to trigger the toast
                        showToastMessage = when (errorMessage) {
                            "Err.mail.taken" -> "Email address is already in use"
                            else -> "Registration failed"
                        }
                    }
                }
            }) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(enabled = !isLoading, onClick = {
                navController.popBackStack()
            }) {
                Text("Already have an account? Login")
            }
        }
    }

    if (isLoading) {
        addScreenLoader()
    }

    // Observe the state and show the toast when the message is not null
    showToastMessage?.let { message ->
        showToast(message)
        showToastMessage = null
    }
}