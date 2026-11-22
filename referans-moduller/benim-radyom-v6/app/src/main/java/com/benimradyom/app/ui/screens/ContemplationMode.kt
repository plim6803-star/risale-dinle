package com.benimradyom.app.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.benimradyom.app.data.model.ContemplationImage
import com.benimradyom.app.data.model.BedMuteCue
import com.benimradyom.app.data.model.BedMuteCueSource
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TranscriptSegment
import com.benimradyom.app.ui.components.VSpace
import com.benimradyom.app.ui.components.formatMs
import com.benimradyom.app.viewmodel.MediaAiState
import java.io.File

@Composable
fun ReadingFollowCard(
    track: Track,
    positionMs: Long,
    segments: List<TranscriptSegment>,
    images: List<ContemplationImage>,
    bedMuteCues: List<BedMuteCue>,
    apiKeyConfigured: Boolean,
    mediaAi: MediaAiState,
    isPlaying: Boolean,
    onImportTranscript: () -> Unit,
    onTranscribe: () -> Unit,
    onGenerateImages: (Int) -> Unit,
    onAnalyzeQuranCues: () -> Unit,
    onSaveBedMuteCues: (List<BedMuteCue>) -> Unit,
    onDeleteBedMuteCues: () -> Unit,
    onDeleteTranscript: () -> Unit,
    onDeleteImages: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit
) {
    var contemplationOpen by remember { mutableStateOf(false) }
    var confirmTranscribe by remember { mutableStateOf(false) }
    var chooseImages by remember { mutableStateOf(false) }
    var confirmCueAnalysis by remember { mutableStateOf(false) }
    var cueEditorSeed by remember { mutableStateOf<List<BedMuteCue>?>(null) }
    var menu by remember { mutableStateOf(false) }
    val currentIndex = currentSegmentIndex(segments, positionMs)
    val current = segments.getOrNull(currentIndex)
    val busy = mediaAi.trackId == track.id && (
        mediaAi.transcribing || mediaAi.generatingImages || mediaAi.analyzingQuranCues
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoStories, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("Okunan yeri takip", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(
                        if (segments.isEmpty()) "Bu ses için henüz metin bağlı değil"
                        else "${currentIndex + 1} / ${segments.size} · ${segments.first().sourceEnum.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box {
                    IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, "Seçenekler") }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(
                            text = { Text("Metni değiştir") },
                            leadingIcon = { Icon(Icons.Default.UploadFile, null) },
                            onClick = { menu = false; onImportTranscript() }
                        )
                        if (segments.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Metin takibini sil") },
                                leadingIcon = { Icon(Icons.Default.DeleteOutline, null) },
                                onClick = { menu = false; onDeleteTranscript() }
                            )
                        }
                        if (images.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Görselleri sil") },
                                leadingIcon = { Icon(Icons.Default.HideImage, null) },
                                onClick = { menu = false; onDeleteImages() }
                            )
                        }
                        if (bedMuteCues.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Ayet sessizliklerini sil") },
                                leadingIcon = { Icon(Icons.Default.VolumeUp, null) },
                                onClick = { menu = false; onDeleteBedMuteCues() }
                            )
                        }
                    }
                }
            }

            if (segments.isEmpty()) {
                VSpace(8)
                Text(
                    "SRT/VTT içe aktarırsanız takip kesin olur. Düz TXT metin süreye yaklaşık dağıtılır.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VSpace(8)
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onImportTranscript, enabled = !busy) {
                        Icon(Icons.Default.UploadFile, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp)); Text("SRT / VTT / TXT ekle")
                    }
                    Button(
                        onClick = { confirmTranscribe = true },
                        enabled = apiKeyConfigured && !busy
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp)); Text("AI zaman kodu")
                    }
                    OutlinedButton(
                        onClick = { confirmCueAnalysis = true },
                        enabled = apiKeyConfigured && !busy
                    ) {
                        Icon(Icons.Default.VolumeOff, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp)); Text("Ayet geçişlerini bul")
                    }
                }
                if (!apiKeyConfigured) {
                    Text("AI seçeneği için Yayın → Ayarlar'dan API anahtarı ekleyin.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                VSpace(8)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        current?.text ?: segments.first().text,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(11.dp)
                    )
                }
                if (current?.isApproximate == true) {
                    Text("TXT zamanlaması yaklaşık; kesin takip için SRT/VTT kullanın.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
                VSpace(8)
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { contemplationOpen = true }) {
                        Icon(Icons.Default.Landscape, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp)); Text("Tefekkür modu")
                    }
                    OutlinedButton(
                        onClick = { chooseImages = true },
                        enabled = apiKeyConfigured && !busy
                    ) {
                        Icon(Icons.Default.Image, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp)); Text(if (images.isEmpty()) "Görsel hazırla" else "Görsel ekle (${images.size})")
                    }
                    OutlinedButton(onClick = onImportTranscript, enabled = !busy) {
                        Icon(Icons.Default.UploadFile, null, Modifier.size(17.dp)); Spacer(Modifier.width(5.dp)); Text("Metni değiştir")
                    }
                    OutlinedButton(
                        onClick = {
                            if (bedMuteCues.isEmpty()) confirmCueAnalysis = true
                            else cueEditorSeed = bedMuteCues
                        },
                        enabled = !busy && (apiKeyConfigured || bedMuteCues.isNotEmpty())
                    ) {
                        Icon(Icons.Default.VolumeOff, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(if (bedMuteCues.isEmpty()) "Ayet geçişlerini bul" else "Ayet sessizlikleri (${bedMuteCues.size})")
                    }
                    OutlinedButton(
                        onClick = {
                            cueEditorSeed = bedMuteCues + BedMuteCue(
                                trackId = track.id,
                                startMs = (positionMs - 2_500L).coerceAtLeast(0L),
                                endMs = positionMs + 10_000L,
                                source = BedMuteCueSource.ELLE.name
                            )
                        },
                        enabled = !busy
                    ) {
                        Icon(Icons.Default.Add, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp)); Text("Elle ayet bölgesi")
                    }
                }
                if (bedMuteCues.isNotEmpty()) {
                    Text(
                        "Fon bu ${bedMuteCues.count { it.isEnabled }} bölgede ayetten önce yumuşakça susar ve sonra geri açılır.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (busy) {
                VSpace(8)
                if (mediaAi.generatingImages && mediaAi.total > 0) {
                    LinearProgressIndicator(
                        progress = { mediaAi.completed.toFloat() / mediaAi.total },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                Text(mediaAi.message, style = MaterialTheme.typography.labelSmall)
            } else if (mediaAi.trackId == track.id && mediaAi.message.isNotBlank()) {
                Text(mediaAi.message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    if (confirmTranscribe) {
        AlertDialog(
            onDismissRequest = { confirmTranscribe = false },
            icon = { Icon(Icons.Default.CloudUpload, null) },
            title = { Text("AI ile zaman kodu çıkarılsın mı?") },
            text = {
                Text(
                    "Ses kaydı OpenAI transkripsiyon hizmetine gönderilir ve API kullanım ücreti doğabilir. " +
                        "Tek dosya en fazla 25 MB olabilir. Devam etmek istiyor musunuz?"
                )
            },
            confirmButton = { Button(onClick = { confirmTranscribe = false; onTranscribe() }) { Text("Gönder ve hazırla") } },
            dismissButton = { TextButton(onClick = { confirmTranscribe = false }) { Text("Vazgeç") } }
        )
    }

    if (chooseImages) {
        AlertDialog(
            onDismissRequest = { chooseImages = false },
            icon = { Icon(Icons.Default.Landscape, null) },
            title = { Text("Tefekkür görselleri") },
            text = {
                Text(
                    "Seçilen metin parçaları görsel üretim API'sine gönderilir ve her görsel ücretli olabilir. " +
                        "Görseller telefonda önbelleğe alınır. Kaç sahne hazırlansın?"
                )
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { chooseImages = false; onGenerateImages(3) }) { Text("3 görsel") }
                    Button(onClick = { chooseImages = false; onGenerateImages(6) }) { Text("6 görsel") }
                }
            },
            dismissButton = { TextButton(onClick = { chooseImages = false }) { Text("Vazgeç") } }
        )
    }

    if (confirmCueAnalysis) {
        AlertDialog(
            onDismissRequest = { confirmCueAnalysis = false },
            icon = { Icon(Icons.Default.VolumeOff, null) },
            title = { Text("Arapça ayet geçişleri analiz edilsin mi?") },
            text = {
                Text(
                    if (segments.isEmpty()) {
                        "Henüz zaman kodlu metin yok. Ses önce OpenAI transkripsiyon hizmetine gönderilecek, ardından olası Arapça ayet okuma bölgeleri analiz edilecek. Dosya en fazla 25 MB olabilir ve API ücreti doğabilir. Sonuçları mutlaka dinleyerek kontrol edin."
                    } else {
                        "Yalnızca zaman kodlu metin segmentleri AI analizine gönderilecek; ses tekrar yüklenmeyecek. Sonuçlar tahmindir ve zaman editöründen kontrol edilmelidir."
                    }
                )
            },
            confirmButton = {
                Button(onClick = { confirmCueAnalysis = false; onAnalyzeQuranCues() }) {
                    Text("Analizi başlat")
                }
            },
            dismissButton = { TextButton(onClick = { confirmCueAnalysis = false }) { Text("Vazgeç") } }
        )
    }

    cueEditorSeed?.let { seed ->
        QuranCueEditorDialog(
            track = track,
            positionMs = positionMs,
            initialCues = seed,
            onDismiss = { cueEditorSeed = null },
            onSave = {
                onSaveBedMuteCues(it)
                cueEditorSeed = null
            },
            onAnalyzeAgain = {
                cueEditorSeed = null
                confirmCueAnalysis = true
            }
        )
    }

    if (contemplationOpen) {
        ContemplationDialog(
            track = track,
            positionMs = positionMs,
            segments = segments,
            images = images,
            isPlaying = isPlaying,
            onDismiss = { contemplationOpen = false },
            onPlayPause = onPlayPause,
            onNext = onNext
        )
    }
}

@Composable
private fun QuranCueEditorDialog(
    track: Track,
    positionMs: Long,
    initialCues: List<BedMuteCue>,
    onDismiss: () -> Unit,
    onSave: (List<BedMuteCue>) -> Unit,
    onAnalyzeAgain: () -> Unit
) {
    var draft by remember(initialCues) { mutableStateOf(initialCues.sortedBy { it.startMs }) }

    fun change(index: Int, transform: (BedMuteCue) -> BedMuteCue) {
        draft = draft.toMutableList().also { list -> list[index] = transform(list[index]) }
            .sortedBy { it.startMs }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.GraphicEq, null) },
        title = { Text("Ayet sırasında fon sessizliği") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    "Başlangıç, fonun kısılmaya başladığı andır. AI başlangıcı ayet okunuşundan yaklaşık 2,5 saniye önceye alır. Dinleyerek düzeltin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VSpace(8)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            draft = (draft + BedMuteCue(
                                trackId = track.id,
                                startMs = (positionMs - 2_500L).coerceAtLeast(0L),
                                endMs = positionMs + 10_000L,
                                source = BedMuteCueSource.ELLE.name
                            )).sortedBy { it.startMs }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp)); Text("Buraya bölge ekle")
                    }
                    OutlinedButton(onClick = onAnalyzeAgain, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.AutoAwesome, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp)); Text("AI yenile")
                    }
                }
                VSpace(8)
                if (draft.isEmpty()) {
                    Text("Henüz sessizlik bölgesi yok.", style = MaterialTheme.typography.bodySmall)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            draft,
                            key = { index, cue -> "${cue.id}-${cue.startMs}-$index" }
                        ) { index, cue ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Switch(
                                            checked = cue.isEnabled,
                                            onCheckedChange = { enabled ->
                                                change(index) { it.copy(isEnabled = enabled) }
                                            }
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(cue.label, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                            Text(
                                                "${formatMs(cue.startMs)} – ${formatMs(cue.endMs)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = { draft = draft.toMutableList().also { it.removeAt(index) } }) {
                                            Icon(Icons.Default.DeleteOutline, "Bölgeyi sil", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    CueTimeAdjustRow(
                                        label = "Başlangıç",
                                        valueMs = cue.startMs,
                                        onEarlier = { change(index) { it.copy(startMs = (it.startMs - 1_000L).coerceAtLeast(0L)) } },
                                        onLater = { change(index) { it.copy(startMs = (it.startMs + 1_000L).coerceAtMost(it.endMs - 500L)) } }
                                    )
                                    CueTimeAdjustRow(
                                        label = "Bitiş",
                                        valueMs = cue.endMs,
                                        onEarlier = { change(index) { it.copy(endMs = (it.endMs - 1_000L).coerceAtLeast(it.startMs + 500L)) } },
                                        onLater = { change(index) { it.copy(endMs = it.endMs + 1_000L) } }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(draft) }) { Text("Kaydet") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Vazgeç") } }
    )
}

@Composable
private fun CueTimeAdjustRow(
    label: String,
    valueMs: Long,
    onEarlier: () -> Unit,
    onLater: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(64.dp))
        IconButton(onClick = onEarlier, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Remove, "1 saniye önce", Modifier.size(16.dp))
        }
        Text(formatMs(valueMs), style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(58.dp), textAlign = TextAlign.Center)
        IconButton(onClick = onLater, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Add, "1 saniye sonra", Modifier.size(16.dp))
        }
        Text("±1 sn", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ContemplationDialog(
    track: Track,
    positionMs: Long,
    segments: List<TranscriptSegment>,
    images: List<ContemplationImage>,
    isPlaying: Boolean,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val segmentIndex = currentSegmentIndex(segments, positionMs)
    val segment = segments.getOrNull(segmentIndex)
    val image = images.filter { File(it.localPath).exists() }
        .lastOrNull { it.startMs <= positionMs }
        ?: images.firstOrNull { File(it.localPath).exists() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFF071B18))) {
            Crossfade(
                targetState = image?.localPath,
                animationSpec = tween(1_500),
                label = "tefekkurImage"
            ) { path ->
                val bitmap = remember(path) {
                    path?.let { BitmapFactory.decodeFile(it) }?.asImageBitmap()
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Tefekkür görseli",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF123F36), Color(0xFF152641), Color(0xFF070E14))
                            )
                        )
                    )
                }
            }
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.38f), Color.Transparent, Color.Black.copy(alpha = 0.82f))
                    )
                )
            )

            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Kapat", tint = Color.White)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("TEFEKKÜR MODU", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text(track.title, color = Color.White, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(
                        "${segmentIndex + 1}/${segments.size}",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Black.copy(alpha = 0.58f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(16.dp).heightIn(max = 260.dp).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            segment?.text ?: "Metin zamanı bekleniyor...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        VSpace(8)
                        Text(
                            "${formatMs(positionMs)} · Görsel temsilîdir; gaybî hakikatlerin tasviri değildir.",
                            color = Color.White.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                VSpace(12)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(onClick = onPlayPause, modifier = Modifier.size(56.dp)) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    FilledTonalIconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.SkipNext, "Sonraki")
                    }
                }
            }
        }
    }
}

private fun currentSegmentIndex(segments: List<TranscriptSegment>, positionMs: Long): Int {
    if (segments.isEmpty()) return -1
    val exact = segments.indexOfLast { positionMs >= it.startMs && positionMs < it.endMs }
    if (exact >= 0) return exact
    return segments.indexOfLast { it.startMs <= positionMs }.coerceAtLeast(0)
}
