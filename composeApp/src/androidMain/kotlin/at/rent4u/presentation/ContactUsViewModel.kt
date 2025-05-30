package at.rent4u.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.rent4u.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun sendEmail(
        context: Context,
        onSuccess: () -> Unit
    ) {
        val body = _message.value
        val email = "sergiu-claudiu.iordanescu@stud.fh-campuswien.ac.at"
        val subject = "Rent4U Contact Us Message"

        if (body.isBlank()) {
            _toastMessage.value = "Please enter a message."
            return
        }

        _isSending.value = true

        viewModelScope.launch {
            try {
                val username = userRepository.getCurrentUserUsername() ?: "unknown"
                val fullBody = "From username: $username\n\n$body"

                val uri = Uri.parse(
                    "mailto:$email" +
                            "?subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(fullBody)
                )

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = uri
                }

                context.startActivity(Intent.createChooser(intent, "Send Email"))
                _message.value = ""
                _toastMessage.value = "Message ready to send via email app."
                onSuccess()
            } catch (e: Exception) {
                _toastMessage.value = "Error opening email client."
            } finally {
                _isSending.value = false
            }
        }
    }
}
