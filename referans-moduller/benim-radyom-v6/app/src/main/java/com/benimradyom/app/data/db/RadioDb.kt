package com.benimradyom.app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.benimradyom.app.data.model.Announcement
import com.benimradyom.app.data.model.AiSettings
import com.benimradyom.app.data.model.BedMuteCue
import com.benimradyom.app.data.model.ContemplationImage
import com.benimradyom.app.data.model.KnowledgeItem
import com.benimradyom.app.data.model.LinkedFolder
import com.benimradyom.app.data.model.Playlist
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.PrayerSettings
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.SmartFlow
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TranscriptSegment
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Yazma islemlerinde artan bir sayac. Akislar (Flow) buna bakarak kendini
 * tazeler - Room'un otomatik gecerlilik takibinin elle yazilmis hali.
 */
object DbSignal {
    private val _version = MutableStateFlow(0L)
    val version: StateFlow<Long> = _version.asStateFlow()
    fun bump() { _version.value = _version.value + 1 }
}

/**
 * Uygulamanin veritabani.
 *
 * Room yerine dogrudan SQLite kullaniliyor: Room, derleme sirasinda kod ureten
 * bir islemci (kapt) gerektiriyor, o da Gradle'a bagli. Bu katman ayni isi
 * yapar, disariya ayni API'yi verir ve hicbir kod uretimine ihtiyac duymaz.
 */
