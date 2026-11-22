package com.benimradyom.app.data.ai

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * Uzun ses kayitlarini transkripsiyon API sinirinin altindaki parcalara boler.
 *
 * Neden: servis istek basina 25 MB kabul ediyor. 45 dakikalik bir Risale dersi
 * 128 kbps'de ~40 MB tutar; bolmeden ayet-fon ozelligi asil kullanimda calismaz.
 *
 * Yeniden kodlama YAPILMAZ (kalite kaybi yok, hizli):
 *  - MP3  : cerceveler oldugu gibi yeni dosyaya yazilir. MP3 bagimsiz
 *           cercevelerden olustugu icin her parca kendi basina gecerli bir MP3'tur.
 *  - AAC  : (m4a / mp4) ornekler MediaMuxer ile yeni bir m4a kabina yazilir.
 * Diger bicimler (wav, ogg, flac) bolunemez; sinirin altindaysa oldugu gibi gider.
 */
internal class AudioChunker(private val context: Context) {

    data class Chunk(
        val file: File,
        /** Parcanin orijinal kayittaki baslangic ani. Zaman kodlari buna eklenir. */
        val startMs: Long,
        val mime: String,
        val fileName: String
    )

    /**
     * [uri] kaydini en fazla [maxBytes] boyutlu parcalara boler ve [workDir]
     * altina yazar. Cagiran taraf is bitince parcalari silmelidir.
     */
    fun split(uri: Uri, maxBytes: Long, workDir: File): List<Chunk> {
        workDir.mkdirs()
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(context, uri, null)
            val trackIndex = (0 until extractor.trackCount).firstOrNull { i ->
                extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME)
                    ?.startsWith("audio/") == true
            } ?: error("Kayitta ses izi bulunamadi")
            val format = extractor.getTrackFormat(trackIndex)
            val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()
            extractor.selectTrack(trackIndex)

            return when (mime) {
                MediaFormat.MIMETYPE_AUDIO_MPEG -> splitMp3(extractor, maxBytes, workDir)
                MediaFormat.MIMETYPE_AUDIO_AAC -> splitAac(extractor, format, maxBytes, workDir)
                else -> error(
                    "Bu ses bicimi ($mime) parcalara bolunemiyor. " +
                        "Kaydi MP3 veya M4A'ya donusturun ya da SRT/VTT aktarin."
                )
            }
        } finally {
            extractor.release()
        }
    }

    private fun splitMp3(extractor: MediaExtractor, maxBytes: Long, dir: File): List<Chunk> {
        val chunks = mutableListOf<Chunk>()
        val buffer = ByteBuffer.allocateDirect(BUFFER_BYTES)
        val bytes = ByteArray(BUFFER_BYTES)
        var out: FileOutputStream? = null
        var written = 0L

        fun startChunk(atMs: Long) {
            out?.close()
            val file = File(dir, "parca-${chunks.size + 1}.mp3")
            chunks += Chunk(file, atMs, "audio/mpeg", file.name)
            out = FileOutputStream(file)
            written = 0L
        }

        try {
            while (true) {
                buffer.clear()
                val size = extractor.readSampleData(buffer, 0)
                if (size < 0) break
                val timeMs = extractor.sampleTime / 1_000L
                if (out == null || written + size > maxBytes) startChunk(timeMs)
                // readSampleData sonrasi konum/sinir Android surumune gore degisebilir;
                // ona guvenme, acikca ayarla.
                buffer.position(0)
                buffer.limit(size)
                buffer.get(bytes, 0, size)
                out!!.write(bytes, 0, size)
                written += size
                extractor.advance()
            }
        } finally {
            out?.close()
        }
        return chunks.filter { it.file.length() > 0 }
    }

    private fun splitAac(
        extractor: MediaExtractor,
        format: MediaFormat,
        maxBytes: Long,
        dir: File
    ): List<Chunk> {
        val chunks = mutableListOf<Chunk>()
        val buffer = ByteBuffer.allocateDirect(BUFFER_BYTES)
        val info = MediaCodec.BufferInfo()
        var muxer: MediaMuxer? = null
        var muxTrack = -1
        var written = 0L
        var samplesInChunk = 0
        var chunkStartUs = 0L

        fun finishChunk() {
            val m = muxer ?: return
            // Bos bir muxer'i durdurmak istisna atar.
            if (samplesInChunk > 0) runCatching { m.stop() }
            runCatching { m.release() }
            muxer = null
        }

        fun startChunk(atUs: Long) {
            finishChunk()
            val file = File(dir, "parca-${chunks.size + 1}.m4a")
            chunks += Chunk(file, atUs / 1_000L, "audio/mp4", file.name)
            val m = MediaMuxer(file.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            muxTrack = m.addTrack(format)
            m.start()
            muxer = m
            written = 0L
            samplesInChunk = 0
            chunkStartUs = atUs
        }

        try {
            while (true) {
                buffer.clear()
                val size = extractor.readSampleData(buffer, 0)
                if (size < 0) break
                val timeUs = extractor.sampleTime
                // Kap (moov) ek yuku icin sinirin biraz altinda kes.
                if (muxer == null || written + size > maxBytes - CONTAINER_OVERHEAD) startChunk(timeUs)
                buffer.position(0)
                buffer.limit(size)
                info.set(
                    0, size,
                    (timeUs - chunkStartUs).coerceAtLeast(0L),
                    MediaCodec.BUFFER_FLAG_KEY_FRAME
                )
                muxer!!.writeSampleData(muxTrack, buffer, info)
                written += size
                samplesInChunk++
                extractor.advance()
            }
        } finally {
            finishChunk()
        }
        return chunks.filter { it.file.length() > 0 }
    }

    private companion object {
        const val BUFFER_BYTES = 512 * 1024
        const val CONTAINER_OVERHEAD = 1L * 1024L * 1024L
    }
}
