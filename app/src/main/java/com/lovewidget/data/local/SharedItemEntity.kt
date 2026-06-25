package com.lovewidget.data.local

import androidx.room.*

@Entity(tableName = "shared_item_cache")
data class SharedItemEntity(
    @PrimaryKey val id: Int = 0,         // always 0 — single-row table
    val note: String = "",
    val pictureUrl: String? = null,
    val petState: String = "HAPPY",
    val petEmotion: String = "NEUTRAL",
    val petAccessory: String? = null,
    val lastUpdated: Long = 0L,          // epoch millis
    val updatedBy: String = ""
)
