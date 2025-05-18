package at.rent4u.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Booking(
    val toolId: String = "",
    val userId: String = "",
    val startDate: String = "",  // ISO 8601 "yyyy-MM-dd"
    val endDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)