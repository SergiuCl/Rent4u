package at.rent4u.auth

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
            val uid = result.user?.uid ?: return false

            val userData = hashMapOf(
                "username" to username,
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "phone" to phone
            )

            Firebase.firestore.collection("users").document(uid).set(userData).await()
            true
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email already exists
            println("Error: Email already in use")
            false
        } catch (e: Exception) {
            // Any other error
            println("Registration failed: ${e.message}")
            false
        }
    }
}