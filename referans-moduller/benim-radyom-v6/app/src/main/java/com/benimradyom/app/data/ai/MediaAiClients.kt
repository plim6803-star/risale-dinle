package com.benimradyom.app.data.ai

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import com.benimradyom.app.data.model.AiSettings
import com.benimradyom.app.data.model.BedMuteCue
import com.benimradyom.app.data.model.BedMuteCueSource
import com.benimradyom.app.data.model.ContemplationImage
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TranscriptSegment
import com.benimradyom.app.data.model.TranscriptSource
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Kullanicinin acik eylemiyle ses kaydindan zaman kodlu metin cikarir.
 *
 * Servisin 25 MB istek siniri var. Daha buyuk kayitlar (tipik 30-60 dakikalik
 * Risale dersleri) yeniden kodlanmadan parcalara bolunur, her parca ayri
 * gonderilir ve zaman kodlari orijinal kayda gore kaydirilarak birlestirilir.
 */
class AudioTranscriptionClient(private val context: Context) {

    /**
     * @param onProgress (tamamlanan parca, toplam parca) - uzun kayitlarda
     *                   kullaniciya ilerleme gostermek icin.
     */
    fun transcribe(
        track: Track,
        settings: AiSettings,
        apiKey: String,
        onProgress: (done: Int, total: Int) -> Unit = { _, _ -> }
    ): List<TranscriptSegment> {
        require(apiKey.isNotBlank()) { "API anahtari gerekli" }
        val uri = Uri.parse(track.uri)
        val endpoint = AiHttp.endpoint(settings.endpoint, "audio/transcriptions")
        val size = runCatching {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
        }.getOrNull() ?: -1L

        // Boyut bilinmiyorsa ya da sinirin altindaysa tek istekte gonder.
        if (size in 0..MAX_UPLOAD_BYTES || size < 0L) {
            onProgress(0, 1)
            val raw = context.contentResolver.openInputStream(uri)?.use { input ->
                requestSegments(
                    endpoint, apiKey,
                    fileName = displayName(uri).ifBlank { safeFileName(track.title) + ".mp3" },
                    mime = context.contentResolver.getType(uri) ?: "audio/mpeg",
                    input = input
                )
            } ?: error("Ses dosyasi acilamadi")
            onProgress(1, 1)
            return toSegments(track, raw)
        }

        // Buyuk kayit: parcala, sirayla gonder, zamanlari kaydir.
        val workDir = File(context.cacheDir, "transkripsiyon-${track.id}")
        try {
            val chunks = AudioChunker(context).split(uri, CHUNK_BYTES, workDir)
            require(chunks.isNotEmpty()) { "Kayit parcalara bolunemedi" }
            val all = mutableListOf<RawSegment>()
            chunks.forEachIndexed { index, chunk ->
                onProgress(index, chunks.size)
                val part = chunk.file.inputStream().use { input ->
                    requestSegments(endpoint, apiKey, chunk.fileName, chunk.mime, input)
                }
                all += part.map {
                    it.copy(startMs = it.startMs + chunk.startMs, endMs = it.endMs + chunk.startMs)
                }
            }
            onProgress(chunks.size, chunks.size)
            return toSegments(track, all)
        } finally {
            workDir.deleteRecursively()
        }
    }

    private data class RawSegment(val startMs: Long, val endMs: Long, val text: String)

    private fun requestSegments(
        endpoint: String,
        apiKey: String,
        fileName: String,
        mime: String,
        input: java.io.InputStream
    ): List<RawSegment> {
        val response = AiHttp.postMultipart(endpoint, apiKey, "Transkripsiyon API", 180_000) {
            field("model", "whisper-1")
            field("response_format", "verbose_json")
            field("timestamp_granularities[]", "segment")
            field("language", "tr")
            field("prompt", TRANSCRIPTION_PROMPT)
            file("file", fileName, mime, input)
        }
        val segments = response.optJSONArray("segments") ?: error("API zaman kodu dondurmedi")
        return (0 until segments.length()).mapNotNull { i ->
            val item = segments.optJSONObject(i) ?: return@mapNotNull null
            val text = item.optString("text").trim()
            val start = (item.optDouble("start", -1.0) * 1_000.0).toLong()
            val end = (item.optDouble("end", -1.0) * 1_000.0).toLong()
            if (text.isBlank() || start < 0 || end <= start) null else RawSegment(start, end, text)
        }
    }

    private fun toSegments(track: Track, raw: List<RawSegment>): List<TranscriptSegment> =
        raw.sortedBy { it.startMs }.mapIndexed { index, s ->
            TranscriptSegment(
                trackId = track.id,
                startMs = s.startMs,
                endMs = s.endMs,
                text = s.text,
                source = TranscriptSource.AI.name,
                sortOrder = index
            )
        }.also { require(it.isNotEmpty()) { "Zaman kodlu metin olusturulamadi" } }

