package at.rent4u.data

import android.util.Log
import at.rent4u.model.Tool
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ToolRepository @Inject constructor() {

    suspend fun getToolById(id: String): Tool? {
        return try {
            // Check for empty ID to prevent Firestore errors
            if (id.isBlank()) {
                Log.e("ToolRepository", "Cannot get tool with empty ID")
                return null
            }

            val document = Firebase.firestore.collection("tools").document(id).get().await()
            if (document.exists()) {
                val tool = document.toTool()
                tool.copy(id = id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error getting tool: ${e.message}")
            null
        }
    }

    suspend fun getToolsPaged(lastDocumentSnapshot: DocumentSnapshot?): List<Pair<String, Tool>> {
        return try {
            var query = Firebase.firestore.collection("tools")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)

            lastDocumentSnapshot?.let {
                query = query.startAfter(it)
            }

            val querySnapshot = query.get().await()

            querySnapshot.documents.map { document ->
                val tool = document.toTool()
                document.id to tool.copy(id = document.id)
            }
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error getting tools: ${e.message}")
            emptyList()
        }
    }

    private fun DocumentSnapshot.toTool(): Tool {
        return Tool(
            id = id,
            brand = getString("brand") ?: "",
            modelNumber = getString("modelNumber") ?: "",
            description = getString("description") ?: "",
            availabilityStatus = getString("availabilityStatus") ?: "Unavailable",
            powerSource = getString("powerSource") ?: "",
            type = getString("type") ?: "",
            voltage = getString("voltage") ?: "",
            fuelType = getString("fuelType") ?: "",
            weight = getString("weight") ?: "",
            dimensions = getString("dimensions") ?: "",
            rentalRate = getDouble("rentalRate") ?: 0.0,
            image = getString("image") ?: "",
            createdAt = getLong("createdAt") ?: 0
        )
    }

    suspend fun addTool(tool: Tool): Pair<Boolean, String?> {
        return try {
            val data = mapOf(
                "brand" to tool.brand,
                "modelNumber" to tool.modelNumber,
                "description" to tool.description,
                "availabilityStatus" to tool.availabilityStatus,
                "powerSource" to tool.powerSource,
                "type" to tool.type,
                "voltage" to tool.voltage,
                "fuelType" to tool.fuelType,
                "weight" to tool.weight,
                "dimensions" to tool.dimensions,
                "rentalRate" to tool.rentalRate,
                "image" to tool.image,
                "createdAt" to tool.createdAt
            )

            Firebase.firestore.collection("tools").add(data).await()
            true to null
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error adding tool: ${e.message}")
            false to e.message
        }
    }

    suspend fun updateTool(toolId: String, updatedData: Map<String, Any>): Pair<Boolean, String?> {
        return try {
            Firebase.firestore
                .collection("tools")
                .document(toolId)
                .update(updatedData)
                .await()
            true to null
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error updating tool: ${e.message}")
            false to e.message
        }
    }

    suspend fun deleteTool(toolId: String): Boolean {
        return try {
            Firebase.firestore
                .collection("tools")
                .document(toolId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error deleting tool: ${e.message}")
            false
        }
    }

    suspend fun getToolsByIds(ids: Set<String>): List<Pair<String, Tool>> {
        return try {
            if (ids.isEmpty()) return emptyList()
            val result = Firebase.firestore.collection("tools")
                .whereIn(FieldPath.documentId(), ids.toList())
                .get().await()
            result.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.let { tool -> doc.id to tool }
            }
        } catch (e: Exception) {
            Log.e("ToolRepository", "Error getting tools by IDs: ${e.message}")
            emptyList()
        }
    }

    /**
     * Returns a cold Flow that, when collected, starts a Firestore snapshot listener on "tools".
     * As soon as the listener emits a new QuerySnapshot, the Flow emits a List<Pair<id,Tool>>.
     */
    fun observeAllTools(): Flow<List<Pair<String, Tool>>> = callbackFlow {
        val listener: ListenerRegistration = Firebase.firestore
            .collection("tools")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ToolRepository", "Listen failed: ${error.message}")
                    // You can choose to close the flow or emit an empty list
                    // channel.close(error)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents
                    ?.mapNotNull { doc ->
                        // Convert the DocumentSnapshot into a Tool data class
                        doc.toObject(Tool::class.java)
                            ?.copy(id = doc.id)  // ensure the ID is filled
                            ?.let { tool -> doc.id to tool }
                    }
                    ?: emptyList()

                try {
                    trySend(list).isSuccess
                } catch (e: Exception) {
                    Log.e("ToolRepository", "Error sending list into Flow: ${e.message}")
                }
            }

        // When the Flow collector is cancelled, remove the Firestore listener:
        awaitClose {
            listener.remove()
        }
    }
}
