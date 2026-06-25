package com.lovewidget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SharedItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sharedItemDao(): SharedItemDao
}
