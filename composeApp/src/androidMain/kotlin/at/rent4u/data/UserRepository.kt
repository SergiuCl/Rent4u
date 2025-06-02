package at.rent4u.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import at.rent4u.model.UserDetails // Ensure the UserDetails class is imported

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
        email: String,
        phone: String
    ) {
        // Update the authenticated user's email
        auth.currentUser?.let { firebaseUser ->
            try {
                firebaseUser.updateEmail(email).await()
            } catch (e: Exception) {
                // Handle or rethrow if needed
            }
        }
        // Now update Firestore document
        val userData: Map<String, Any> = mapOf(
            "username" to username,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone
        )
        firestore.collection("users").document(userId).update(userData).await()
    }

    suspend fun deleteUser(userId: String) {
        firestore.collection("users").document(userId).delete().await()
        auth.currentUser?.delete()?.await()
    }


}
