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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import at.rent4u.localization.LocalizedStringProvider
import at.rent4u.localization.StringResourceId

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onChangePassword: (currentPassword: String, newPassword: String) -> Unit
) {
    // Setup localization
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val strings = remember(configuration) {
        LocalizedStringProvider(context)
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordError by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf(false) }
    var passwordsMatchError by remember { mutableStateOf(false) }

    // Validate form
    val isFormValid = currentPassword.isNotBlank() &&
                     newPassword.isNotBlank() &&
                     confirmPassword.isNotBlank() &&
                     newPassword == confirmPassword &&
                     newPassword.length >= 6 &&
                     !currentPasswordError &&
                     !newPasswordError &&
                     !passwordsMatchError

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.getString(StringResourceId.CHANGE_PASSWORD)) },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        currentPasswordError = it.isEmpty()
                    },
                    label = { Text(strings.getString(StringResourceId.CURRENT_PASSWORD)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = currentPasswordError,
                    supportingText = {
                        if (currentPasswordError) {
                            Text(strings.getString(StringResourceId.CURRENT_PASSWORD_REQUIRED))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        newPasswordError = it.length < 6 && it.isNotEmpty()
                        passwordsMatchError = confirmPassword.isNotEmpty() && it != confirmPassword
                    },
                    label = { Text(strings.getString(StringResourceId.NEW_PASSWORD)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = newPasswordError,
                    supportingText = {
                        if (newPasswordError) {
                            Text(strings.getString(StringResourceId.PASSWORD_LENGTH_ERROR))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        passwordsMatchError = it.isNotEmpty() && it != newPassword
                    },
                    label = { Text(strings.getString(StringResourceId.CONFIRM_NEW_PASSWORD)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordsMatchError,
                    supportingText = {
                        if (passwordsMatchError) {
                            Text(strings.getString(StringResourceId.PASSWORDS_DONT_MATCH))
                        }
                    }
                )

                Text(
                    strings.getString(StringResourceId.PASSWORD_SECURITY_HINT),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onChangePassword(currentPassword, newPassword) },
                enabled = isFormValid
            ) {
                Text(strings.getString(StringResourceId.CHANGE_PASSWORD))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
