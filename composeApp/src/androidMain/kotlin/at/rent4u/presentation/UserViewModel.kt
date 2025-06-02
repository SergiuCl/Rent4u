package at.rent4u.presentation

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

    fun logout() = userRepository.logoutUser()

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
}
