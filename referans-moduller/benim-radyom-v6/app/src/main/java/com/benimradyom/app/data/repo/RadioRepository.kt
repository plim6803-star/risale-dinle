package com.benimradyom.app.data.repo

import android.content.Context
import com.benimradyom.app.data.db.DbSignal
import com.benimradyom.app.data.db.RadioDb
import com.benimradyom.app.data.media.DeviceTrack
import com.benimradyom.app.data.media.MediaStoreScanner
import com.benimradyom.app.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** Bir klasor senkronunun sonucu. */
data class SyncResult(val playlistName: String, val addedCount: Int)

class RadioRepository(context: Context) {

    private val app = context.applicationContext
    private val db = RadioDb.get(app)

    /** Her yazma isleminden sonra yeniden sorgulayan akis. */
    private fun <T> observe(read: () -> T): Flow<T> =
        DbSignal.version.map { read() }.flowOn(Dispatchers.IO).distinctUntilChanged()

    // ── Listeler ────────────────────────────────────────────────────────────

    fun observePlaylists(): Flow<List<Playlist>> = observe { db.playlists() }

    suspend fun playlists(): List<Playlist> = io { db.playlists() }
    suspend fun playlist(id: Long): Playlist? = io { db.playlist(id) }
    suspend fun playlistsOfKind(kind: PlaylistKind): List<Playlist> = io { db.playlistsOfKind(kind) }

    suspend fun createPlaylist(name: String, kind: PlaylistKind, color: Int): Long = io {
        db.insertPlaylist(
            Playlist(name = name, kind = kind.name, color = color, sortOrder = db.playlistCount())
        )
    }

    suspend fun updatePlaylist(p: Playlist) = io { db.updatePlaylist(p) }
    suspend fun deletePlaylist(p: Playlist) = io {
        db.tracks(p.id).flatMap { db.contemplationImages(it.id) }
            .forEach { runCatching { java.io.File(it.localPath).delete() } }
        db.deletePlaylist(p.id)
    }

    // ── Parcalar ────────────────────────────────────────────────────────────

    fun observeTracks(playlistId: Long): Flow<List<Track>> = observe { db.tracks(playlistId) }
    fun observeAllTracks(): Flow<List<Track>> = observe { db.tracks() }
    fun observeHasTracks(): Flow<Boolean> = observe { db.totalTrackCount() > 0 }

    suspend fun tracks(playlistId: Long): List<Track> = io { db.tracks(playlistId) }
    suspend fun tracks(): List<Track> = io { db.tracks() }
    suspend fun track(id: Long): Track? = io { db.track(id) }
    suspend fun trackCount(playlistId: Long): Int = io { db.trackCount(playlistId) }
    suspend fun deleteTrack(t: Track) = io {
        db.contemplationImages(t.id).forEach { runCatching { java.io.File(it.localPath).delete() } }
        db.deleteTrack(t.id)
    }

    suspend fun moveTrack(t: Track, targetPlaylistId: Long) = io {
        if (t.uri in db.trackUris(targetPlaylistId)) {
            db.deleteTrack(t.id)
        } else {
            db.updateTrack(
                t.copy(playlistId = targetPlaylistId, sortOrder = db.trackCount(targetPlaylistId))
            )
        }
    }

    /** Bir parcayi listede bir sira yukari/asagi tasir. */
    suspend fun shiftTrack(track: Track, delta: Int) = io {
        val list = db.tracks(track.playlistId).toMutableList()
        val from = list.indexOfFirst { it.id == track.id }
        if (from < 0) return@io
        val to = (from + delta).coerceIn(0, list.size - 1)
        if (to == from) return@io
        val item = list.removeAt(from)
        list.add(to, item)
        list.forEachIndexed { i, t -> if (t.sortOrder != i) db.updateTrack(t.copy(sortOrder = i)) }
    }

    /** Listenin tamamini verilen olcute gore yeniden siralar. */
    suspend fun sortPlaylist(playlistId: Long, order: TrackOrder) = io {
        val list = db.tracks(playlistId)
        val sorted = when (order) {
            TrackOrder.ISME_GORE -> list.sortedWith(naturalTitleComparator)
            TrackOrder.ESKIDEN_YENIYE -> list.sortedBy { it.addedAt }
            TrackOrder.YENIDEN_ESKIYE -> list.sortedByDescending { it.addedAt }
            TrackOrder.EN_AZ_CALAN -> list.sortedBy { it.lastPlayedAt }
            TrackOrder.KARISIK -> list.shuffled()
            TrackOrder.SIRALI -> list
        }
        sorted.forEachIndexed { i, t -> if (t.sortOrder != i) db.updateTrack(t.copy(sortOrder = i)) }
    }

