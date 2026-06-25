package com.lovewidget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Refreshes the Glance widget after device reboot so it still shows cached content.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                GlanceAppWidgetManager(context)
                    .getGlanceIds(SharedLoveGlanceWidget::class.java)
                    .forEach { id ->
                        SharedLoveGlanceWidget().update(context, id)
                    }
            }
        }
    }
}
