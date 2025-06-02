package at.rent4u.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import at.rent4u.model.Booking
import at.rent4u.model.Tool
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Remove duplicate tool methods as they belong in ToolRepository
    
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun bookTool(booking: Booking): Boolean {
        return try {
            val start = LocalDate.parse(booking.startDate)
            val end = LocalDate.parse(booking.endDate)

            val snapshot = firestore.collection("bookings")
                .whereEqualTo("toolId", booking.toolId)
                .get()
                .await()

            val isOverlapping = snapshot.documents.any { doc ->
                val existingStartStr = doc.getString("startDate")
                val existingEndStr = doc.getString("endDate")

                if (existingStartStr != null && existingEndStr != null) {
                    val existingStart = LocalDate.parse(existingStartStr)
                    val existingEnd = LocalDate.parse(existingEndStr)

                    // Check if ranges overlap: A_start <= B_end && B_start <= A_end
                    !start.isAfter(existingEnd) && !end.isBefore(existingStart)
                } else false
            }

            if (isOverlapping) {
                false
            } else {
                firestore.collection("bookings").add(booking).await()
                true
            }
        } catch (e: Exception) {
            Log.e("BookingRepository", "Error booking tool: ${e.message}")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getBookedDates(toolId: String): List<LocalDate> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("toolId", toolId)
                .get()
                .await()

            val bookedRanges = snapshot.documents.flatMap { doc ->
                val startDateStr = doc.getString("startDate")
                val endDateStr = doc.getString("endDate")

                if (startDateStr != null && endDateStr != null) {
                    val start = LocalDate.parse(startDateStr)
                    val end = LocalDate.parse(endDateStr)
                    generateSequence(start) { current ->
                        if (current.isBefore(end)) current.plusDays(1) else null
                    }.toList() + end // include end date
                } else emptyList()
            }

            bookedRanges.distinct()
        } catch (e: Exception) {
            Log.e("BookingRepository", "Error getting booked dates: ${e.message}")
            emptyList()
        }
    }

    suspend fun getBookingsForUser(userId: String): List<Booking> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            Log.e("BookingRepository", "Error getting bookings for user: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCurrentUserBookings(): List<Booking> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return getBookingsForUser(userId)
    }

    suspend fun cancelBooking(booking: Booking): Boolean {
        try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("toolId", booking.toolId)
                .whereEqualTo("userId", booking.userId)
                .whereEqualTo("startDate", booking.startDate)
                .whereEqualTo("endDate", booking.endDate)
                .get()
                .await()
            
            if (snapshot.documents.isEmpty()) {
                Log.e("BookingRepository", "No booking found to cancel")
                return false
            }
            
            snapshot.documents.forEach { it.reference.delete().await() }
            return true
        } catch (e: Exception) {
            Log.e("BookingRepository", "Error canceling booking: ${e.message}")
            return false
        }
    }

    suspend fun cancelBookingById(bookingId: String): Boolean {
        return try {
            firestore.collection("bookings").document(bookingId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("BookingRepository", "Error canceling booking by ID: ${e.message}")
            false
        }
    }
}
