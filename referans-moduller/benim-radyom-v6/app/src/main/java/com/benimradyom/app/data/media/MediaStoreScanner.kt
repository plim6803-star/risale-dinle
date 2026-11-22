package com.benimradyom.app.data.media

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat

/** Cihazdaki tek bir ses dosyasi. */
data class DeviceTrack(
    val mediaId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uri: String,
    val folderKey: String,
    val folderName: String,
    val fileName: String,
    val dateAdded: Long,
    val sizeBytes: Long
)

/** Cihazdaki bir klasor (MediaStore bucket). */
data class DeviceFolder(
    val key: String,
    val name: String,
    val trackCount: Int,
    val totalDurationMs: Long
)

/**
 * Cihazdaki ses dosyalarini MediaStore uzerinden okur.
 *
 * Neden dosya secici (SAF) degil:
 *  - MediaStore URI'leri kalicidir; izin dusmez, telefon yeniden baslasa da calar.
 *  - Klasor / sanatci / album bilgisi hazir gelir, uygulama kendi arayuzunde gosterir.
 *  - Kullanici dosya seciciye hic cikmaz.
 */
object MediaStoreScanner {

    /** Android surumune gore gereken okuma izni. */
    val audioPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

    fun hasPermission(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, audioPermission) == PackageManager.PERMISSION_GRANTED

    private val projection: Array<String>
        get() = buildList {
            add(MediaStore.Audio.Media._ID)
            add(MediaStore.Audio.Media.TITLE)
            add(MediaStore.Audio.Media.ARTIST)
            add(MediaStore.Audio.Media.ALBUM)
            add(MediaStore.Audio.Media.DURATION)
            add(MediaStore.Audio.Media.DISPLAY_NAME)
            add(MediaStore.Audio.Media.DATE_ADDED)
            add(MediaStore.Audio.Media.SIZE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
                add(MediaStore.Audio.Media.RELATIVE_PATH)
            } else {
                @Suppress("DEPRECATION")
                add(MediaStore.Audio.Media.DATA)
            }
        }.toTypedArray()

    private const val UNKNOWN_ARTIST = "<unknown>"
    private const val SLASH = '/'

    /**
     * Tum ses dosyalarini okur.
     *
     * [onlyMusic] false oldugunda ses kayitlari, sesli kitaplar ve podcast'ler de
     * gelir. Kendi kayitlariniz cogu zaman "muzik" olarak isaretlenmedigi icin
     * varsayilan false.
     */
    fun queryAll(
        ctx: Context,
        onlyMusic: Boolean = false,
        minDurationMs: Long = 3000L
    ): List<DeviceTrack> {
        if (!hasPermission(ctx)) return emptyList()

        val selection = StringBuilder(MediaStore.Audio.Media.DURATION + " >= ?")
        val args = mutableListOf(minDurationMs.toString())
        if (onlyMusic) selection.append(" AND " + MediaStore.Audio.Media.IS_MUSIC + " != 0")

        val out = ArrayList<DeviceTrack>(512)
        runCatching {
            ctx.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection.toString(),
                args.toTypedArray(),
                MediaStore.Audio.Media.DATE_ADDED + " DESC"
            )?.use { c ->
                val iId = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val iTitle = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val iArtist = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val iAlbum = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val iDur = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val iName = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val iDate = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val iSize = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val iBucket = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    c.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME) else -1
                val iRel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    c.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH) else -1
                @Suppress("DEPRECATION")
                val iData = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    c.getColumnIndex(MediaStore.Audio.Media.DATA) else -1

                while (c.moveToNext()) {
                    val id = c.getLong(iId)
                    val fileName = c.getString(iName).orEmpty()

                    // Klasor bilgisi: Android 10+ hazir gelir, oncesinde yoldan cikarilir.
                    var folderKey: String
                    var folderName: String
                    if (iRel >= 0) {
                        val rel = c.getString(iRel).orEmpty().trim(SLASH)
                        folderKey = rel.ifBlank { "/" }
                        folderName = if (iBucket >= 0) {
                            c.getString(iBucket) ?: rel.substringAfterLast(SLASH)
                        } else {
                            rel.substringAfterLast(SLASH)
                        }
                    } else {
                        val path = if (iData >= 0) c.getString(iData).orEmpty() else ""
                        val dir = path.substringBeforeLast(SLASH, "")
                        folderKey = dir.ifBlank { "/" }
                        folderName = dir.substringAfterLast(SLASH)
                    }
                    if (folderName.isBlank()) folderName = "Bilinmeyen klasor"

                    val title = c.getString(iTitle)?.takeIf { it.isNotBlank() }
                        ?: fileName.substringBeforeLast('.')
                    val artist = c.getString(iArtist)?.takeIf { it != UNKNOWN_ARTIST }.orEmpty()

                    out += DeviceTrack(
                        mediaId = id,
                        title = title,
                        artist = artist,
                        album = c.getString(iAlbum).orEmpty(),
                        durationMs = c.getLong(iDur),
                        uri = uriOf(id).toString(),
                        folderKey = folderKey,
                        folderName = folderName,
                        fileName = fileName,
                        dateAdded = c.getLong(iDate) * 1000L,
                        sizeBytes = c.getLong(iSize)
                    )
                }
            }
        }
        return out
    }

    /** Klasorleri dosya sayisi ve toplam sure ile birlikte dondurur. */
    fun folders(tracks: List<DeviceTrack>): List<DeviceFolder> =
        tracks.groupBy { it.folderKey }
            .map { (key, items) ->
                DeviceFolder(
                    key = key,
                    name = items.first().folderName,
                    trackCount = items.size,
                    totalDurationMs = items.sumOf { it.durationMs }
                )
            }
            .sortedWith(compareByDescending<DeviceFolder> { it.trackCount }.thenBy { it.name.lowercase() })

    fun artists(tracks: List<DeviceTrack>): List<Pair<String, Int>> =
        tracks.asSequence()
            .filter { it.artist.isNotBlank() }
            .groupBy { it.artist }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }

    fun uriOf(mediaId: Long): Uri =
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId)
}
