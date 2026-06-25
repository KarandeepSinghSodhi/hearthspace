package com.lovewidget.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.lovewidget.BuildConfig
import kotlinx.coroutines.tasks.await

object UpdateChecker {
    private const val KEY_LATEST_VERSION_CODE = "latest_version_code"
    private const val RELEASES_URL = "https://github.com/YOUR_USERNAME/shared-love-widget/releases/latest"

    suspend fun checkForUpdate(context: Context) {
        try {
            val config = FirebaseRemoteConfig.getInstance()
            config.fetchAndActivate().await()
            val latest = config[KEY_LATEST_VERSION_CODE].asLong()
            val current = BuildConfig.VERSION_CODE.toLong()
            if (latest > current) {
                Toast.makeText(
                    context,
                    "New version available! Visit GitHub Releases to download.",
                    Toast.LENGTH_LONG
                ).show()
                // In a real app open the URL in a browser
            }
        } catch (_: Exception) { /* Silent fail — no internet or RemoteConfig issue */ }
    }
}
