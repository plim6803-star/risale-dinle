package com.benimradyom.app.data.media

import com.benimradyom.app.data.model.TranscriptSegment
import com.benimradyom.app.data.model.TranscriptSource

/** SRT/VTT zaman kodlarini veya duz metni yayin takip bolumlerine donusturur. */
object TranscriptParser {

    fun parse(
        fileName: String,
        content: String,
        trackId: Long,
        durationMs: Long
    ): List<TranscriptSegment> {
        val clean = content.removePrefix("\uFEFF").replace("\r\n", "\n").replace('\r', '\n')
        val lower = fileName.lowercase()
        return when {
            lower.endsWith(".srt") -> parseTimed(clean, trackId, TranscriptSource.SRT)
            lower.endsWith(".vtt") || clean.trimStart().startsWith("WEBVTT") ->
                parseTimed(clean, trackId, TranscriptSource.VTT)
            else -> parsePlainText(clean, trackId, durationMs)
        }
    }

    private fun parseTimed(
        content: String,
        trackId: Long,
        source: TranscriptSource
    ): List<TranscriptSegment> {
        val lines = content.lines()
        val result = mutableListOf<TranscriptSegment>()
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (!line.contains("-->")) { i++; continue }
            val parts = line.split("-->", limit = 2)
            val start = parseTime(parts.getOrNull(0).orEmpty().trim())
            val endToken = parts.getOrNull(1).orEmpty().trim().substringBefore(' ')
            val end = parseTime(endToken)
            i++
            val textLines = mutableListOf<String>()
            while (i < lines.size) {
                val current = lines[i].trim()
                if (current.contains("-->")) break
                if (current.isBlank()) { i++; break }
                if (!current.startsWith("NOTE")) textLines += current
                i++
            }
            val text = textLines.joinToString(" ")
                .replace(Regex("<[^>]+>"), "")
                .replace(Regex("\\s+"), " ")
                .trim()
            if (start != null && end != null && end > start && text.isNotBlank()) {
                result += TranscriptSegment(
                    trackId = trackId,
                    startMs = start,
                    endMs = end,
                    text = text,
                    source = source.name,
                    sortOrder = result.size
                )
            }
        }
        require(result.isNotEmpty()) { "Dosyada okunabilir zaman kodu bulunamadi" }
        return result
    }

    private fun parsePlainText(
        content: String,
        trackId: Long,
        durationMs: Long
    ): List<TranscriptSegment> {
        val paragraphs = content.split(Regex("\\n\\s*\\n"))
            .flatMap(::splitLongParagraph)
            .map { it.replace(Regex("\\s+"), " ").trim() }
            .filter { it.isNotBlank() }
        require(paragraphs.isNotEmpty()) { "Metin dosyasi bos" }

        val words = paragraphs.map { it.split(Regex("\\s+")).size.coerceAtLeast(1) }
        val totalWords = words.sum().coerceAtLeast(1)
        val totalDuration = durationMs.takeIf { it > 0L }
            ?: (paragraphs.size * DEFAULT_PARAGRAPH_MS)
        var elapsedWords = 0
        return paragraphs.mapIndexed { index, paragraph ->
            val start = totalDuration * elapsedWords / totalWords
            elapsedWords += words[index]
            val end = if (index == paragraphs.lastIndex) totalDuration
            else totalDuration * elapsedWords / totalWords
            TranscriptSegment(
                trackId = trackId,
                startMs = start,
                endMs = end.coerceAtLeast(start + 1_000L),
                text = paragraph,
                source = TranscriptSource.TXT_APPROX.name,
                sortOrder = index
            )
        }
    }

    private fun splitLongParagraph(value: String): List<String> {
        val clean = value.trim()
        if (clean.length <= MAX_CHARS) return listOf(clean)
        val sentences = clean.split(Regex("(?<=[.!?…])\\s+"))
        val out = mutableListOf<String>()
        var current = StringBuilder()
        sentences.forEach { sentence ->
            if (current.isNotEmpty() && current.length + sentence.length + 1 > MAX_CHARS) {
                out += current.toString()
                current = StringBuilder()
            }
            if (current.isNotEmpty()) current.append(' ')
            current.append(sentence)
        }
        if (current.isNotEmpty()) out += current.toString()
        return out.ifEmpty { clean.chunked(MAX_CHARS) }
    }

    private fun parseTime(value: String): Long? = runCatching {
        val normalized = value.replace(',', '.')
        val pieces = normalized.split(':')
        val secondsToken = pieces.last()
        val seconds = secondsToken.substringBefore('.').toLong()
        val millis = secondsToken.substringAfter('.', "0").padEnd(3, '0').take(3).toLong()
        val minutes = pieces.getOrNull(pieces.size - 2)?.toLong() ?: 0L
        val hours = pieces.getOrNull(pieces.size - 3)?.toLong() ?: 0L
        ((hours * 3600L + minutes * 60L + seconds) * 1_000L) + millis
    }.getOrNull()

    private const val MAX_CHARS = 360
    private const val DEFAULT_PARAGRAPH_MS = 25_000L
}
