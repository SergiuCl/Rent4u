package at.rent4u.data

import at.rent4u.model.Tool
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
}