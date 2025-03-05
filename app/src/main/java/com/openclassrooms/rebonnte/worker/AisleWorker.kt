package com.openclassrooms.rebonnte.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AisleWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        // Input keys
        const val OPERATION_TYPE = "operation_type"
        const val AISLE_NAME = "aisle_name"
        const val AISLE_ID = "aisle_id"
        const val AISLE_TIMESTAMP = "aisle_timestamp"

        // Operation types
        const val OPERATION_ADD = "add"
        const val OPERATION_DELETE = "delete"
    }

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        val operationType = inputData.getString(OPERATION_TYPE) ?: return Result.failure()

        return when (operationType) {
            OPERATION_ADD -> performAdd()
            OPERATION_DELETE -> performDelete()
            else -> Result.failure()
        }
    }

    private suspend fun performAdd(): Result {
        val name = inputData.getString(AISLE_NAME) ?: return Result.failure()
        val id = inputData.getString(AISLE_ID) ?: UUID.randomUUID().toString()
        val timestamp = inputData.getLong(AISLE_TIMESTAMP, System.currentTimeMillis())
        val aisle = Aisle(name, id, timestamp)

        return try {
            firestore.collection("aisles")
                .document(aisle.id)
                .set(aisle)
                .await()
            println("Worker: Aisle ${aisle.name} added successfully, ID: ${aisle.id}")
            Result.success()
        } catch (e: Exception) {
            println("Worker: Failed to add aisle ${aisle.name}: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun performDelete(): Result {
        val aisleId = inputData.getString(AISLE_ID) ?: return Result.failure()
        val aisleName = inputData.getString(AISLE_NAME) ?: "Unknown"

        return try {
            firestore.collection("aisles")
                .document(aisleId)
                .delete()
                .await()
            println("Worker: Aisle $aisleName deleted successfully, ID: $aisleId")
            Result.success()
        } catch (e: Exception) {
            println("Worker: Failed to delete aisle $aisleName: ${e.message}")
            Result.retry()
        }
    }
}