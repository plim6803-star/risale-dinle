package com.benimradyom.app.data.ai

import com.benimradyom.app.data.model.AiSettings
import com.benimradyom.app.data.model.FlowEntryKind
import com.benimradyom.app.data.model.KnowledgeItem
import com.benimradyom.app.data.model.KnowledgeType
import com.benimradyom.app.data.model.SmartFlowEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.Normalizer

data class PlanResult(
    val name: String,
    val entries: List<SmartFlowEntry>,
    val usedAi: Boolean,
    val note: String,
    /** Planin tahmini toplam suresi. */
    val totalMs: Long = 0L
)

/**
 * Once yerel katalogda adaylari bulur. API aciksa yalnizca bu adaylarin
 * kimliklerini modele gonderir ve donen kimlikleri yerelde dogrular. Model
 * katalogda olmayan bir kaynak UYDURAMAZ.
 *
 * Hedef sure gercek surelerle tutturulur: Risale kartlari eslestirildigi ses
 * kaydinin suresini, ayet/hadis kartlari okunacak metnin tahmini seslendirme
 * suresini kullanir.
 */
class SmartFlowPlanner {

    /**
     * @param trackDurations parca kimligi -> sure (ms). Risale kartlarinin
     *                       gercek suresi buradan okunur.
     */
    suspend fun plan(
        request: String,
        targetMinutes: Int,
        catalog: List<KnowledgeItem>,
        trackDurations: Map<Long, Long>,
        settings: AiSettings,
        apiKey: String
    ): PlanResult = withContext(Dispatchers.IO) {
        val targetMs = targetMinutes.coerceIn(5, 600) * 60_000L
        val duration: (KnowledgeItem) -> Long = { estimateMs(it, trackDurations) }
        val enabled = catalog.filter { it.isEnabled }
        val ranked = rank(request, enabled).take(MAX_CANDIDATES)
        if (ranked.isEmpty()) {
            return@withContext PlanResult(
                "Akilli yayin", emptyList(), false,
                "Katalogda kullanilabilecek kaynak bulunamadi."
            )
        }

        if (settings.enabled && apiKey.isNotBlank()) {
            val ai = runCatching { requestAi(request, targetMs, ranked, duration, settings, apiKey) }
            ai.getOrNull()?.let { return@withContext it }
            val fallback = localPlan(request, targetMs, ranked, duration)
            return@withContext fallback.copy(
                note = "API yanit veremedi; guvenli yerel plan olusturuldu. " +
                    (ai.exceptionOrNull()?.message ?: "Bilinmeyen hata") + " " + durationNote(fallback.totalMs)
            )
        }
        localPlan(request, targetMs, ranked, duration)
    }

