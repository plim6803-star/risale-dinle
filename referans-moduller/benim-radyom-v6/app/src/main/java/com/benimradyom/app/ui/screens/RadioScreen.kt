package com.benimradyom.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.benimradyom.app.data.model.BedMuteCue
import com.benimradyom.app.data.model.ContemplationImage
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TranscriptSegment
import com.benimradyom.app.engine.QueueItem
import com.benimradyom.app.engine.RadioUiState
import com.benimradyom.app.ui.components.EmptyState
import com.benimradyom.app.ui.components.formatMs
import com.benimradyom.app.ui.theme.LiveRed
import com.benimradyom.app.viewmodel.MediaAiState

/** Dinleme ekrani: yayin kontrolu, program secimi ve siradaki icerikler. */
@Composable
fun RadioScreen(
    state: RadioUiState,
    programs: List<Program>,
    hasContent: Boolean,
    transcriptSegments: List<TranscriptSegment>,
    contemplationImages: List<ContemplationImage>,
    bedMuteCues: List<BedMuteCue>,
    apiKeyConfigured: Boolean,
    mediaAi: MediaAiState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Float) -> Unit,
    onSelectProgram: (Program) -> Unit,
    onSleepTimer: (Int) -> Unit,
    onImportTranscript: (Track) -> Unit,
    onTranscribeTrack: (Track) -> Unit,
    onGenerateContemplationImages: (Track, Int) -> Unit,
    onAnalyzeQuranCues: (Track) -> Unit,
    onSaveBedMuteCues: (Track, List<BedMuteCue>) -> Unit,
    onDeleteBedMuteCues: (Track) -> Unit,
    onDeleteTranscript: (Track) -> Unit,
    onDeleteContemplationImages: (Track) -> Unit,
    onGoToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasContent) {
        EmptyState(
            modifier = modifier,
            icon = Icons.Default.LibraryMusic,
            title = "Radyonuz henuz bos",
            subtitle = "Kutuphane sekmesinden cihazdaki sesleri bir listeye ekleyin.",
            action = {
                Button(onClick = onGoToLibrary, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.LibraryMusic, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kutuphaneyi ac")
                }
            }
        )
        return
    }

    val currentSong = state.current as? QueueItem.Song
    val currentTrack = currentSong?.track

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        LiveHeader(state)
        Spacer(Modifier.height(12.dp))
        NowPlaying(state)
        Spacer(Modifier.height(10.dp))
        Progress(state, onSeek)
        Spacer(Modifier.height(8.dp))
        Controls(state, onPlayPause, onNext, onPrev, onStop)
        Spacer(Modifier.height(10.dp))

        if (programs.isNotEmpty()) {
            ProgramChips(programs, state.programName, onSelectProgram)
            Spacer(Modifier.height(8.dp))
        }

        UtilityRow(state, onSleepTimer)

        if (currentTrack != null && currentSong?.isSpot != true && currentSong?.isPrayer != true) {
            Spacer(Modifier.height(8.dp))
            ReadingFollowCard(
                track = currentTrack,
                positionMs = state.positionMs,
                segments = transcriptSegments.filter { it.trackId == currentTrack.id },
                images = contemplationImages.filter { it.trackId == currentTrack.id },
                bedMuteCues = bedMuteCues.filter { it.trackId == currentTrack.id },
                apiKeyConfigured = apiKeyConfigured,
                mediaAi = mediaAi,
                isPlaying = state.isPlaying,
                onImportTranscript = { onImportTranscript(currentTrack) },
                onTranscribe = { onTranscribeTrack(currentTrack) },
                onGenerateImages = { count -> onGenerateContemplationImages(currentTrack, count) },
                onAnalyzeQuranCues = { onAnalyzeQuranCues(currentTrack) },
                onSaveBedMuteCues = { cues -> onSaveBedMuteCues(currentTrack, cues) },
                onDeleteBedMuteCues = { onDeleteBedMuteCues(currentTrack) },
                onDeleteTranscript = { onDeleteTranscript(currentTrack) },
                onDeleteImages = { onDeleteContemplationImages(currentTrack) },
                onPlayPause = onPlayPause,
                onNext = onNext
            )
        }

        state.prayerPause?.let {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "$it · yayin vakit ayariniza gore yonetiliyor",
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        state.message?.let {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(it, Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        UpNext(state.upNext, Modifier.weight(1f))
    }
}

@Composable
private fun LiveHeader(state: RadioUiState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(state.isPlaying) {
            val transition = rememberInfiniteTransition(label = "live")
            val alpha by transition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
                label = "liveAlpha"
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(LiveRed.copy(alpha = alpha)))
                Spacer(Modifier.width(6.dp))
                Text("CANLI", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = LiveRed)
                Spacer(Modifier.width(10.dp))
            }
        }
        Text(
            state.programName ?: "Benim Radyom",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        when {
            state.bedMutedForQuran -> Text(
                "ayet okunurken fon sessiz",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            state.bedActive -> Text(
                "fon acik",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NowPlaying(state: RadioUiState) {
    val item = state.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(72.dp).clip(RoundedCornerShape(16.dp)).background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.GraphicEq, null, tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item?.title ?: "Baslamaya hazir",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    item?.subtitle ?: "Oynat tusuna basin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val badge = when {
                    item is QueueItem.Song && item.isPrayer -> "EZAN"
                    item is QueueItem.Song && item.isSpot -> "ANONS"
                    item is QueueItem.Narration -> item.knowledge.typeEnum.label.uppercase()
                    item is QueueItem.Spot || item is QueueItem.ClockAnnounce -> "ANONS"
                    else -> null
                }
                if (badge != null) {
                    Spacer(Modifier.height(5.dp))
                    Text(badge, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
private fun Progress(state: RadioUiState, onSeek: (Float) -> Unit) {
    Column {
        Slider(value = state.progress, onValueChange = onSeek, enabled = state.durationMs > 0)
        Row(Modifier.fillMaxWidth()) {
            Text(formatMs(state.positionMs), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.weight(1f))
            Text(formatMs(state.durationMs), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun Controls(
    state: RadioUiState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(onClick = onPrev, modifier = Modifier.size(50.dp)) {
            Icon(Icons.Default.SkipPrevious, "Onceki")
        }
        Spacer(Modifier.width(16.dp))
        FilledIconButton(onClick = onPlayPause, modifier = Modifier.size(72.dp)) {
            if (state.isPreparing) {
                CircularProgressIndicator(Modifier.size(26.dp), strokeWidth = 2.5.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, Modifier.size(36.dp))
            }
        }
        Spacer(Modifier.width(16.dp))
        FilledTonalIconButton(onClick = onNext, modifier = Modifier.size(50.dp)) {
            Icon(Icons.Default.SkipNext, "Sonraki")
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onStop) { Icon(Icons.Default.Stop, "Durdur") }
    }
}

@Composable
private fun ProgramChips(programs: List<Program>, activeName: String?, onSelect: (Program) -> Unit) {
    Column {
        Text("Yayin programi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            programs.filter { it.isEnabled }.forEach { program ->
                FilterChip(
                    selected = program.name == activeName,
                    onClick = { onSelect(program) },
                    label = { Text(program.name) }
                )
            }
        }
    }
}

@Composable
private fun UtilityRow(state: RadioUiState, onSleepTimer: (Int) -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box {
            AssistChip(
                onClick = { menu = true },
                leadingIcon = { Icon(Icons.Default.Bedtime, null, Modifier.size(17.dp)) },
                label = {
                    Text(if (state.sleepMinutesLeft > 0) "Uyku: ${state.sleepMinutesLeft} dk" else "Uyku zamanlayicisi")
                }
            )
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                listOf(0 to "Kapali", 15 to "15 dakika", 30 to "30 dakika", 60 to "1 saat", 90 to "90 dakika").forEach { (minutes, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = { onSleepTimer(minutes); menu = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun UpNext(items: List<QueueItem>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) return
    Column(modifier.fillMaxWidth()) {
        Text("Sirada", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            itemsIndexed(items) { index, item ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", Modifier.width(24.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
                    Column(Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            }
        }
    }
}
