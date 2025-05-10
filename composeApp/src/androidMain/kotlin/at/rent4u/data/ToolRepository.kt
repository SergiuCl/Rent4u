package at.rent4u.data

import at.rent4u.model.Tool
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ToolRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun addTool(tool: Tool): Pair<Boolean, String?> {
        return try {
            firestore.collection("tools").add(tool).await()
            true to null
        } catch (e: Exception) {
            false to e.localizedMessage
        }
    }
}