    private fun requestAi(
        request: String,
        targetMs: Long,
        candidates: List<KnowledgeItem>,
        duration: (KnowledgeItem) -> Long,
        settings: AiSettings,
        apiKey: String
    ): PlanResult {
        val candidateArray = JSONArray().apply {
            candidates.forEach { item ->
                put(JSONObject().apply {
                    put("id", "k_${item.id}")
                    put("type", item.type)
                    put("title", item.title)
                    put("reference", item.reference)
                    put("topics", JSONArray(item.topics))
                    put("summary", item.summary)
                    put("ready", item.isReady)
                    put("minutes", (duration(item) / 60_000.0).let { Math.round(it * 10) / 10.0 })
                })
            }
        }

        val input = buildString {
            append("Kullanicinin istegi: ").append(request.trim()).append('\n')
            append("Hedef sure: ").append(targetMs / 60_000L).append(" dakika. ")
            append("Her adayin 'minutes' alani suresidir; toplam hedefe yakin olsun.\n")
            append("Aday katalog (yalnizca bu kimlikleri kullan):\n")
            append(candidateArray.toString())
            append("\nHazir olmayan kartlari ancak konu icin cok gerekliyse sec; editor bunlari kapali gosterecek.")
        }

        val schema = JSONObject().apply {
            put("type", "object")
            put("additionalProperties", false)
            put("properties", JSONObject().apply {
                put("name", JSONObject().put("type", "string"))
                put("item_ids", JSONObject().apply {
                    put("type", "array")
                    put("items", JSONObject().put("type", "string"))
                })
            })
            put("required", JSONArray().put("name").put("item_ids"))
        }

        val body = JSONObject().apply {
            put("model", settings.model.trim())
            put("store", false)
            put("max_output_tokens", 1200)
            put(
                "instructions",
                "Turkce bir kisisel radyo yayin planlayicisisin. " +
                    "Risale-i Nur seslerini, ayet meali ve hadis kartlarini konu butunluguyle sirala. " +
                    "Kaynak metni veya kimlik UYDURMA. Yalnizca verilen item kimliklerini kullan. " +
                    "Ayni kimligi tekrarlama; mumkunse Risale, ayet ve hadis arasinda dengeli gecis kur. " +
                    "Secilenlerin toplam suresi hedef sureye yakin olsun."
            )
            put("input", input)
            put("text", JSONObject().put("format", JSONObject().apply {
                put("type", "json_schema")
                put("name", "radio_flow")
                put("strict", true)
                put("schema", schema)
            }))
        }

        val response = AiHttp.postJson(settings.endpoint, apiKey, body, "API", 45_000)
        val result = JSONObject(AiHttp.stripCodeFence(AiHttp.extractOutputText(response)))
        val ids = result.optJSONArray("item_ids") ?: JSONArray()
        val byToken = candidates.associateBy { "k_${it.id}" }

        // Yalnizca katalogda gercekten olan kimlikler; tekrarlar atilir.
        val chosen = LinkedHashMap<Long, KnowledgeItem>()
        for (i in 0 until ids.length()) {
            byToken[ids.optString(i)]?.let { chosen.putIfAbsent(it.id, it) }
        }
        if (chosen.isEmpty()) error("API gecerli bir katalog kimligi dondurmedi")

        // Model hedefi asarsa kirp; eksik kalirsa uydurma dolgu ekleme.
        val fitted = fitToTarget(chosen.values.toList(), targetMs, duration)
        val total = fitted.sumOf(duration)
        return PlanResult(
            name = result.optString("name").trim().take(60).ifBlank { makeName(request) },
            entries = fitted.map { SmartFlowEntry(FlowEntryKind.KNOWLEDGE.name, it.id, it.isReady) },
            usedAi = true,
            note = "Yapay zeka plani katalog kimlikleriyle dogrulandi. " + durationNote(total),
            totalMs = total
        )
    }

    private fun localPlan(
        request: String,
        targetMs: Long,
        ranked: List<KnowledgeItem>,
        duration: (KnowledgeItem) -> Long
    ): PlanResult {
        val readyFirst = ranked.sortedWith(
            compareByDescending<KnowledgeItem> { it.isReady }.thenBy { typePriority(it.typeEnum) }
        )
        val buckets = KnowledgeType.entries.associateWith { type ->
            ArrayDeque(readyFirst.filter { it.typeEnum == type })
        }

        // Turleri donusumlu dagit: Risale, ayet, Risale, hadis, ...
        // (Eski surumde indeks her turda 2 atliyor ve hep Risale seciliyordu.)
        // Sirasi gelen tur hedefi fazla asacaksa, sigan kisa bir kart aranir;
        // boylece "45 dakikalik yayin" gercekten ~45 dakika olur.
        val out = mutableListOf<KnowledgeItem>()
        var total = 0L
        while (total < targetMs && out.size < MAX_ITEMS) {
            val preferred = PREFERRED_ORDER[out.size % PREFERRED_ORDER.size]
            val fronts = listOfNotNull(buckets[preferred]?.firstOrNull()) +
                buckets.values.mapNotNull { it.firstOrNull() }
            val pick = fronts.firstOrNull { fits(total, duration(it), targetMs) } ?: break
            buckets.getValue(pick.typeEnum).removeFirst()
            out += pick
            total += duration(pick)
        }

        return PlanResult(
            name = makeName(request),
            entries = out.map { SmartFlowEntry(FlowEntryKind.KNOWLEDGE.name, it.id, it.isReady) },
            usedAi = false,
            note = "Konu fihristine gore cihazda yerel taslak olusturuldu. " + durationNote(total),
            totalMs = total
        )
    }

    /**
     * Modelin sirasini bozmadan hedefe oturtur: hedefi fazla asacak kart atlanir,
     * listede ilerideki sigan kartlar alinmaya devam eder.
     */
    private fun fitToTarget(
        items: List<KnowledgeItem>,
        targetMs: Long,
        duration: (KnowledgeItem) -> Long
    ): List<KnowledgeItem> {
        val out = mutableListOf<KnowledgeItem>()
        var total = 0L
        for (item in items) {
            if (total >= targetMs || out.size >= MAX_ITEMS) break
            val d = duration(item)
            if (!fits(total, d, targetMs)) continue
            out += item
            total += d
        }
        return out.ifEmpty { items.take(1) }
    }

