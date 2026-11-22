package com.benimradyom.app.engine

import android.content.Context
import android.media.audiofx.LoudnessEnhancer
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.benimradyom.app.data.model.Announcement
import com.benimradyom.app.data.model.BedMuteCue
import com.benimradyom.app.data.model.FlowEntryKind
import com.benimradyom.app.data.model.KnowledgeType
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.PrayerSettings
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.Rule
import com.benimradyom.app.data.model.RuleAction
import com.benimradyom.app.data.model.RuleTrigger
import com.benimradyom.app.data.model.SmartFlow
import com.benimradyom.app.data.repo.RadioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

/** Arayuze gonderilen tek dogruluk kaynagi. */
data class RadioUiState(
    val isPlaying: Boolean = false,
    val isPreparing: Boolean = false,
    val current: QueueItem? = null,
    val upNext: List<QueueItem> = emptyList(),
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val programName: String? = null,
    val bedActive: Boolean = false,
    /** Ayet tilaveti zaman isareti nedeniyle fon gecici olarak sessiz mi. */
    val bedMutedForQuran: Boolean = false,
    val bedMuteLabel: String? = null,
    /** Uyku zamanlayicisinin bitisine kalan dakika (0 = kapali). */
    val sleepMinutesLeft: Int = 0,
    /** Namaz vakti nedeniyle duraklatildi mi. */
    val prayerPause: String? = null,
    val message: String? = null
) {
    val progress: Float
        get() = if (durationMs > 0L) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}

/**
 * Radyonun beyni. Servisin icinde yasar; arayuz kapansa da calmaya devam eder.
 */
