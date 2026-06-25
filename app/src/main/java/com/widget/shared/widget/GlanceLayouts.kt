package com.widget.shared.widget

import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.widget.shared.data.local.SharedItemEntity
import com.widget.shared.ui.EditActivity

// ────────────────────────────────────────────────────────────────────────────
// Small  (approx 2×1 cells)
// Shows: note (1 line) + last-updated footer
// ────────────────────────────────────────────────────────────────────────────
@Composable
fun SmallWidgetLayout(item: SharedItemEntity?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFE4EF)))
            .clickable(actionStartActivity<EditActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❤️  ${item?.note?.take(40) ?: "Tap to share"}",
                style = TextStyle(fontSize = 12.sp),
                maxLines = 1
            )
            Text(
                text = timeAgo(item?.lastUpdated ?: 0L),
                style = TextStyle(fontSize = 9.sp),
                maxLines = 1
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Medium (approx 4×2 cells)
// Shows: note + pet emoji + footer
// ────────────────────────────────────────────────────────────────────────────
@Composable
fun MediumWidgetLayout(item: SharedItemEntity?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFE4EF)))
            .clickable(actionStartActivity<EditActivity>()),
        contentAlignment = Alignment.TopStart
    ) {
        Row(modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "❤️  ${item?.note?.take(80) ?: "Tap to share a thought"}",
                    style = TextStyle(fontSize = 13.sp),
                    maxLines = 2
                )
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = "🕒 ${timeAgo(item?.lastUpdated ?: 0L)}  •  👤 ${item?.updatedBy?.take(6) ?: "—"}",
                    style = TextStyle(fontSize = 9.sp),
                    maxLines = 1
                )
            }
            Spacer(GlanceModifier.width(8.dp))
            Text(text = petEmoji(item?.petState), style = TextStyle(fontSize = 28.sp))
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Large (approx 4×4 cells)
// Shows: picture thumbnail + note + pet + footer
// ────────────────────────────────────────────────────────────────────────────
@Composable
fun LargeWidgetLayout(item: SharedItemEntity?) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFE4EF)))
            .clickable(actionStartActivity<EditActivity>())
            .padding(12.dp)
    ) {
        // Picture placeholder (Glance doesn't support Coil directly — use ImageProvider)
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(120.dp)
                .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFF48FB1))),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📷", style = TextStyle(fontSize = 36.sp))
        }
        Spacer(GlanceModifier.height(8.dp))
        Text(
            text = "❤️  ${item?.note?.take(120) ?: "Tap to share a thought"}",
            style = TextStyle(fontSize = 14.sp),
            maxLines = 3
        )
        Spacer(GlanceModifier.defaultWeight())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = petEmoji(item?.petState), style = TextStyle(fontSize = 24.sp))
            Spacer(GlanceModifier.width(8.dp))
            Text(
                text = "🕒 ${timeAgo(item?.lastUpdated ?: 0L)}",
                style = TextStyle(fontSize = 10.sp)
            )
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
private fun petEmoji(state: String?) = when (state?.uppercase()) {
    "SLEEPY" -> "😴"
    "PLAYFUL" -> "🥳"
    else -> "😊"
}

private fun timeAgo(epochMillis: Long): String {
    if (epochMillis == 0L) return "never"
    val diff = System.currentTimeMillis() - epochMillis
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}