    /**
     * Karti eklemek hedefe, eklememekten daha yakin (ya da ayni uzaklikta)
     * birakiyorsa eklenir. Esitlikte eklenir: kisa kalmak yerine hedefi tut.
     */
    private fun fits(totalMs: Long, itemMs: Long, targetMs: Long): Boolean {
        val after = totalMs + itemMs
        return after <= targetMs || (after - targetMs) <= (targetMs - totalMs)
    }

    /** Bir kartin yayinda ne kadar surecegi. */
    private fun estimateMs(item: KnowledgeItem, trackDurations: Map<Long, Long>): Long =
        when (item.typeEnum) {
            KnowledgeType.RISALE ->
                trackDurations[item.trackId]?.takeIf { it > 0L } ?: DEFAULT_RISALE_MS
            KnowledgeType.VERSE, KnowledgeType.HADITH ->
                if (item.text.isBlank()) DEFAULT_SHORT_MS
                else {
                    val speed = item.voiceSpeed.coerceIn(0.5f, 2.0f)
                    (item.text.length / TTS_CHARS_PER_SECOND / speed * 1_000.0).toLong() + TTS_PAUSE_MS
                }
        }

    private fun durationNote(totalMs: Long): String =
        "Tahmini sure: ${Math.round(totalMs / 60_000.0)} dk."

    private fun rank(request: String, items: List<KnowledgeItem>): List<KnowledgeItem> {
        val tokens = tokens(request)
        if (tokens.isEmpty()) return items.sortedBy { it.sortOrder }
        return items.map { item ->
            val title = normalize(item.title + " " + item.reference)
            val topics = normalize(item.topics.joinToString(" ") + " " + item.keywords.joinToString(" "))
            val all = normalize(item.searchableText())
            var score = 0
            tokens.forEach { token ->
                if (title.contains(token)) score += 8
                if (topics.contains(token)) score += 6
                if (all.contains(token)) score += 2
            }
            if (item.isReady) score += 2
            item to score
        }.filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<KnowledgeItem, Int>> { it.second }
                .thenBy { it.first.sortOrder })
            .map { it.first }
            .ifEmpty {
                items.sortedWith(compareByDescending<KnowledgeItem> { it.isReady }.thenBy { it.sortOrder })
            }
    }

    private fun tokens(value: String): Set<String> = normalize(value)
        .split(Regex("[^a-z0-9]+"))
        .filter { it.length >= 3 && it !in STOP_WORDS }
        .toSet()

    private fun normalize(value: String): String {
        val turkish = value.lowercase()
            .replace('ı', 'i').replace('ğ', 'g').replace('ü', 'u')
            .replace('ş', 's').replace('ö', 'o').replace('ç', 'c')
        return Normalizer.normalize(turkish, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
    }

    private fun makeName(request: String): String {
        val clean = request.trim().replace(Regex("\\s+"), " ")
        return if (clean.isBlank()) "Akilli yayin" else clean.take(48)
    }

    private fun typePriority(type: KnowledgeType): Int = when (type) {
        KnowledgeType.RISALE -> 0
        KnowledgeType.VERSE -> 1
        KnowledgeType.HADITH -> 2
    }

    private companion object {
        const val MAX_CANDIDATES = 80
        const val MAX_ITEMS = 40
        const val DEFAULT_RISALE_MS = 8L * 60_000L
        const val DEFAULT_SHORT_MS = 45_000L
        /** Turkce TTS'in normal hizda saniyede okudugu yaklasik karakter. */
        const val TTS_CHARS_PER_SECOND = 14.0
        const val TTS_PAUSE_MS = 1_500L
        val PREFERRED_ORDER = listOf(
            KnowledgeType.RISALE, KnowledgeType.VERSE, KnowledgeType.RISALE, KnowledgeType.HADITH
        )
        // Metin normalize edildiginden (ı -> i) yalnizca ASCII halleri yeterli.
        val STOP_WORDS = setOf("icin", "olan", "bana", "bir", "yayin", "akisi", "akis", "olustur")
    }
}
