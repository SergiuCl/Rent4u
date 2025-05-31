package at.rent4u.data

import android.os.Build
import androidx.annotation.RequiresApi
import at.rent4u.model.Booking
import at.rent4u.model.Tool
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class ToolRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private var lastVisibleSnapshot: DocumentSnapshot? = null

    suspend fun addTool(tool: Tool): Pair<Boolean, String?> {
        return try {
            firestore.collection("tools").add(tool).await()
            true to null
        } catch (e: Exception) {
            false to e.localizedMessage
        }
    }

    /**
     * Get only a limited number of items.
     */
    suspend fun getToolsPaged(limit: Long = 10): List<Pair<String, Tool>> {
        return try {
            val query = firestore.collection("tools")
                // Need to order by a field that exists in all documents and is unique. Currently using createdAt
                .orderBy("createdAt")
                .let {
                    if (lastVisibleSnapshot != null) it.startAfter(lastVisibleSnapshot!!)
                    else it
                }
                .limit(limit)

            val result = query.get().await()
            lastVisibleSnapshot = result.documents.lastOrNull()

            result.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.let { tool -> doc.id to tool }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getToolById(id: String): Tool? {
        return try {
            val doc = firestore.collection("tools").document(id).get().await()
            doc.toObject(Tool::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTools(): List<Pair<String, Tool>> {
        return try {
            val result = firestore.collection("tools").get().await()
            result.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.let { tool ->
                    doc.id to tool
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

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
            emptyList()
        }
    }

    suspend fun getToolsByIds(ids: Set<String>): List<Pair<String, Tool>> {
        return try {
            if (ids.isEmpty()) return emptyList()
            val result = firestore.collection("tools")
                .whereIn(FieldPath.documentId(), ids.toList())
                .get().await()
            result.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.let { tool -> doc.id to tool }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun cancelBooking(booking: Booking) {
        try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("toolId", booking.toolId)
                .whereEqualTo("userId", booking.userId)
                .whereEqualTo("startDate", booking.startDate)
                .whereEqualTo("endDate", booking.endDate)
                .get()
                .await()
            snapshot.documents.forEach { it.reference.delete().await() }
        } catch (_: Exception) {
        }
    }

    suspend fun updateTool(toolId: String, data: Map<String, Any>): Pair<Boolean, String?> {
        return try {
            firestore.collection("tools").document(toolId).update(data).await()
            true to null
        } catch (e: Exception) {
            false to e.message
        }
    }
}