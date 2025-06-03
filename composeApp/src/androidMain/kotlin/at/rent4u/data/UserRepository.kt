package at.rent4u.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import at.rent4u.model.UserDetails // Ensure the UserDetails class is imported
import com.google.firebase.auth.EmailAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("rent4u_prefs", Context.MODE_PRIVATE)
    private val KEY_KEEP_LOGGED_IN = "keep_logged_in"

    // Check if user has chosen to stay logged in
    fun isKeepLoggedInEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_KEEP_LOGGED_IN, false)
    }

    // Set keep logged in preference
    fun setKeepLoggedIn(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_KEEP_LOGGED_IN, enabled).apply()
        Log.d("UserRepository", "Keep logged in set to: $enabled")
    }

    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null && isKeepLoggedInEnabled()
    }

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Pair<Boolean, String?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false to "Err.general"

            val userData = hashMapOf(
                "username" to username,
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "phone" to phone,
                "admin" to false // default new users to non-admin
            )

            firestore.collection("users").document(uid).set(userData).await()

            // Send email verification to the newly registered user
            try {
                result.user?.sendEmailVerification()?.await()
                Log.d("UserRepository", "Verification email sent to $email")
            } catch (e: Exception) {
                Log.e("UserRepository", "Failed to send verification email: ${e.message}")
                // We don't return false here because the account was created successfully
                // The verification email is a secondary step
            }

            true to null
        } catch (e: FirebaseAuthUserCollisionException) {
            false to "Err.mail.taken"
        } catch (e: Exception) {
            false to "Err.general"
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logoutUser() {
        auth.signOut()
    }

    suspend fun isCurrentUserAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            val doc = firestore.collection("users").document(user.uid).get().await()
            doc.getBoolean("admin") == true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getCurrentUserUsername(): String? {
        val uid = auth.currentUser?.uid ?: return null

        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("username")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserDetails(userId: String): UserDetails {
        val doc = firestore.collection("users").document(userId).get().await()
        return UserDetails(
            username = doc.getString("username") ?: "",
            firstName = doc.getString("firstName") ?: "",
            lastName = doc.getString("lastName") ?: "",
            email = doc.getString("email") ?: "",
            phone = doc.getString("phone") ?: ""
        )
    }

    suspend fun updateUserDetails(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        newEmail: String,
        password: String,
        phone: String
    ) {
        val currentUser = auth.currentUser ?: throw Exception("No logged-in user")
        val currentEmail = currentUser.email ?: throw Exception("Current user has no email")

        val userData = mapOf(
            "username" to username,
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone
        )

        // Update Firestore user data first (except email)
        firestore.collection("users").document(userId).update(userData).await()

        // If email is being changed, handle re-authentication and email update
        if (newEmail.isNotBlank() && newEmail != currentEmail) {
            if (password.isEmpty()) {
                throw Exception("Password is required to change email")
            }

            try {
                // Re-authenticate with current credentials
                val credential = EmailAuthProvider.getCredential(currentEmail, password)
                try {
                    currentUser.reauthenticate(credential).await()
                } catch (e: Exception) {
                    throw Exception("Authentication failed: Incorrect password")
                }

                try {
                    // Update email in Firebase Authentication
                    currentUser.updateEmail(newEmail).await()

                    // Update email in Firestore
                    firestore.collection("users").document(userId).update("email", newEmail).await()

                    try {
                        // Send verification email (this is optional and shouldn't fail the whole process)
                        currentUser.sendEmailVerification().await()
                    } catch (e: Exception) {
                        // Just log this error but don't throw
                        Log.e("UserRepository", "Failed to send verification email: ${e.message}")
                    }
                } catch (e: Exception) {
                    throw Exception("Failed to update email: ${e.message}")
                }
            } catch (e: Exception) {
                // Throw the specific error
                throw e
            }
        }
    }

    // Update only basic user profile details (no email/password)
    suspend fun updateUserProfileDetails(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        val userData = mapOf(
            "username" to username,
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone
        )

        // Update Firestore user data
        firestore.collection("users").document(userId).update(userData).await()
    }

    // Specifically handle email changes with re-authentication
    suspend fun updateUserEmail(
        userId: String,
        newEmail: String,
        password: String
    ) {
        val currentUser = auth.currentUser ?: throw Exception("No logged-in user")
        val currentEmail = currentUser.email ?: throw Exception("Current user has no email")

        if (newEmail.isBlank()) {
            throw Exception("New email cannot be empty")
        }

        if (password.isEmpty()) {
            throw Exception("Password is required to change email")
        }

        try {
            // Re-authenticate with current credentials
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            try {
                currentUser.reauthenticate(credential).await()
            } catch (e: Exception) {
                throw Exception("Authentication failed: Incorrect password")
            }

            try {
                // Force refresh the user data to ensure we have the latest verification status
                currentUser.reload().await()

                // Try to update email directly without checking verification status
                try {
                    // Update email in Firebase Authentication
                    currentUser.updateEmail(newEmail).await()

                    // Update email in Firestore
                    firestore.collection("users").document(userId).update("email", newEmail).await()

                    // Send verification email to the new address
                    try {
                        currentUser.sendEmailVerification().await()
                    } catch (e: Exception) {
                        // Just log this error but don't throw
                        Log.w("UserRepository", "Failed to send verification email: ${e.message}")
                    }
                } catch (e: Exception) {
                    // If direct update fails, try a workaround
                    if (e.message?.contains("Please verify", ignoreCase = true) == true ||
                        e.message?.contains("This operation is not allowed", ignoreCase = true) == true) {

                        // Workaround: Use a custom token if verification check is blocking the update
                        // Update only in Firestore for now and inform the user
                        firestore.collection("users").document(userId).update("email", newEmail).await()
                        throw Exception("Email updated in our database. You'll need to sign in with your original email until verification is complete.")
                    } else {
                        throw Exception("Failed to update email: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        } catch (e: Exception) {
            // Throw the specific error
            throw e
        }
    }

    /**
     * Updates the email address in Firebase Authentication
     * This is the primary method that handles authentication changes
     * 
     * @param newEmail The new email address to set
     * @param password The current user's password for re-authentication
     */
    suspend fun updateAuthEmail(newEmail: String, password: String) {
        val currentUser = auth.currentUser ?: throw Exception("No logged-in user")
        val currentEmail = currentUser.email ?: throw Exception("Current user has no email")

        if (newEmail.isBlank()) {
            throw Exception("New email cannot be empty")
        }

        if (password.isEmpty()) {
            throw Exception("Password is required to change email")
        }

        // Re-authenticate with current credentials
        val credential = EmailAuthProvider.getCredential(currentEmail, password)
        try {
            currentUser.reauthenticate(credential).await()
        } catch (e: Exception) {
            throw Exception("Authentication failed: Incorrect password")
        }

        // Force refresh the user data
        currentUser.reload().await()

        try {
            // Update email in Firebase Authentication
            currentUser.updateEmail(newEmail).await()
            Log.d("UserRepository", "Email updated in Firebase Auth to: $newEmail")
        } catch (e: Exception) {
            if (e.message?.contains("Please verify", ignoreCase = true) == true ||
                e.message?.contains("operation is not allowed", ignoreCase = true) == true) {
                throw Exception("Email change requires a verified email. Please verify your current email first.")
            } else {
                throw Exception("Failed to update email in authentication: ${e.message}")
            }
        }
    }

    /**
     * Updates the email address in Firestore database
     * This should be called after updateAuthEmail to keep systems in sync
     * 
     * @param userId The ID of the user whose email is being updated
     * @param newEmail The new email address to set in the database
     */
    suspend fun updateEmailInDatabase(userId: String, newEmail: String) {
        try {
            // Update email in Firestore
            firestore.collection("users").document(userId)
                .update("email", newEmail)
                .await()
            
            Log.d("UserRepository", "Email updated in database for user $userId to: $newEmail")
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to update email in database: ${e.message}")
            throw Exception("Email was updated in authentication but failed to update in database: ${e.message}")
        }
    }

    // Specifically handle password changes with re-authentication
    suspend fun updateUserPassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ) {
        val currentUser = auth.currentUser ?: throw Exception("No logged-in user")
        val email = currentUser.email ?: throw Exception("Current user has no email")

        if (currentPassword.isEmpty()) {
            throw Exception("Current password is required")
        }

        if (newPassword.isEmpty()) {
            throw Exception("New password cannot be empty")
        }

        if (newPassword.length < 6) {
            throw Exception("Password must be at least 6 characters long")
        }

        try {
            // Re-authenticate with current credentials
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            try {
                currentUser.reauthenticate(credential).await()
            } catch (e: Exception) {
                throw Exception("Authentication failed: Incorrect current password")
            }

            try {
                // Update password in Firebase Authentication
                currentUser.updatePassword(newPassword).await()
            } catch (e: Exception) {
                throw Exception("Failed to update password: ${e.message}")
            }
        } catch (e: Exception) {
            // Throw the specific error
            throw e
        }
    }

    suspend fun deleteUser(userId: String) {
        firestore.collection("users").document(userId).delete().await()
        auth.currentUser?.delete()?.await()
    }

    // Check if the current user's email is verified
    fun isCurrentEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    // Send verification email to the current user
    suspend fun sendVerificationEmail() {
        val currentUser = auth.currentUser ?: throw Exception("No logged-in user")
        currentUser.sendEmailVerification().await()
    }

    // Refresh the current user to get the latest information
    suspend fun refreshCurrentUser() {
        val currentUser = auth.currentUser ?: throw Exception("No logged-in user")
        currentUser.reload().await()
    }
}
