package com.benimradyom.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.benimradyom.app.data.ai.ApiKeyStore
import com.benimradyom.app.data.ai.AudioTranscriptionClient
import com.benimradyom.app.data.ai.ContemplationImageClient
import com.benimradyom.app.data.ai.QuranCueAnalyzer
import com.benimradyom.app.data.ai.SmartFlowPlanner
import com.benimradyom.app.data.media.DeviceFolder
import com.benimradyom.app.data.media.DeviceTrack
import com.benimradyom.app.data.media.MediaStoreScanner
import com.benimradyom.app.data.media.TranscriptParser
import com.benimradyom.app.data.model.Announcement
import com.benimradyom.app.data.model.AiSettings
import com.benimradyom.app.data.model.BedMuteCue
import com.benimradyom.app.data.model.BedMuteCueSource
import com.benimradyom.app.data.model.LinkedFolder
import com.benimradyom.app.data.model.KnowledgeItem
import com.benimradyom.app.data.model.Playlist
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.PrayerSettings
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.SmartFlow
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TrackOrder
import com.benimradyom.app.data.model.TranscriptSegment
import com.benimradyom.app.data.repo.RadioRepository
import com.benimradyom.app.engine.RadioEngineHolder
import com.benimradyom.app.engine.VoiceRecorder
import com.benimradyom.app.service.AlarmScheduler
import com.benimradyom.app.service.RadioService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Cihaz kutuphanesinin arayuzde tutulan hali. */
data class LibraryState(
    val loading: Boolean = false,
    val permissionGranted: Boolean = false,
    val allTracks: List<DeviceTrack> = emptyList(),
    val folders: List<DeviceFolder> = emptyList(),
    val artists: List<Pair<String, Int>> = emptyList(),
    val query: String = "",
    val openFolderKey: String? = null,
    val selected: Set<String> = emptySet()
) {
    val openFolder: DeviceFolder?
        get() = folders.firstOrNull { it.key == openFolderKey }

    val visibleTracks: List<DeviceTrack>
        get() {
            val base = if (openFolderKey != null) {
                allTracks.filter { it.folderKey == openFolderKey }
            } else allTracks
            if (query.isBlank()) return base
            val q = query.trim().lowercase()
            return base.filter {
                it.title.lowercase().contains(q) ||
                    it.artist.lowercase().contains(q) ||
                    it.folderName.lowercase().contains(q)
            }
        }

    val visibleFolders: List<DeviceFolder>
        get() {
            if (query.isBlank()) return folders
            val q = query.trim().lowercase()
            return folders.filter { it.name.lowercase().contains(q) }
        }
}

data class Toast(val id: Long, val text: String)

data class SmartGenerationState(
    val loading: Boolean = false,
    val message: String = "",
    val selectedFlowId: Long? = null,
    val usedAi: Boolean = false
)

data class MediaAiState(
    val trackId: Long = -1L,
    val transcribing: Boolean = false,
    val generatingImages: Boolean = false,
    val analyzingQuranCues: Boolean = false,
    val completed: Int = 0,
    val total: Int = 0,
    val message: String = ""
)

/** Arayuz, veri tabani, alarm ve radyo motoru arasindaki tek kopru. */
class RadioViewModel(app: Application) : AndroidViewModel(app) {
    private val ctx: Context get() = getApplication<Application>().applicationContext
    private val repo = RadioRepository(ctx)
    private val engine = RadioEngineHolder.get(ctx)
    private val recorder = VoiceRecorder(ctx)
    private val apiKeyStore = ApiKeyStore(ctx)
    private val planner = SmartFlowPlanner()
    private val transcriptionClient = AudioTranscriptionClient(ctx)
    private val imageClient = ContemplationImageClient(ctx)
    private val quranCueAnalyzer = QuranCueAnalyzer()

