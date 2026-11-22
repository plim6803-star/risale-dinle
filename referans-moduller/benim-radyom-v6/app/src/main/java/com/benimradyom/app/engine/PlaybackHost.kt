package com.benimradyom.app.engine

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Ses katmani: iki ana calar + bir fon calar.
 *
 *  - [primary] / [secondary] : sirayla kullanilir. Capraz gecis (crossfade)
 *    acikken biri kisilirken digeri acilir; kapaliyken tek calar kullanilir.
 *  - [bed] : konusmanin ALTINDA donen fon muzik. Ses odagi istemez, yoksa ana
 *    calarla kavga eder.
 */
@OptIn(UnstableApi::class)
class PlaybackHost(
    ctx: Context,
    private val scope: CoroutineScope
) {
    private val musicAttrs = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    private fun newPlayer(ctx: Context, handleFocus: Boolean): ExoPlayer =
        ExoPlayer.Builder(ctx)
            .setAudioAttributes(musicAttrs, handleFocus)
            .setHandleAudioBecomingNoisy(handleFocus)
            .build()
            .apply { repeatMode = Player.REPEAT_MODE_OFF }

    private val playerA: ExoPlayer = newPlayer(ctx, handleFocus = true)
    private val playerB: ExoPlayer = newPlayer(ctx, handleFocus = true)

    private var usingA = true

    /** Su an calan ana calar. */
    val main: ExoPlayer get() = if (usingA) playerA else playerB

    /** Sirada kullanilacak ana calar. */
    private val standby: ExoPlayer get() = if (usingA) playerB else playerA

    /** Fon calar. */
    val bed: ExoPlayer = newPlayer(ctx, handleFocus = false).apply {
        repeatMode = Player.REPEAT_MODE_ALL
        volume = 0f
    }

    private var fadeJob: Job? = null
    private var crossJob: Job? = null
    private var bedFadeJob: Job? = null

    /** Programdan gelen ana ses seviyesi (0..1). */
    private var targetVolume = 1f

    /** Arayuze bildirilecek calar degisimi. */
    var onPlayerSwapped: ((ExoPlayer) -> Unit)? = null

    // ── Ana calar ───────────────────────────────────────────────────────────

    /**
     * Verilen sesi calar.
     *
     * @param volume       bu parca icin hedef ses (liste basina ayar * ana ses)
     * @param crossfadeMs  0'dan buyukse ust uste binen gecis yapilir
     */
    fun play(
        uri: String,
        title: String,
        artist: String,
        startPositionMs: Long = 0L,
        fadeMs: Int = 0,
        volume: Float = 1f,
        crossfadeMs: Int = 0
    ) {
        targetVolume = volume.coerceIn(0f, 1f)
        val item = MediaItem.Builder()
            .setUri(Uri.parse(uri))
            .setMediaMetadata(MediaMetadata.Builder().setTitle(title).setArtist(artist).build())
            .build()

        if (crossfadeMs > 0 && main.isPlaying) {
            crossfadeTo(item, startPositionMs, crossfadeMs)
            return
        }

        fadeJob?.cancel()
        crossJob?.cancel()
        standby.stop()
        main.setMediaItem(item)
        if (startPositionMs > 0L) main.seekTo(startPositionMs)
        main.prepare()

        if (fadeMs > 0) {
            main.volume = 0f
            main.play()
            fadeTo(main, targetVolume, fadeMs)
        } else {
            main.volume = targetVolume
            main.play()
        }
    }

    /** Eski parca kisilirken yeni parca acilir - gercek capraz gecis. */
    private fun crossfadeTo(item: MediaItem, startPositionMs: Long, durationMs: Int) {
        val outgoing = main
        val incoming = standby

        incoming.setMediaItem(item)
        if (startPositionMs > 0L) incoming.seekTo(startPositionMs)
        incoming.prepare()
        incoming.volume = 0f
        incoming.play()

        usingA = !usingA
        onPlayerSwapped?.invoke(incoming)

        crossJob?.cancel()
        crossJob = scope.launch {
            val steps = (durationMs / STEP_MS).coerceAtLeast(1)
            val from = outgoing.volume
            for (i in 1..steps) {
                if (!isActive) return@launch
                val t = i.toFloat() / steps
                outgoing.volume = from * (1f - t)
                incoming.volume = targetVolume * t
                delay(STEP_MS.toLong())
            }
            outgoing.stop()
            outgoing.clearMediaItems()
            outgoing.volume = 0f
            incoming.volume = targetVolume
        }
    }

    fun pause() = main.pause()
    fun resume() = main.play()

    fun stop() {
        fadeJob?.cancel()
        crossJob?.cancel()
        playerA.stop(); playerA.clearMediaItems()
        playerB.stop(); playerB.clearMediaItems()
        stopBed()
    }

    /** Bitmeden once sesi kisarak yumusak cikis yapar. */
    fun fadeOutThen(durationMs: Int, action: () -> Unit) {
        if (durationMs <= 0) { action(); return }
        fadeTo(main, 0f, durationMs) { action() }
    }

    /** Kullanicinin / programin ses seviyesi tercihi. */
    fun setVolume(v: Float) {
        targetVolume = v.coerceIn(0f, 1f)
        if (fadeJob?.isActive != true && crossJob?.isActive != true) main.volume = targetVolume
    }

    val currentVolume: Float get() = targetVolume

    private fun fadeTo(player: ExoPlayer, to: Float, durationMs: Int, onEnd: (() -> Unit)? = null) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val from = player.volume
            val steps = (durationMs / STEP_MS).coerceAtLeast(1)
            for (i in 1..steps) {
                if (!isActive) return@launch
                player.volume = from + (to - from) * (i.toFloat() / steps)
                delay(STEP_MS.toLong())
            }
            player.volume = to
            onEnd?.invoke()
        }
    }

    // ── Fon muzik (ducking) ─────────────────────────────────────────────────

    fun loadBed(uris: List<String>) {
        bed.clearMediaItems()
        if (uris.isEmpty()) return
        bed.setMediaItems(uris.map { MediaItem.fromUri(Uri.parse(it)) })
        bed.shuffleModeEnabled = true
        bed.prepare()
    }

    val hasBed: Boolean get() = bed.mediaItemCount > 0

    fun bedIn(volume: Float, fadeMs: Int = 800) {
        if (!hasBed) return
        if (!bed.isPlaying) { bed.volume = 0f; bed.play() }
        fadeBed(volume.coerceIn(0f, 1f), fadeMs)
    }

    fun bedOut(fadeMs: Int = 800) {
        if (!hasBed || !bed.isPlaying) return
        fadeBed(0f, fadeMs) { bed.pause() }
    }

    private fun stopBed() {
        bedFadeJob?.cancel()
        bed.pause()
        bed.volume = 0f
    }

    private fun fadeBed(to: Float, durationMs: Int, onEnd: (() -> Unit)? = null) {
        bedFadeJob?.cancel()
        bedFadeJob = scope.launch {
            val from = bed.volume
            val steps = (durationMs / STEP_MS).coerceAtLeast(1)
            for (i in 1..steps) {
                if (!isActive) return@launch
                bed.volume = from + (to - from) * (i.toFloat() / steps)
                delay(STEP_MS.toLong())
            }
            bed.volume = to
            onEnd?.invoke()
        }
    }

    fun release() {
        fadeJob?.cancel()
        crossJob?.cancel()
        bedFadeJob?.cancel()
        playerA.release()
        playerB.release()
        bed.release()
    }

    private companion object {
        const val STEP_MS = 40
    }
}
