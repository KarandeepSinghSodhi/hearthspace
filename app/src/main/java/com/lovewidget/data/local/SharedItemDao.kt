package com.lovewidget.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedItemDao {
    @Query("SELECT * FROM shared_item_cache WHERE id = 0 LIMIT 1")
    fun observe(): Flow<SharedItemEntity?>

    @Query("SELECT * FROM shared_item_cache WHERE id = 0 LIMIT 1")
    suspend fun get(): SharedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SharedItemEntity)

    @Query("DELETE FROM shared_item_cache")
    suspend fun clear()
}
