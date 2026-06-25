package com.widget.shared.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widget.shared.data.local.SharedItemDao
import com.widget.shared.data.local.SharedItemEntity
import com.widget.shared.data.repository.SharedRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton service that keeps a Firestore real-time listener alive and
 * pushes updates to Glance whenever the shared document changes.
 */
@Singleton
class WidgetUpdateService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedRepo: SharedRepository,
    private val dao: SharedItemDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startListening() {
        sharedRepo.observeSharedItem()
            .onEach { item ->
                // 1. Persist to local Room cache
                dao.upsert(
                    SharedItemEntity(
                        note = item.note,
                        pictureUrl = item.pictureUrl,
                        petState = item.pet.state,
                        petEmotion = item.pet.emotion,
                        lastUpdated = item.lastUpdated?.toDate()?.time ?: 0L,
                        updatedBy = item.updatedBy
                    )
                )
                // 2. Trigger Glance widget refresh
                GlanceAppWidgetManager(context)
                    .getGlanceIds(SharedLoveGlanceWidget::class.java)
                    .forEach { id ->
                        SharedLoveGlanceWidget().update(context, id)
                    }
            }
            .launchIn(scope)
    }
}
