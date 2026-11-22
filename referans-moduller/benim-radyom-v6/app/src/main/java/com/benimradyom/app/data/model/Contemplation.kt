package com.benimradyom.app.data.model

/** Ses kaydinda okunan metnin zaman kodlu bir bolumu. */
data class TranscriptSegment(
    val id: Long = 0L,
    val trackId: Long,
    val startMs: Long,
    val endMs: Long,
    val text: String,
    /** SRT, VTT, TXT_APPROX veya AI. */
    val source: String = TranscriptSource.SRT.name,
    val sortOrder: Int = 0
) {
    val sourceEnum: TranscriptSource get() = TranscriptSource.from(source)
    val isApproximate: Boolean get() = sourceEnum == TranscriptSource.TXT_APPROX
}

enum class TranscriptSource(val label: String) {
    SRT("Zaman kodlu SRT"),
    VTT("Zaman kodlu VTT"),
    TXT_APPROX("Yaklasik TXT"),
    AI("AI zaman kodu");

    companion object {
        fun from(value: String?): TranscriptSource =
            entries.firstOrNull { it.name == value } ?: SRT
    }
}

/** Okunan zaman araligiyla eslesen, cihazda onbelleklenmis tefekkur gorseli. */
data class ContemplationImage(
    val id: Long = 0L,
    val trackId: Long,
    val startMs: Long,
    val endMs: Long,
    val segmentId: Long = -1L,
    val prompt: String = "",
    /** Uygulamanin ozel klasorundeki JPEG dosyasinin mutlak yolu. */
    val localPath: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class ContemplationSettings(
    val imageModel: String = "gpt-image-2.5-flare",
    val imageQuality: String = "low",
    val visualStyle: String =
        "Sakin sinematik doga, İslam sanatindan geometrik motifler, yumusak isik, " +
            "derinlikli ve huzurlu, fotograf ile suluboya arasi"
)

/**
 * Ana ses kaydinda fon muziginin tamamen susacagi zaman araligi.
 *
 * [startMs] AI'nin buldugu ayet baslangicindan varsayilan olarak 2,5 saniye
 * onceye cekilir. Boylece motor bu noktada fonu yumusakca sifira indirebilir.
 */
data class BedMuteCue(
    val id: Long = 0L,
    val trackId: Long,
    val startMs: Long,
    val endMs: Long,
    val label: String = "Arapça ayet okunuşu",
    /** AI, YEREL veya ELLE. */
    val source: String = BedMuteCueSource.AI.name,
    val isEnabled: Boolean = true
)

enum class BedMuteCueSource(val label: String) {
    AI("AI analizi"),
    YEREL("Metin algılama"),
    ELLE("Elle düzenlendi");

    companion object {
        fun from(value: String?): BedMuteCueSource =
            entries.firstOrNull { it.name == value } ?: AI
    }
}
