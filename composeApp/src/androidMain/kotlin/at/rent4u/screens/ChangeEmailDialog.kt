package at.rent4u.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import at.rent4u.screens.validateEmail

@Composable
fun ChangeEmailDialog(
    currentEmail: String,
    onDismiss: () -> Unit,
    onChangeEmail: (newEmail: String, password: String) -> Unit
) {
    var newEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var passwordError by remember { mutableStateOf(false) }

    // Validate on every keystroke
    val isFormValid = newEmail.isNotBlank() &&
                     newEmail != currentEmail &&
                     password.isNotBlank() &&
                     validateEmail(newEmail) &&
                     !passwordError

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Email Address") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Current Email: $currentEmail")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = {
                        newEmail = it
                        isEmailValid = validateEmail(it) || it.isEmpty()
                    },
                    label = { Text("New Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isEmailValid,
                    supportingText = {
                        if (!isEmailValid) {
                            Text("Please enter a valid email address")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = it.isEmpty()
                    },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError,
                    supportingText = {
                        if (passwordError) {
                            Text("Password is required")
                        }
                    }
                )

                Text(
                    "You'll need to verify this new email address after changing.",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onChangeEmail(newEmail, password) },
                enabled = isFormValid
            ) {
                Text("Change Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