    val radioState = engine.state
    val playlists = repo.observePlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val announcements = repo.observeAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val programs = repo.observePrograms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val prayerSettings = repo.observePrayerSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PrayerSettings())
    val linkedFolders = repo.observeLinkedFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val hasTracks = repo.observeHasTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val allTracks = repo.observeAllTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val knowledgeItems = repo.observeKnowledgeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val smartFlows = repo.observeSmartFlows()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val aiSettings = repo.observeAiSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiSettings())
    val transcriptSegments = repo.observeTranscriptSegments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val contemplationImages = repo.observeContemplationImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val bedMuteCues = repo.observeBedMuteCues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _apiKeyConfigured = MutableStateFlow(apiKeyStore.hasKey())
    val apiKeyConfigured: StateFlow<Boolean> = _apiKeyConfigured.asStateFlow()
    private val _smartGeneration = MutableStateFlow(SmartGenerationState())
    val smartGeneration: StateFlow<SmartGenerationState> = _smartGeneration.asStateFlow()
    private val _mediaAi = MutableStateFlow(MediaAiState())
    val mediaAi: StateFlow<MediaAiState> = _mediaAi.asStateFlow()

    private val _library = MutableStateFlow(LibraryState())
    val library: StateFlow<LibraryState> = _library.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()
    private val _selectedTracks = MutableStateFlow<List<Track>>(emptyList())
    val selectedTracks: StateFlow<List<Track>> = _selectedTracks.asStateFlow()
    private var trackJob: Job? = null
    private val programWriteMutex = Mutex()
    private val prayerWriteMutex = Mutex()
    private val pendingPrograms = mutableMapOf<Long, Program>()
    private val programSaveJobs = mutableMapOf<Long, Job>()

    private val _toast = MutableStateFlow<Toast?>(null)
    val toast: StateFlow<Toast?> = _toast.asStateFlow()
    private val _recording = MutableStateFlow(false)
    val recording: StateFlow<Boolean> = _recording.asStateFlow()

    init {
        viewModelScope.launch {
            repo.seedIfEmpty()
            repo.autoMapKnowledgeTracks()
            AlarmScheduler.rescheduleAll(ctx)
            refreshLibrary()
            syncLinkedFolders()
        }
    }

    // ── Radyo ───────────────────────────────────────────────────────────────

    fun startRadio() = RadioService.send(ctx, RadioService.ACTION_START)
    fun togglePlay() {
        if (radioState.value.current == null) startRadio()
        else RadioService.send(ctx, RadioService.ACTION_PLAY_PAUSE)
    }
    fun next() = RadioService.send(ctx, RadioService.ACTION_NEXT)
    fun previous() = RadioService.send(ctx, RadioService.ACTION_PREV)
    fun stopRadio() = RadioService.send(ctx, RadioService.ACTION_STOP)
    fun seek(fraction: Float) = engine.seekTo(fraction)
    fun setSleepTimer(minutes: Int) = engine.setSleepTimer(minutes)

    fun playTrack(track: Track) {
        RadioService.ensureRunning(ctx)
        engine.playTrackNow(track.id)
    }

    // ── Cihaz kutuphanesi ──────────────────────────────────────────────────

    fun onPermissionResult(granted: Boolean) {
        _library.value = _library.value.copy(permissionGranted = granted)
        if (granted) refreshLibrary()
    }

    fun refreshLibrary() {
        viewModelScope.launch {
            val granted = MediaStoreScanner.hasPermission(ctx)
            _library.value = _library.value.copy(loading = true, permissionGranted = granted)
            if (!granted) {
                _library.value = _library.value.copy(loading = false)
                return@launch
            }
            val tracks = withContext(Dispatchers.IO) { MediaStoreScanner.queryAll(ctx) }
            _library.value = _library.value.copy(
                loading = false,
                allTracks = tracks,
                folders = MediaStoreScanner.folders(tracks),
                artists = MediaStoreScanner.artists(tracks)
            )
        }
    }

    fun setQuery(query: String) { _library.value = _library.value.copy(query = query) }
    fun openFolder(key: String?) {
        _library.value = _library.value.copy(openFolderKey = key, selected = emptySet(), query = "")
    }
    fun toggleSelection(uri: String) {
        val selected = _library.value.selected
        _library.value = _library.value.copy(
            selected = if (uri in selected) selected - uri else selected + uri
        )
    }
    fun selectAllVisible() {
        val visible = _library.value.visibleTracks.map { it.uri }.toSet()
        val selected = _library.value.selected
        _library.value = _library.value.copy(
            selected = if (visible.isNotEmpty() && selected.containsAll(visible)) {
                selected - visible
            } else selected + visible
        )
    }
    fun clearSelection() { _library.value = _library.value.copy(selected = emptySet()) }

    fun addSelectedTo(playlistId: Long) {
        val state = _library.value
        val items = state.allTracks.filter { it.uri in state.selected }
        if (items.isEmpty()) return
        viewModelScope.launch {
            val added = repo.addDeviceTracks(playlistId, items)
            val mapped = repo.autoMapKnowledgeTracks()
            val name = repo.playlist(playlistId)?.name.orEmpty()
            clearSelection()
            engine.onProgramChanged()
            showToast(
                if (added > 0) "$added ses '$name' listesine eklendi" +
                    if (mapped > 0) " · $mapped Risale kaydi eslesti" else ""
                else "Bu sesler zaten listedeydi"
            )
        }
    }

    fun addFolderTo(folderKey: String, playlistId: Long, keepInSync: Boolean) {
        val items = _library.value.allTracks.filter { it.folderKey == folderKey }
        if (items.isEmpty()) return
        viewModelScope.launch {
            val added = repo.addDeviceTracks(playlistId, items)
            val mapped = repo.autoMapKnowledgeTracks()
            val name = repo.playlist(playlistId)?.name.orEmpty()
            if (keepInSync) {
                val display = _library.value.folders.firstOrNull { it.key == folderKey }?.name
                    ?: folderKey
                repo.linkFolder(playlistId, folderKey, display)
            }
            engine.onProgramChanged()
            showToast(
                "$added yeni ses eklendi -> $name" +
                    if (keepInSync) " (klasor bagli)" else "" +
                    if (mapped > 0) " · $mapped Risale kaydi eslesti" else ""
            )
        }
    }

    fun addMediaLink(playlistId: Long, title: String, url: String) = viewModelScope.launch {
        runCatching { repo.addMediaLink(playlistId, title, url) }
            .onSuccess { added ->
                engine.onProgramChanged()
                showToast(
                    if (added > 0) "Medya baglantisi listeye eklendi"
                    else "Bu baglanti zaten listede"
                )
            }
            .onFailure { showToast(it.message ?: "Medya baglantisi eklenemedi") }
    }

    fun unlinkFolder(playlistId: Long, folderKey: String) = viewModelScope.launch {
        repo.unlinkFolder(playlistId, folderKey)
        showToast("Klasor baglantisi kaldirildi")
    }

    fun syncLinkedFolders() {
        viewModelScope.launch {
            val total = repo.syncLinkedFolders().sumOf { it.addedCount }
            if (total > 0) {
                repo.autoMapKnowledgeTracks()
                engine.onProgramChanged()
                showToast("$total yeni ses bagli klasorlerden eklendi")
            }
        }
    }

    // ── Listeler ve parca sirasi ────────────────────────────────────────────

    fun selectPlaylist(playlist: Playlist?) {
        _selectedPlaylist.value = playlist
        trackJob?.cancel()
        if (playlist == null) {
            _selectedTracks.value = emptyList()
            return
        }
        trackJob = viewModelScope.launch {
            repo.observeTracks(playlist.id).collect { _selectedTracks.value = it }
        }
    }

    fun createPlaylist(
        name: String,
        kind: PlaylistKind,
        color: Int,
        onCreated: (Long) -> Unit = {}
    ) = viewModelScope.launch {
        val id = repo.createPlaylist(name, kind, color)
        onCreated(id)
        showToast("'$name' listesi olusturuldu")
    }

    fun updatePlaylist(playlist: Playlist) = viewModelScope.launch {
        repo.updatePlaylist(playlist)
        _selectedPlaylist.value = playlist.takeIf { _selectedPlaylist.value?.id == it.id }
            ?: _selectedPlaylist.value
        engine.onProgramChanged()
    }

    fun deletePlaylist(playlist: Playlist) = viewModelScope.launch {
        repo.deletePlaylist(playlist)
        if (_selectedPlaylist.value?.id == playlist.id) selectPlaylist(null)
        engine.onProgramChanged()
    }

    fun deleteTrack(track: Track) = viewModelScope.launch {
        repo.deleteTrack(track)
        engine.onProgramChanged()
    }
    fun moveTrack(track: Track, targetPlaylistId: Long) = viewModelScope.launch {
        repo.moveTrack(track, targetPlaylistId)
        engine.onProgramChanged()
    }
    fun shiftTrack(track: Track, delta: Int) = viewModelScope.launch {
        repo.shiftTrack(track, delta)
        engine.onProgramChanged()
    }
    fun sortPlaylist(playlistId: Long, order: TrackOrder) = viewModelScope.launch {
        repo.sortPlaylist(playlistId, order)
        engine.onProgramChanged()
        showToast("Liste '${order.label}' duzenine gore siralandi")
    }

    // ── Anonslar ve kayit ──────────────────────────────────────────────────

    fun saveAnnouncement(announcement: Announcement) = viewModelScope.launch {
        repo.upsertAnnouncement(announcement)
        engine.onProgramChanged()
    }
    fun toggleAnnouncement(announcement: Announcement) = viewModelScope.launch {
        repo.updateAnnouncement(announcement.copy(isEnabled = !announcement.isEnabled))
        engine.onProgramChanged()
    }
    fun deleteAnnouncement(announcement: Announcement) = viewModelScope.launch {
        repo.deleteAnnouncement(announcement)
        recorder.deleteIfOwned(announcement.audioUri)
        engine.onProgramChanged()
    }
    fun previewText(text: String, speed: Float, pitch: Float) =
        engine.previewAnnouncement(text, speed, pitch)
    fun stopPreview() = engine.stopPreview()

    fun startRecording(): Boolean {
        val started = recorder.start()
        _recording.value = started
        if (!started) showToast("Kayit baslatilamadi; mikrofon iznini kontrol edin")
        return started
    }
    fun stopRecording(onResult: (Uri?, Long) -> Unit) {
        val result = recorder.stop()
        _recording.value = false
        if (result == null) {
            showToast("Kayit cok kisa; kaydedilmedi")
            onResult(null, 0L)
        } else onResult(result.first, result.second)
    }
    fun cancelRecording() {
        recorder.cancel()
        _recording.value = false
    }

    // ── Programlar ─────────────────────────────────────────────────────────

    fun createProgram(name: String) {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return
        viewModelScope.launch {
            val saved = persistProgram(Program(name = cleanName))
            engine.onProgramChanged()
            showToast("'${saved.name}' programi olusturuldu")
        }
    }

    /**
     * Editor kaydiricilari cok sik deger uretir. Son degeri kisa bir sure
     * bekleyip tek sefer yazmak arayuzu akici, veritabanini da sirali tutar.
     */
    fun saveProgram(program: Program) {
        if (program.id <= 0L) {
            viewModelScope.launch {
                persistProgram(program)
                engine.onProgramChanged()
            }
            return
        }
        pendingPrograms[program.id] = program
        programSaveJobs.remove(program.id)?.cancel()
        programSaveJobs[program.id] = viewModelScope.launch {
            delay(180L)
            val latest = pendingPrograms[program.id] ?: return@launch
            persistProgram(latest)
            if (pendingPrograms[program.id] == latest) pendingPrograms.remove(program.id)
            programSaveJobs.remove(program.id)
            engine.onProgramChanged()
        }
    }

    fun activateProgram(program: Program) {
        val latest = takePendingProgram(program)
        viewModelScope.launch {
            val enabled = latest.copy(isEnabled = true)
            programWriteMutex.withLock {
                if (!latest.isEnabled) repo.upsertProgram(enabled)
                repo.activateProgram(enabled.id)
            }
            AlarmScheduler.schedule(ctx, enabled)
            engine.onProgramChanged()
            showToast("'${enabled.name}' programi etkin")
        }
    }

    fun toggleProgram(program: Program) {
        val latest = takePendingProgram(program)
        viewModelScope.launch {
            val updated = latest.copy(isEnabled = !latest.isEnabled)
            programWriteMutex.withLock { repo.upsertProgram(updated) }
            if (updated.isEnabled) AlarmScheduler.schedule(ctx, updated)
            else AlarmScheduler.cancel(ctx, updated.id)
            engine.onProgramChanged()
        }
    }

    fun deleteProgram(program: Program) {
        val latest = takePendingProgram(program)
        viewModelScope.launch {
            AlarmScheduler.cancel(ctx, latest.id)
            programWriteMutex.withLock { repo.deleteProgram(latest) }
            engine.onProgramChanged()
            showToast("Program silindi")
        }
    }

    private suspend fun persistProgram(program: Program): Program = programWriteMutex.withLock {
        val id = repo.upsertProgram(program)
        val saved = if (program.id == 0L) program.copy(id = id) else program
        AlarmScheduler.schedule(ctx, saved)
        saved
    }

    private fun takePendingProgram(program: Program): Program {
        if (program.id <= 0L) return program
        programSaveJobs.remove(program.id)?.cancel()
        return pendingPrograms.remove(program.id) ?: program
    }

    // ── Namaz vakitleri ────────────────────────────────────────────────────

    fun savePrayerSettings(settings: PrayerSettings) = viewModelScope.launch {
        prayerWriteMutex.withLock { repo.savePrayerSettings(settings) }
        engine.onProgramChanged()
    }

    // ── Akilli yayin ve kaynak katalogu ────────────────────────────────────

    fun generateSmartFlow(request: String, targetMinutes: Int) {
        val clean = request.trim()
        if (clean.isBlank() || _smartGeneration.value.loading) return
        viewModelScope.launch {
            _smartGeneration.value = SmartGenerationState(loading = true, message = "Katalog taraniyor...")
            val result = runCatching {
                repo.autoMapKnowledgeTracks()
                val plan = planner.plan(
                    request = clean,
                    targetMinutes = targetMinutes,
                    catalog = repo.knowledgeItems(),
                    trackDurations = repo.tracks().associate { it.id to it.durationMs },
                    settings = repo.aiSettings(),
                    apiKey = apiKeyStore.load()
                )
                if (plan.entries.isEmpty()) error(plan.note)
                val bedId = repo.playlistsOfKind(PlaylistKind.BED).firstOrNull()?.id ?: -1L
                val flow = SmartFlow(
                    name = plan.name,
                    request = clean,
                    itemsJson = SmartFlow.entriesToJson(plan.entries),
                    bedPlaylistId = bedId
                )
                val id = repo.upsertSmartFlow(flow)
                Triple(id, plan.usedAi, plan.note)
            }
            result.onSuccess { (id, usedAi, note) ->
                _smartGeneration.value = SmartGenerationState(
                    loading = false, message = note, selectedFlowId = id, usedAi = usedAi
                )
                showToast("Yayin taslagi olusturuldu")
            }.onFailure {
                _smartGeneration.value = SmartGenerationState(
                    loading = false,
                    message = it.message ?: "Yayin taslagi olusturulamadi"
                )
                showToast(_smartGeneration.value.message)
            }
        }
    }

    fun saveSmartFlow(flow: SmartFlow) = viewModelScope.launch {
        val clean = flow.copy(name = flow.name.trim().ifBlank { "Akilli yayin" })
        val id = repo.upsertSmartFlow(clean)
        _smartGeneration.value = _smartGeneration.value.copy(selectedFlowId = id)
        if (flow.isActive) engine.onProgramChanged()
        showToast("Akis kaydedildi")
    }

    fun activateSmartFlow(flow: SmartFlow) = viewModelScope.launch {
        val clean = flow.copy(
            name = flow.name.trim().ifBlank { "Akilli yayin" },
            updatedAt = System.currentTimeMillis()
        )
        val id = repo.upsertSmartFlow(clean)
        repo.activateSmartFlow(id)
        engine.onProgramChanged()
        RadioService.send(ctx, RadioService.ACTION_START)
        _smartGeneration.value = _smartGeneration.value.copy(selectedFlowId = id)
        showToast("'${clean.name}' yayina alindi")
    }

    fun deleteSmartFlow(flow: SmartFlow) = viewModelScope.launch {
        repo.deleteSmartFlow(flow)
        engine.onProgramChanged()
        if (_smartGeneration.value.selectedFlowId == flow.id) {
            _smartGeneration.value = _smartGeneration.value.copy(selectedFlowId = null)
        }
        showToast("Akilli akis silindi")
    }

    fun saveKnowledge(item: KnowledgeItem) = viewModelScope.launch {
        repo.upsertKnowledge(item)
        engine.onProgramChanged()
        showToast("Kaynak karti kaydedildi")
    }

    fun deleteKnowledge(item: KnowledgeItem) = viewModelScope.launch {
        repo.deleteKnowledge(item)
        engine.onProgramChanged()
        showToast("Kaynak karti silindi")
    }

    fun autoMapKnowledge() = viewModelScope.launch {
        val count = repo.autoMapKnowledgeTracks()
        showToast(if (count > 0) "$count Risale kaydi sesle eslesti" else "Yeni otomatik eslesme bulunamadi")
    }

    fun previewKnowledge(item: KnowledgeItem) {
        if (item.text.isBlank()) showToast("Once okunacak metni ekleyin")
        else engine.previewAnnouncement(item.text, item.voiceSpeed, item.voicePitch)
    }

    fun saveAiConfiguration(settings: AiSettings, apiKey: String?) = viewModelScope.launch {
        repo.saveAiSettings(settings)
        if (apiKey != null) {
            if (apiKey.isBlank()) apiKeyStore.clear() else apiKeyStore.save(apiKey)
        }
        _apiKeyConfigured.value = apiKeyStore.hasKey()
        showToast(
            when {
                !settings.enabled -> "Yapay zeka kapali; yerel planlayici kullanilacak"
                _apiKeyConfigured.value -> "API ayarlari guvenli bicimde kaydedildi"
                else -> "Ayar kaydedildi; API kullanmak icin anahtar gerekli"
            }
        )
    }

    fun clearApiKey() = viewModelScope.launch {
        apiKeyStore.clear()
        _apiKeyConfigured.value = false
        showToast("API anahtari silindi")
    }

    // ── Okunan yeri takip ve tefekkur modu ────────────────────────────────

    fun importTranscript(trackId: Long, uri: Uri) = viewModelScope.launch {
        val result = runCatching {
            val track = repo.track(trackId) ?: error("Ses kaydi bulunamadi")
            val fileName = withContext(Dispatchers.IO) {
                ctx.contentResolver.query(
                    uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else uri.lastPathSegment.orEmpty()
                } ?: uri.lastPathSegment.orEmpty()
            }
            val content = withContext(Dispatchers.IO) {
                ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Metin dosyasi acilamadi")
            }
            val parsed = TranscriptParser.parse(fileName, content, track.id, track.durationMs)
            repo.replaceTranscript(track.id, parsed)
            repo.deleteContemplationImages(track.id)
            repo.deleteBedMuteCues(track.id)
            engine.onBedMuteCuesChanged(track.id)
            parsed
        }
        result.onSuccess { segments ->
            val approximate = segments.firstOrNull()?.isApproximate == true
            showToast(
                "${segments.size} metin bolumu eklendi" +
                    if (approximate) " · TXT zamanlari yaklasiktir" else ""
            )
        }.onFailure { showToast(it.message ?: "Metin ice aktarilamadi") }
    }

    fun transcribeTrack(track: Track) {
        if (mediaAiBusy()) return
        viewModelScope.launch {
            val key = apiKeyStore.load()
            if (key.isBlank()) {
                showToast("AI zaman kodu icin Ayarlar'dan API anahtari ekleyin")
                return@launch
            }
            _mediaAi.value = MediaAiState(
                trackId = track.id,
                transcribing = true,
                message = "Ses yukleniyor ve zaman kodlari cikariliyor..."
            )
            val result = runCatching {
                val settings = repo.aiSettings()
                withContext(Dispatchers.IO) {
                    transcriptionClient.transcribe(track, settings, key, ::reportTranscriptionProgress)
                }
            }
            result.onSuccess { segments ->
                repo.replaceTranscript(track.id, segments)
                // Gorseller eski metin bolumlerine bagliydi, yenilenmeli. Ayet
                // bolgelerinden ise yalnizca OTOMATIK olanlar gider; elle
                // onaylanmis olanlar korunur.
                repo.deleteContemplationImages(track.id)
                val kept = keepOnlyManualCues(track)
                engine.onBedMuteCuesChanged(track.id)
                _mediaAi.value = MediaAiState(
                    trackId = track.id,
                    message = buildString {
                        append("${segments.size} zaman kodlu bolum hazir")
                        if (kept > 0) append(" · $kept elle duzenlenmis ayet bolgesi korundu")
                    }
                )
                showToast("Okunan yer takibi hazir")
            }.onFailure {
                _mediaAi.value = MediaAiState(trackId = track.id, message = it.message.orEmpty())
                showToast(it.message ?: "Zaman kodu olusturulamadi")
            }
        }
    }

    fun generateContemplationImages(track: Track, count: Int) {
        if (mediaAiBusy()) return
        viewModelScope.launch {
            val key = apiKeyStore.load()
            if (key.isBlank()) {
                showToast("Gorsel uretmek icin Ayarlar'dan API anahtari ekleyin")
                return@launch
            }
            val segments = repo.transcriptSegments(track.id).filter { it.text.isNotBlank() }
            if (segments.isEmpty()) {
                showToast("Once bu sese zaman kodlu metin ekleyin")
                return@launch
            }
            val chosen = chooseEvenly(segments, count.coerceIn(1, 6))
            val existing = repo.contemplationImages(track.id)
                .filter { java.io.File(it.localPath).exists() }
                .associateBy { it.startMs }
            var completed = 0
            var generated = 0
            _mediaAi.value = MediaAiState(
                trackId = track.id,
                generatingImages = true,
                total = chosen.size,
                message = "Tefekkur gorselleri hazirlaniyor..."
            )
            val result = runCatching {
                chosen.forEach { segment ->
                    if (!existing.containsKey(segment.startMs)) {
                        val image = withContext(Dispatchers.IO) {
                            imageClient.generate(track, segment, repo.aiSettings(), key)
                        }
                        repo.saveContemplationImage(image)
                        generated++
                    }
                    completed++
                    _mediaAi.value = _mediaAi.value.copy(
                        completed = completed,
                        message = "$completed / ${chosen.size} gorsel hazir"
                    )
                }
            }
            result.onSuccess {
                _mediaAi.value = MediaAiState(
                    trackId = track.id,
                    completed = completed,
                    total = chosen.size,
                    message = if (generated == 0) "Gorseller zaten onbellekte" else "$generated yeni gorsel hazir"
                )
                showToast(_mediaAi.value.message)
            }.onFailure {
                _mediaAi.value = MediaAiState(
                    trackId = track.id,
                    completed = completed,
                    total = chosen.size,
                    message = it.message.orEmpty()
                )
                showToast("$completed gorselden sonra durdu: ${it.message ?: "API hatasi"}")
            }
        }
    }

    /**
     * Varsa kayitli zaman kodlu metni, yoksa once ses dosyasini kullanir.
     * Sonuc kullanici tarafindan duzenlenebilen fon sessizlik bolgeleridir.
     */
    fun analyzeQuranCues(track: Track) {
        if (mediaAiBusy()) return
        viewModelScope.launch {
            val key = apiKeyStore.load()
            if (key.isBlank()) {
                showToast("Ayet gecislerini analiz etmek icin API anahtari ekleyin")
                return@launch
            }
            _mediaAi.value = MediaAiState(
                trackId = track.id,
                analyzingQuranCues = true,
                message = "Zaman kodlu metin ayet okuma bolgeleri icin inceleniyor..."
            )
            val result = runCatching {
                var segments = repo.transcriptSegments(track.id)
                if (segments.isEmpty()) {
                    _mediaAi.value = _mediaAi.value.copy(
                        transcribing = true,
                        message = "Once ses yukleniyor ve zaman kodlari cikariliyor..."
                    )
                    val settings = repo.aiSettings()
                    segments = withContext(Dispatchers.IO) {
                        transcriptionClient.transcribe(track, settings, key, ::reportTranscriptionProgress)
                    }
                    repo.replaceTranscript(track.id, segments)
                    _mediaAi.value = _mediaAi.value.copy(
                        transcribing = false,
                        message = "Arapca ayet okuma bolgeleri belirleniyor..."
                    )
                }
                val settings = repo.aiSettings()
                val fresh = withContext(Dispatchers.IO) {
                    quranCueAnalyzer.analyze(track, segments, settings, key)
                }
                // Elle onaylanmis bolgeler korunur; cakisirsa elle olan kazanir.
                val existing = repo.bedMuteCues(track.id)
                val manual = existing.filter { it.source == BedMuteCueSource.ELLE.name }
                val added = fresh.filter { f ->
                    manual.none { m -> f.startMs < m.endMs && m.startMs < f.endMs }
                }
                repo.replaceBedMuteCues(track.id, (manual + added).sortedBy { it.startMs })
                engine.onBedMuteCuesChanged(track.id)
                added.size to manual.size
            }
            result.onSuccess { (added, kept) ->
                _mediaAi.value = MediaAiState(
                    trackId = track.id,
                    message = when {
                        added == 0 && kept == 0 ->
                            "Arapca ayet okunusu bulunamadi; zamanlari elle kontrol edin"
                        kept > 0 -> "$added yeni ayet bolgesi eklendi, $kept elle duzenlenmis bolge korundu"
                        else -> "$added ayet sessizlik bolgesi hazir"
                    }
                )
                showToast(_mediaAi.value.message)
            }.onFailure {
                _mediaAi.value = MediaAiState(trackId = track.id, message = it.message.orEmpty())
                showToast(it.message ?: "Ayet gecisleri analiz edilemedi")
            }
        }
    }

    fun saveBedMuteCues(track: Track, cues: List<BedMuteCue>) = viewModelScope.launch {
        val normalized = cues.map {
            val start = it.startMs.coerceAtLeast(0L)
            it.copy(
                id = 0L,
                trackId = track.id,
                startMs = start,
                endMs = it.endMs.coerceAtLeast(start + 500L),
                source = BedMuteCueSource.ELLE.name
            )
        }
        repo.replaceBedMuteCues(track.id, normalized)
        engine.onBedMuteCuesChanged(track.id)
        showToast("Ayet gecisleri kaydedildi")
    }

    fun deleteBedMuteCues(track: Track) = viewModelScope.launch {
        repo.deleteBedMuteCues(track.id)
        engine.onBedMuteCuesChanged(track.id)
        showToast("Ayet sessizlik bolgeleri silindi")
    }

    fun deleteTranscript(track: Track) = viewModelScope.launch {
        repo.deleteTranscript(track.id)
        repo.deleteContemplationImages(track.id)
        repo.deleteBedMuteCues(track.id)
        engine.onBedMuteCuesChanged(track.id)
        showToast("Metin takibi, ayet gecisleri ve tefekkur gorselleri silindi")
    }

    fun deleteContemplationImages(track: Track) = viewModelScope.launch {
        repo.deleteContemplationImages(track.id)
        showToast("Tefekkur gorselleri silindi")
    }

    private fun chooseEvenly(
        segments: List<TranscriptSegment>,
        count: Int
    ): List<TranscriptSegment> {
        if (segments.size <= count) return segments
        if (count == 1) return listOf(segments[segments.size / 2])
        return (0 until count).map { index ->
            segments[index * (segments.lastIndex) / (count - 1)]
        }.distinctBy { it.startMs }
    }

    private fun mediaAiBusy(): Boolean = _mediaAi.value.let {
        it.transcribing || it.generatingImages || it.analyzingQuranCues
    }

    /** Uzun kayitlar parcalara bolunup gonderilirken ilerlemeyi gosterir. */
    private fun reportTranscriptionProgress(done: Int, total: Int) {
        if (total <= 1) return
        _mediaAi.value = _mediaAi.value.copy(
            completed = done,
            total = total,
            message = "Uzun kayit $total parcaya bolundu · $done / $total parca yaziya dokuldu"
        )
    }

    /** Otomatik ayet bolgelerini siler, elle onaylananlari korur. Korunan sayiyi dondurur. */
    private suspend fun keepOnlyManualCues(track: Track): Int {
        val manual = repo.bedMuteCues(track.id).filter { it.source == BedMuteCueSource.ELLE.name }
        repo.replaceBedMuteCues(track.id, manual)
        return manual.size
    }

    // ── Yedekleme ──────────────────────────────────────────────────────────

    fun exportBackup(uri: Uri) = viewModelScope.launch {
        val result = runCatching {
            val backup = repo.exportBackup()
            withContext(Dispatchers.IO) {
                ctx.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use {
                    it.write(backup)
                } ?: error("Dosya acilamadi")
            }
        }
        showToast(if (result.isSuccess) "Yedek dosyasi kaydedildi" else "Yedek kaydedilemedi")
    }

    fun importBackup(uri: Uri) = viewModelScope.launch {
        val text = runCatching {
            withContext(Dispatchers.IO) {
                ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Dosya acilamadi")
            }
        }.getOrNull()
        val restored = text != null && repo.importBackup(text)
        if (restored) {
            AlarmScheduler.rescheduleAll(ctx)
            engine.onProgramChanged()
            selectPlaylist(null)
            showToast("Yedek geri yuklendi")
        } else showToast("Bu dosya gecerli bir Benim Radyom yedegi degil")
    }

    fun showToast(text: String) { _toast.value = Toast(System.currentTimeMillis(), text) }
    fun consumeToast() { _toast.value = null }

    override fun onCleared() {
        trackJob?.cancel()
        programSaveJobs.values.forEach { it.cancel() }
        recorder.cancel()
        super.onCleared()
    }
}