    private fun displayName(uri: Uri): String = runCatching {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else "" }.orEmpty()
    }.getOrDefault("")

    private fun safeFileName(value: String): String =
        value.replace(Regex("[^a-zA-Z0-9._-]+"), "_").take(80).ifBlank { "audio" }

    private companion object {
        /** Tek istekte gonderilebilecek en buyuk dosya (servis siniri 25 MB). */
        const val MAX_UPLOAD_BYTES = 24L * 1024L * 1024L
        /** Parcalama hedefi - ek yuk ve cok parcali basliklar icin pay birakir. */
        const val CHUNK_BYTES = 22L * 1024L * 1024L
        const val TRANSCRIPTION_PROMPT =
            "Risale-i Nur, Sozler, Mektubat, Lem'alar, Sualar, iman, tevhid, hasir, " +
                "Bediuzzaman Said Nursi. Turkce imla ve noktalama kullan. " +
                "Arapca okunan ayetleri tercume etmeden Arap alfabesiyle yaz."
    }
}

/**
 * Zaman kodlu metinde gercek Arapca ayet tilaveti bulunan bolgeleri secer.
 * API yalnizca segment kimliklerini dondurur; zamanlar uygulamada dogrulanir.
 */
class QuranCueAnalyzer {

    fun analyze(
        track: Track,
        segments: List<TranscriptSegment>,
        settings: AiSettings,
        apiKey: String
    ): List<BedMuteCue> {
        require(apiKey.isNotBlank()) { "API anahtari gerekli" }
        AiHttp.requireHttps(settings.endpoint)
        require(segments.isNotEmpty()) { "Once zaman kodlu metin olusturun" }

        val raw = mutableListOf<RawCue>()
        // Arap alfabesi acikca goruluyorsa API kararindan bagimsiz guvenli bir
        // yerel isaret de ekle. Latin harfli tilavetleri ise model ayirt eder.
        segments.forEach { segment ->
            if (containsArabicScript(segment.text)) {
                raw += RawCue(segment.startMs, segment.endMs, "Arapça ayet okunuşu", false)
            }
        }

        segments.chunked(MAX_SEGMENTS_PER_REQUEST).forEach { chunk ->
            raw += analyzeChunk(track, chunk, settings, apiKey)
        }

        val merged = mergeRaw(raw, gapMs = 2_000L)
        return mergeRaw(
            merged.map {
                RawCue(
                    startMs = (it.startMs - PRE_ROLL_MS).coerceAtLeast(0L),
                    endMs = (it.endMs + POST_ROLL_MS).let { end ->
                        if (track.durationMs > 0L) end.coerceAtMost(track.durationMs) else end
                    },
                    label = it.label,
                    fromAi = it.fromAi
                )
            },
            gapMs = 750L
        ).map {
            BedMuteCue(
                trackId = track.id,
                startMs = it.startMs,
                endMs = it.endMs,
                label = it.label.ifBlank { "Arapça ayet okunuşu" },
                source = if (it.fromAi) BedMuteCueSource.AI.name else BedMuteCueSource.YEREL.name
            )
        }
    }

    private fun analyzeChunk(
        track: Track,
        chunk: List<TranscriptSegment>,
        settings: AiSettings,
        apiKey: String
    ): List<RawCue> {
        val byId = chunk.mapIndexed { index, segment -> "s$index" to segment }.toMap()
        val inputSegments = JSONArray().apply {
            byId.forEach { (id, segment) ->
                put(JSONObject().apply {
                    put("id", id)
                    put("start_ms", segment.startMs)
                    put("end_ms", segment.endMs)
                    put("text", segment.text.take(700))
                })
            }
        }
        val rangeSchema = JSONObject().apply {
            put("type", "object")
            put("additionalProperties", false)
            put("properties", JSONObject().apply {
                put("from_id", JSONObject().put("type", "string"))
                put("to_id", JSONObject().put("type", "string"))
                put("label", JSONObject().put("type", "string"))
            })
            put("required", JSONArray().put("from_id").put("to_id").put("label"))
        }
        val schema = JSONObject().apply {
            put("type", "object")
            put("additionalProperties", false)
            put("properties", JSONObject().put("ranges", JSONObject().apply {
                put("type", "array")
                put("items", rangeSchema)
            }))
            put("required", JSONArray().put("ranges"))
        }
        val body = JSONObject().apply {
            put("model", settings.model.trim())
            put("store", false)
            put("max_output_tokens", 1_500)
            put(
                "instructions",
                "Sen bir dini ses yayini zaman cizelgesi editorusun. Yalnizca GERCEKTEN Arapca " +
                    "okunan Kur'an ayeti veya ayet tilaveti bolumlerini sec. Turkce meal, aciklama, " +
                    "ayet adinin anilmasi, dua veya normal Arapca terim tek basina secilmemeli. " +
                    "Arap harfleri yanlis taninmis veya Latin harfleriyle yazilmis tilaveti baglamdan " +
                    "ayirt edebilirsin. Emin degilsen bolumu secme. Yalnizca verilen segment kimliklerini kullan."
            )
            put("input", "Ses kaydi: ${track.title.take(180)}\nZaman kodlu segmentler:\n$inputSegments")
            put("text", JSONObject().put("format", JSONObject().apply {
                put("type", "json_schema")
                put("name", "quran_audio_ranges")
                put("strict", true)
                put("schema", schema)
            }))
        }

        val response = AiHttp.postJson(settings.endpoint, apiKey, body, "Ayet analizi API", 90_000)
        val result = JSONObject(AiHttp.stripCodeFence(AiHttp.extractOutputText(response)))
        val ranges = result.optJSONArray("ranges") ?: JSONArray()
        val ids = byId.keys.toList()
        return (0 until ranges.length()).mapNotNull { index ->
            val range = ranges.optJSONObject(index) ?: return@mapNotNull null
            val fromIndex = ids.indexOf(range.optString("from_id"))
            val toIndex = ids.indexOf(range.optString("to_id"))
            if (fromIndex < 0 || toIndex < fromIndex) return@mapNotNull null
            val selected = chunk.subList(fromIndex, toIndex + 1)
            RawCue(
                startMs = selected.first().startMs,
                endMs = selected.last().endMs,
                label = range.optString("label", "Arapça ayet okunuşu").take(100),
                fromAi = true
            )
        }
    }

