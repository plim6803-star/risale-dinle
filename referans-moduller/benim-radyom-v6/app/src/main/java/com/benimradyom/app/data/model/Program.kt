package com.benimradyom.app.data.model

import org.json.JSONArray
import org.json.JSONObject

/** Bir listenin parcalarinin hangi sirayla calacagi. */
enum class TrackOrder(val label: String, val hint: String) {
    SIRALI("Sirali", "Listedeki sira korunur - dersler, bolumler icin"),
    ISME_GORE("Isme gore", "01, 02, 03... dosya adina gore siralanir"),
    ESKIDEN_YENIYE("Eskiden yeniye", "Once eklenen once calar"),
    YENIDEN_ESKIYE("Yeniden eskiye", "Son eklenen once calar"),
    KARISIK("Karisik", "Her turda yeniden karistirilir"),
    EN_AZ_CALAN("En az calan", "En uzun suredir calmayan once gelir");

    companion object {
        fun from(value: String?): TrackOrder = entries.firstOrNull { it.name == value } ?: SIRALI
    }
}

/** Sartli gorev: bir sey olunca baska bir sey yap. */
enum class RuleTrigger(val label: String) {
    PROGRAM_BASLAYINCA("Program baslayinca"),
    LISTE_CALINCA("Su listeden bir parca calinca"),
    HER_N_PARCA("Her N parcada bir"),
    SAAT_GELINCE("Saat gelince"),
    PROGRAM_BITINCE("Program bitince");

    companion object {
        fun from(v: String?) = entries.firstOrNull { it.name == v } ?: PROGRAM_BASLAYINCA
    }
}

enum class RuleAction(val label: String) {
    LISTE_CAL("Su listeden bir parca cal"),
    ANONS_CAL("Su anonsu cal"),
    SES_AYARLA("Sesi su seviyeye getir"),
    FON_AC("Fon muzigi ac"),
    FON_KAPAT("Fon muzigi kapat"),
    DURAKLAT("Su kadar dakika duraklat"),
    PROGRAM_DEGISTIR("Su programa gec");

    companion object {
        fun from(v: String?) = entries.firstOrNull { it.name == v } ?: LISTE_CAL
    }
}

/**
 * Tek bir sartli gorev.
 *
 * Ornek: "Risale listesinden bir bolum calinca, arkasindan Motivasyon anonsu cal"
 *   trigger = LISTE_CALINCA, triggerValue = "<risale liste id>"
 *   action  = ANONS_CAL,     actionValue  = "<anons id>"
 */
data class Rule(
    val trigger: String = RuleTrigger.PROGRAM_BASLAYINCA.name,
    val triggerValue: String = "",
    val action: String = RuleAction.LISTE_CAL.name,
    val actionValue: String = "",
    val isEnabled: Boolean = true
) {
    val triggerEnum: RuleTrigger get() = RuleTrigger.from(trigger)
    val actionEnum: RuleAction get() = RuleAction.from(action)

    fun toJson(): JSONObject = JSONObject().apply {
        put("t", trigger); put("tv", triggerValue)
        put("a", action); put("av", actionValue)
        put("on", isEnabled)
    }

    companion object {
        fun fromJson(o: JSONObject) = Rule(
            trigger = o.optString("t", RuleTrigger.PROGRAM_BASLAYINCA.name),
            triggerValue = o.optString("tv", ""),
            action = o.optString("a", RuleAction.LISTE_CAL.name),
            actionValue = o.optString("av", ""),
            isEnabled = o.optBoolean("on", true)
        )
    }
}

/** Belirli bir saatte calacak anons. */
data class TimedSpot(
    val hour: Int,
    val minute: Int,
    val announcementId: Long,
    val isEnabled: Boolean = true
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("h", hour); put("m", minute); put("id", announcementId); put("on", isEnabled)
    }

    companion object {
        fun fromJson(o: JSONObject) = TimedSpot(
            hour = o.optInt("h", 0),
            minute = o.optInt("m", 0),
            announcementId = o.optLong("id", -1L),
            isEnabled = o.optBoolean("on", true)
        )
    }
}

/**
 * Yayin programi. Eski "Recete" ve "Zamanlayici" kavramlarinin birlesimi.
 *
 * Bir program sunlari bir arada tutar:
 *   - ZAMAN     : hangi gunler, saat kacta baslar, kacta biter
 *   - ICERIK    : hangi listeden ne oranda, hangi sirayla
 *   - SES       : ana ses, liste basina ses, fon muzik orani
 *   - ANONS     : kac parcada bir, sabit saatlerde, saat anonsu
 *   - KURALLAR  : sartli gorevler
 */
