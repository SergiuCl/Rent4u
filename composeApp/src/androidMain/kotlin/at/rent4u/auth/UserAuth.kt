package at.rent4u.auth

import at.rent4u.logging.logMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

actual class UserAuth actual constructor() {
    actual suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean {
        return try {
            val auth = FirebaseAuth.getInstance()
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: run {
                logMessage("Registration", "UID is null")
                return false
            }

            logMessage("Registration", "Got UID: $uid — writing to Firestore")

            val userData = hashMapOf(
                "username" to username,
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "phone" to phone
            )

            Firebase.firestore.collection("users").document(uid).set(userData).await()

            logMessage("Registration", "Registration and Firestore write successful")
            true
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email already exists
            logMessage("Registration", "Email already in use")
            false
        } catch (e: Exception) {
            // Any other error
            logMessage("Registration", "Registration failed: ${e.message}")
            false
        }
    }
}