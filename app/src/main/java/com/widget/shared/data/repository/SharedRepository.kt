// data/repository/SharedRepository.kt
package com.widget.shared.data.repository

import com.widget.shared.data.model.SharedItem
import com.widget.shared.data.model.PetInfo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that abstracts Firestore interaction for the shared note/picture/pet.
 * It also mirrors the latest document in a local in‑memory cache for quick reads.
 */
@Singleton
class SharedRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val pairId: String?
        get() = currentPairId

    // The pair document ID is derived from the pairing logic (stored elsewhere).
    var currentPairId: String? = null
        private set

    private var sharedDocRef: DocumentReference? = null
    private var listener: ListenerRegistration? = null

    /** In‑memory cache of the latest shared item */
    var cachedItem: SharedItem? = null
        private set

    /** Subscribe to real‑time updates of the shared document */
    fun observeSharedItem() = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        // Resolve pairId if not set – this would normally be done via a separate PairingRepository.
        // For now we assume currentPairId is already populated.
        val docRef = pairId?.let { firestore.collection("shared").document(it) } ?: return@callbackFlow
        sharedDocRef = docRef
        listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val item = snapshot.toObject(SharedItem::class.java)
                cachedItem = item
                trySend(item!!)
            }
        }
        awaitClose {
            listener?.remove()
        }
    }

    /** Update shared content atomically. This uses a transaction to enforce "last write wins". */
    suspend fun updateSharedItem(
        note: String? = null,
        pictureUrl: String? = null,
        petInfo: PetInfo? = null,
        eventType: String = "UPDATE"
    ): Boolean {
        val docRef = sharedDocRef ?: return false
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val existing = snapshot.toObject(SharedItem::class.java)
                val newItem = existing?.copy(
                    note = note ?: existing.note,
                    pictureUrl = pictureUrl ?: existing.pictureUrl,
                    pet = petInfo ?: existing.pet,
                    lastUpdated = Timestamp.now(),
                    updatedBy = auth.currentUser?.uid ?: "",
                    softDelete = false
                ) ?: SharedItem(
                    note = note ?: "",
                    pictureUrl = pictureUrl,
                    pet = petInfo ?: PetInfo(),
                    lastUpdated = Timestamp.now(),
                    updatedBy = auth.currentUser?.uid ?: "",
                    softDelete = false
                )
                transaction.set(docRef, newItem)
                // Also write a history record (fire-and-forget)
                val historyRef = firestore.collection("history").document(pairId!!)
                    .collection("records").document()
                val historyRecord = mapOf(
                    "timestamp" to Timestamp.now(),
                    "author" to auth.currentUser?.uid,
                    "note" to note,
                    "pictureUrl" to pictureUrl,
                    "pet" to petInfo,
                    "eventType" to eventType,
                    "softDelete" to false
                )
                transaction.set(historyRef, historyRecord)
                null
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Called when the UI knows a new pair ID (e.g., after pairing flow). */
    fun setPairId(id: String) {
        currentPairId = id
        // Reset listener so that observeSharedItem will attach to the new doc.
        listener?.remove()
    }
}