    suspend fun savePosition(trackId: Long, positionMs: Long) = io {
        db.saveTrackPosition(trackId, positionMs)
    }

    suspend fun markPlayed(trackId: Long) = io {
        db.markTrackPlayed(trackId, System.currentTimeMillis())
    }

    /** Dogrudan calinabilir bir HTTPS ses adresini listeye ekler. */
    suspend fun addMediaLink(playlistId: Long, title: String, url: String): Int = io {
        val cleanUrl = url.trim()
        val parsed = runCatching { java.net.URI(cleanUrl) }.getOrNull()
        require(parsed?.scheme.equals("https", ignoreCase = true) && !parsed?.host.isNullOrBlank()) {
            "Gecerli bir HTTPS ses baglantisi girin"
        }
        if (cleanUrl in db.trackUris(playlistId)) return@io 0
        db.insertTracks(
            listOf(
                Track(
                    playlistId = playlistId,
                    title = title.trim().ifBlank {
                        parsed?.path?.substringAfterLast('/')?.substringBeforeLast('.')
                            ?.takeIf { it.isNotBlank() } ?: "Medya baglantisi"
                    },
                    artist = "Cevrim ici medya",
                    uri = cleanUrl,
                    durationMs = 0L,
                    sortOrder = db.trackCount(playlistId),
                    addedAt = System.currentTimeMillis()
                )
            )
        )
    }

    // ── Okunan yeri takip / tefekkur ──────────────────────────────────────

    fun observeTranscriptSegments(): Flow<List<TranscriptSegment>> =
        observe { db.transcriptSegments() }

    fun observeContemplationImages(): Flow<List<ContemplationImage>> =
        observe { db.contemplationImages() }

    fun observeBedMuteCues(): Flow<List<BedMuteCue>> = observe { db.bedMuteCues() }

    suspend fun transcriptSegments(trackId: Long): List<TranscriptSegment> =
        io { db.transcriptSegments(trackId) }

    suspend fun replaceTranscript(trackId: Long, segments: List<TranscriptSegment>) =
        io { db.replaceTranscript(trackId, segments) }

    suspend fun deleteTranscript(trackId: Long) = io { db.deleteTranscript(trackId) }

    suspend fun contemplationImages(trackId: Long): List<ContemplationImage> =
        io { db.contemplationImages(trackId) }

    suspend fun saveContemplationImage(image: ContemplationImage): Long =
        io { db.upsertContemplationImage(image) }

    suspend fun deleteContemplationImages(trackId: Long) = io {
        db.contemplationImages(trackId).forEach { runCatching { java.io.File(it.localPath).delete() } }
        db.deleteContemplationImages(trackId)
    }

    suspend fun bedMuteCues(trackId: Long): List<BedMuteCue> = io { db.bedMuteCues(trackId) }

    suspend fun replaceBedMuteCues(trackId: Long, cues: List<BedMuteCue>) =
        io { db.replaceBedMuteCues(trackId, cues) }

    suspend fun deleteBedMuteCues(trackId: Long) = io { db.deleteBedMuteCues(trackId) }

    /**
     * Cihaz kutuphanesinden secilen parcalari listeye ekler.
     *
     * Eklerken dosya adina gore dogal siralama yapilir: "01, 02, ... 10" dogru
     * siraya girer. Ders ve Kur'an listelerinde bolum sirasinin bozulmamasi icin
     * bu sart.
     */
    suspend fun addDeviceTracks(playlistId: Long, items: List<DeviceTrack>): Int = io {
        if (items.isEmpty()) return@io 0
        val existing = db.trackUris(playlistId).toHashSet()
        val fresh = items.filter { it.uri !in existing }.sortedWith(naturalDeviceComparator)
        if (fresh.isEmpty()) return@io 0

        var order = db.trackCount(playlistId)
        val now = System.currentTimeMillis()
        db.insertTracks(
            fresh.map { d ->
                Track(
                    playlistId = playlistId,
                    title = d.title,
                    artist = d.artist,
                    uri = d.uri,
                    durationMs = d.durationMs,
                    sortOrder = order++,
                    addedAt = now
                )
            }
        )
    }

    // ── Bagli klasorler (otomatik senkron) ──────────────────────────────────

    fun observeLinkedFolders(): Flow<List<LinkedFolder>> = observe { db.linkedFolders() }

