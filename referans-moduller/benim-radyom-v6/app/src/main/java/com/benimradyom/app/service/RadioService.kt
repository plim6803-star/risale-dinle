package com.benimradyom.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.benimradyom.app.MainActivity
import com.benimradyom.app.R
import com.benimradyom.app.engine.RadioEngineHolder
import com.benimradyom.app.engine.RadioEngine
import com.benimradyom.app.engine.RadioUiState
import com.benimradyom.app.widget.RadioWidget

/**
 * Radyo servisi. Motoru burada tutar ki arayuz kapansa bile yayin devam etsin.
 *
 * Eski surumde motor ViewModel'in icindeydi ve servis manifeste hic yazilmamisti;
 * bu yuzden uygulama hicbir sey calamiyordu.
 */
@OptIn(UnstableApi::class)
class RadioService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    lateinit var engine: RadioEngine
        private set

    companion object {
        const val CHANNEL_ID = "benim_radyom_playback"
        const val NOTIF_ID = 4201

        const val ACTION_PLAY_PAUSE = "com.benimradyom.app.PLAY_PAUSE"
        const val ACTION_NEXT = "com.benimradyom.app.NEXT"
        const val ACTION_PREV = "com.benimradyom.app.PREV"
        const val ACTION_STOP = "com.benimradyom.app.STOP"
        const val ACTION_START = "com.benimradyom.app.START"

        @Volatile var instance: RadioService? = null
            private set

        /** Servisi baslatir (gerekiyorsa) ve verilen komutu gonderir. */
        fun send(ctx: Context, action: String) = launch(ctx, action)

        fun ensureRunning(ctx: Context) = launch(ctx, null)

        private fun launch(ctx: Context, action: String?) {
            val i = Intent(ctx, RadioService::class.java)
            if (action != null) i.action = action
            // Android 12+ uygulama arka plandayken on plan servisi baslatmayi
            // engelleyebilir (zamanlayici tetiklemesi gibi). Cokmek yerine
            // servisin zaten ayakta olmasina guveniyoruz.
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ctx.startForegroundService(i)
                else ctx.startService(i)
            }.onFailure {
                runCatching { ctx.startService(i) }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createChannel()

        engine = RadioEngineHolder.get(applicationContext)
        engine.onStateChanged = { state -> onEngineState(state) }

        mediaSession = MediaSession.Builder(this, engine.player).build()

        // Capraz gecişte motor calari degistirir; medya oturumu da onu izlemeli,
        // yoksa kilit ekraninda eski parca gorunur.
        engine.onPlayerChanged = { player -> mediaSession?.player = player }

        // Android 12+ foreground servis icin bildirimi hemen yayinlamak zorunlu.
        startForegroundCompat(buildNotification(null))
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    /**
     * Media3 varsayilan olarak KENDI bildirimini yayinlar; boylece ekranda iki
     * bildirim birden cikar. Burayi ezip sadece bizimkini gosteriyoruz.
     */
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        startForegroundCompat(buildNotification(engine.state.value))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> engine.start()
            ACTION_PLAY_PAUSE -> engine.pauseResume()
            ACTION_NEXT -> engine.next()
            ACTION_PREV -> engine.previous()
            ACTION_STOP -> {
                engine.stop()
                stopForegroundCompat()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun onEngineState(state: RadioUiState) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(state))
        RadioWidget.push(
            this,
            state.current?.title.orEmpty(),
            state.current?.subtitle.orEmpty(),
            state.isPlaying
        )
    }

    private fun startForegroundCompat(n: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this, NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIF_ID, n)
        }
    }

    private fun stopForegroundCompat() =
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)

    private fun buildNotification(state: RadioUiState?): Notification {
        val playing = state?.isPlaying == true
        val title = state?.current?.title?.takeIf { it.isNotBlank() } ?: getString(R.string.app_name)
        val text = state?.current?.subtitle?.takeIf { it.isNotBlank() }
            ?: if (playing) "Yayinda" else "Hazir"

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_stat_radio)
            .setContentIntent(openIntent)
            .setOngoing(playing)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                android.R.drawable.ic_media_previous, "Onceki", servicePi(ACTION_PREV, 11)
            )
            .addAction(
                if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (playing) "Duraklat" else "Oynat",
                servicePi(ACTION_PLAY_PAUSE, 12)
            )
            .addAction(
                android.R.drawable.ic_media_next, "Sonraki", servicePi(ACTION_NEXT, 13)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel, "Durdur", servicePi(ACTION_STOP, 14)
            )
            .build()
    }

    private fun servicePi(action: String, requestCode: Int): PendingIntent =
        PendingIntent.getService(
            this, requestCode,
            Intent(this, RadioService::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID, "Radyo oynatici", NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Calan yayin ve kontroller"
            setShowBadge(false)
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Uygulama kapatilsa da yayin sursun; sadece calmiyorsa servisi birak.
        if (!engine.state.value.isPlaying) {
            stopForegroundCompat()
            stopSelf()
        }
    }

    override fun onDestroy() {
        instance = null
        mediaSession?.release()
        mediaSession = null
        // Motor uygulama omru boyunca yasar; burada sadece baglantiyi kes.
        engine.onStateChanged = null
        engine.onPlayerChanged = null
        super.onDestroy()
    }
}
