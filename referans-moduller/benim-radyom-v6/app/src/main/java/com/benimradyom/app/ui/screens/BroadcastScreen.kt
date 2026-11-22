package com.benimradyom.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.benimradyom.app.data.model.Announcement
import com.benimradyom.app.data.model.AiSettings
import com.benimradyom.app.data.model.KnowledgeItem
import com.benimradyom.app.data.model.Playlist
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.PrayerSettings
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.SmartFlow
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.engine.PrayerTimes
import com.benimradyom.app.ui.components.EmptyState
import com.benimradyom.app.ui.components.VSpace
import com.benimradyom.app.ui.components.formatMs
import com.benimradyom.app.viewmodel.SmartGenerationState

/**
 * Yayin ayarlari: programlar, anonslar, genel ayarlar.
 *
 * Programlar eski "recete" ve "zamanlayici" kavramlarinin birlesimi; bir
 * programa girince ayarlar katman katman acilir.
 */
@Composable
fun BroadcastScreen(
    programs: List<Program>,
    smartFlows: List<SmartFlow>,
    knowledgeItems: List<KnowledgeItem>,
    allTracks: List<Track>,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    prayer: PrayerSettings,
    aiSettings: AiSettings,
    apiKeyConfigured: Boolean,
    smartGeneration: SmartGenerationState,
    isRecording: Boolean,
    onSaveProgram: (Program) -> Unit,
    onActivateProgram: (Program) -> Unit,
    onDeleteProgram: (Program) -> Unit,
    onCreateProgram: (String) -> Unit,
    onGenerateSmartFlow: (String, Int) -> Unit,
    onSaveSmartFlow: (SmartFlow) -> Unit,
    onActivateSmartFlow: (SmartFlow) -> Unit,
    onDeleteSmartFlow: (SmartFlow) -> Unit,
    onSaveKnowledge: (KnowledgeItem) -> Unit,
    onDeleteKnowledge: (KnowledgeItem) -> Unit,
    onPreviewKnowledge: (KnowledgeItem) -> Unit,
    onPlayTrack: (Track) -> Unit,
    onSaveAnnouncement: (Announcement) -> Unit,
    onToggleAnnouncement: (Announcement) -> Unit,
    onDeleteAnnouncement: (Announcement) -> Unit,
    onPreviewText: (String, Float, Float) -> Unit,
    onStopPreview: () -> Unit,
    onPickAudioForAnnouncement: ((String, Long) -> Unit) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: ((String?, Long) -> Unit) -> Unit,
    onSavePrayer: (PrayerSettings) -> Unit,
    onSaveAiConfiguration: (AiSettings, String?) -> Unit,
    onClearApiKey: () -> Unit,
    onAutoMapKnowledge: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<Program?>(null) }
    var catalogOpen by remember { mutableStateOf(false) }

    val open = editing
    if (open != null) {
        // Duzenlenen program listeden tazelenir ki degisiklikler yansisin.
        val fresh = programs.firstOrNull { it.id == open.id } ?: open
        ProgramEditor(
            program = fresh,
            playlists = playlists,
            announcements = announcements,
            allPrograms = programs,
            onSave = onSaveProgram,
            onDelete = onDeleteProgram,
            onClose = { editing = null }
        )
        return
    }

    Column(modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = tab, edgePadding = 6.dp) {
            Tab(tab == 0, { tab = 0 }, text = { Text("Akıllı Akış") })
            Tab(tab == 1, { tab = 1 }, text = { Text("Programlar") })
            Tab(tab == 2, { tab = 2 }, text = { Text("Anonslar") })
            Tab(tab == 3, { tab = 3 }, text = { Text("Ayarlar") })
        }
        when (tab) {
            0 -> SmartFlowScreen(
                flows = smartFlows,
                catalog = knowledgeItems,
                tracks = allTracks,
                playlists = playlists,
                announcements = announcements,
                generation = smartGeneration,
                onGenerate = onGenerateSmartFlow,
                onSave = onSaveSmartFlow,
                onActivate = onActivateSmartFlow,
                onDelete = onDeleteSmartFlow,
                onSaveKnowledge = onSaveKnowledge,
                onPreviewKnowledge = onPreviewKnowledge,
                onStopPreview = onStopPreview,
                onPlayTrack = onPlayTrack,
                onOpenUrl = onOpenUrl,
                onOpenCatalog = { catalogOpen = true }
            )
            1 -> ProgramTab(programs, onActivateProgram, onCreateProgram) { editing = it }
            2 -> AnnouncementTab(
                announcements, isRecording,
                onSaveAnnouncement, onToggleAnnouncement, onDeleteAnnouncement,
                onPreviewText, onStopPreview,
                onPickAudioForAnnouncement, onStartRecording, onStopRecording
            )
            else -> SettingsTab(
                prayer = prayer,
                playlists = playlists,
                aiSettings = aiSettings,
                apiKeyConfigured = apiKeyConfigured,
                onSave = onSavePrayer,
                onSaveAiConfiguration = onSaveAiConfiguration,
                onClearApiKey = onClearApiKey,
                onOpenUrl = onOpenUrl,
                onOpenCatalog = { catalogOpen = true },
                onAutoMapKnowledge = onAutoMapKnowledge,
                onExport = onExportBackup,
                onImport = onImportBackup
            )
        }
    }

    if (catalogOpen) {
        KnowledgeCatalogSheet(
            catalog = knowledgeItems,
            tracks = allTracks,
            playlists = playlists,
            onDismiss = { catalogOpen = false },
            onSave = onSaveKnowledge,
            onDelete = onDeleteKnowledge,
            onPreview = onPreviewKnowledge,
            onStopPreview = onStopPreview,
            onOpenUrl = onOpenUrl
        )
    }
}