class RadioDb private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "benim_radyom.db"
        private const val DB_VERSION = 5
        private const val KEY_PRAYER = "prayer_settings"
        private const val KEY_AI = "ai_settings"

        @Volatile private var INSTANCE: RadioDb? = null

        fun get(context: Context): RadioDb = INSTANCE ?: synchronized(this) {
            INSTANCE ?: RadioDb(context).also { INSTANCE = it }
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE playlists(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                kind TEXT NOT NULL,
                weight INTEGER NOT NULL,
                color INTEGER NOT NULL,
                sortOrder INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE tracks(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                playlistId INTEGER NOT NULL,
                title TEXT NOT NULL,
                artist TEXT NOT NULL,
                uri TEXT NOT NULL,
                durationMs INTEGER NOT NULL,
                sortOrder INTEGER NOT NULL,
                lastPositionMs INTEGER NOT NULL,
                playCount INTEGER NOT NULL,
                lastPlayedAt INTEGER NOT NULL,
                addedAt INTEGER NOT NULL,
                FOREIGN KEY(playlistId) REFERENCES playlists(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_tracks_playlist ON tracks(playlistId)")
        // Ayni dosya ayni listeye iki kez giremez -> klasor senkronu tekrarlansa
        // bile kopya olusmaz.
        db.execSQL("CREATE UNIQUE INDEX idx_tracks_unique ON tracks(playlistId, uri)")

        db.execSQL(
            """
            CREATE TABLE linked_folders(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                playlistId INTEGER NOT NULL,
                folderKey TEXT NOT NULL,
                displayName TEXT NOT NULL,
                lastSyncAt INTEGER NOT NULL,
                lastAddedCount INTEGER NOT NULL,
                FOREIGN KEY(playlistId) REFERENCES playlists(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX idx_linked_unique ON linked_folders(playlistId, folderKey)")

        db.execSQL(
            """
            CREATE TABLE announcements(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                label TEXT NOT NULL,
                text TEXT NOT NULL,
                audioUri TEXT,
                durationMs INTEGER NOT NULL,
                voiceSpeed REAL NOT NULL,
                voicePitch REAL NOT NULL,
                isEnabled INTEGER NOT NULL,
                category TEXT NOT NULL,
                sortOrder INTEGER NOT NULL
            )
            """.trimIndent()
        )

        createProgramTables(db)
        createKnowledgeTables(db)
        createContemplationTables(db)
        createBedMuteTables(db)
    }

    /** Program ve genel ayar tablolari (v2). */
    private fun createProgramTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS programs(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                isEnabled INTEGER NOT NULL,
                startHour INTEGER NOT NULL,
                startMinute INTEGER NOT NULL,
                endHour INTEGER NOT NULL,
                endMinute INTEGER NOT NULL,
                days TEXT NOT NULL,
                autoStart INTEGER NOT NULL,
                mixJson TEXT NOT NULL,
                trackOrder TEXT NOT NULL,
                masterVolume REAL NOT NULL,
                volumesJson TEXT NOT NULL,
                bedEnabled INTEGER NOT NULL,
                bedPlaylistId INTEGER NOT NULL,
                bedVolume REAL NOT NULL,
                spotEveryN INTEGER NOT NULL,
                announceClock INTEGER NOT NULL,
                hourlyPlaylistId INTEGER NOT NULL,
                timedSpotsJson TEXT NOT NULL,
                fadeMs INTEGER NOT NULL,
                crossfade INTEGER NOT NULL,
                normalizeVolume INTEGER NOT NULL,
                quietStartHour INTEGER NOT NULL,
                quietEndHour INTEGER NOT NULL,
                rulesJson TEXT NOT NULL,
                isActive INTEGER NOT NULL,
                sortOrder INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS settings(
                k TEXT PRIMARY KEY NOT NULL,
                v TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    /** Dogrulanabilir kaynak katalogu ve kesin sirali akillar (v3). */
    private fun createKnowledgeTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS knowledge_items(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                title TEXT NOT NULL,
                collectionName TEXT NOT NULL,
                sectionName TEXT NOT NULL,
                reference TEXT NOT NULL,
                summary TEXT NOT NULL,
                bodyText TEXT NOT NULL,
                topicsJson TEXT NOT NULL,
                keywordsJson TEXT NOT NULL,
                sourceUrl TEXT NOT NULL,
                trackId INTEGER NOT NULL,
                isReviewed INTEGER NOT NULL,
                isEnabled INTEGER NOT NULL,
                voiceSpeed REAL NOT NULL,
                voicePitch REAL NOT NULL,
                sortOrder INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_knowledge_type ON knowledge_items(type)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_knowledge_track ON knowledge_items(trackId)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS smart_flows(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                request TEXT NOT NULL,
                itemsJson TEXT NOT NULL,
                isActive INTEGER NOT NULL,
                loop INTEGER NOT NULL,
                bedEnabled INTEGER NOT NULL,
                bedPlaylistId INTEGER NOT NULL,
                bedVolume REAL NOT NULL,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    /** Okunan yeri takip eden metin ve onbelleklenmis tefekkur gorselleri (v4). */
    private fun createContemplationTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS transcript_segments(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                trackId INTEGER NOT NULL,
                startMs INTEGER NOT NULL,
                endMs INTEGER NOT NULL,
                bodyText TEXT NOT NULL,
                source TEXT NOT NULL,
                sortOrder INTEGER NOT NULL,
                FOREIGN KEY(trackId) REFERENCES tracks(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_transcript_track_time " +
                "ON transcript_segments(trackId, startMs, endMs)"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS contemplation_images(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                trackId INTEGER NOT NULL,
                startMs INTEGER NOT NULL,
                endMs INTEGER NOT NULL,
                segmentId INTEGER NOT NULL,
                prompt TEXT NOT NULL,
                localPath TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(trackId) REFERENCES tracks(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS idx_contemplation_unique " +
                "ON contemplation_images(trackId, startMs)"
        )
    }

    /** Parca icinde fon muziginin yumusakca susturulacagi bolgeler (v5). */
    private fun createBedMuteTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS bed_mute_cues(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                trackId INTEGER NOT NULL,
                startMs INTEGER NOT NULL,
                endMs INTEGER NOT NULL,
                label TEXT NOT NULL,
                source TEXT NOT NULL,
                isEnabled INTEGER NOT NULL,
                FOREIGN KEY(trackId) REFERENCES tracks(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_bed_mute_track_time " +
                "ON bed_mute_cues(trackId, startMs, endMs)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // v1 -> v2: recete ve zamanlayici tek bir "program" kavraminda birlesti.
        // Listeler, parcalar, anonslar ve bagli klasorler KORUNUR.
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS recipes")
            db.execSQL("DROP TABLE IF EXISTS schedules")
            createProgramTables(db)
        }
        if (oldVersion < 3) createKnowledgeTables(db)
        if (oldVersion < 4) createContemplationTables(db)
        if (oldVersion < 5) createBedMuteTables(db)
    }

    // ── Yardimcilar ─────────────────────────────────────────────────────────

    private inline fun <T> query(sql: String, args: Array<String>? = null, map: (Cursor) -> T): List<T> {
        val out = ArrayList<T>()
        readableDatabase.rawQuery(sql, args).use { c ->
            while (c.moveToNext()) out.add(map(c))
        }
        return out
    }

    private fun countOf(sql: String, args: Array<String>? = null): Int =
        readableDatabase.rawQuery(sql, args).use { c ->
            if (c.moveToFirst()) c.getInt(0) else 0
        }

    private fun changed(): Long { DbSignal.bump(); return 0L }

    // ── Listeler ────────────────────────────────────────────────────────────

    private fun readPlaylist(c: Cursor) = Playlist(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        name = c.getString(c.getColumnIndexOrThrow("name")),
        kind = c.getString(c.getColumnIndexOrThrow("kind")),
        weight = c.getInt(c.getColumnIndexOrThrow("weight")),
        color = c.getInt(c.getColumnIndexOrThrow("color")),
        sortOrder = c.getInt(c.getColumnIndexOrThrow("sortOrder"))
    )

    private fun valuesOf(p: Playlist) = ContentValues().apply {
        put("name", p.name)
        put("kind", p.kind)
        put("weight", p.weight)
        put("color", p.color)
        put("sortOrder", p.sortOrder)
    }

    fun playlists(): List<Playlist> =
        query("SELECT * FROM playlists ORDER BY sortOrder ASC, id ASC") { readPlaylist(it) }

    fun playlist(id: Long): Playlist? =
        query("SELECT * FROM playlists WHERE id = ? LIMIT 1", arrayOf(id.toString())) {
            readPlaylist(it)
        }.firstOrNull()

    fun playlistsOfKind(kind: PlaylistKind): List<Playlist> =
        query("SELECT * FROM playlists WHERE kind = ? ORDER BY sortOrder ASC", arrayOf(kind.name)) {
            readPlaylist(it)
        }

    fun playlistCount(): Int = countOf("SELECT COUNT(*) FROM playlists")

    fun insertPlaylist(p: Playlist): Long {
        val id = writableDatabase.insert("playlists", null, valuesOf(p))
        DbSignal.bump()
        return id
    }

    fun updatePlaylist(p: Playlist) {
        writableDatabase.update("playlists", valuesOf(p), "id = ?", arrayOf(p.id.toString()))
        changed()
    }

    fun deletePlaylist(id: Long) {
        writableDatabase.delete("playlists", "id = ?", arrayOf(id.toString()))
        changed()
    }

    // ── Parcalar ────────────────────────────────────────────────────────────

    private fun readTrack(c: Cursor) = Track(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        playlistId = c.getLong(c.getColumnIndexOrThrow("playlistId")),
        title = c.getString(c.getColumnIndexOrThrow("title")),
        artist = c.getString(c.getColumnIndexOrThrow("artist")),
        uri = c.getString(c.getColumnIndexOrThrow("uri")),
        durationMs = c.getLong(c.getColumnIndexOrThrow("durationMs")),
        sortOrder = c.getInt(c.getColumnIndexOrThrow("sortOrder")),
        lastPositionMs = c.getLong(c.getColumnIndexOrThrow("lastPositionMs")),
        playCount = c.getInt(c.getColumnIndexOrThrow("playCount")),
        lastPlayedAt = c.getLong(c.getColumnIndexOrThrow("lastPlayedAt")),
        addedAt = c.getLong(c.getColumnIndexOrThrow("addedAt"))
    )

    private fun valuesOf(t: Track) = ContentValues().apply {
        put("playlistId", t.playlistId)
        put("title", t.title)
        put("artist", t.artist)
        put("uri", t.uri)
        put("durationMs", t.durationMs)
        put("sortOrder", t.sortOrder)
        put("lastPositionMs", t.lastPositionMs)
        put("playCount", t.playCount)
        put("lastPlayedAt", t.lastPlayedAt)
        put("addedAt", t.addedAt)
    }

    fun tracks(playlistId: Long): List<Track> = query(
        "SELECT * FROM tracks WHERE playlistId = ? ORDER BY sortOrder ASC, id ASC",
        arrayOf(playlistId.toString())
    ) { readTrack(it) }

    fun tracks(): List<Track> =
        query("SELECT * FROM tracks ORDER BY playlistId ASC, sortOrder ASC, id ASC") { readTrack(it) }

    fun track(id: Long): Track? =
        query("SELECT * FROM tracks WHERE id = ? LIMIT 1", arrayOf(id.toString())) {
            readTrack(it)
        }.firstOrNull()

    fun trackCount(playlistId: Long): Int =
        countOf("SELECT COUNT(*) FROM tracks WHERE playlistId = ?", arrayOf(playlistId.toString()))

    fun totalTrackCount(): Int = countOf("SELECT COUNT(*) FROM tracks")

    fun trackUris(playlistId: Long): List<String> = query(
        "SELECT uri FROM tracks WHERE playlistId = ?", arrayOf(playlistId.toString())
    ) { it.getString(0) }

    /** Var olanlari atlar; eklenen satir sayisini dondurur. */
    fun insertTracks(items: List<Track>): Int {
        if (items.isEmpty()) return 0
        val db = writableDatabase
        var added = 0
        db.beginTransaction()
        try {
            for (t in items) {
                val id = db.insertWithOnConflict(
                    "tracks", null, valuesOf(t), SQLiteDatabase.CONFLICT_IGNORE
                )
                if (id != -1L) added++
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        if (added > 0) DbSignal.bump()
        return added
    }

    fun updateTrack(t: Track) {
        writableDatabase.update("tracks", valuesOf(t), "id = ?", arrayOf(t.id.toString()))
        changed()
    }

    fun deleteTrack(id: Long) {
        writableDatabase.delete("tracks", "id = ?", arrayOf(id.toString()))
        changed()
    }

    /** Sik cagrilir; akislari tetiklemez ki arayuz bosuna yenilenmesin. */
    fun saveTrackPosition(id: Long, positionMs: Long) {
        writableDatabase.execSQL(
            "UPDATE tracks SET lastPositionMs = ? WHERE id = ?",
            arrayOf<Any>(positionMs, id)
        )
    }

    fun markTrackPlayed(id: Long, now: Long) {
        writableDatabase.execSQL(
            "UPDATE tracks SET playCount = playCount + 1, lastPlayedAt = ? WHERE id = ?",
            arrayOf<Any>(now, id)
        )
    }

    // ── Okunan yeri takip / tefekkur ──────────────────────────────────────

    private fun readTranscript(c: Cursor) = TranscriptSegment(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        trackId = c.getLong(c.getColumnIndexOrThrow("trackId")),
        startMs = c.getLong(c.getColumnIndexOrThrow("startMs")),
        endMs = c.getLong(c.getColumnIndexOrThrow("endMs")),
        text = c.getString(c.getColumnIndexOrThrow("bodyText")),
        source = c.getString(c.getColumnIndexOrThrow("source")),
        sortOrder = c.getInt(c.getColumnIndexOrThrow("sortOrder"))
    )

    fun transcriptSegments(): List<TranscriptSegment> = query(
        "SELECT * FROM transcript_segments ORDER BY trackId ASC, startMs ASC, sortOrder ASC"
    ) { readTranscript(it) }

    fun transcriptSegments(trackId: Long): List<TranscriptSegment> = query(
        "SELECT * FROM transcript_segments WHERE trackId = ? ORDER BY startMs ASC, sortOrder ASC",
        arrayOf(trackId.toString())
    ) { readTranscript(it) }

    fun replaceTranscript(trackId: Long, segments: List<TranscriptSegment>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("transcript_segments", "trackId = ?", arrayOf(trackId.toString()))
            segments.forEachIndexed { index, segment ->
                db.insert("transcript_segments", null, ContentValues().apply {
                    put("trackId", trackId)
                    put("startMs", segment.startMs)
                    put("endMs", segment.endMs)
                    put("bodyText", segment.text)
                    put("source", segment.source)
                    put("sortOrder", index)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        DbSignal.bump()
    }

    fun deleteTranscript(trackId: Long) {
        writableDatabase.delete("transcript_segments", "trackId = ?", arrayOf(trackId.toString()))
        changed()
    }

    private fun readContemplationImage(c: Cursor) = ContemplationImage(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        trackId = c.getLong(c.getColumnIndexOrThrow("trackId")),
        startMs = c.getLong(c.getColumnIndexOrThrow("startMs")),
        endMs = c.getLong(c.getColumnIndexOrThrow("endMs")),
        segmentId = c.getLong(c.getColumnIndexOrThrow("segmentId")),
        prompt = c.getString(c.getColumnIndexOrThrow("prompt")),
        localPath = c.getString(c.getColumnIndexOrThrow("localPath")),
        createdAt = c.getLong(c.getColumnIndexOrThrow("createdAt"))
    )

    fun contemplationImages(): List<ContemplationImage> = query(
        "SELECT * FROM contemplation_images ORDER BY trackId ASC, startMs ASC"
    ) { readContemplationImage(it) }

    fun contemplationImages(trackId: Long): List<ContemplationImage> = query(
        "SELECT * FROM contemplation_images WHERE trackId = ? ORDER BY startMs ASC",
        arrayOf(trackId.toString())
    ) { readContemplationImage(it) }

    fun upsertContemplationImage(image: ContemplationImage): Long {
        val id = writableDatabase.insertWithOnConflict(
            "contemplation_images", null, ContentValues().apply {
                if (image.id > 0L) put("id", image.id)
                put("trackId", image.trackId)
                put("startMs", image.startMs)
                put("endMs", image.endMs)
                put("segmentId", image.segmentId)
                put("prompt", image.prompt)
                put("localPath", image.localPath)
                put("createdAt", image.createdAt)
            }, SQLiteDatabase.CONFLICT_REPLACE
        )
        DbSignal.bump()
        return id
    }

    fun deleteContemplationImages(trackId: Long) {
        writableDatabase.delete("contemplation_images", "trackId = ?", arrayOf(trackId.toString()))
        changed()
    }

    // ── Ayet okunurken fonu susturma bolgeleri ─────────────────────────────

    private fun readBedMuteCue(c: Cursor) = BedMuteCue(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        trackId = c.getLong(c.getColumnIndexOrThrow("trackId")),
        startMs = c.getLong(c.getColumnIndexOrThrow("startMs")),
        endMs = c.getLong(c.getColumnIndexOrThrow("endMs")),
        label = c.getString(c.getColumnIndexOrThrow("label")),
        source = c.getString(c.getColumnIndexOrThrow("source")),
        isEnabled = c.getInt(c.getColumnIndexOrThrow("isEnabled")) != 0
    )

    fun bedMuteCues(): List<BedMuteCue> = query(
        "SELECT * FROM bed_mute_cues ORDER BY trackId ASC, startMs ASC"
    ) { readBedMuteCue(it) }

    fun bedMuteCues(trackId: Long): List<BedMuteCue> = query(
        "SELECT * FROM bed_mute_cues WHERE trackId = ? ORDER BY startMs ASC",
        arrayOf(trackId.toString())
    ) { readBedMuteCue(it) }

    fun replaceBedMuteCues(trackId: Long, cues: List<BedMuteCue>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("bed_mute_cues", "trackId = ?", arrayOf(trackId.toString()))
            cues.sortedBy { it.startMs }.forEach { cue ->
                if (cue.endMs <= cue.startMs) return@forEach
                db.insert("bed_mute_cues", null, ContentValues().apply {
                    put("trackId", trackId)
                    put("startMs", cue.startMs.coerceAtLeast(0L))
                    put("endMs", cue.endMs)
                    put("label", cue.label)
                    put("source", cue.source)
                    put("isEnabled", if (cue.isEnabled) 1 else 0)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        DbSignal.bump()
    }

    fun deleteBedMuteCues(trackId: Long) {
        writableDatabase.delete("bed_mute_cues", "trackId = ?", arrayOf(trackId.toString()))
        changed()
    }

    // ── Bagli klasorler ─────────────────────────────────────────────────────

    private fun readFolder(c: Cursor) = LinkedFolder(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        playlistId = c.getLong(c.getColumnIndexOrThrow("playlistId")),
        folderKey = c.getString(c.getColumnIndexOrThrow("folderKey")),
        displayName = c.getString(c.getColumnIndexOrThrow("displayName")),
        lastSyncAt = c.getLong(c.getColumnIndexOrThrow("lastSyncAt")),
        lastAddedCount = c.getInt(c.getColumnIndexOrThrow("lastAddedCount"))
    )

    private fun valuesOf(f: LinkedFolder) = ContentValues().apply {
        put("playlistId", f.playlistId)
        put("folderKey", f.folderKey)
        put("displayName", f.displayName)
        put("lastSyncAt", f.lastSyncAt)
        put("lastAddedCount", f.lastAddedCount)
    }

    fun linkedFolders(): List<LinkedFolder> =
        query("SELECT * FROM linked_folders ORDER BY id ASC") { readFolder(it) }

    fun linkedFoldersOf(playlistId: Long): List<LinkedFolder> = query(
        "SELECT * FROM linked_folders WHERE playlistId = ?", arrayOf(playlistId.toString())
    ) { readFolder(it) }

    fun insertLinkedFolder(f: LinkedFolder): Long {
        val id = writableDatabase.insertWithOnConflict(
            "linked_folders", null, valuesOf(f), SQLiteDatabase.CONFLICT_REPLACE
        )
        DbSignal.bump()
        return id
    }

    fun updateLinkedFolder(f: LinkedFolder) {
        writableDatabase.update("linked_folders", valuesOf(f), "id = ?", arrayOf(f.id.toString()))
    }

    fun unlinkFolder(playlistId: Long, folderKey: String) {
        writableDatabase.delete(
            "linked_folders", "playlistId = ? AND folderKey = ?",
            arrayOf(playlistId.toString(), folderKey)
        )
        changed()
    }

    // ── Anonslar ────────────────────────────────────────────────────────────

    private fun readAnnouncement(c: Cursor) = Announcement(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        label = c.getString(c.getColumnIndexOrThrow("label")),
        text = c.getString(c.getColumnIndexOrThrow("text")),
        audioUri = c.getColumnIndexOrThrow("audioUri").let { if (c.isNull(it)) null else c.getString(it) },
        durationMs = c.getLong(c.getColumnIndexOrThrow("durationMs")),
        voiceSpeed = c.getFloat(c.getColumnIndexOrThrow("voiceSpeed")),
        voicePitch = c.getFloat(c.getColumnIndexOrThrow("voicePitch")),
        isEnabled = c.getInt(c.getColumnIndexOrThrow("isEnabled")) == 1,
        category = c.getString(c.getColumnIndexOrThrow("category")),
        sortOrder = c.getInt(c.getColumnIndexOrThrow("sortOrder"))
    )

    private fun valuesOf(a: Announcement) = ContentValues().apply {
        put("label", a.label)
        put("text", a.text)
        put("audioUri", a.audioUri)
        put("durationMs", a.durationMs)
        put("voiceSpeed", a.voiceSpeed)
        put("voicePitch", a.voicePitch)
        put("isEnabled", if (a.isEnabled) 1 else 0)
        put("category", a.category)
        put("sortOrder", a.sortOrder)
    }

    fun announcements(): List<Announcement> =
        query("SELECT * FROM announcements ORDER BY sortOrder ASC, id ASC") { readAnnouncement(it) }

    fun enabledAnnouncements(): List<Announcement> =
        query("SELECT * FROM announcements WHERE isEnabled = 1 ORDER BY sortOrder ASC, id ASC") {
            readAnnouncement(it)
        }

    fun announcement(id: Long): Announcement? =
        query("SELECT * FROM announcements WHERE id = ? LIMIT 1", arrayOf(id.toString())) {
            readAnnouncement(it)
        }.firstOrNull()

    fun announcementCount(): Int = countOf("SELECT COUNT(*) FROM announcements")

    /** id 0 ise ekler, degilse gunceller. */
    fun upsertAnnouncement(a: Announcement): Long {
        val db = writableDatabase
        val id = if (a.id == 0L) {
            db.insert("announcements", null, valuesOf(a))
        } else {
            db.update("announcements", valuesOf(a), "id = ?", arrayOf(a.id.toString()))
            a.id
        }
        DbSignal.bump()
        return id
    }

    fun deleteAnnouncement(id: Long) {
        writableDatabase.delete("announcements", "id = ?", arrayOf(id.toString()))
        changed()
    }

    // ── Programlar ──────────────────────────────────────────────────────────

    private fun readProgram(c: Cursor) = Program(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        name = c.getString(c.getColumnIndexOrThrow("name")),
        isEnabled = c.getInt(c.getColumnIndexOrThrow("isEnabled")) == 1,
        startHour = c.getInt(c.getColumnIndexOrThrow("startHour")),
        startMinute = c.getInt(c.getColumnIndexOrThrow("startMinute")),
        endHour = c.getInt(c.getColumnIndexOrThrow("endHour")),
        endMinute = c.getInt(c.getColumnIndexOrThrow("endMinute")),
        days = c.getString(c.getColumnIndexOrThrow("days")),
        autoStart = c.getInt(c.getColumnIndexOrThrow("autoStart")) == 1,
        mixJson = c.getString(c.getColumnIndexOrThrow("mixJson")),
        trackOrder = c.getString(c.getColumnIndexOrThrow("trackOrder")),
        masterVolume = c.getFloat(c.getColumnIndexOrThrow("masterVolume")),
        volumesJson = c.getString(c.getColumnIndexOrThrow("volumesJson")),
        bedEnabled = c.getInt(c.getColumnIndexOrThrow("bedEnabled")) == 1,
        bedPlaylistId = c.getLong(c.getColumnIndexOrThrow("bedPlaylistId")),
        bedVolume = c.getFloat(c.getColumnIndexOrThrow("bedVolume")),
        spotEveryN = c.getInt(c.getColumnIndexOrThrow("spotEveryN")),
        announceClock = c.getInt(c.getColumnIndexOrThrow("announceClock")) == 1,
        hourlyPlaylistId = c.getLong(c.getColumnIndexOrThrow("hourlyPlaylistId")),
        timedSpotsJson = c.getString(c.getColumnIndexOrThrow("timedSpotsJson")),
        fadeMs = c.getInt(c.getColumnIndexOrThrow("fadeMs")),
        crossfade = c.getInt(c.getColumnIndexOrThrow("crossfade")) == 1,
        normalizeVolume = c.getInt(c.getColumnIndexOrThrow("normalizeVolume")) == 1,
        quietStartHour = c.getInt(c.getColumnIndexOrThrow("quietStartHour")),
        quietEndHour = c.getInt(c.getColumnIndexOrThrow("quietEndHour")),
        rulesJson = c.getString(c.getColumnIndexOrThrow("rulesJson")),
        isActive = c.getInt(c.getColumnIndexOrThrow("isActive")) == 1,
        sortOrder = c.getInt(c.getColumnIndexOrThrow("sortOrder"))
    )

    private fun valuesOf(p: Program) = ContentValues().apply {
        put("name", p.name)
        put("isEnabled", if (p.isEnabled) 1 else 0)
        put("startHour", p.startHour)
        put("startMinute", p.startMinute)
        put("endHour", p.endHour)
        put("endMinute", p.endMinute)
        put("days", p.days)
        put("autoStart", if (p.autoStart) 1 else 0)
        put("mixJson", p.mixJson)
        put("trackOrder", p.trackOrder)
        put("masterVolume", p.masterVolume)
        put("volumesJson", p.volumesJson)
        put("bedEnabled", if (p.bedEnabled) 1 else 0)
        put("bedPlaylistId", p.bedPlaylistId)
        put("bedVolume", p.bedVolume)
        put("spotEveryN", p.spotEveryN)
        put("announceClock", if (p.announceClock) 1 else 0)
        put("hourlyPlaylistId", p.hourlyPlaylistId)
        put("timedSpotsJson", p.timedSpotsJson)
        put("fadeMs", p.fadeMs)
        put("crossfade", if (p.crossfade) 1 else 0)
        put("normalizeVolume", if (p.normalizeVolume) 1 else 0)
        put("quietStartHour", p.quietStartHour)
        put("quietEndHour", p.quietEndHour)
        put("rulesJson", p.rulesJson)
        put("isActive", if (p.isActive) 1 else 0)
        put("sortOrder", p.sortOrder)
    }

    fun programs(): List<Program> =
        query("SELECT * FROM programs ORDER BY sortOrder ASC, id ASC") { readProgram(it) }

    fun activeProgram(): Program? =
        query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1") { readProgram(it) }.firstOrNull()

    fun program(id: Long): Program? =
        query("SELECT * FROM programs WHERE id = ? LIMIT 1", arrayOf(id.toString())) {
            readProgram(it)
        }.firstOrNull()

    fun programCount(): Int = countOf("SELECT COUNT(*) FROM programs")

    /** Saati gelince kendiliginden baslamasi gereken programlar. */
    fun autoStartPrograms(): List<Program> =
        query("SELECT * FROM programs WHERE isEnabled = 1 AND autoStart = 1 AND startHour >= 0") {
            readProgram(it)
        }

    fun upsertProgram(p: Program): Long {
        val db = writableDatabase
        val id = if (p.id == 0L) {
            db.insert("programs", null, valuesOf(p))
        } else {
            db.update("programs", valuesOf(p), "id = ?", arrayOf(p.id.toString()))
            p.id
        }
        DbSignal.bump()
        return id
    }

    fun deleteProgram(id: Long) {
        writableDatabase.delete("programs", "id = ?", arrayOf(id.toString()))
        changed()
    }

    fun activateProgram(id: Long) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("UPDATE programs SET isActive = 0")
            db.execSQL("UPDATE programs SET isActive = 1 WHERE id = ?", arrayOf<Any>(id))
            db.execSQL("UPDATE smart_flows SET isActive = 0")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        changed()
    }

    // ── Akilli kaynak katalogu ─────────────────────────────────────────────

    private fun readKnowledge(c: Cursor) = KnowledgeItem(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        type = c.getString(c.getColumnIndexOrThrow("type")),
        title = c.getString(c.getColumnIndexOrThrow("title")),
        collection = c.getString(c.getColumnIndexOrThrow("collectionName")),
        section = c.getString(c.getColumnIndexOrThrow("sectionName")),
        reference = c.getString(c.getColumnIndexOrThrow("reference")),
        summary = c.getString(c.getColumnIndexOrThrow("summary")),
        text = c.getString(c.getColumnIndexOrThrow("bodyText")),
        topicsJson = c.getString(c.getColumnIndexOrThrow("topicsJson")),
        keywordsJson = c.getString(c.getColumnIndexOrThrow("keywordsJson")),
        sourceUrl = c.getString(c.getColumnIndexOrThrow("sourceUrl")),
        trackId = c.getLong(c.getColumnIndexOrThrow("trackId")),
        isReviewed = c.getInt(c.getColumnIndexOrThrow("isReviewed")) == 1,
        isEnabled = c.getInt(c.getColumnIndexOrThrow("isEnabled")) == 1,
        voiceSpeed = c.getFloat(c.getColumnIndexOrThrow("voiceSpeed")),
        voicePitch = c.getFloat(c.getColumnIndexOrThrow("voicePitch")),
        sortOrder = c.getInt(c.getColumnIndexOrThrow("sortOrder"))
    )

    private fun valuesOf(item: KnowledgeItem) = ContentValues().apply {
        put("type", item.type)
        put("title", item.title)
        put("collectionName", item.collection)
        put("sectionName", item.section)
        put("reference", item.reference)
        put("summary", item.summary)
        put("bodyText", item.text)
        put("topicsJson", item.topicsJson)
        put("keywordsJson", item.keywordsJson)
        put("sourceUrl", item.sourceUrl)
        put("trackId", item.trackId)
        put("isReviewed", if (item.isReviewed) 1 else 0)
        put("isEnabled", if (item.isEnabled) 1 else 0)
        put("voiceSpeed", item.voiceSpeed)
        put("voicePitch", item.voicePitch)
        put("sortOrder", item.sortOrder)
    }

    fun knowledgeItems(): List<KnowledgeItem> =
        query("SELECT * FROM knowledge_items ORDER BY sortOrder ASC, id ASC") { readKnowledge(it) }

    fun knowledgeItem(id: Long): KnowledgeItem? =
        query("SELECT * FROM knowledge_items WHERE id = ? LIMIT 1", arrayOf(id.toString())) {
            readKnowledge(it)
        }.firstOrNull()

    fun knowledgeCount(): Int = countOf("SELECT COUNT(*) FROM knowledge_items")

    fun upsertKnowledge(item: KnowledgeItem): Long {
        val db = writableDatabase
        val id = if (item.id == 0L) db.insert("knowledge_items", null, valuesOf(item)) else {
            db.update("knowledge_items", valuesOf(item), "id = ?", arrayOf(item.id.toString()))
            item.id
        }
        DbSignal.bump()
        return id
    }

    fun deleteKnowledge(id: Long) {
        writableDatabase.delete("knowledge_items", "id = ?", arrayOf(id.toString()))
        changed()
    }

    // ── Akilli yayin akislari ──────────────────────────────────────────────

    private fun readSmartFlow(c: Cursor) = SmartFlow(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        name = c.getString(c.getColumnIndexOrThrow("name")),
        request = c.getString(c.getColumnIndexOrThrow("request")),
        itemsJson = c.getString(c.getColumnIndexOrThrow("itemsJson")),
        isActive = c.getInt(c.getColumnIndexOrThrow("isActive")) == 1,
        loop = c.getInt(c.getColumnIndexOrThrow("loop")) == 1,
        bedEnabled = c.getInt(c.getColumnIndexOrThrow("bedEnabled")) == 1,
        bedPlaylistId = c.getLong(c.getColumnIndexOrThrow("bedPlaylistId")),
        bedVolume = c.getFloat(c.getColumnIndexOrThrow("bedVolume")),
        createdAt = c.getLong(c.getColumnIndexOrThrow("createdAt")),
        updatedAt = c.getLong(c.getColumnIndexOrThrow("updatedAt"))
    )

    private fun valuesOf(flow: SmartFlow) = ContentValues().apply {
        put("name", flow.name)
        put("request", flow.request)
        put("itemsJson", flow.itemsJson)
        put("isActive", if (flow.isActive) 1 else 0)
        put("loop", if (flow.loop) 1 else 0)
        put("bedEnabled", if (flow.bedEnabled) 1 else 0)
        put("bedPlaylistId", flow.bedPlaylistId)
        put("bedVolume", flow.bedVolume)
        put("createdAt", flow.createdAt)
        put("updatedAt", flow.updatedAt)
    }

    fun smartFlows(): List<SmartFlow> =
        query("SELECT * FROM smart_flows ORDER BY updatedAt DESC, id DESC") { readSmartFlow(it) }

    fun smartFlow(id: Long): SmartFlow? =
        query("SELECT * FROM smart_flows WHERE id = ? LIMIT 1", arrayOf(id.toString())) {
            readSmartFlow(it)
        }.firstOrNull()

    fun activeSmartFlow(): SmartFlow? =
        query("SELECT * FROM smart_flows WHERE isActive = 1 LIMIT 1") { readSmartFlow(it) }
            .firstOrNull()

    fun upsertSmartFlow(flow: SmartFlow): Long {
        val db = writableDatabase
        val id = if (flow.id == 0L) db.insert("smart_flows", null, valuesOf(flow)) else {
            db.update("smart_flows", valuesOf(flow), "id = ?", arrayOf(flow.id.toString()))
            flow.id
        }
        DbSignal.bump()
        return id
    }

    fun deleteSmartFlow(id: Long) {
        writableDatabase.delete("smart_flows", "id = ?", arrayOf(id.toString()))
        changed()
    }

    fun activateSmartFlow(id: Long) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("UPDATE smart_flows SET isActive = 0")
            db.execSQL("UPDATE smart_flows SET isActive = 1 WHERE id = ?", arrayOf<Any>(id))
            db.execSQL("UPDATE programs SET isActive = 0")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        changed()
    }

    fun deactivateSmartFlows() {
        writableDatabase.execSQL("UPDATE smart_flows SET isActive = 0")
        changed()
    }

    // ── Genel ayarlar (anahtar-deger) ───────────────────────────────────────

    fun setting(key: String): String? =
        query("SELECT v FROM settings WHERE k = ? LIMIT 1", arrayOf(key)) { it.getString(0) }
            .firstOrNull()

    fun putSetting(key: String, value: String) {
        writableDatabase.insertWithOnConflict(
            "settings", null,
            ContentValues().apply { put("k", key); put("v", value) },
            SQLiteDatabase.CONFLICT_REPLACE
        )
        DbSignal.bump()
    }

    fun prayerSettings(): PrayerSettings {
        val raw = setting(KEY_PRAYER) ?: return PrayerSettings()
        return runCatching {
            val o = JSONObject(raw)
            PrayerSettings(
                enabled = o.optBoolean("enabled", false),
                latitude = o.optDouble("lat", 41.0082),
                longitude = o.optDouble("lng", 28.9784),
                cityName = o.optString("city", "Istanbul"),
                fajrAngle = o.optDouble("fajr", 18.0),
                ishaAngle = o.optDouble("isha", 17.0),
                muteBeforeMinutes = o.optInt("before", 0),
                muteAfterMinutes = o.optInt("after", 10),
                adhanPlaylistId = o.optLong("adhan", -1L),
                activePrayers = o.optString("active", "11111")
            )
        }.getOrDefault(PrayerSettings())
    }

    fun putPrayerSettings(s: PrayerSettings) {
        val o = JSONObject().apply {
            put("enabled", s.enabled)
            put("lat", s.latitude)
            put("lng", s.longitude)
            put("city", s.cityName)
            put("fajr", s.fajrAngle)
            put("isha", s.ishaAngle)
            put("before", s.muteBeforeMinutes)
            put("after", s.muteAfterMinutes)
            put("adhan", s.adhanPlaylistId)
            put("active", s.activePrayers)
        }
        putSetting(KEY_PRAYER, o.toString())
    }

    fun aiSettings(): AiSettings {
        val raw = setting(KEY_AI) ?: return AiSettings()
        return runCatching {
            val o = JSONObject(raw)
            AiSettings(
                enabled = o.optBoolean("enabled", false),
                endpoint = o.optString("endpoint", AiSettings().endpoint),
                model = o.optString("model", AiSettings().model),
                imageModel = o.optString("imageModel", AiSettings().imageModel),
                imageQuality = o.optString("imageQuality", AiSettings().imageQuality),
                visualStyle = o.optString("visualStyle", AiSettings().visualStyle)
            )
        }.getOrDefault(AiSettings())
    }

    fun putAiSettings(settings: AiSettings) {
        putSetting(KEY_AI, JSONObject().apply {
            put("enabled", settings.enabled)
            put("endpoint", settings.endpoint.trim())
            put("model", settings.model.trim())
            put("imageModel", settings.imageModel.trim())
            put("imageQuality", settings.imageQuality)
            put("visualStyle", settings.visualStyle.trim())
        }.toString())
    }

    /** Ham tablo dokumu - yedekleme icin. */
    fun dumpTable(table: String): List<Map<String, String?>> {
        val out = ArrayList<Map<String, String?>>()
        readableDatabase.rawQuery("SELECT * FROM $table", null).use { c ->
            while (c.moveToNext()) {
                val row = LinkedHashMap<String, String?>()
                for (i in 0 until c.columnCount) row[c.getColumnName(i)] = c.getString(i)
                out.add(row)
            }
        }
        return out
    }

    /** Yedekten geri yukleme - once tabloyu temizler. */
    fun restoreTable(table: String, rows: List<Map<String, String?>>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(table, null, null)
            for (row in rows) {
                val cv = ContentValues()
                row.forEach { (k, v) -> if (v == null) cv.putNull(k) else cv.put(k, v) }
                db.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        DbSignal.bump()
    }
}
