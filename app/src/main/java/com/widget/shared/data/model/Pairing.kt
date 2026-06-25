// data/model/Pairing.kt
package com.widget.shared.data.model

import com.google.firebase.Timestamp

data class Pairing(
    val pairId: String = "",
    val userA: String = "",
    val userB: String = "",
    val createdAt: Timestamp? = null
)
