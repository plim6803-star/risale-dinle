package com.benimradyom.app.engine

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Uygulama ici ses kaydi - motivasyon sozunu kendi sesinizle 30 saniyede
 * kaydedip anons havuzuna atabilmek icin.
 *
 * Kayitlar uygulamanin kendi klasorune yazilir; ekstra depolama izni gerekmez
 * ve uygulama silinince onlar da gider.
 */
class VoiceRecorder(private val ctx: Context) {

    private var recorder: MediaRecorder? = null
    private var currentFile: File? = null
    private var startedAt = 0L

    val isRecording: Boolean get() = recorder != null

    /** Kayitlarin tutuldugu klasor. */
    private val dir: File
        get() = File(ctx.filesDir, "anonslar").apply { if (!exists()) mkdirs() }

    fun start(): Boolean {
        if (isRecording) return false
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "anons_$stamp.m4a")

        val rec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(ctx)
        else @Suppress("DEPRECATION") MediaRecorder()

        return runCatching {
            rec.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(96_000)
                setAudioSamplingRate(44_100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            recorder = rec
            currentFile = file
            startedAt = System.currentTimeMillis()
            true
        }.getOrElse {
            runCatching { rec.release() }
            recorder = null
            currentFile = null
            false
        }
    }

    /** Kaydi bitirir; dosya URI'si ve suresini dondurur. */
    fun stop(): Pair<Uri, Long>? {
        val rec = recorder ?: return null
        val file = currentFile
        recorder = null
        currentFile = null

        runCatching { rec.stop() }
        runCatching { rec.release() }

        if (file == null || !file.exists() || file.length() < 1024L) {
            file?.delete()
            return null
        }
        return file.toUri() to durationOf(file)
    }

    fun cancel() {
        val rec = recorder ?: return
        recorder = null
        runCatching { rec.stop() }
        runCatching { rec.release() }
        currentFile?.delete()
        currentFile = null
    }

    val elapsedMs: Long
        get() = if (isRecording) System.currentTimeMillis() - startedAt else 0L

    private fun durationOf(file: File): Long {
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(file.absolutePath)
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            runCatching { mmr.release() }
        }
    }

    /** Anons silinince ses dosyasini da temizle. */
    fun deleteIfOwned(uriString: String?) {
        val path = uriString?.toUri()?.path ?: return
        val file = File(path)
        if (file.exists() && file.parentFile?.name == "anonslar") file.delete()
    }
}
