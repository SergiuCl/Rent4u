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
    ): Pair<Boolean, String?> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: run {
                logMessage("Registration", "UID is null")
                return false to "Err.general"
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

            true to null
        } catch (e: FirebaseAuthUserCollisionException) {
            false to "Err.mail.taken"
        } catch (e: Exception) {
            false to "Err.general"
        }
    }

    actual suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password).await()

            logMessage("Login", "Login successful for $email")
            true
        } catch (e: Exception) {
            logMessage("Login", "Login failed: ${e.message}")
            false
        }
    }
}