// ── Programlar ──────────────────────────────────────────────────────────────

@Composable
private fun ProgramTab(
    programs: List<Program>,
    onActivate: (Program) -> Unit,
    onCreate: (String) -> Unit,
    onEdit: (Program) -> Unit
) {
    var creating by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Button(
            onClick = { creating = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Yeni program")
        }
        VSpace(10)

        if (programs.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Campaign,
                title = "Program yok",
                subtitle = "Bir program olusturun: ne zaman, ne calacagini ve kurallari icinde tanimlarsiniz."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(programs, key = { it.id }) { p ->
                    ProgramRow(p, onActivate = { onActivate(p) }, onEdit = { onEdit(p) })
                }
            }
        }
    }

    if (creating) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { creating = false },
            title = { Text("Yeni program") },
            text = {
                OutlinedTextField(
                    name, { name = it },
                    label = { Text("Program adi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    { onCreate(name.trim()); creating = false },
                    enabled = name.isNotBlank()
                ) { Text("Olustur") }
            },
            dismissButton = { TextButton({ creating = false }) { Text("Vazgec") } }
        )
    }
}

@Composable
private fun ProgramRow(p: Program, onActivate: () -> Unit, onEdit: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (p.isActive) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        p.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    if (p.isActive) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "etkin",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!p.isEnabled) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "kapali",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    buildString {
                        if (p.startHour >= 0) {
                            append("%02d:%02d".format(p.startHour, p.startMinute))
                            if (p.endHour >= 0) append(" - %02d:%02d".format(p.endHour, p.endMinute))
                        } else {
                            append("Saat kisiti yok")
                        }
                        if (p.autoStart) append(" · otomatik")
                        if (p.spotEveryN > 0) append(" · ${p.spotEveryN} parcada anons")
                        if (p.rules.isNotEmpty()) append(" · ${p.rules.size} kural")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    DAY_LABELS.forEachIndexed { i, d ->
                        val on = p.days.getOrNull(i) == '1'
                        Text(
                            d,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (on) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        )
                    }
                }
            }
            if (!p.isActive) {
                TextButton(onClick = onActivate) {
                    Text("Etkinlestir", style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onEdit, Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, "Duzenle", Modifier.size(16.dp))
            }
        }
    }
}

// ── Anonslar ────────────────────────────────────────────────────────────────

