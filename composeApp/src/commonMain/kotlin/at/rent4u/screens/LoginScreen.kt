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
import at.rent4u.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showToastMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome Back", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(enabled = !isLoading, onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    isLoading = true

                    val userAuth = UserAuth()
                    val success = userAuth.loginUser(email, password)

                    isLoading = false

                    if (success) {
                        navController.navigate(Screen.ToolList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        showToastMessage = "Login failed. Please try again."
                    }
                }
            }) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(enabled = !isLoading, onClick = {
                navController.navigate(Screen.Register.route)
            }) {
                Text("Don't have an account? Register")
            }
        }
    }

    if (isLoading) {
        addScreenLoader()
    }

    showToastMessage?.let {
        showToast(it)
        showToastMessage = null
    }
}