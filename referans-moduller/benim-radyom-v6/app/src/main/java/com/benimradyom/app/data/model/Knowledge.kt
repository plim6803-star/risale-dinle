package com.benimradyom.app.data.model

import org.json.JSONArray
import org.json.JSONObject

/** Akilli yayin katalogundaki dogrulanabilir kaynak turleri. */
enum class KnowledgeType(val label: String, val emoji: String) {
    RISALE("Risale-i Nur", "📘"),
    VERSE("Ayet meali", "📖"),
    HADITH("Hadis", "☘️");

    companion object {
        fun from(value: String?): KnowledgeType =
            entries.firstOrNull { it.name == value } ?: RISALE
    }
}

/**
 * Yayinda kullanilabilecek kaynak karti.
 *
 * Risale kartlari kullanicinin cihazindaki bir ses kaydina [trackId] ile
 * baglanir. Ayet ve hadis kartlari ise, kaynakla karsilastirilip kullanici
 * tarafindan onaylanan [text] alanini Android TTS ile seslendirir.
 */
data class KnowledgeItem(
    val id: Long = 0L,
    val type: String = KnowledgeType.RISALE.name,
    val title: String = "",
    val collection: String = "",
    val section: String = "",
    val reference: String = "",
    /** Kaynak metni degil; arama ve oneride kullanilan kisa konu ozeti. */
    val summary: String = "",
    /** TTS ile okunacak, kullanicinin kontrol ettigi metin. */
    val text: String = "",
    val topicsJson: String = "[]",
    val keywordsJson: String = "[]",
    val sourceUrl: String = "",
    /** RISALE turunde cihazdaki ses kaydi; diger turlerde -1. */
    val trackId: Long = -1L,
    /** Metin kaynakla karsilastirildi mi? */
    val isReviewed: Boolean = false,
    val isEnabled: Boolean = true,
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val sortOrder: Int = 0
) {
    val typeEnum: KnowledgeType get() = KnowledgeType.from(type)
    val topics: List<String> get() = parseStringList(topicsJson)
    val keywords: List<String> get() = parseStringList(keywordsJson)

    /** Yayin motorunun bu karti guvenle kullanabilmesi icin gerekenler. */
    val isReady: Boolean
        get() = isEnabled && when (typeEnum) {
            KnowledgeType.RISALE -> trackId > 0L
            KnowledgeType.VERSE, KnowledgeType.HADITH -> isReviewed && text.isNotBlank()
        }

    val statusText: String
        get() = when {
            !isEnabled -> "Kapali"
            typeEnum == KnowledgeType.RISALE && trackId <= 0L -> "Ses kaydi eslestirilmedi"
            typeEnum != KnowledgeType.RISALE && text.isBlank() -> "Okunacak metin eklenmedi"
            typeEnum != KnowledgeType.RISALE && !isReviewed -> "Kaynak kontrolu bekliyor"
            else -> "Yayina hazir"
        }

    fun searchableText(): String = buildString {
        append(title).append(' ')
        append(collection).append(' ')
        append(section).append(' ')
        append(reference).append(' ')
        append(summary).append(' ')
        append(topics.joinToString(" ")).append(' ')
        append(keywords.joinToString(" "))
    }

    companion object {
        fun listToJson(values: List<String>): String =
            JSONArray().apply { values.filter { it.isNotBlank() }.forEach { put(it.trim()) } }.toString()

        fun csvToJson(value: String): String = listToJson(
            value.split(',', ';', '\n').map { it.trim() }.filter { it.isNotBlank() }
        )

        private fun parseStringList(json: String): List<String> = runCatching {
            val a = JSONArray(json)
            (0 until a.length()).mapNotNull { a.optString(it).takeIf(String::isNotBlank) }
        }.getOrDefault(emptyList())
    }
}

/** Akista katalog, cihaz sesi ve mevcut anons birlikte kullanilabilir. */
enum class FlowEntryKind(val label: String) {
    KNOWLEDGE("Kaynak"), TRACK("Ses"), ANNOUNCEMENT("Anons");

    companion object {
        fun from(value: String?): FlowEntryKind =
            entries.firstOrNull { it.name == value } ?: KNOWLEDGE
    }
}

data class SmartFlowEntry(
    val kind: String = FlowEntryKind.KNOWLEDGE.name,
    val sourceId: Long,
    val isEnabled: Boolean = true
) {
    val kindEnum: FlowEntryKind get() = FlowEntryKind.from(kind)

    fun toJson(): JSONObject = JSONObject().apply {
        put("kind", kind)
        put("id", sourceId)
        put("on", isEnabled)
    }

    companion object {
        fun fromJson(o: JSONObject) = SmartFlowEntry(
            kind = o.optString("kind", FlowEntryKind.KNOWLEDGE.name),
            sourceId = o.optLong("id", -1L),
            isEnabled = o.optBoolean("on", true)
        )
    }
}

/** Kullanici tarafindan duzenlenebilen ve kaydedilen kesin yayin sirasi. */
data class SmartFlow(
    val id: Long = 0L,
    val name: String = "Akilli yayin",
    val request: String = "",
    val itemsJson: String = "[]",
    val isActive: Boolean = false,
    val loop: Boolean = false,
    val bedEnabled: Boolean = true,
    val bedPlaylistId: Long = -1L,
    val bedVolume: Float = 0.18f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val entries: List<SmartFlowEntry>
        get() = runCatching {
            val a = JSONArray(itemsJson)
            (0 until a.length()).map { SmartFlowEntry.fromJson(a.getJSONObject(it)) }
        }.getOrDefault(emptyList())

    fun withEntries(items: List<SmartFlowEntry>): SmartFlow = copy(
        itemsJson = entriesToJson(items),
        updatedAt = System.currentTimeMillis()
    )

    companion object {
        fun entriesToJson(items: List<SmartFlowEntry>): String =
            JSONArray().apply { items.forEach { put(it.toJson()) } }.toString()
    }
}

/** API anahtari burada tutulmaz; Android Keystore ile ayri saklanir. */
data class AiSettings(
    val enabled: Boolean = false,
    val endpoint: String = "https://api.openai.com/v1/responses",
    val model: String = "gpt-5.6-luna",
    val imageModel: String = "gpt-image-2.5-flare",
    val imageQuality: String = "low",
    val visualStyle: String = ContemplationSettings().visualStyle
)
