package com.widget.shared.widget

import com.widget.shared.data.local.SharedItemDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt EntryPoint that lets the Glance widget (which cannot use @Inject)
 * retrieve the Room DAO from the Application component.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun sharedItemDao(): SharedItemDao
}
