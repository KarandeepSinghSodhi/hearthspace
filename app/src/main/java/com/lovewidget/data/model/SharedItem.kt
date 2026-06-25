// data/model/SharedItem.kt
package com.lovewidget.data.model

import com.google.firebase.Timestamp

data class SharedItem(
    val note: String = "",
    val pictureUrl: String? = null,
    val pet: PetInfo = PetInfo(),
    val lastUpdated: Timestamp? = null,
    val updatedBy: String = "",
    val softDelete: Boolean = false
)

data class PetInfo(
    val state: String = "HAPPY", // could be enum later
    val emotion: String = "NEUTRAL",
    val accessory: String? = null,
    val lastUpdated: Timestamp? = null
)
