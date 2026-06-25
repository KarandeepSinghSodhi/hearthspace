package com.widget.shared

import android.app.Application
import com.widget.shared.widget.WidgetUpdateService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SharedLoveApp : Application() {
    @Inject lateinit var widgetUpdateService: WidgetUpdateService

    override fun onCreate() {
        super.onCreate()
        // Start the persistent Firestore listener that keeps the widget in sync
        widgetUpdateService.startListening()
    }
}