@OptIn(UnstableApi::class)
class RadioEngine(
    private val ctx: Context,
    private val repo: RadioRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private val host = PlaybackHost(ctx, scope)
    private val tts = TtsSpeaker(ctx)
    private val builder = QueueBuilder()

    private val _state = MutableStateFlow(RadioUiState())
    val state: StateFlow<RadioUiState> = _state.asStateFlow()

    private val queue = ArrayDeque<QueueItem>()
    private val history = ArrayDeque<QueueItem>()
    private var current: QueueItem? = null

    private var program: Program? = null
    private var smartFlow: SmartFlow? = null
    private var smartItems: List<QueueItem> = emptyList()
    private var smartCursor = 0
    private var sources: List<LoadedPlaylist> = emptyList()
    private var spots: List<Announcement> = emptyList()
    private var prayer: PrayerSettings = PrayerSettings()
    private var prayerTimes: PrayerTimes.Times? = null

    private var tickJob: Job? = null
    private var endListener: Player.Listener? = null
    private var loudness: LoudnessEnhancer? = null
    private var loudnessSessionId = C_AUDIO_SESSION_ID_UNSET
    private var resumeJob: Job? = null

    private var fadingOut = false
    private var stopped = true
    private var songsPlayed = 0
    private var lastHourlyHour = -1
    private var lastClockHour = -1
    private var firedTimedSpots = mutableSetOf<String>()
    private var lastPrayerKey: String? = null
    private var sleepDeadline = 0L
    private var pauseUntil = 0L
    private var currentBedCues: List<BedMuteCue> = emptyList()
    private var bedShouldPlay = false
    private var bedTargetVolume = DEFAULT_BED_VOLUME
    private var bedMutedByCue = false

    /** Servis bildirimi guncellemek icin. */
    var onStateChanged: ((RadioUiState) -> Unit)? = null

    /** Capraz gecişte calar degisince medya oturumu guncellensin diye. */
    var onPlayerChanged: ((Player) -> Unit)? = null

    val player: Player get() = host.main

    init {
        host.onPlayerSwapped = { p ->
            attachEndListener()
            onPlayerChanged?.invoke(p)
        }
    }

    // ── Genel kontroller ────────────────────────────────────────────────────

    fun start() {
        stopped = false
        pauseUntil = 0L
        emit(_state.value.copy(isPreparing = true, message = null, prayerPause = null))
        scope.launch {
            reload()
            if (smartFlow != null && smartItems.isEmpty()) {
                emit(
                    _state.value.copy(
                        isPreparing = false,
                        message = "Bu akista yayina hazir oge yok. Ses eslestirmelerini ve metin onaylarini kontrol edin."
                    )
                )
                return@launch
            }
            if (smartFlow == null && sources.isEmpty()) {
                emit(
                    _state.value.copy(
                        isPreparing = false,
                        message = "Once Kutuphane sekmesinden listelere ses ekleyin."
                    )
                )
                return@launch
            }
            if (smartFlow == null && program == null) {
                emit(
                    _state.value.copy(
                        isPreparing = false,
                        message = "Etkin bir yayin programi bulunamadi. Yayin sekmesinden bir program secin."
                    )
                )
                return@launch
            }
            builder.reset()
            smartCursor = 0
            queue.clear()
            history.clear()
            songsPlayed = 0
            firedTimedSpots.clear()
            runRules(RuleTrigger.PROGRAM_BASLAYINCA, "")
            refill()
            playNextFromQueue(fade = false)
        }
    }

    fun stop() {
        stopped = true
        saveCurrentPosition()
        tickJob?.cancel()
        resumeJob?.cancel()
        detachEndListener()
        tts.stop()
        host.stop()
        releaseLoudness()
        current = null
        currentBedCues = emptyList()
        bedShouldPlay = false
        bedMutedByCue = false
        emit(RadioUiState(isPlaying = false, programName = smartFlow?.name ?: program?.name))
    }

    fun pauseResume() {
        if (stopped || current == null) { start(); return }
        if (host.main.isPlaying) {
            host.pause()
            host.bedOut()
            emit(_state.value.copy(isPlaying = false))
        } else {
            host.resume()
            applyBedMuteCue(host.main.currentPosition, force = true)
            emit(_state.value.copy(isPlaying = true))
        }
    }

    fun next() {
        if (stopped) { start(); return }
        saveCurrentPosition()
        detachEndListener()
        tts.stop()
        scope.launch { playNextFromQueue(fade = true) }
    }

    fun previous() {
        if (stopped) { start(); return }
        if (host.main.isPlaying && host.main.currentPosition > 5_000L) {
            host.main.seekTo(0)
            return
        }
        val prev = history.removeLastOrNull() ?: run { host.main.seekTo(0); return }
        current?.let { queue.addFirst(it) }
        detachEndListener()
        tts.stop()
        scope.launch { playItem(prev, fade = true, addToHistory = false) }
    }

    fun seekTo(fraction: Float) {
        val d = host.main.duration
        if (d > 0) host.main.seekTo((d * fraction.coerceIn(0f, 1f)).toLong())
    }

    /** Uyku zamanlayicisi. 0 = kapat. */
    fun setSleepTimer(minutes: Int) {
        sleepDeadline = if (minutes <= 0) 0L else System.currentTimeMillis() + minutes * 60_000L
        emit(_state.value.copy(sleepMinutesLeft = minutes.coerceAtLeast(0)))
    }

    fun previewAnnouncement(text: String, speed: Float, pitch: Float) =
        tts.preview(text, speed, pitch)

    fun stopPreview() = tts.stop()

    /** Program degisince kuyrugu tazeler ama calani kesmez. */
    fun onProgramChanged() {
        scope.launch {
            reload()
            refreshCurrentBedConfiguration()
            smartCursor = 0
            queue.clear()
            refill()
            emit(
                _state.value.copy(
                    programName = smartFlow?.name ?: program?.name,
                    upNext = queue.take(PREVIEW)
                )
            )
        }
    }

    /** Calan parcaya ait ayet zamanlari editor tarafindan degistiginde yenile. */
    fun onBedMuteCuesChanged(trackId: Long) {
        scope.launch {
            val item = current as? QueueItem.Song ?: return@launch
            if (item.track.id != trackId) return@launch
            currentBedCues = withContext(Dispatchers.IO) {
                repo.bedMuteCues(trackId).filter { it.isEnabled }
            }
            applyBedMuteCue(host.main.currentPosition, force = true)
        }
    }

    /** Kullanici kutuphaneden bir parcaya dokundu: hemen onu cal. */
    fun playTrackNow(trackId: Long) {
        scope.launch {
            reload()
            val track = withContext(Dispatchers.IO) { repo.track(trackId) } ?: return@launch
            val playlist = withContext(Dispatchers.IO) { repo.playlist(track.playlistId) } ?: return@launch
            val found = QueueItem.Song(track, playlist)
            stopped = false
            detachEndListener()
            tts.stop()
            if (queue.isEmpty()) refill()
            playItem(found, fade = true)
        }
    }

    // ── Veri yukleme ────────────────────────────────────────────────────────

    private suspend fun reload() = withContext(Dispatchers.IO) {
        smartFlow = repo.activeSmartFlow()
        program = repo.currentProgram() ?: smartFlow?.let { Program(name = it.name) }
        val all = repo.playlists()
        sources = all
            .filter {
                it.kindEnum == PlaylistKind.MUSIC ||
                    it.kindEnum == PlaylistKind.SPEECH ||
                    it.kindEnum == PlaylistKind.SPOT
            }
            .map { LoadedPlaylist(it, repo.tracks(it.id)) }
            .filter { it.tracks.isNotEmpty() }
        spots = repo.enabledAnnouncements()
        prayer = repo.prayerSettings()
        prayerTimes = if (prayer.enabled) PrayerTimes.forToday(prayer) else null

        val playlistById = all.associateBy { it.id }
        smartItems = smartFlow?.entries.orEmpty().filter { it.isEnabled }.mapNotNull { entry ->
            when (entry.kindEnum) {
                FlowEntryKind.KNOWLEDGE -> {
                    val item = repo.knowledgeItem(entry.sourceId) ?: return@mapNotNull null
                    if (!item.isReady) return@mapNotNull null
                    when (item.typeEnum) {
                        KnowledgeType.RISALE -> {
                            val track = repo.track(item.trackId) ?: return@mapNotNull null
                            val pl = playlistById[track.playlistId] ?: return@mapNotNull null
                            QueueItem.Song(track, pl)
                        }
                        KnowledgeType.VERSE, KnowledgeType.HADITH -> QueueItem.Narration(item)
                    }
                }
                FlowEntryKind.TRACK -> {
                    val track = repo.track(entry.sourceId) ?: return@mapNotNull null
                    val pl = playlistById[track.playlistId] ?: return@mapNotNull null
                    QueueItem.Song(track, pl)
                }
                FlowEntryKind.ANNOUNCEMENT -> repo.announcement(entry.sourceId)
                    ?.takeIf { it.isEnabled }
                    ?.let { QueueItem.Spot(it) }
            }
        }

        val p = program
        val flow = smartFlow
        val bedId = if (flow != null) flow.bedPlaylistId else p?.bedPlaylistId ?: -1L
        val bedTracks = when {
            !bedEnabledSetting -> emptyList()
            bedId > 0 -> repo.tracks(bedId)
            else -> all.firstOrNull { it.kindEnum == PlaylistKind.BED }
                ?.let { repo.tracks(it.id) }.orEmpty()
        }
        withContext(Dispatchers.Main) { host.loadBed(bedTracks.map { it.uri }) }
    }

    private fun refill() {
        val flow = smartFlow
        if (flow != null) {
            if (smartItems.isEmpty()) return
            val needed = LOOKAHEAD - queue.size
            var added = 0
            while (added < needed) {
                if (smartCursor >= smartItems.size) {
                    if (flow.loop) smartCursor = 0 else break
                }
                queue.addLast(smartItems[smartCursor++])
                added++
            }
            return
        }
        val p = program ?: return
        val needed = LOOKAHEAD - queue.size
        if (needed <= 0) return
        queue.addAll(builder.fill(p, sources, spots, needed))
    }

    // ── Calma dongusu ───────────────────────────────────────────────────────

    private suspend fun playNextFromQueue(fade: Boolean) {
        if (stopped) return

        // Uyku zamanlayicisi
        if (sleepDeadline > 0 && System.currentTimeMillis() >= sleepDeadline) {
            sleepDeadline = 0
            stop()
            emit(_state.value.copy(message = "Uyku zamanlayicisi: radyo durduruldu"))
            return
        }
        // Kural ile verilen duraklatma
        if (pauseUntil > System.currentTimeMillis()) {
            waitUntilResume(
                if (_state.value.prayerPause != null) _state.value.prayerPause + " nedeniyle duraklatildi"
                else "Kural geregi duraklatildi"
            )
            return
        }
        if (isQuietNow()) {
            host.stop()
            pauseUntil = System.currentTimeMillis() + quietRemainingMillis()
            waitUntilResume("Sessiz saatler - radyo duraklatildi")
            return
        }

        when (maybeInsertPrayer()) {
            PrayerDecision.PAUSE -> {
                waitUntilResume(_state.value.prayerPause + " nedeniyle duraklatildi")
                return
            }
            PrayerDecision.PLAY_ADHAN -> Unit
            PrayerDecision.NONE -> {
                maybeInsertTimedSpot()
                maybeInsertClock()
                maybeInsertHourly()
                checkTimeRules()
            }
        }

        if (queue.isEmpty()) refill()
        val item = queue.removeFirstOrNull()
        if (item == null) {
            if (smartFlow != null && smartItems.isNotEmpty()) {
                stop()
                emit(_state.value.copy(message = "Akilli yayin akisi tamamlandi."))
            } else {
                emit(_state.value.copy(isPlaying = false, message = "Calinacak bir sey bulunamadi."))
            }
            return
        }
        playItem(item, fade)
    }

    private suspend fun playItem(item: QueueItem, fade: Boolean, addToHistory: Boolean = true) {
        if (stopped) return
        current?.let { if (addToHistory) pushHistory(it) }
        current = item
        fadingOut = false

        val p = program
        val fadeMs = if (fade) p?.fadeMs ?: 0 else 0
        val crossMs = if (fade && p?.crossfade == true) p.fadeMs else 0

        when (item) {
            is QueueItem.Song -> playSong(item, fadeMs, crossMs)
            is QueueItem.Spot -> playSpot(item)
            is QueueItem.Narration -> playNarration(item)
            is QueueItem.ClockAnnounce -> playClock(item)
        }

        if (queue.size < LOOKAHEAD / 2) refill()
        emit(
            _state.value.copy(
                isPreparing = false,
                isPlaying = true,
                current = item,
                upNext = queue.take(PREVIEW),
                programName = smartFlow?.name ?: p?.name,
                sleepMinutesLeft = sleepMinutesLeft(),
                message = null
            )
        )
    }

    private suspend fun playSong(item: QueueItem.Song, fadeMs: Int, crossMs: Int) {
        val p = program
        val resumeAt = if (item.isSpeech && !item.isPrayer) item.track.lastPositionMs else 0L
        val volume = p?.volumeOf(item.playlist.id) ?: 1f
        val bedOn = item.isSpeech && !item.isPrayer && bedEnabledSetting && host.hasBed

        // Ayet zamanlarini SESI BASLATMADAN once oku. Eskiden once ses baslatilip
        // sonra veritabani beklendigi icin, bu arada gelen hata (silinmis/bozuk
        // dosya) henuz dinleyici bagli olmadigindan kayboluyor ve radyo 0:00'da
        // donup kaliyordu.
        val cues = if (bedOn) {
            withContext(Dispatchers.IO) { repo.bedMuteCues(item.track.id).filter { it.isEnabled } }
        } else emptyList()
        // Beklerken radyo durdurulmus ya da baska bir parcaya gecilmis olabilir.
        if (stopped || current !== item) return

        host.play(
            uri = item.track.uri,
            title = item.track.title,
            artist = item.track.artist.ifBlank { item.playlist.name },
            startPositionMs = resumeAt,
            fadeMs = fadeMs,
            volume = volume,
            crossfadeMs = crossMs
        )
        // Dinleyici arada hicbir bekleme olmadan baglanir.
        attachEndListener()
        applyLoudness(p?.normalizeVolume == true)

        bedShouldPlay = bedOn
        bedTargetVolume = bedVolumeSetting
        bedMutedByCue = false
        currentBedCues = cues
        if (bedOn) applyBedMuteCue(resumeAt, force = true) else {
            host.bedOut()
            emitBed(false)
        }

        startTicker()
        if (!item.isSpot && !item.isPrayer) songsPlayed++
        scope.launch(Dispatchers.IO) { repo.markPlayed(item.track.id) }

        // Sartli gorevler
        runRules(RuleTrigger.LISTE_CALINCA, item.playlist.id.toString())
        if (!item.isSpot && !item.isPrayer) runRules(RuleTrigger.HER_N_PARCA, "")
    }

    private fun playSpot(item: QueueItem.Spot) {
        val ann = item.announcement
        if (!ann.isAudio) {
            speakOverBed(ann.text, ann.voiceSpeed, ann.voicePitch)
            return
        }
        // Kendi ses kaydin: dosya olarak calar, fon hafif yukselir.
        detachEndListener()
        startBedForSpeech()
        host.play(
            uri = ann.audioUri.orEmpty(),
            title = ann.label.ifBlank { "Anons" },
            artist = "Anons",
            fadeMs = 300,
            volume = program?.masterVolume ?: 1f
        )
        attachEndListener()
        startTicker()
    }

    private fun playNarration(item: QueueItem.Narration) {
        val k = item.knowledge
        speakOverBed(k.text, k.voiceSpeed, k.voicePitch)
    }

    private fun playClock(item: QueueItem.ClockAnnounce) {
        speakOverBed(spokenClock(item.hour, item.minute), 1.0f, 1.0f)
    }

    /**
     * Seslendirilen her sey (yazili anons, kart okuma, saat) ayni yoldan gecer:
     * ana calar susar, fon muzik konusmanin altinda hafif yukselir, metin
     * bitince siradakine gecilir.
     */
    private fun speakOverBed(text: String, speed: Float, pitch: Float) {
        detachEndListener()
        host.main.pause()
        stopTicker()
        startBedForSpeech()
        emit(_state.value.copy(positionMs = 0L, durationMs = 0L, isPlaying = true))
        tts.speak(text, speed, pitch) {
            scope.launch { playNextFromQueue(fade = true) }
        }
    }

    /** Konusma sirasinda fonu (varsa) biraz yukseltilmis seviyede acar. */
    private fun startBedForSpeech() {
        val on = bedEnabledSetting && host.hasBed
        val volume = bedVolumeSetting * SPEECH_BED_BOOST
        resetTimedBedDucking(on, volume)
        if (on) host.bedIn(volume) else host.bedOut()
        emitBed(on)
    }

    /** Fon ayari: akilli akis calarken onunki, yoksa programinki. Tek kaynak. */
    private val bedEnabledSetting: Boolean
        get() = smartFlow?.bedEnabled ?: program?.bedEnabled ?: false

    private val bedVolumeSetting: Float
        get() = smartFlow?.bedVolume ?: program?.bedVolume ?: DEFAULT_BED_VOLUME

    private fun spokenClock(hour: Int, minute: Int): String =
        if (minute == 0) "Saat $hour" else "Saat $hour $minute"

    // ── Otomatik araya girenler ─────────────────────────────────────────────

    private suspend fun maybeInsertHourly() {
        val p = program ?: return
        if (p.hourlyPlaylistId <= 0L) return
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour == lastHourlyHour || cal.get(Calendar.MINUTE) > 10) return
        lastHourlyHour = hour

        val pl = withContext(Dispatchers.IO) { repo.playlist(p.hourlyPlaylistId) } ?: return
        val tracks = withContext(Dispatchers.IO) { repo.tracks(pl.id) }
        val pick = tracks.minByOrNull { it.lastPlayedAt } ?: return
        queue.addFirst(QueueItem.Song(pick, pl))
    }

    private fun maybeInsertClock() {
        val p = program ?: return
        if (!p.announceClock) return
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour == lastClockHour || cal.get(Calendar.MINUTE) > 5) return
        lastClockHour = hour
        queue.addFirst(QueueItem.ClockAnnounce(hour, 0))
    }

    private suspend fun maybeInsertTimedSpot() {
        val p = program ?: return
        val spotsAtTime = p.timedSpots.filter { it.isEnabled }
        if (spotsAtTime.isEmpty()) return
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        for (s in spotsAtTime) {
            val key = "${cal.get(Calendar.DAY_OF_YEAR)}-${s.hour}-${s.minute}-${s.announcementId}"
            if (key in firedTimedSpots) continue
            // Vaktin ilk 3 dakikasi icinde yakala.
            val due = hour == s.hour && minute >= s.minute && minute < s.minute + 3
            if (!due) continue
            firedTimedSpots.add(key)
            val ann = withContext(Dispatchers.IO) { repo.announcement(s.announcementId) } ?: continue
            if (ann.isEnabled) queue.addFirst(QueueItem.Spot(ann))
        }
    }

    /** Namaz vakti geldiyse radyoyu susturur, ezan varsa calar. */
    private suspend fun maybeInsertPrayer(): PrayerDecision {
        if (!prayer.enabled) return PrayerDecision.NONE
        val times = prayerTimes ?: return PrayerDecision.NONE
        val cal = Calendar.getInstance()
        val nowMinute = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        times.prayers.forEachIndexed { index, (name, minuteOfDay) ->
            if (!prayer.isActiveFor(index)) return@forEachIndexed
            val from = minuteOfDay - prayer.muteBeforeMinutes
            val to = minuteOfDay + prayer.muteAfterMinutes
            if (nowMinute !in from..to) return@forEachIndexed

            val key = "${cal.get(Calendar.DAY_OF_YEAR)}-$name"
            // Ezan bir kayit olarak caldiysa bittikten sonra kalan sessiz sureyi
            // koru. Ayni vakti ikinci kez kuyruga ekleme.
            if (key == lastPrayerKey) {
                if (pauseUntil > System.currentTimeMillis()) return PrayerDecision.PAUSE
                return@forEachIndexed
            }
            lastPrayerKey = key

            emit(_state.value.copy(prayerPause = "$name vakti"))
            pauseUntil = System.currentTimeMillis() +
                (to - nowMinute).coerceAtLeast(1) * 60_000L

            // Ezan kaydi varsa calalim, yoksa sadece sus.
            if (prayer.adhanPlaylistId > 0) {
                val pl = withContext(Dispatchers.IO) { repo.playlist(prayer.adhanPlaylistId) }
                val tracks = pl?.let { withContext(Dispatchers.IO) { repo.tracks(it.id) } }.orEmpty()
                val pick = tracks.minByOrNull { it.lastPlayedAt }
                if (pl != null && pick != null) {
                    queue.addFirst(QueueItem.Song(pick, pl, isPrayer = true))
                    return PrayerDecision.PLAY_ADHAN
                }
            }
            return PrayerDecision.PAUSE
        }
        return PrayerDecision.NONE
    }

    private fun waitUntilResume(message: String) {
        host.pause()
        host.bedOut()
        emit(_state.value.copy(isPlaying = false, message = message))
        resumeJob?.cancel()
        val waitMs = (pauseUntil - System.currentTimeMillis()).coerceAtLeast(1_000L)
        resumeJob = scope.launch {
            delay(waitMs)
            if (!stopped) {
                pauseUntil = 0L
                emit(_state.value.copy(prayerPause = null, message = null))
                playNextFromQueue(fade = true)
            }
        }
    }

    // ── Sartli gorevler ─────────────────────────────────────────────────────

    private fun runRules(trigger: RuleTrigger, value: String) {
        val p = program ?: return
        p.rules.asSequence()
            .filter { it.isEnabled && it.triggerEnum == trigger }
            .filter { matchesTrigger(it, value) }
            .forEach { applyRule(it) }
    }

    private fun matchesTrigger(rule: Rule, value: String): Boolean = when (rule.triggerEnum) {
        RuleTrigger.LISTE_CALINCA -> rule.triggerValue == value
        RuleTrigger.HER_N_PARCA -> {
            val n = rule.triggerValue.toIntOrNull() ?: 0
            n > 0 && songsPlayed > 0 && songsPlayed % n == 0
        }
        else -> true
    }

    private fun checkTimeRules() {
        val p = program ?: return
        val cal = Calendar.getInstance()
        val hhmm = "%02d:%02d".format(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        p.rules.asSequence()
            .filter { it.isEnabled && it.triggerEnum == RuleTrigger.SAAT_GELINCE }
            .filter { it.triggerValue == hhmm }
            .forEach { rule ->
                val key = "${cal.get(Calendar.DAY_OF_YEAR)}-rule-$hhmm-${rule.action}"
                if (firedTimedSpots.add(key)) applyRule(rule)
            }
    }

    private fun applyRule(rule: Rule) {
        when (rule.actionEnum) {
            RuleAction.LISTE_CAL -> scope.launch {
                val id = rule.actionValue.toLongOrNull() ?: return@launch
                val pl = withContext(Dispatchers.IO) { repo.playlist(id) } ?: return@launch
                val tracks = withContext(Dispatchers.IO) { repo.tracks(id) }
                val pick = tracks.minByOrNull { it.lastPlayedAt } ?: return@launch
                queue.addFirst(QueueItem.Song(pick, pl))
            }

            RuleAction.ANONS_CAL -> scope.launch {
                val id = rule.actionValue.toLongOrNull() ?: return@launch
                val ann = withContext(Dispatchers.IO) { repo.announcement(id) } ?: return@launch
                if (ann.isEnabled) queue.addFirst(QueueItem.Spot(ann))
            }

            RuleAction.SES_AYARLA -> {
                val pct = rule.actionValue.toIntOrNull() ?: return
                host.setVolume(pct / 100f)
            }

            RuleAction.FON_AC -> {
                bedShouldPlay = host.hasBed
                // Eskiden burada yalnizca program ayari okunuyordu; akilli akis
                // calarken onun fon seviyesi yok sayiliyordu.
                bedTargetVolume = bedVolumeSetting
                applyBedMuteCue(host.main.currentPosition, force = true)
            }

            RuleAction.FON_KAPAT -> {
                bedShouldPlay = false
                bedMutedByCue = false
                host.bedOut()
                emitBed(false)
            }

            RuleAction.DURAKLAT -> {
                val minutes = rule.actionValue.toIntOrNull() ?: return
                pauseUntil = System.currentTimeMillis() + minutes * 60_000L
            }

            RuleAction.PROGRAM_DEGISTIR -> scope.launch {
                val id = rule.actionValue.toLongOrNull() ?: return@launch
                withContext(Dispatchers.IO) { repo.activateProgram(id) }
                onProgramChanged()
            }
        }
    }

    // ── Bitis / ilerleme ────────────────────────────────────────────────────

    private fun attachEndListener() {
        detachEndListener()
        val l = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    detachEndListener()
                    saveCurrentPosition(reset = true)
                    scope.launch { playNextFromQueue(fade = true) }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                // Bozuk / silinmis dosyada takilma, sonrakine gec.
                detachEndListener()
                emit(_state.value.copy(message = "Bir ses dosyasi acilamadi, atlandi"))
                scope.launch { playNextFromQueue(fade = false) }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                emit(_state.value.copy(isPlaying = isPlaying))
            }
        }
        endListener = l
        host.main.addListener(l)
    }

    private fun detachEndListener() {
        endListener?.let { host.main.removeListener(it) }
        endListener = null
    }

    private fun startTicker() {
        stopTicker()
        tickJob = scope.launch {
            var sinceSave = 0
            while (isActive) {
                val p = host.main
                val pos = p.currentPosition
                val dur = p.duration.takeIf { it > 0 } ?: 0L
                applyBedMuteCue(pos)
                emit(
                    _state.value.copy(
                        positionMs = pos,
                        durationMs = dur,
                        isPlaying = p.isPlaying,
                        sleepMinutesLeft = sleepMinutesLeft()
                    )
                )

                val prog = program
                val fadeMs = prog?.fadeMs ?: 0
                if (!fadingOut && fadeMs > 0 && dur > 0 && dur - pos in 1..fadeMs.toLong()) {
                    fadingOut = true
                    if (prog?.crossfade == true) {
                        // Capraz gecis: siradakini simdi baslat.
                        detachEndListener()
                        scope.launch { playNextFromQueue(fade = true) }
                    } else {
                        host.fadeOutThen(fadeMs) { }
                    }
                }

                if (sleepDeadline > 0 && System.currentTimeMillis() >= sleepDeadline) {
                    sleepDeadline = 0
                    stop()
                    return@launch
                }

                if (++sinceSave >= 10) { sinceSave = 0; saveCurrentPosition() }
                delay(TICK_MS)
            }
        }
    }

    private fun stopTicker() { tickJob?.cancel(); tickJob = null }

    private fun sleepMinutesLeft(): Int =
        if (sleepDeadline <= 0) 0
        else ((sleepDeadline - System.currentTimeMillis()) / 60_000L).toInt().coerceAtLeast(0)

    private fun saveCurrentPosition(reset: Boolean = false) {
        val item = current as? QueueItem.Song ?: return
        if (!item.isSpeech) return
        val pos = if (reset) 0L else host.main.currentPosition
        scope.launch(Dispatchers.IO) { repo.savePosition(item.track.id, pos.coerceAtLeast(0L)) }
    }

    // ── Ses dengeleme ───────────────────────────────────────────────────────

    /**
     * Kisik kayitlari yukseltir. Gercek olcume dayali bir normalizasyon degil;
     * sabit bir kazanc uygular ve kulaktaki seviye farkini azaltir.
     */
    private fun applyLoudness(enabled: Boolean) {
        if (!enabled) { releaseLoudness(); return }
        runCatching {
            val sessionId = host.main.audioSessionId
            if (sessionId == C_AUDIO_SESSION_ID_UNSET) return
            if (loudness == null || loudnessSessionId != sessionId) {
                releaseLoudness()
                loudness = LoudnessEnhancer(sessionId)
                loudnessSessionId = sessionId
            }
            loudness?.setTargetGain(NORMALIZE_GAIN_MB)
            loudness?.enabled = true
        }
    }

    private fun releaseLoudness() {
        runCatching {
            loudness?.enabled = false
            loudness?.release()
        }
        loudness = null
        loudnessSessionId = C_AUDIO_SESSION_ID_UNSET
    }

    // ── Sessiz saatler ──────────────────────────────────────────────────────

    private fun isQuietNow(): Boolean {
        val p = program ?: return false
        if (p.quietStartHour < 0 || p.quietEndHour < 0) return false
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (p.quietStartHour <= p.quietEndHour) h in p.quietStartHour until p.quietEndHour
        else h >= p.quietStartHour || h < p.quietEndHour
    }

    private fun quietRemainingMillis(): Long {
        val p = program ?: return 60_000L
        val now = Calendar.getInstance()
        val nowMinute = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        var endMinute = p.quietEndHour.coerceIn(0, 23) * 60
        if (endMinute <= nowMinute) endMinute += 24 * 60
        return ((endMinute - nowMinute).coerceAtLeast(1) * 60_000L)
    }

    // ── Yardimcilar ─────────────────────────────────────────────────────────

    private fun pushHistory(item: QueueItem) {
        history.addLast(item)
        while (history.size > HISTORY_MAX) history.removeFirst()
    }

    private fun emit(s: RadioUiState) {
        _state.value = s
        onStateChanged?.invoke(s)
    }

    private fun emitBed(on: Boolean) {
        val state = _state.value
        if (state.bedActive != on || state.bedMutedForQuran || state.bedMuteLabel != null) {
            emit(
                state.copy(
                    bedActive = on,
                    bedMutedForQuran = false,
                    bedMuteLabel = null
                )
            )
        }
    }

    /** Zaman cizelgesine gore fonu yumusakca kapatir veya geri acar. */
    private fun applyBedMuteCue(positionMs: Long, force: Boolean = false) {
        if (!bedShouldPlay) return
        val cue = currentBedCues.firstOrNull {
            it.isEnabled && positionMs >= it.startMs && positionMs < it.endMs
        }
        if (cue != null) {
            if (!bedMutedByCue || force) host.bedOut(BED_CUE_FADE_OUT_MS)
            bedMutedByCue = true
            val state = _state.value
            if (!state.bedMutedForQuran || state.bedMuteLabel != cue.label) {
                emit(
                    state.copy(
                        bedActive = false,
                        bedMutedForQuran = true,
                        bedMuteLabel = cue.label
                    )
                )
            }
        } else {
            if (bedMutedByCue || force) host.bedIn(bedTargetVolume, BED_CUE_FADE_IN_MS)
            bedMutedByCue = false
            val state = _state.value
            if (!state.bedActive || state.bedMutedForQuran || state.bedMuteLabel != null) {
                emit(
                    state.copy(
                        bedActive = true,
                        bedMutedForQuran = false,
                        bedMuteLabel = null
                    )
                )
            }
        }
    }

    private fun resetTimedBedDucking(shouldPlay: Boolean, targetVolume: Float) {
        currentBedCues = emptyList()
        bedShouldPlay = shouldPlay
        bedTargetVolume = targetVolume
        bedMutedByCue = false
    }

    /** Listeye yeni fon eklendiginde calan ana sesi yeniden baslatmadan uygular. */
    private suspend fun refreshCurrentBedConfiguration() {
        val item = current as? QueueItem.Song ?: return
        val shouldPlay = item.isSpeech && !item.isPrayer && bedEnabledSetting && host.hasBed
        bedShouldPlay = shouldPlay
        bedTargetVolume = bedVolumeSetting
        bedMutedByCue = false
        currentBedCues = if (shouldPlay) {
            withContext(Dispatchers.IO) {
                repo.bedMuteCues(item.track.id).filter { it.isEnabled }
            }
        } else emptyList()
        if (shouldPlay) applyBedMuteCue(host.main.currentPosition, force = true) else {
            host.bedOut()
            emitBed(false)
        }
    }

    fun release() {
        stopTicker()
        resumeJob?.cancel()
        detachEndListener()
        releaseLoudness()
        tts.release()
        host.release()
        scope.cancel()
    }

    private companion object {
        const val LOOKAHEAD = 24
        const val PREVIEW = 6
        const val HISTORY_MAX = 40
        const val TICK_MS = 500L
        const val DEFAULT_BED_VOLUME = 0.18f
        /** Konusma (anons, kart, saat) sirasinda fon bu oranda yukselir. */
        const val SPEECH_BED_BOOST = 1.4f
        const val BED_CUE_FADE_OUT_MS = 1_000
        const val BED_CUE_FADE_IN_MS = 1_500
        const val NORMALIZE_GAIN_MB = 400
        const val C_AUDIO_SESSION_ID_UNSET = 0
    }

    private enum class PrayerDecision { NONE, PLAY_ADHAN, PAUSE }
}
