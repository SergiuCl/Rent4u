package at.rent4u.logging

import android.util.Log

actual fun logMessage(tag: String, message: String) {
    Log.d(tag, message)
}