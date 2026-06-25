package com.widget.shared.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.widget.shared.data.local.SharedItemDao
import com.widget.shared.data.local.SharedItemEntity
import com.widget.shared.data.model.PetInfo
import com.widget.shared.data.repository.SharedRepository
import com.widget.shared.domain.pet.PetStateMachine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that:
 * 1. Compresses the picked image (if any).
 * 2. Uploads it to Firebase Storage.
 * 3. Updates the Firestore shared document (last-write-wins via transaction).
 * 4. Updates the local Room cache.
 */
@HiltWorker
class SyncEditWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val storage: FirebaseStorage,
    private val sharedRepo: SharedRepository,
    private val dao: SharedItemDao,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val note = inputData.getString(KEY_NOTE) ?: ""
        val imageUriStr = inputData.getString(KEY_IMAGE_URI)

        var pictureUrl: String? = null

        // Upload image if provided
        if (imageUriStr != null) {
            try {
                val uri = Uri.parse(imageUriStr)
                val stream = applicationContext.contentResolver.openInputStream(uri)
                    ?: return Result.failure()
                val original = BitmapFactory.decodeStream(stream)
                stream.close()

                // Compress: max 1080px wide, JPEG 80%
                val compressed = compressBitmap(original)
                val uid = auth.currentUser?.uid ?: return Result.failure()
                val ref = storage.reference.child("shared/${uid}_${System.currentTimeMillis()}.jpg")
                ref.putBytes(compressed).await()
                pictureUrl = ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                return Result.retry()
            }
        }

        // Derive pet state from current time
        val petState = PetStateMachine.stateForNow()
        val petInfo = PetInfo(
            state = petState.emotion,
            emotion = petState.emotion,
            accessory = null
        )

        val success = sharedRepo.updateSharedItem(
            note = note,
            pictureUrl = pictureUrl,
            petInfo = petInfo,
            eventType = "USER_EDIT"
        )

        return if (success) {
            // Update local cache
            val cached = dao.get() ?: SharedItemEntity()
            dao.upsert(
                cached.copy(
                    note = note,
                    pictureUrl = pictureUrl ?: cached.pictureUrl,
                    petState = petState.emotion,
                    lastUpdated = System.currentTimeMillis(),
                    updatedBy = auth.currentUser?.uid ?: ""
                )
            )
            Result.success()
        } else {
            Result.retry()
        }
    }

    private fun compressBitmap(src: Bitmap): ByteArray {
        val maxWidth = 1080
        val scaled = if (src.width > maxWidth) {
            val ratio = maxWidth.toFloat() / src.width
            Bitmap.createScaledBitmap(src, maxWidth, (src.height * ratio).toInt(), true)
        } else src
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
        return out.toByteArray()
    }

    companion object {
        const val KEY_NOTE = "note"
        const val KEY_IMAGE_URI = "image_uri"

        fun enqueue(note: String, imageUri: String?) {
            // Call via WorkManager.getInstance(context).enqueue(...)
            // Context passed from ViewModel — simplified here as a static helper
            val data = Data.Builder()
                .putString(KEY_NOTE, note)
                .putString(KEY_IMAGE_URI, imageUri)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncEditWorker>()
                .setInputData(data)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .build()

            // Note: context needed here — EditViewModel should call this directly
            // WorkManager.getInstance(context).enqueueUniqueWork("sync_edit", ExistingWorkPolicy.REPLACE, request)
        }
    }
}
