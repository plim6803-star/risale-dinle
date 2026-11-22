package com.benimradyom.app.data.model

/**
 * Liste turu. Radyonun icerigi nasil kullanacagini belirleyen tek ayar.
 * Kullanici akis/adim dizmek yerine sadece listeye tur verir.
 */
enum class PlaylistKind(val label: String, val emoji: String, val hint: String) {
    MUSIC ("Ezgi / Ilahi",  "🎵", "Normal calar, karistirilabilir"),
    SPEECH("Ders / Sohbet", "🎙", "Uzun kayit; kaldigi yerden devam eder"),
    SPOT  ("Anons",         "📢", "Kisa; araya serpistirilir"),
    PRAYER("Ezan / Vakit",  "🕌", "Sadece namaz vakitlerinde calar"),
    BED   ("Fon Muzik",     "🌙", "Konusmanin altinda kisik calar");

    companion object {
        fun from(value: String?): PlaylistKind =
            entries.firstOrNull { it.name == value } ?: MUSIC
    }
}

data class Playlist(
    val id: Long = 0,
    val name: String,
    val kind: String = PlaylistKind.MUSIC.name,
    /** Yayin recetesindeki agirlik (0..100). 0 = bu listeyi calma. */
    val weight: Int = 25,
    val color: Int = 0xFF2E7D6F.toInt(),
    val sortOrder: Int = 0
) {
    val kindEnum: PlaylistKind get() = PlaylistKind.from(kind)
}

data class Track(
    val id: Long = 0,
    val playlistId: Long,
    val title: String,
    val artist: String = "",
    val uri: String,
    val durationMs: Long = 0L,
    val sortOrder: Int = 0,
    /** Uzun kayitlarda (SPEECH) kaldigi yer. */
    val lastPositionMs: Long = 0L,
    val playCount: Int = 0,
    val lastPlayedAt: Long = 0L,
    val addedAt: Long = 0L
)

/**
 * Cihazdaki bir klasoru bir listeye baglar. Klasore sonradan atilan dosyalar
 * uygulama acilinca otomatik listeye duser.
 */
data class LinkedFolder(
    val id: Long = 0,
    val playlistId: Long,
    /** MediaStore klasor yolu - eslestirme anahtari. */
    val folderKey: String,
    val displayName: String,
    val lastSyncAt: Long = 0L,
    val lastAddedCount: Int = 0
)

/** Anons: ya kendi ses kaydin (audioUri) ya da yazip seslendirilen metin. */
data class Announcement(
    val id: Long = 0,
    /** Ekranda gorunur, ASLA seslendirilmez. */
    val label: String = "",
    /** audioUri yoksa bu metin seslendirilir. */
    val text: String = "",
    /** Kendi ses kaydin / sectigin ses dosyasi. Doluysa metin kullanilmaz. */
    val audioUri: String? = null,
    val durationMs: Long = 0L,
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val isEnabled: Boolean = true,
    val category: String = "genel",
    val sortOrder: Int = 0
) {
    val isAudio: Boolean get() = !audioUri.isNullOrBlank()
}

// Yayin recetesi ve zamanlayici tek bir kavramda birlesti: bkz. Program.kt