data class Program(
    val id: Long = 0,
    val name: String,
    val isEnabled: Boolean = true,

    // ── Zaman ───────────────────────────────────────────────────────────────
    /** -1 = saat kisiti yok (elle baslatilir). */
    val startHour: Int = -1,
    val startMinute: Int = 0,
    val endHour: Int = -1,
    val endMinute: Int = 0,
    /** Pazartesi'den Pazar'a 7 karakter. */
    val days: String = "1111111",
    /** Saati gelince kendiliginden baslasin mi. */
    val autoStart: Boolean = false,

    // ── Icerik ──────────────────────────────────────────────────────────────
    /** {"listeId": agirlik} - bu programa ozel karisim. */
    val mixJson: String = "{}",
    val trackOrder: String = TrackOrder.SIRALI.name,

    // ── Ses ─────────────────────────────────────────────────────────────────
    val masterVolume: Float = 1.0f,
    /** {"listeId": 0.0..1.0} - liste basina ses seviyesi. */
    val volumesJson: String = "{}",
    val bedEnabled: Boolean = true,
    val bedPlaylistId: Long = -1L,
    val bedVolume: Float = 0.18f,

    // ── Anons ───────────────────────────────────────────────────────────────
    /** 0 = kapali. */
    val spotEveryN: Int = 3,
    /** Saat basi "saat 14:00" anonsu. */
    val announceClock: Boolean = false,
    /** Saat basi calacak liste (-1 = kapali). */
    val hourlyPlaylistId: Long = -1L,
    /** Sabit saatlerde calacak anonslar. */
    val timedSpotsJson: String = "[]",

    // ── Gecis ───────────────────────────────────────────────────────────────
    val fadeMs: Int = 1200,
    /** true = ust uste binen gercek gecis, false = kis-ac. */
    val crossfade: Boolean = false,
    /** Kayitlarin ses seviyesini yaklasik esitle. */
    val normalizeVolume: Boolean = true,

    // ── Sessiz saatler ──────────────────────────────────────────────────────
    val quietStartHour: Int = -1,
    val quietEndHour: Int = -1,

    // ── Kurallar ────────────────────────────────────────────────────────────
    val rulesJson: String = "[]",

    val isActive: Boolean = false,
    val sortOrder: Int = 0
) {
    val orderEnum: TrackOrder get() = TrackOrder.from(trackOrder)

    /** Bu programa ozel karisim agirliklari. */
    val mix: Map<Long, Int> get() = parseLongIntMap(mixJson)

    /** Liste basina ses seviyeleri. */
    val volumes: Map<Long, Float> get() = parseLongFloatMap(volumesJson)

    val rules: List<Rule>
        get() = runCatching {
            val arr = JSONArray(rulesJson)
            (0 until arr.length()).map { Rule.fromJson(arr.getJSONObject(it)) }
        }.getOrDefault(emptyList())

    val timedSpots: List<TimedSpot>
        get() = runCatching {
            val arr = JSONArray(timedSpotsJson)
            (0 until arr.length()).map { TimedSpot.fromJson(arr.getJSONObject(it)) }
        }.getOrDefault(emptyList())

    /** Bir listenin bu programdaki agirligi; tanimsizsa listenin kendi agirligi. */
    fun weightOf(playlist: Playlist): Int = mix[playlist.id] ?: playlist.weight

    /** Bir listenin bu programdaki ses seviyesi. */
    fun volumeOf(playlistId: Long): Float = (volumes[playlistId] ?: 1.0f) * masterVolume

    /** Bu program su anda (verilen gun/dakika icin) gecerli mi. */
    fun coversNow(dayIndex: Int, minuteOfDay: Int): Boolean {
        if (!isEnabled) return false
        if (startHour < 0 || endHour < 0) return days.getOrNull(dayIndex) == '1'
        val start = startHour * 60 + startMinute
        val end = endHour * 60 + endMinute
        return if (start < end) {
            days.getOrNull(dayIndex) == '1' && minuteOfDay in start until end
        } else {
            // Gece yarisi asilan programda (23:00-06:00 gibi) 02:00, bir
            // onceki gun baslayan programin parcasidir. Gun secimini buna gore
            // onceki gunden okuruz.
            if (minuteOfDay >= start) {
                days.getOrNull(dayIndex) == '1'
            } else if (minuteOfDay < end) {
                val previousDay = (dayIndex + 6) % 7
                days.getOrNull(previousDay) == '1'
            } else false
        }
    }

    companion object {
        fun mixToJson(map: Map<Long, Int>): String = JSONObject().apply {
            map.forEach { (k, v) -> put(k.toString(), v) }
        }.toString()

        fun volumesToJson(map: Map<Long, Float>): String = JSONObject().apply {
            map.forEach { (k, v) -> put(k.toString(), v.toDouble()) }
        }.toString()

        fun rulesToJson(rules: List<Rule>): String =
            JSONArray().apply { rules.forEach { put(it.toJson()) } }.toString()

        fun timedSpotsToJson(spots: List<TimedSpot>): String =
            JSONArray().apply { spots.forEach { put(it.toJson()) } }.toString()

        private fun parseLongIntMap(json: String): Map<Long, Int> = runCatching {
            val o = JSONObject(json)
            o.keys().asSequence().mapNotNull { k ->
                k.toLongOrNull()?.let { it to o.optInt(k, 0) }
            }.toMap()
        }.getOrDefault(emptyMap())

        private fun parseLongFloatMap(json: String): Map<Long, Float> = runCatching {
            val o = JSONObject(json)
            o.keys().asSequence().mapNotNull { k ->
                k.toLongOrNull()?.let { it to o.optDouble(k, 1.0).toFloat() }
            }.toMap()
        }.getOrDefault(emptyMap())
    }
}

/** Namaz vakti ayarlari (programlardan bagimsiz, genel ayar). */
data class PrayerSettings(
    val enabled: Boolean = false,
    val latitude: Double = 41.0082,
    val longitude: Double = 28.9784,
    val cityName: String = "Istanbul",
    /** Diyanet'e yakin varsayilan. */
    val fajrAngle: Double = 18.0,
    val ishaAngle: Double = 17.0,
    /** Vakitten kac dakika once sussun. */
    val muteBeforeMinutes: Int = 0,
    /** Vakitten sonra kac dakika susuk kalsin. */
    val muteAfterMinutes: Int = 10,
    /** Ezan kaydinin bulundugu liste (-1 = sadece sustur, ezan calma). */
    val adhanPlaylistId: Long = -1L,
    /** Hangi vakitlerde uygulansin: sabah,ogle,ikindi,aksam,yatsi. */
    val activePrayers: String = "11111"
) {
    fun isActiveFor(index: Int): Boolean = activePrayers.getOrNull(index) == '1'
}
