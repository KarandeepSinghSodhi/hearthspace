package com.widget.shared.widget

import android.content.Context
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.widget.shared.data.local.SharedItemDao
import com.widget.shared.data.local.SharedItemEntity
import com.widget.shared.ui.EditActivity
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class SharedLoveGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Retrieve DAO via Hilt entry point (Glance doesn't support injection directly)
        val dao = getDaoFromEntryPoint(context)
        val item: SharedItemEntity? = dao?.observe()?.first()

        provideContent {
            val sizeMode = LocalSize.current
            when {
                sizeMode.width < 200.dp -> SmallWidgetLayout(item)
                sizeMode.width < 350.dp -> MediumWidgetLayout(item)
                else -> LargeWidgetLayout(item)
            }
        }
    }

    private fun getDaoFromEntryPoint(context: Context): SharedItemDao? {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java
            )
            entryPoint.sharedItemDao()
        } catch (e: Exception) {
            null
        }
    }
}

class SharedLoveWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SharedLoveGlanceWidget()
}
