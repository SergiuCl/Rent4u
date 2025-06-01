package at.rent4u.data

import android.util.Log

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
            val docId = firestore.collection("tools").document().id
            val toolWithId = tool.copy(id = docId)

            // Create the document with the pre-generated ID and tool object
            firestore.collection("tools").document(docId).set(toolWithId).await()

            true to null
        } catch (e: Exception) {
            false to e.localizedMessage
        }
    }

    /**
     * Get only a limited number of items.
     */
    suspend fun getToolsPaged(limit: Long? = 10): List<Pair<String, Tool>> {
        return try {
            // 1) Log entry into method
            Log.d("ToolRepository", "getToolsPaged called with limit = $limit, lastVisibleSnapshot = $lastVisibleSnapshot")

            // Reset lastVisibleSnapshot when loading all tools at once
            if (limit == null) {
                lastVisibleSnapshot = null
                Log.d("ToolRepository", "Reset lastVisibleSnapshot for full data load")
            }

            // Build base query
            val baseQuery = firestore.collection("tools")
                .orderBy("createdAt")
            Log.d("ToolRepository", "Base query on 'tools' created")

            // If paginating, start after lastVisibleSnapshot
            val pagedQuery = if (lastVisibleSnapshot != null) {
                Log.d("ToolRepository", "lastVisibleSnapshot is not null, starting after it")
                baseQuery.startAfter(lastVisibleSnapshot!!)
            } else {
                Log.d("ToolRepository", "lastVisibleSnapshot is null, using baseQuery")
                baseQuery
            }

            // Apply limit only if not null
            val finalQuery = if (limit != null) {
                Log.d("ToolRepository", "Applying limit of $limit")
                pagedQuery.limit(limit)
            } else {
                Log.d("ToolRepository", "No limit applied (limit is null)")
                pagedQuery
            }

            // 2) Log the final query before execution
            Log.d("ToolRepository", "Executing finalQuery: $finalQuery")

            val result = finalQuery.get().await()
            Log.d("ToolRepository", "QuerySnapshot received, size = ${result.size()}")

            // Update lastVisibleSnapshot for paging only when using pagination
            if (limit != null) {
                lastVisibleSnapshot = result.documents.lastOrNull()
                Log.d("ToolRepository", "lastVisibleSnapshot updated to = $lastVisibleSnapshot")
            }

            // Map each document to Pair<id, Tool>
            val mapped = result.documents.mapNotNull { doc ->
                try {
                    // Check if rentalRate is a String and convert it if needed
                    if (doc.contains("rentalRate") && doc.get("rentalRate") is String) {
                        val rentalRateString = doc.getString("rentalRate")
                        val rentalRateDouble = rentalRateString?.toDoubleOrNull() ?: 0.0

                        // Create a new map with the converted value
                        val data = doc.data ?: emptyMap()
                        val updatedData = data.toMutableMap()
                        updatedData["rentalRate"] = rentalRateDouble

                        // Update the document in Firestore to fix the type issue
                        firestore.collection("tools").document(doc.id).update("rentalRate", rentalRateDouble)

                        // Convert using the updated data
                        val tool = Tool(
                            id = doc.id,
                            brand = doc.getString("brand") ?: "",
                            modelNumber = doc.getString("modelNumber") ?: "",
                            description = doc.getString("description") ?: "",
                            powerSource = doc.getString("powerSource") ?: "",
                            weight = doc.getString("weight") ?: "",
                            dimensions = doc.getString("dimensions") ?: "",
                            fuelType = doc.getString("fuelType") ?: "",
                            voltage = doc.getString("voltage") ?: "",
                            availabilityStatus = doc.getString("availabilityStatus") ?: "",
                            rentalRate = rentalRateDouble,
                            image = doc.getString("image") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0,
                            type = doc.getString("type") ?: ""
                        )

                        doc.id to tool
                    } else {
                        // Normal conversion if rentalRate is already a number
                        val toolObj = doc.toObject(Tool::class.java)
                        Log.d("ToolRepository", "Document ID: ${doc.id}, toObject returned: $toolObj")
                        toolObj?.let { tool -> doc.id to tool }
                    }
                } catch (e: Exception) {
                    Log.e("ToolRepository", "Error converting document ${doc.id}: ${e.message}", e)
                    null
                }
            }
            Log.d("ToolRepository", "Mapped results size = ${mapped.size}")
            mapped
        } catch (e: Exception) {
            Log.e("ToolRepository", "Exception in getToolsPaged: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    suspend fun getToolById(id: String): Tool? {
        return try {
            val doc = firestore.collection("tools").document(id).get().await()

            // Check if rentalRate is a String and convert it if needed
            if (doc.contains("rentalRate") && doc.get("rentalRate") is String) {
                val rentalRateString = doc.getString("rentalRate")
                val rentalRateDouble = rentalRateString?.toDoubleOrNull() ?: 0.0

                // Update the document in Firestore to fix the type issue
                firestore.collection("tools").document(id).update("rentalRate", rentalRateDouble)

                // Create the Tool object manually with the correct type
                Tool(
                    id = doc.id,
                    brand = doc.getString("brand") ?: "",
                    modelNumber = doc.getString("modelNumber") ?: "",
                    description = doc.getString("description") ?: "",
                    powerSource = doc.getString("powerSource") ?: "",
                    weight = doc.getString("weight") ?: "",
                    dimensions = doc.getString("dimensions") ?: "",
                    fuelType = doc.getString("fuelType") ?: "",
                    voltage = doc.getString("voltage") ?: "",
                    availabilityStatus = doc.getString("availabilityStatus") ?: "",
                    rentalRate = rentalRateDouble,
                    image = doc.getString("image") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0,
                    type = doc.getString("type") ?: ""
                )
            } else {
                doc.toObject(Tool::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error in getToolById: ${e.message}", e)
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

