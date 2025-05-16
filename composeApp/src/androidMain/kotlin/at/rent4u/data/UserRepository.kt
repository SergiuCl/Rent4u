package at.rent4u.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

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
}