@Composable
private fun AnnouncementTab(
    items: List<Announcement>,
    isRecording: Boolean,
    onSave: (Announcement) -> Unit,
    onToggle: (Announcement) -> Unit,
    onDelete: (Announcement) -> Unit,
    onPreviewText: (String, Float, Float) -> Unit,
    onStopPreview: () -> Unit,
    onPickAudio: ((String, Long) -> Unit) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: ((String?, Long) -> Unit) -> Unit
) {
    var editing by remember { mutableStateOf<Announcement?>(null) }
    var creating by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Button(
            onClick = { creating = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Yeni anons")
        }
        VSpace(10)

        if (items.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Campaign,
                title = "Anons yok",
                subtitle = "Kendi sesinizle kaydedebilir, hazir bir ses dosyasi secebilir " +
                    "veya yazdiginiz metni seslendirtebilirsiniz."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(items, key = { it.id }) { ann ->
                    AnnouncementRow(
                        ann,
                        onToggle = { onToggle(ann) },
                        onEdit = { editing = ann },
                        onDelete = { onDelete(ann) }
                    )
                }
            }
        }
    }

    if (creating || editing != null) {
        AnnouncementEditor(
            existing = editing,
            isRecording = isRecording,
            onDismiss = { creating = false; editing = null },
            onSave = { onSave(it); creating = false; editing = null },
            onPreviewText = onPreviewText,
            onStopPreview = onStopPreview,
            onPickAudio = onPickAudio,
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording
        )
    }
}