    suspend fun linkedFolders(): List<LinkedFolder> = io { db.linkedFolders() }
    suspend fun linkedFoldersOf(playlistId: Long): List<LinkedFolder> =
        io { db.linkedFoldersOf(playlistId) }

    suspend fun linkFolder(playlistId: Long, folderKey: String, displayName: String): Long = io {
        db.insertLinkedFolder(
            LinkedFolder(playlistId = playlistId, folderKey = folderKey, displayName = displayName)
        )
    }

    suspend fun unlinkFolder(playlistId: Long, folderKey: String) =
        io { db.unlinkFolder(playlistId, folderKey) }

    suspend fun syncLinkedFolders(): List<SyncResult> = io {
        val links = db.linkedFolders()
        if (links.isEmpty()) return@io emptyList()
        if (!MediaStoreScanner.hasPermission(app)) return@io emptyList()

        val device = MediaStoreScanner.queryAll(app).groupBy { it.folderKey }
        val results = mutableListOf<SyncResult>()
        val now = System.currentTimeMillis()

        for (link in links) {
            val items = device[link.folderKey].orEmpty()
            if (items.isEmpty()) continue
            val existing = db.trackUris(link.playlistId).toHashSet()
            val fresh = items.filter { it.uri !in existing }.sortedWith(naturalDeviceComparator)
            var order = db.trackCount(link.playlistId)
            val added = db.insertTracks(
                fresh.map { d ->
                    Track(
                        playlistId = link.playlistId,
                        title = d.title,
                        artist = d.artist,
                        uri = d.uri,
                        durationMs = d.durationMs,
                        sortOrder = order++,
                        addedAt = now
                    )
                }
            )
            db.updateLinkedFolder(link.copy(lastSyncAt = now, lastAddedCount = added))
            if (added > 0) {
                results += SyncResult(db.playlist(link.playlistId)?.name ?: link.displayName, added)
            }
        }
        results
    }

    // ── Anonslar ────────────────────────────────────────────────────────────

    fun observeAnnouncements(): Flow<List<Announcement>> = observe { db.announcements() }

    suspend fun enabledAnnouncements(): List<Announcement> = io { db.enabledAnnouncements() }
    suspend fun announcement(id: Long): Announcement? = io { db.announcement(id) }
    suspend fun upsertAnnouncement(a: Announcement): Long = io { db.upsertAnnouncement(a) }
    suspend fun updateAnnouncement(a: Announcement) = io { db.upsertAnnouncement(a) }
    suspend fun deleteAnnouncement(a: Announcement) = io { db.deleteAnnouncement(a.id) }

    // ── Programlar ──────────────────────────────────────────────────────────

    fun observePrograms(): Flow<List<Program>> = observe { db.programs() }

    suspend fun programs(): List<Program> = io { db.programs() }
    suspend fun program(id: Long): Program? = io { db.program(id) }
    suspend fun upsertProgram(p: Program): Long = io { db.upsertProgram(p) }
    suspend fun deleteProgram(p: Program) = io { db.deleteProgram(p.id) }
    suspend fun activateProgram(id: Long) = io { db.activateProgram(id) }
    suspend fun autoStartPrograms(): List<Program> = io { db.autoStartPrograms() }

    /**
     * Su an gecerli program: kullanicinin veya zamanlayicinin en son etkin
     * yaptigi program. Etkin secim yoksa saate uyan program kullanilir.
     */
    suspend fun currentProgram(): Program? = io {
        val all = db.programs()
        val cal = java.util.Calendar.getInstance()
        val dayIndex = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
        val minuteOfDay = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
        all.firstOrNull { it.isActive && it.isEnabled }
            ?: all.firstOrNull { it.autoStart && it.coversNow(dayIndex, minuteOfDay) }
            ?: all.firstOrNull { it.isEnabled }
    }

    // ── Akilli kaynak katalogu ve kesin akislar ────────────────────────────

    fun observeKnowledgeItems(): Flow<List<KnowledgeItem>> = observe { db.knowledgeItems() }
    fun observeSmartFlows(): Flow<List<SmartFlow>> = observe { db.smartFlows() }
    fun observeAiSettings(): Flow<AiSettings> = observe { db.aiSettings() }

    suspend fun knowledgeItems(): List<KnowledgeItem> = io { db.knowledgeItems() }
    suspend fun knowledgeItem(id: Long): KnowledgeItem? = io { db.knowledgeItem(id) }
    suspend fun upsertKnowledge(item: KnowledgeItem): Long = io { db.upsertKnowledge(item) }
    suspend fun deleteKnowledge(item: KnowledgeItem) = io { db.deleteKnowledge(item.id) }

