package com.benimradyom.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.benimradyom.app.MainActivity
import com.benimradyom.app.R
import com.benimradyom.app.service.RadioService

/** Ana ekran widget'i: calan parca + oynat/durdur/sonraki. */
class RadioWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        manager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            manager.updateAppWidget(id, build(context, null, null, false))
        }
    }

    companion object {
        /** Servis her durum degisiminde burayi cagirir. */
        fun push(ctx: Context, title: String, subtitle: String, playing: Boolean) {
            val manager = AppWidgetManager.getInstance(ctx)
            val ids = manager.getAppWidgetIds(ComponentName(ctx, RadioWidget::class.java))
            if (ids.isEmpty()) return
            val views = build(ctx, title, subtitle, playing)
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun build(
            ctx: Context,
            title: String?,
            subtitle: String?,
            playing: Boolean
        ): RemoteViews = RemoteViews(ctx.packageName, R.layout.widget_radio).apply {
            setTextViewText(R.id.widget_title, title?.takeIf { it.isNotBlank() } ?: "Benim Radyom")
            setTextViewText(
                R.id.widget_subtitle,
                subtitle?.takeIf { it.isNotBlank() } ?: if (playing) "Yayinda" else "Hazir"
            )
            setImageViewResource(
                R.id.widget_play,
                if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )

            setOnClickPendingIntent(R.id.widget_root, activityPi(ctx))
            setOnClickPendingIntent(
                R.id.widget_play,
                servicePi(ctx, RadioService.ACTION_PLAY_PAUSE, 101)
            )
            setOnClickPendingIntent(
                R.id.widget_next,
                servicePi(ctx, RadioService.ACTION_NEXT, 102)
            )
            setOnClickPendingIntent(
                R.id.widget_stop,
                servicePi(ctx, RadioService.ACTION_STOP, 103)
            )
        }

        private fun activityPi(ctx: Context): PendingIntent = PendingIntent.getActivity(
            ctx, 100,
            Intent(ctx, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        private fun servicePi(ctx: Context, action: String, rc: Int): PendingIntent =
            PendingIntent.getForegroundService(
                ctx, rc,
                Intent(ctx, RadioService::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    }
}
