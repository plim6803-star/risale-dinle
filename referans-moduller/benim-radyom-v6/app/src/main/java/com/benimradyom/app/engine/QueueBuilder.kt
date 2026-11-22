package com.benimradyom.app.engine

import com.benimradyom.app.data.model.Announcement
import com.benimradyom.app.data.model.KnowledgeItem
import com.benimradyom.app.data.model.Playlist
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TrackOrder
import kotlin.random.Random

/** Kuyruktaki tek bir oge. */
sealed interface QueueItem {
    val title: String
    val subtitle: String

    data class Song(
        val track: Track,
        val playlist: Playlist,
        /** Namaz vakti icin araya giren ezan kaydi mi. */
        val isPrayer: Boolean = false
    ) : QueueItem {
        override val title: String get() = track.title
        override val subtitle: String
            get() = if (isPrayer) "Namaz vakti" else track.artist.ifBlank { playlist.name }
        val isSpeech: Boolean get() = playlist.kindEnum == PlaylistKind.SPEECH
        val isSpot: Boolean get() = playlist.kindEnum == PlaylistKind.SPOT
    }

    data class Spot(val announcement: Announcement) : QueueItem {
        override val title: String get() = announcement.label.ifBlank { "Anons" }
        override val subtitle: String
            get() = if (announcement.isAudio) "Kendi sesin" else "Seslendirilen metin"
    }

    /** Kaynakla kontrol edilmis ayet meali veya hadis metni; TTS ile okunur. */
    data class Narration(val knowledge: KnowledgeItem) : QueueItem {
        override val title: String get() = knowledge.title
        override val subtitle: String
            get() = buildString {
                append(knowledge.typeEnum.label)
                if (knowledge.reference.isNotBlank()) append(" · ").append(knowledge.reference)
            }
    }

    /** "Saat on dort sifir sifir" gibi otomatik saat anonsu. */
    data class ClockAnnounce(val hour: Int, val minute: Int) : QueueItem {
        override val title: String get() = "Saat %02d:%02d".format(hour, minute)
        override val subtitle: String get() = "Saat anonsu"
    }
}

/** Bir listenin parcalariyla birlikte hali. */
data class LoadedPlaylist(val playlist: Playlist, val tracks: List<Track>)

/**
 * Yayin programindan sonsuz kuyruk uretir.
 *
 * Kullanici adim dizmez: programdaki agirliklar, siralama tercihi ve anons
 * sikligi burada sıraya donusur.
 */
class QueueBuilder(private val random: Random = Random.Default) {

    /** Her liste icin sirali okuma imleci. */
    private val cursors = mutableMapOf<Long, Int>()

    /** Karistirilmis listeler icin kalan indeksler. */
    private val shuffleBags = mutableMapOf<Long, MutableList<Int>>()

    /** Yarim kalmis uzun kayit her kuyruk dolumunda yalniz bir kez eklenir. */
    private val resumedPlaylists = mutableSetOf<Long>()

    private var spotCursor = 0
    private var sinceSpot = 0

    fun reset() {
        cursors.clear()
        shuffleBags.clear()
        resumedPlaylists.clear()
        spotCursor = 0
        sinceSpot = 0
    }

    fun focus(playlistId: Long, indexInPlaylist: Int) {
        cursors[playlistId] = indexInPlaylist
        shuffleBags.remove(playlistId)
    }

    /**
     * Kuyrugu [count] ogeye kadar doldurur.
     *
     * @param sources MUSIC ve SPEECH listeleri (BED ve SPOT haric).
     * @param spots   etkin anonslar.
     */
    fun fill(
        program: Program,
        sources: List<LoadedPlaylist>,
        spots: List<Announcement>,
        count: Int
    ): List<QueueItem> {
        val playable = sources.filter {
            it.tracks.isNotEmpty() &&
                (it.playlist.kindEnum == PlaylistKind.MUSIC ||
                    it.playlist.kindEnum == PlaylistKind.SPEECH) &&
                program.weightOf(it.playlist) > 0
        }
        val audioSpotSources = sources.filter {
            it.tracks.isNotEmpty() &&
                it.playlist.kindEnum == PlaylistKind.SPOT &&
                program.weightOf(it.playlist) > 0
        }
        if (playable.isEmpty()) return emptyList()

        val out = ArrayList<QueueItem>(count)
        var guard = 0
        while (out.size < count && guard < count * 8) {
            guard++

            if (
                program.spotEveryN > 0 &&
                (spots.isNotEmpty() || audioSpotSources.isNotEmpty()) &&
                sinceSpot >= program.spotEveryN
            ) {
                val insertedSpot = nextSpot(spots, audioSpotSources)
                if (insertedSpot != null) {
                    out += insertedSpot
                    sinceSpot = 0
                    continue
                }
            }

            val source = pickWeighted(playable, program) ?: break
            val track = pickTrack(source, program.orderEnum) ?: continue
            out += QueueItem.Song(track, source.playlist)
            sinceSpot++
        }
        return out
    }