    suspend fun smartFlows(): List<SmartFlow> = io { db.smartFlows() }
    suspend fun smartFlow(id: Long): SmartFlow? = io { db.smartFlow(id) }
    suspend fun activeSmartFlow(): SmartFlow? = io { db.activeSmartFlow() }
    suspend fun upsertSmartFlow(flow: SmartFlow): Long = io { db.upsertSmartFlow(flow) }
    suspend fun deleteSmartFlow(flow: SmartFlow) = io { db.deleteSmartFlow(flow.id) }
    suspend fun activateSmartFlow(id: Long) = io { db.activateSmartFlow(id) }
    suspend fun deactivateSmartFlows() = io { db.deactivateSmartFlows() }

    suspend fun aiSettings(): AiSettings = io { db.aiSettings() }
    suspend fun saveAiSettings(settings: AiSettings) = io { db.putAiSettings(settings) }

    /**
     * Risale kartlarini dosya adina gore otomatik eslestirir. Emin olmadigi
     * kaydi baglamaz; kullanici katalog editorunden elle secebilir.
     */
    suspend fun autoMapKnowledgeTracks(): Int = io {
        val tracks = db.tracks()
        if (tracks.isEmpty()) return@io 0
        val alreadyUsed = db.knowledgeItems().mapNotNull { it.trackId.takeIf { id -> id > 0 } }.toMutableSet()
        var mapped = 0
        db.knowledgeItems()
            .filter { it.typeEnum == KnowledgeType.RISALE && it.trackId <= 0L }
            .forEach { item ->
                val best = tracks.asSequence()
                    .filter { it.id !in alreadyUsed }
                    .map { it to risaleMatchScore(item, it) }
                    .maxByOrNull { it.second }
                if (best != null && best.second >= 10) {
                    db.upsertKnowledge(item.copy(trackId = best.first.id))
                    alreadyUsed += best.first.id
                    mapped++
                }
            }
        mapped
    }

    // ── Namaz vakitleri ─────────────────────────────────────────────────────

    fun observePrayerSettings(): Flow<PrayerSettings> = observe { db.prayerSettings() }
    suspend fun prayerSettings(): PrayerSettings = io { db.prayerSettings() }
    suspend fun savePrayerSettings(s: PrayerSettings) = io { db.putPrayerSettings(s) }

    // ── Yedekleme ───────────────────────────────────────────────────────────

    private val backupTables = listOf(
        "playlists", "tracks", "linked_folders", "announcements", "programs",
        "knowledge_items", "smart_flows", "transcript_segments", "bed_mute_cues", "settings"
    )

    /** Tum ayarlari tek bir metin olarak dondurur (dosyaya yazilmak uzere). */
    suspend fun exportBackup(): String = io {
        val root = JSONObject()
        root.put("version", 5)
        root.put("createdAt", System.currentTimeMillis())
        backupTables.forEach { table ->
            val arr = JSONArray()
            db.dumpTable(table).forEach { row ->
                val o = JSONObject()
                row.forEach { (k, v) -> if (v == null) o.put(k, JSONObject.NULL) else o.put(k, v) }
                arr.put(o)
            }
            root.put(table, arr)
        }
        root.toString()
    }

    /** Yedegi geri yukler. Mevcut veriler silinir. */
    suspend fun importBackup(text: String): Boolean = io {
        runCatching {
            val root = JSONObject(text)
            // Sirali: once ust tablolar, sonra bagli olanlar.
            backupTables.forEach { table ->
                val arr = root.optJSONArray(table) ?: return@forEach
                val rows = (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    o.keys().asSequence().associateWith { k ->
                        if (o.isNull(k)) null else o.getString(k)
                    }
                }
                db.restoreTable(table, rows)
            }
            true
        }.getOrDefault(false)
    }

    // ── Ilk kurulum ─────────────────────────────────────────────────────────

