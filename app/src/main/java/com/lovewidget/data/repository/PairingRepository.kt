// data/repository/PairingRepository.kt
package com.lovewidget.data.repository

import com.lovewidget.data.model.Pairing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid: String?
        get() = auth.currentUser?.uid

    /**
     * Creates a new pairing document where this user becomes userA.
     * Returns the generated pairId.
     */
    suspend fun createPairing(): String? {
        val currentUid = uid ?: return null
        val newPairRef = firestore.collection("pairings").document()
        val pairing = Pairing(
            pairId = newPairRef.id,
            userA = currentUid,
            userB = "",
            createdAt = com.google.firebase.Timestamp.now()
        )
        newPairRef.set(pairing).await()
        return newPairRef.id
    }

    /**
     * Join an existing pairing by ID – sets this user as userB if slot is free.
     */
    suspend fun joinPairing(pairId: String): Boolean {
        val currentUid = uid ?: return false
        val pairDoc = firestore.collection("pairings").document(pairId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(pairDoc)
                if (!snapshot.exists()) throw IllegalArgumentException("Pair not found")
                val existing = snapshot.toObject(Pairing::class.java)!!
                if (existing.userB.isNotEmpty()) throw IllegalStateException("Pair already full")
                val updated = existing.copy(userB = currentUid)
                transaction.set(pairDoc, updated)
                null
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Retrieve the pairing document that includes this UID, if any. */
    suspend fun getPairingForCurrentUser(): Pairing? {
        val currentUid = uid ?: return null
        // First check if user is userA
        val queryA = firestore.collection("pairings")
            .whereEqualTo("userA", currentUid)
            .get()
            .await()
        if (!queryA.isEmpty) return queryA.documents.first().toObject(Pairing::class.java)
        // Then check if user is userB
        val queryB = firestore.collection("pairings")
            .whereEqualTo("userB", currentUid)
            .get()
            .await()
        return if (!queryB.isEmpty) queryB.documents.first().toObject(Pairing::class.java) else null
    }
}