    /** Metin/kayit anonslariyla Anons turundeki MP3 listelerini birlikte dondurur. */
    private fun nextSpot(
        spots: List<Announcement>,
        audioSources: List<LoadedPlaylist>
    ): QueueItem? {
        val optionCount = spots.size + audioSources.size
        if (optionCount == 0) return null
        repeat(optionCount) {
            val slot = spotCursor.mod(optionCount)
            spotCursor++
            if (slot < spots.size) return QueueItem.Spot(spots[slot])
            val source = audioSources[slot - spots.size]
            pickTrack(source, TrackOrder.SIRALI)?.let { track ->
                return QueueItem.Song(track, source.playlist)
            }
        }
        return null
    }

    /** Agirliklara gore liste secer; agirligi buyuk olan daha sik gelir. */
    private fun pickWeighted(sources: List<LoadedPlaylist>, program: Program): LoadedPlaylist? {
        val total = sources.sumOf { program.weightOf(it.playlist) }
        if (total <= 0) return sources.randomOrNull(random)
        var roll = random.nextInt(total)
        for (s in sources) {
            roll -= program.weightOf(s.playlist)
            if (roll < 0) return s
        }
        return sources.lastOrNull()
    }

    /**
     * Listeden bir parca secer.
     *
     * SPEECH (ders / Kur'an) listelerinde sira ASLA karistirilmaz: bir dersin
     * bolumleri sirayla gitmeli. Yarim kalmis bir bolum varsa once o gelir.
     */
    private fun pickTrack(source: LoadedPlaylist, order: TrackOrder): Track? {
        val originalTracks = source.tracks
        val tracks = when (order) {
            TrackOrder.ISME_GORE -> originalTracks.sortedWith { a, b -> naturalCompare(a.title, b.title) }
            TrackOrder.ESKIDEN_YENIYE -> originalTracks.sortedBy { it.addedAt }
            TrackOrder.YENIDEN_ESKIYE -> originalTracks.sortedByDescending { it.addedAt }
            else -> originalTracks
        }
        if (tracks.isEmpty()) return null
        val pid = source.playlist.id
        val isSpeech = source.playlist.kindEnum == PlaylistKind.SPEECH

        if (isSpeech) {
            if (resumedPlaylists.add(pid)) {
                val resumable = tracks.firstOrNull {
                    it.lastPositionMs > 5_000L &&
                        (it.durationMs <= 0L || it.lastPositionMs < it.durationMs - 10_000L)
                }
                if (resumable != null) {
                    cursors[pid] = tracks.indexOf(resumable) + 1
                    return resumable
                }
            }
            return nextSequential(pid, tracks)
        }

        return when (order) {
            TrackOrder.KARISIK -> nextShuffled(pid, tracks)
            TrackOrder.EN_AZ_CALAN -> tracks.minByOrNull { it.lastPlayedAt }
            else -> nextSequential(pid, tracks)
        }
    }

    private fun nextSequential(pid: Long, tracks: List<Track>): Track {
        val idx = (cursors[pid] ?: 0) % tracks.size
        cursors[pid] = idx + 1
        return tracks[idx]
    }

    private fun nextShuffled(pid: Long, tracks: List<Track>): Track? {
        val bag = shuffleBags.getOrPut(pid) { tracks.indices.shuffled(random).toMutableList() }
        if (bag.isEmpty()) {
            shuffleBags[pid] = tracks.indices.shuffled(random).toMutableList()
            return nextShuffled(pid, tracks)
        }
        return tracks.getOrNull(bag.removeAt(0))
    }

    /** "Bolum 2"yi "Bolum 10"dan once tutan dogal baslik sirasi. */
    private fun naturalCompare(a: String, b: String): Int {
        var i = 0
        var j = 0
        while (i < a.length && j < b.length) {
            val ca = a[i]
            val cb = b[j]
            if (ca.isDigit() && cb.isDigit()) {
                val startA = i
                val startB = j
                while (i < a.length && a[i].isDigit()) i++
                while (j < b.length && b[j].isDigit()) j++
                val numberA = a.substring(startA, i).trimStart('0').ifEmpty { "0" }
                val numberB = b.substring(startB, j).trimStart('0').ifEmpty { "0" }
                if (numberA.length != numberB.length) return numberA.length - numberB.length
                val numberCompare = numberA.compareTo(numberB)
                if (numberCompare != 0) return numberCompare
            } else {
                val compare = ca.lowercaseChar().compareTo(cb.lowercaseChar())
                if (compare != 0) return compare
                i++
                j++
            }
        }
        return (a.length - i) - (b.length - j)
    }
}