    /**
     * Ilk acilista bos gelmesin diye varsayilan listeleri ve programlari kurar.
     */
    suspend fun seedIfEmpty(): Boolean = io {
        var seeded = false

        if (db.playlistCount() == 0) {
            seeded = true
            val defaults = listOf(
                Triple("Ezgi / Ilahi", PlaylistKind.MUSIC, 0xFF2E7D6F.toInt()),
                Triple("Ders / Sohbet", PlaylistKind.SPEECH, 0xFF3D6FB4.toInt()),
                Triple("Kur'an", PlaylistKind.SPEECH, 0xFF7B5EA7.toInt()),
                Triple("Motivasyon", PlaylistKind.SPOT, 0xFFD98324.toInt()),
                Triple("Fon Muzik", PlaylistKind.BED, 0xFF5B7A8C.toInt()),
                Triple("Ezan", PlaylistKind.PRAYER, 0xFF4C956C.toInt())
            )
            defaults.forEachIndexed { i, (name, kind, color) ->
                db.insertPlaylist(
                    Playlist(
                        name = name, kind = kind.name, color = color, sortOrder = i,
                        weight = when (kind) {
                            PlaylistKind.MUSIC -> 40
                            PlaylistKind.SPEECH -> 30
                            PlaylistKind.SPOT -> 20
                            PlaylistKind.PRAYER -> 0
                            PlaylistKind.BED -> 0
                        }
                    )
                )
            }
        }

        // Eski surumun olusturdugu "Ezan" listesini normal yayin akisindan
        // ayir. Boylece ezan sadece secilen namaz vaktinde calar.
        db.playlists().firstOrNull {
            it.name.equals("Ezan", ignoreCase = true) && it.kindEnum != PlaylistKind.PRAYER
        }?.let { db.updatePlaylist(it.copy(kind = PlaylistKind.PRAYER.name, weight = 0)) }

        if (db.programCount() == 0) {
            seeded = true
            val lists = db.playlists()
            val bed = lists.firstOrNull { it.kindEnum == PlaylistKind.BED }?.id ?: -1L
            val speech = lists.filter { it.kindEnum == PlaylistKind.SPEECH }
            val music = lists.filter { it.kindEnum == PlaylistKind.MUSIC }
            val quran = lists.firstOrNull { it.name.startsWith("Kur") }?.id ?: -1L

            fun mix(musicW: Int, speechW: Int): String = Program.mixToJson(
                buildMap {
                    music.forEach { put(it.id, musicW) }
                    speech.forEach { put(it.id, speechW) }
                }
            )

            val defaults = listOf(
                Program(
                    name = "Sabah", startHour = 6, endHour = 11, autoStart = true,
                    mixJson = mix(30, 40), spotEveryN = 3, announceClock = true,
                    hourlyPlaylistId = quran, bedPlaylistId = bed, sortOrder = 0
                ),
                Program(
                    name = "Gunduz", startHour = 11, endHour = 17, autoStart = true,
                    mixJson = mix(45, 25), spotEveryN = 4,
                    bedPlaylistId = bed, sortOrder = 1
                ),
                Program(
                    name = "Aksam", startHour = 17, endHour = 23, autoStart = true,
                    mixJson = mix(35, 40), spotEveryN = 5, announceClock = true,
                    bedPlaylistId = bed, sortOrder = 2
                ),
                Program(
                    name = "Gece", startHour = 23, endHour = 6, autoStart = true,
                    mixJson = mix(20, 50), spotEveryN = 0, bedEnabled = false,
                    bedPlaylistId = bed, quietStartHour = 0, quietEndHour = 5, sortOrder = 3
                )
            )
            val cal = java.util.Calendar.getInstance()
            val day = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
            val minute = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 +
                cal.get(java.util.Calendar.MINUTE)
            val activeIndex = defaults.indexOfFirst {
                it.autoStart && it.coversNow(day, minute)
            }.takeIf { it >= 0 } ?: 0
            defaults.forEachIndexed { index, program ->
                db.upsertProgram(program.copy(isActive = index == activeIndex))
            }
        }
        if (db.knowledgeCount() == 0) {
            seedKnowledgeCatalog()
            seeded = true
        }
        seeded
    }