    private fun containsArabicScript(text: String): Boolean =
        text.count { it.code in 0x0600..0x06FF || it.code in 0x0750..0x077F } >= 3

    private fun mergeRaw(items: List<RawCue>, gapMs: Long): List<RawCue> {
        val sorted = items.filter { it.endMs > it.startMs }.sortedBy { it.startMs }
        if (sorted.isEmpty()) return emptyList()
        val result = mutableListOf<RawCue>()
        var current = sorted.first()
        sorted.drop(1).forEach { next ->
            if (next.startMs <= current.endMs + gapMs) {
                current = current.copy(
                    endMs = maxOf(current.endMs, next.endMs),
                    label = current.label.ifBlank { next.label },
                    fromAi = current.fromAi || next.fromAi
                )
            } else {
                result += current
                current = next
            }
        }
        result += current
        return result
    }

    private data class RawCue(
        val startMs: Long,
        val endMs: Long,
        val label: String,
        val fromAi: Boolean
    )

    private companion object {
        const val MAX_SEGMENTS_PER_REQUEST = 120
        const val PRE_ROLL_MS = 2_500L
        const val POST_ROLL_MS = 1_200L
    }
}

/** Metin temasindan saygili, sembolik bir tefekkur gorseli uretip onbellekler. */
class ContemplationImageClient(private val context: Context) {

    fun generate(
        track: Track,
        segment: TranscriptSegment,
        settings: AiSettings,
        apiKey: String
    ): ContemplationImage {
        require(apiKey.isNotBlank()) { "API anahtari gerekli" }
        val prompt = buildPrompt(track, segment, settings.visualStyle)
        val body = JSONObject().apply {
            put("model", settings.imageModel)
            put("prompt", prompt)
            put("size", "1024x1536")
            put("quality", settings.imageQuality)
            put("output_format", "jpeg")
            put("n", 1)
        }
        val response = AiHttp.postJson(
            AiHttp.endpoint(settings.endpoint, "images/generations"),
            apiKey, body, "Gorsel API", 180_000
        )
        val encoded = response.optJSONArray("data")
            ?.optJSONObject(0)?.optString("b64_json")
            ?.takeIf { it.isNotBlank() }
            ?: error("API gorsel verisi dondurmedi")
        val directory = File(context.filesDir, "contemplation").apply { mkdirs() }
        val file = File(directory, "track-${track.id}-at-${segment.startMs}.jpg")
        file.writeBytes(Base64.decode(encoded, Base64.DEFAULT))
        return ContemplationImage(
            trackId = track.id,
            startMs = segment.startMs,
            endMs = segment.endMs,
            segmentId = segment.id,
            prompt = prompt,
            localPath = file.absolutePath
        )
    }

    private fun buildPrompt(track: Track, segment: TranscriptSegment, style: String): String =
        """
        Create a respectful vertical contemplative visual inspired by the THEMES of the Turkish passage below.
        Style: $style.
        Use nature, sky, light, water, trees, stars, architecture, Islamic geometric patterns, and symbolic composition when relevant.
        Do not depict Allah or any deity. Do not depict prophets, companions, angels, jinn, heaven, or hell as people or literal beings.
        No faces, no gore, no fear imagery, no readable text, no Arabic calligraphy, no Qur'an pages, and no invented religious symbols.
        The image must support quiet reflection rather than claim to illustrate unseen realities.
        Audio title: ${track.title.take(160)}
        Passage: ${segment.text.take(1_400)}
        """.trimIndent()
}
