package at.rent4u.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import at.rent4u.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import at.rent4u.model.UserDetails

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _keepLoggedIn = MutableStateFlow(userRepository.isKeepLoggedInEnabled())
    val keepLoggedIn: StateFlow<Boolean> = _keepLoggedIn

    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean {
        return userRepository.isUserLoggedIn()
    }

    // Update the keep logged in preference
    fun setKeepLoggedIn(enabled: Boolean) {
        userRepository.setKeepLoggedIn(enabled)
        _keepLoggedIn.value = enabled
    }

    suspend fun register(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Pair<Boolean, String?> {
        _isLoading.value = true
        val result =
            userRepository.registerUser(email, password, username, firstName, lastName, phone)
        _isLoading.value = false
        return result
    }

    suspend fun login(email: String, password: String): Boolean {
        _isLoading.value = true
        val result = userRepository.loginUser(email, password)
        _isLoading.value = false
        return result
    }

    fun logout() {
        userRepository.logoutUser()
        // When logging out, also clear the keep logged in preference
        setKeepLoggedIn(false)
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun setToastMessage(message: String) {
        _toastMessage.value = message
    }

    suspend fun getUserDetails(userId: String): UserDetails {
        return userRepository.getUserDetails(userId)
    }

    suspend fun updateUserDetails(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        oldPassword: String,
        phone: String
    ) {
        userRepository.updateUserDetails(
            userId,
            username,
            firstName,
            lastName,
            email,
            oldPassword,
            phone
        )
    }

    suspend fun deleteUser(userId: String) {
        userRepository.deleteUser(userId)
    }

    suspend fun getCurrentUserId(): String? {
        return userRepository.getCurrentUserId()
    }

    suspend fun getUserName(): String? {
        return userRepository.getCurrentUserUsername()
    }

    // Update only basic profile details (no email/password changes)
    suspend fun updateUserProfileDetails(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        _isLoading.value = true
        try {
            userRepository.updateUserProfileDetails(
                userId, username, firstName, lastName, phone
            )
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            throw e
        }
    }

    // Specifically handle email changes with password verification
    suspend fun updateUserEmail(
        userId: String,
        newEmail: String,
        password: String
    ) {
        _isLoading.value = true
        try {
            // First update the email in Firebase Authentication
            userRepository.updateAuthEmail(newEmail, password)
            
            // Then update the email in the database to keep them in sync
            userRepository.updateEmailInDatabase(userId, newEmail)
            
            // After changing email, it needs to be verified again
            userRepository.sendVerificationEmail()
            
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            throw e
        }
    }

    // Specifically handle password changes with current password verification
    suspend fun updateUserPassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ) {
        _isLoading.value = true
        try {
            userRepository.updateUserPassword(userId, currentPassword, newPassword)
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            throw e
        }
    }

    // Check if the current user's email is verified with a forced refresh
    suspend fun isCurrentEmailVerifiedWithRefresh(): Boolean {
        return userRepository.isCurrentEmailVerifiedWithRefresh()
    }
    
    // Synchronous version that doesn't force refresh - use with caution
    fun isCurrentEmailVerified(): Boolean {
        return userRepository.isCurrentEmailVerified()
    }

    // Send a verification email to the current user
    suspend fun sendVerificationEmail() {
        _isLoading.value = true
        try {
            userRepository.getCurrentUserId()?.let {
                userRepository.sendVerificationEmail()
            }
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            throw e
        }
    }

    // Force refresh the current user data from Firebase
    // Remove the duplicate method and keep only this suspend version
    suspend fun refreshCurrentUser() {
        try {
            userRepository.refreshCurrentUser()
        } catch (e: Exception) {
            // Log the error but don't throw
        }
    }
}