@Composable
private fun AnnouncementRow(
    ann: Announcement,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (ann.isAudio) Icons.Default.Mic else Icons.Default.Campaign,
                null,
                Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    ann.label.ifBlank { ann.text.take(30).ifBlank { "Anons" } },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    if (ann.isAudio) "Kendi sesiniz - ${formatMs(ann.durationMs)}"
                    else ann.text.take(60),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Switch(checked = ann.isEnabled, onCheckedChange = { onToggle() })
            IconButton(onEdit, Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, "Duzenle", Modifier.size(16.dp))
            }
            IconButton(onDelete, Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.DeleteOutline, "Sil", Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnnouncementEditor(
    existing: Announcement?,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onSave: (Announcement) -> Unit,
    onPreviewText: (String, Float, Float) -> Unit,
    onStopPreview: () -> Unit,
    onPickAudio: ((String, Long) -> Unit) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: ((String?, Long) -> Unit) -> Unit
) {
    var label by remember { mutableStateOf(existing?.label.orEmpty()) }
    var text by remember { mutableStateOf(existing?.text.orEmpty()) }
    var audioUri by remember { mutableStateOf(existing?.audioUri) }
    var duration by remember { mutableLongStateOf(existing?.durationMs ?: 0L) }
    var speed by remember { mutableFloatStateOf(existing?.voiceSpeed ?: 1.0f) }
    var pitch by remember { mutableFloatStateOf(existing?.voicePitch ?: 1.0f) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                if (existing == null) "Yeni anons" else "Anonsu duzenle",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            VSpace(12)

            OutlinedTextField(
                label, { label = it },
                label = { Text("Etiket (ekranda gorunur, okunmaz)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            VSpace(12)

            Text(
                "Anons kaynagi",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            VSpace(6)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        if (isRecording) onStopRecording { uri, dur ->
                            if (uri != null) { audioUri = uri; duration = dur }
                        } else onStartRecording()
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        null, Modifier.size(15.dp),
                        tint = if (isRecording) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (isRecording) "Kaydi bitir" else "Kaydet",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                OutlinedButton(
                    onClick = { onPickAudio { uri, dur -> audioUri = uri; duration = dur } },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.FolderOpen, null, Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Dosya sec", style = MaterialTheme.typography.labelSmall)
                }
                if (audioUri != null) {
                    TextButton(onClick = { audioUri = null; duration = 0L }) {
                        Text("Kaldir", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (audioUri != null) {
                VSpace(8)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Ses kaydi hazir (${formatMs(duration)}). Metin kullanilmayacak.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            VSpace(12)
            OutlinedTextField(
                text, { text = it },
                label = { Text("Okunacak metin") },
                minLines = 3, maxLines = 6,
                enabled = audioUri == null,
                supportingText = {
                    Text(
                        if (audioUri == null) "Ses kaydi yoksa bu metin seslendirilir"
                        else "Ses kaydi secili oldugu icin metin kullanilmiyor"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (audioUri == null) {
                VSpace(12)
                Text("Hiz: ${"%.1f".format(speed)}x", style = MaterialTheme.typography.labelSmall)
                Slider(speed, { speed = it }, valueRange = 0.6f..1.6f)
                Text("Perde: ${"%.1f".format(pitch)}x", style = MaterialTheme.typography.labelSmall)
                Slider(pitch, { pitch = it }, valueRange = 0.6f..1.6f)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onPreviewText(text, speed, pitch) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Dinle", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(onClick = onStopPreview, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Stop, null, Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Dur", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            VSpace(16)
            Button(
                onClick = {
                    onSave(
                        (existing ?: Announcement()).copy(
                            label = label.trim(),
                            text = text.trim(),
                            audioUri = audioUri,
                            durationMs = duration,
                            voiceSpeed = speed,
                            voicePitch = pitch
                        )
                    )
                },
                enabled = audioUri != null || text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Kaydet") }
        }
    }
}

// ── Ayarlar: namaz vakti + yedekleme ────────────────────────────────────────

private val PRAYER_NAMES = listOf("Sabah", "Ogle", "Ikindi", "Aksam", "Yatsi")

@Composable
private fun SettingsTab(
    prayer: PrayerSettings,
    playlists: List<Playlist>,
    aiSettings: AiSettings,
    apiKeyConfigured: Boolean,
    onSave: (PrayerSettings) -> Unit,
    onSaveAiConfiguration: (AiSettings, String?) -> Unit,
    onClearApiKey: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenCatalog: () -> Unit,
    onAutoMapKnowledge: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    var cityMenu by remember { mutableStateOf(false) }
    val times = remember(prayer.latitude, prayer.longitude) { PrayerTimes.forToday(prayer) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp)
    ) {
        SmartAiSettingsCard(
            settings = aiSettings,
            keyConfigured = apiKeyConfigured,
            onSave = onSaveAiConfiguration,
            onClearKey = onClearApiKey,
            onOpenUrl = onOpenUrl,
            onOpenCatalog = onOpenCatalog,
            onAutoMap = onAutoMapKnowledge
        )
        VSpace(12)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    "Namaz vakitleri",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Vakit girince radyo susar; ezan kaydi secerseniz onu calar. Internet gerekmez.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VSpace(8)
                SwitchRow("Namaz vakitlerinde duraklat", null, prayer.enabled) { v ->
                    onSave(prayer.copy(enabled = v))
                }

                if (prayer.enabled) {
                    VSpace(10)
                    Box {
                        OutlinedButton(
                            onClick = { cityMenu = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Sehir: ${prayer.cityName}") }
                        DropdownMenu(cityMenu, { cityMenu = false }) {
                            PrayerTimes.cities.forEach { (name, lat, lng) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        cityMenu = false
                                        onSave(
                                            prayer.copy(
                                                cityName = name, latitude = lat, longitude = lng
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    VSpace(10)
                    Text("Bugunun vakitleri", style = MaterialTheme.typography.labelMedium)
                    VSpace(4)
                    times.prayers.forEachIndexed { index, (name, minuteOfDay) ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = prayer.isActiveFor(index),
                                onCheckedChange = { on ->
                                    val chars = prayer.activePrayers.padEnd(5, '1').toCharArray()
                                    chars[index] = if (on) '1' else '0'
                                    onSave(prayer.copy(activePrayers = String(chars)))
                                }
                            )
                            Text(name, style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f))
                            Text(
                                "%02d:%02d".format(minuteOfDay / 60, minuteOfDay % 60),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    VSpace(10)
                    PlaylistDropdown(
                        "Ezan listesi (bos = sadece sus)",
                        playlists.filter { it.kindEnum != PlaylistKind.BED },
                        prayer.adhanPlaylistId,
                        allowNone = true
                    ) { id -> onSave(prayer.copy(adhanPlaylistId = id)) }

                    VSpace(10)
                    Text(
                        "Vakitten once sus: ${prayer.muteBeforeMinutes} dk",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Slider(
                        value = prayer.muteBeforeMinutes.toFloat(),
                        onValueChange = { v -> onSave(prayer.copy(muteBeforeMinutes = v.toInt())) },
                        valueRange = 0f..15f,
                        steps = 14
                    )
                    Text(
                        "Vakitten sonra susuk kal: ${prayer.muteAfterMinutes} dk",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Slider(
                        value = prayer.muteAfterMinutes.toFloat(),
                        onValueChange = { v -> onSave(prayer.copy(muteAfterMinutes = v.toInt())) },
                        valueRange = 0f..45f
                    )
                }
            }
        }

        VSpace(12)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    "Yedekleme",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Listeler, anonslar, programlar ve ayarlar tek dosyaya yazilir. " +
                        "Ses dosyalari yedege dahil degildir; telefonda kaldiklari surece calisir.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VSpace(10)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onExport, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Download, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Yedek al")
                    }
                    OutlinedButton(onClick = onImport, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Upload, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Geri yukle")
                    }
                }
            }
        }
        VSpace(28)
    }
}