    /**
     * Internetten kontrol edilen baslangic fihristi. Korunan eserlerin tam
     * metni uygulamaya kopyalanmaz; baslik, konu, kaynak ve referans tutulur.
     * Ayet/hadis okunacak metni kullanici kaynaktan kontrol edip onaylar.
     */
    private fun seedKnowledgeCatalog() {
        val risaleIndex = "https://konularlarisale.net/wp-content/uploads/2015/07/%C4%B0ki-boyutlu-Risale-i-Nur-fihristi..pdf"
        val erisale = "https://www.erisale.com/"
        val quran = "https://kuran.diyanet.gov.tr/"
        val hadith = "https://hadislerleislam.diyanet.gov.tr/icindekiler.php"

        fun tags(vararg values: String) = KnowledgeItem.listToJson(values.toList())
        val items = listOf(
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Meleklerin varligi ve vazifeleri",
                collection = "Sozler", section = "29. Soz - Mukaddime ve Birinci Esas",
                reference = "29. Soz: Mukaddime + 1. Esas",
                summary = "Meleklerin varligi, kainattaki vazifeleri ve gorunmeyen alem.",
                topicsJson = tags("melekler", "gayb", "nurani varliklar", "kainat"),
                keywordsJson = tags("melek", "ruhani", "vazife", "ibadet"),
                sourceUrl = risaleIndex, isReviewed = true, sortOrder = 0
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Cehennem nerededir?",
                collection = "Mektubat", section = "1. Mektup - Ucuncu Sual",
                reference = "1. Mektup: 3. Sual",
                summary = "Cehennemin yeri hakkindaki suale verilen izah.",
                topicsJson = tags("cehennem", "ahiret", "azap"),
                keywordsJson = tags("ates", "ceza", "gayb"),
                sourceUrl = risaleIndex, isReviewed = true, sortOrder = 1
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Meydan-i hasir nerededir?",
                collection = "Mektubat", section = "10. Mektup",
                reference = "10. Mektup",
                summary = "Hasir meydani, dirilis ve ahiret alemi hakkinda konu fihristi.",
                topicsJson = tags("hasir", "dirilis", "ahiret", "mahser"),
                keywordsJson = tags("meydan-i hasir", "kiyamet", "beka"),
                sourceUrl = risaleIndex, isReviewed = true, sortOrder = 2
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Hasir Risalesi",
                collection = "Sozler", section = "10. Soz",
                reference = "10. Soz",
                summary = "Dirilis, ahiret ve ilahi adalet uzerine temsil ve deliller.",
                topicsJson = tags("hasir", "ahiret", "dirilis", "adalet"),
                keywordsJson = tags("beka", "mahser", "cennet", "cehennem"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 3
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Imanin insana kazandirdiklari",
                collection = "Sozler", section = "23. Soz",
                reference = "23. Soz",
                summary = "Iman, insanin mahiyeti, acz ve kulluk perspektifi.",
                topicsJson = tags("iman", "insan", "kulluk", "umut"),
                keywordsJson = tags("acz", "fakr", "saadet", "motivasyon"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 4
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Namazin hikmeti",
                collection = "Sozler", section = "21. Soz - Birinci Makam",
                reference = "21. Soz, Birinci Makam",
                summary = "Namazin mana ve hikmeti; kulluk ve gunluk hayat.",
                topicsJson = tags("namaz", "ibadet", "kulluk"),
                keywordsJson = tags("vakit", "huzur", "dua"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 5
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Tevhid delilleri",
                collection = "Sozler", section = "22. Soz",
                reference = "22. Soz",
                summary = "Kainattan tevhid ve iman delilleri.",
                topicsJson = tags("tevhid", "iman", "kainat"),
                keywordsJson = tags("vahdaniyet", "delil", "yaratilis"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 6
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Kader ve cuz-i ihtiyari",
                collection = "Sozler", section = "26. Soz",
                reference = "26. Soz",
                summary = "Kader, insan iradesi, sorumluluk ve tevekkul.",
                topicsJson = tags("kader", "irade", "sorumluluk", "tevekkul"),
                keywordsJson = tags("cuz-i ihtiyari", "adalet", "secim"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 7
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Mu'cizat-i Ahmediye",
                collection = "Mektubat", section = "19. Mektup",
                reference = "19. Mektup",
                summary = "Hz. Muhammed'in (asm) mucizeleri ve risaleti.",
                topicsJson = tags("peygamber", "mucize", "risalet", "sunnet"),
                keywordsJson = tags("ahmediye", "nübüvvet", "hadis"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 8
            ),
            KnowledgeItem(
                type = KnowledgeType.RISALE.name,
                title = "Esma-i Husna pencereleri",
                collection = "Sozler", section = "33. Soz",
                reference = "33. Soz",
                summary = "Varliklardan Allah'in isimlerine acilan tefekkur pencereleri.",
                topicsJson = tags("esma-i husna", "tevhid", "tefekkur"),
                keywordsJson = tags("isimler", "kainat", "marifetullah"),
                sourceUrl = erisale, isReviewed = true, sortOrder = 9
            ),

            KnowledgeItem(
                type = KnowledgeType.VERSE.name,
                title = "Melekler ve cehennem uyarisi",
                collection = "Kur'an-i Kerim", section = "Tahrim Suresi",
                reference = "Tahrim 66:6",
                summary = "Aileyi atesten koruma sorumlulugu ve cehennemde gorevli melekler.",
                topicsJson = tags("melekler", "cehennem", "aile", "sorumluluk"),
                keywordsJson = tags("ates", "korunma", "itaat"),
                sourceUrl = "https://kuran.diyanet.gov.tr/tefsir/Tahr%C3%AEm-suresi/5235/6-ayet-tefsiri",
                sortOrder = 20
            ),
            KnowledgeItem(
                type = KnowledgeType.VERSE.name,
                title = "Cehennem atesi hakkinda uyari",
                collection = "Kur'an-i Kerim", section = "Bakara Suresi",
                reference = "Bakara 2:24",
                summary = "Insanlar ve taslarla tutuşturulan atese karsi uyari.",
                topicsJson = tags("cehennem", "uyari", "iman"),
                keywordsJson = tags("ates", "azap", "inkar"), sourceUrl = quran, sortOrder = 21
            ),
            KnowledgeItem(
                type = KnowledgeType.VERSE.name,
                title = "Meleklerin elciligi",
                collection = "Kur'an-i Kerim", section = "Fatir Suresi",
                reference = "Fatir 35:1",
                summary = "Meleklerin elciligi ve farkli kanatlarla yaratilisi.",
                topicsJson = tags("melekler", "yaratilis", "elcilik"),
                keywordsJson = tags("kanat", "hamd", "gokler"), sourceUrl = quran, sortOrder = 22
            ),
            KnowledgeItem(
                type = KnowledgeType.VERSE.name,
                title = "Allah'in rahmetinden umut kesmemek",
                collection = "Kur'an-i Kerim", section = "Zumer Suresi",
                reference = "Zumer 39:53",
                summary = "Hata eden kullara tevbe, bagislanma ve umut cagrisi.",
                topicsJson = tags("umut", "rahmet", "tevbe", "motivasyon"),
                keywordsJson = tags("bagislanma", "umitsizlik", "merhamet"), sourceUrl = quran, sortOrder = 23
            ),
            KnowledgeItem(
                type = KnowledgeType.VERSE.name,
                title = "Kalplerin huzuru",
                collection = "Kur'an-i Kerim", section = "Ra'd Suresi",
                reference = "Ra'd 13:28",
                summary = "Allah'i anmakla kalbin huzur bulmasi.",
                topicsJson = tags("huzur", "zikir", "kalp", "motivasyon"),
                keywordsJson = tags("Allah'i anmak", "sukunet", "iman"), sourceUrl = quran, sortOrder = 24
            ),
            KnowledgeItem(
                type = KnowledgeType.VERSE.name,
                title = "Zorlukla beraber kolaylik",
                collection = "Kur'an-i Kerim", section = "Insirah Suresi",
                reference = "Insirah 94:5-6",
                summary = "Her zorlukla beraber kolayligin bulundugunu hatirlatir.",
                topicsJson = tags("zorluk", "kolaylik", "sabir", "motivasyon"),
                keywordsJson = tags("umut", "ferahlama", "dayaniklilik"), sourceUrl = quran, sortOrder = 25
            ),

            KnowledgeItem(
                type = KnowledgeType.HADITH.name,
                title = "Meleklerin nurdan yaratilisi",
                collection = "Muslim", section = "Zuhd",
                reference = "Muslim, Zuhd 60 (M7495); Hadislerle Islam Cilt 1, s. 243",
                summary = "Meleklerin yaratilisina dair hadis kaydi.",
                topicsJson = tags("melekler", "yaratilis", "nur"),
                keywordsJson = tags("nurdan yaratilis", "gayb"),
                sourceUrl = "https://hadislerleislam.diyanet.gov.tr/sayfa.php?CILT=1&SAYFA=243",
                sortOrder = 40
            ),
            KnowledgeItem(
                type = KnowledgeType.HADITH.name,
                title = "Meleklere iman",
                collection = "Muslim", section = "Iman",
                reference = "Muslim, Iman 5 (M97); Hadislerle Islam Cilt 1, s. 529",
                summary = "Cibril hadisinde iman esaslari arasinda meleklere iman.",
                topicsJson = tags("melekler", "iman esaslari", "cibril hadisi"),
                keywordsJson = tags("iman", "kitaplar", "peygamberler", "ahiret"),
                sourceUrl = "https://hadislerleislam.diyanet.gov.tr/sayfa.php?CILT=1&SAYFA=529",
                sortOrder = 41
            ),
            KnowledgeItem(
                type = KnowledgeType.HADITH.name,
                title = "Cehennem: yakiti insan ve tas olan azap yeri",
                collection = "Hadislerle Islam", section = "Ahiret",
                reference = "Hadislerle Islam, Cilt 7, s. 649",
                summary = "Cehennem hakkindaki ayet ve hadislerin konu anlatimi.",
                topicsJson = tags("cehennem", "azap", "ahiret"),
                keywordsJson = tags("ates", "uyari", "hesap"),
                sourceUrl = "https://hadislerleislam.diyanet.gov.tr/sayfa.php?CILT=7&SAYFA=649",
                sortOrder = 42
            ),
            KnowledgeItem(
                type = KnowledgeType.HADITH.name,
                title = "Cehenneme goturen davranislardan sakinmak",
                collection = "Hadislerle Islam", section = "Ahiret",
                reference = "Hadislerle Islam, Cilt 7, s. 635",
                summary = "Cehenneme goturen yollar ve korunma yollarina dair konu basligi.",
                topicsJson = tags("cehennem", "gunah", "tevbe", "sorumluluk"),
                keywordsJson = tags("korunma", "amel", "ahlak"),
                sourceUrl = "https://hadislerleislam.diyanet.gov.tr/sayfa.php?CILT=7&SAYFA=635",
                sortOrder = 43
            ),
            KnowledgeItem(
                type = KnowledgeType.HADITH.name,
                title = "Hadislerle Islam konu fihristi",
                collection = "Hadislerle Islam", section = "Genel fihrist",
                reference = "Diyanet konu fihristi",
                summary = "Yeni hadis kartlari eklerken kullanilabilecek resmi konu dizini.",
                topicsJson = tags("hadis", "fihrist", "konu arama"),
                keywordsJson = tags("kaynak", "oneriler"), sourceUrl = hadith,
                isEnabled = false, sortOrder = 44
            )
        )
        items.forEach(db::upsertKnowledge)
    }

    private suspend inline fun <T> io(crossinline block: () -> T): T =
        withContext(Dispatchers.IO) { block() }

    companion object {
        /** "Bolum 2" < "Bolum 10" olacak sekilde dogal siralama. */
        private val naturalTitleComparator = Comparator<Track> { a, b ->
            naturalCompare(a.title, b.title)
        }

        private val naturalDeviceComparator = Comparator<DeviceTrack> { a, b ->
            val byName = naturalCompare(a.fileName.ifBlank { a.title }, b.fileName.ifBlank { b.title })
            if (byName != 0) byName else a.dateAdded.compareTo(b.dateAdded)
        }

        /** Metindeki sayilari sayi olarak karsilastirir. */
        fun naturalCompare(a: String, b: String): Int {
            var i = 0
            var j = 0
            while (i < a.length && j < b.length) {
                val ca = a[i]
                val cb = b[j]
                if (ca.isDigit() && cb.isDigit()) {
                    var si = i
                    var sj = j
                    while (i < a.length && a[i].isDigit()) i++
                    while (j < b.length && b[j].isDigit()) j++
                    val na = a.substring(si, i).trimStart('0').ifEmpty { "0" }
                    val nb = b.substring(sj, j).trimStart('0').ifEmpty { "0" }
                    if (na.length != nb.length) return na.length - nb.length
                    val cmp = na.compareTo(nb)
                    if (cmp != 0) return cmp
                } else {
                    val cmp = ca.lowercaseChar().compareTo(cb.lowercaseChar())
                    if (cmp != 0) return cmp
                    i++
                    j++
                }
            }
            return (a.length - i) - (b.length - j)
        }

        private fun risaleMatchScore(item: KnowledgeItem, track: Track): Int {
            fun norm(value: String) = value.lowercase()
                .replace('ı', 'i').replace('ğ', 'g').replace('ü', 'u')
                .replace('ş', 's').replace('ö', 'o').replace('ç', 'c')
                .replace(Regex("[^a-z0-9]+"), " ")
            val haystack = norm(track.title + " " + track.artist)
            val important = norm(item.reference + " " + item.section)
                .split(' ').filter { it.isNotBlank() && (it.all(Char::isDigit) || it.length >= 3) }
            val general = norm(item.title + " " + item.collection)
                .split(' ').filter { it.length >= 4 }
            return important.sumOf {
                if (!haystack.contains(it)) 0 else if (it.all(Char::isDigit)) 7 else 4
            } +
                general.sumOf { if (haystack.contains(it)) 1 else 0 }
        }
    }
}
