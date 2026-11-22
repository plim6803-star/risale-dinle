package com.benimradyom.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.benimradyom.app.data.model.*
import com.benimradyom.app.ui.components.VSpace
import com.benimradyom.app.ui.components.formatMs
import com.benimradyom.app.viewmodel.SmartGenerationState

/** Dogal dille taslak olusturma ve kesin sirali karma yayin editoru. */
@Composable
fun SmartFlowScreen(
    flows: List<SmartFlow>,
    catalog: List<KnowledgeItem>,
    tracks: List<Track>,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    generation: SmartGenerationState,
    onGenerate: (String, Int) -> Unit,
    onSave: (SmartFlow) -> Unit,
    onActivate: (SmartFlow) -> Unit,
    onDelete: (SmartFlow) -> Unit,
    onSaveKnowledge: (KnowledgeItem) -> Unit,
    onPreviewKnowledge: (KnowledgeItem) -> Unit,
    onStopPreview: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenCatalog: () -> Unit
) {
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingKnowledge by remember { mutableStateOf<KnowledgeItem?>(null) }

    LaunchedEffect(flows, generation.selectedFlowId) {
        val id = generation.selectedFlowId
        if (id != null && flows.any { it.id == id }) editingId = id
    }

    val editing = flows.firstOrNull { it.id == editingId }
    if (editing != null) {
        SmartFlowEditor(
            flow = editing,
            catalog = catalog,
            tracks = tracks,
            playlists = playlists,
            announcements = announcements,
            onBack = { editingId = null },
            onSave = onSave,
            onActivate = onActivate,
            onEditKnowledge = { editingKnowledge = it },
            onPreviewKnowledge = onPreviewKnowledge,
            onStopPreview = onStopPreview,
            onPlayTrack = onPlayTrack
        )
    } else {
        SmartFlowHome(
            flows = flows,
            catalog = catalog,
            generation = generation,
            onGenerate = onGenerate,
            onOpen = { editingId = it.id },
            onActivate = onActivate,
            onDelete = onDelete,
            onOpenCatalog = onOpenCatalog
        )
    }

    editingKnowledge?.let { item ->
        KnowledgeEditorSheet(
            existing = item,
            tracks = tracks,
            playlists = playlists,
            onDismiss = { editingKnowledge = null },
            onSave = { onSaveKnowledge(it); editingKnowledge = null },
            onDelete = null,
            onPreview = onPreviewKnowledge,
            onStopPreview = onStopPreview,
            onOpenUrl = onOpenUrl
        )
    }
}

@Composable
private fun SmartFlowHome(
    flows: List<SmartFlow>,
    catalog: List<KnowledgeItem>,
    generation: SmartGenerationState,
    onGenerate: (String, Int) -> Unit,
    onOpen: (SmartFlow) -> Unit,
    onActivate: (SmartFlow) -> Unit,
    onDelete: (SmartFlow) -> Unit,
    onOpenCatalog: () -> Unit
) {
    var request by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableIntStateOf(45) }
    var deleting by remember { mutableStateOf<SmartFlow?>(null) }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Nasıl bir yayın istiyorsunuz?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "Örnek: Risale-i Nur'dan melekler hakkında, aralarda ilgili ayet ve hadis bulunan bir yayın hazırla.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    VSpace(10)
                    OutlinedTextField(
                        value = request,
                        onValueChange = { request = it },
                        label = { Text("Yayın isteği") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                    VSpace(8)
                    Text("Yaklaşık süre", style = MaterialTheme.typography.labelMedium)
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(20, 30, 45, 60, 90).forEach { value ->
                            FilterChip(
                                selected = minutes == value,
                                onClick = { minutes = value },
                                label = { Text("$value dk") }
                            )
                        }
                    }
                    VSpace(8)
                    Button(
                        onClick = { onGenerate(request, minutes) },
                        enabled = request.isNotBlank() && !generation.loading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (generation.loading) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Taslak hazırlanıyor")
                        } else {
                            Icon(Icons.Default.AutoAwesome, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(7.dp))
                            Text("Akıllı taslak oluştur")
                        }
                    }
                    if (generation.message.isNotBlank()) {
                        VSpace(6)
                        Text(
                            generation.message,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onOpenCatalog,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.MenuBook, null, Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text("Kaynak kataloğu ve ses eşleştirme (${catalog.size})")
            }
        }

        item {
            Text(
                "Kayıtlı akışlar",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        if (flows.isEmpty()) {
            item {
                Text(
                    "Henüz akış yok. Yukarıdaki kutuya konuyu tarif ederek başlayın.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        } else {
            items(flows, key = { it.id }) { flow ->
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    tonalElevation = 1.dp,
                    color = if (flow.isActive) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().clickable { onOpen(flow) }
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QueueMusic, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                flow.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${flow.entries.size} öğe" + if (flow.loop) " · döngü" else "" +
                                    if (flow.isActive) " · yayında" else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!flow.isActive) {
                            TextButton(onClick = { onActivate(flow) }) { Text("Yayınla") }
                        }
                        IconButton(onClick = { deleting = flow }) {
                            Icon(Icons.Default.DeleteOutline, "Sil", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    deleting?.let { flow ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Akış silinsin mi?") },
            text = { Text("'${flow.name}' silinecek. Ses dosyalarınız silinmez.") },
            confirmButton = {
                Button(onClick = { onDelete(flow); deleting = null }) { Text("Sil") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Vazgeç") } }
        )
    }
}

@Composable
private fun SmartFlowEditor(
    flow: SmartFlow,
    catalog: List<KnowledgeItem>,
    tracks: List<Track>,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    onBack: () -> Unit,
    onSave: (SmartFlow) -> Unit,
    onActivate: (SmartFlow) -> Unit,
    onEditKnowledge: (KnowledgeItem) -> Unit,
    onPreviewKnowledge: (KnowledgeItem) -> Unit,
    onStopPreview: () -> Unit,
    onPlayTrack: (Track) -> Unit
) {
    var draft by remember(flow.id, flow.updatedAt) { mutableStateOf(flow) }
    var suggestionQuery by rememberSaveable(flow.id) { mutableStateOf(flow.request) }
    var suggestionType by rememberSaveable(flow.id) { mutableIntStateOf(0) }
    val entries = draft.entries
    val trackById = remember(tracks) { tracks.associateBy { it.id } }
    val playlistById = remember(playlists) { playlists.associateBy { it.id } }
    val knowledgeById = remember(catalog) { catalog.associateBy { it.id } }
    val annById = remember(announcements) { announcements.associateBy { it.id } }
    val readyCount = entries.count {
        it.isEnabled && entryReady(it, knowledgeById, trackById, annById)
    }
    val estimate = estimateDuration(entries, knowledgeById, trackById, annById)

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }
            Column(Modifier.weight(1f)) {
                Text("Akış düzenleyici", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(
                    "$readyCount hazır · yaklaşık ${formatMs(estimate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = { onSave(draft) }) { Text("Kaydet") }
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    draft.name,
                    { draft = draft.copy(name = it, updatedAt = System.currentTimeMillis()) },
                    label = { Text("Yayın adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp) {
                    Column(Modifier.padding(12.dp)) {
                        SwitchLine("Bittiğinde başa dön", draft.loop) {
                            draft = draft.copy(loop = it, updatedAt = System.currentTimeMillis())
                        }
                        SwitchLine("Konuşmaların altında fon çal", draft.bedEnabled) {
                            draft = draft.copy(bedEnabled = it, updatedAt = System.currentTimeMillis())
                        }
                        if (draft.bedEnabled) {
                            BedPlaylistPicker(playlists, draft.bedPlaylistId) {
                                draft = draft.copy(bedPlaylistId = it, updatedAt = System.currentTimeMillis())
                            }
                            Text(
                                "Yeni fon eklemek için Kütüphane → Listelerim → Fon Müzik bölümünde cihaz sesi veya HTTPS medya bağlantısı kullanın.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text("Fon sesi: %${(draft.bedVolume * 100).toInt()}", style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = draft.bedVolume,
                                onValueChange = { draft = draft.copy(bedVolume = it) },
                                valueRange = 0.05f..0.45f
                            )
                        }
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Yayın sırası",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text("${entries.size} öğe", style = MaterialTheme.typography.labelSmall)
                }
            }
            if (entries.isEmpty()) {
                item { Text("Aşağıdaki önerilerden içerik ekleyin.", style = MaterialTheme.typography.bodySmall) }
            } else {
                itemsIndexed(entries, key = { index, item -> "$index-${item.kind}-${item.sourceId}" }) { index, entry ->
                    FlowEntryRow(
                        index = index,
                        total = entries.size,
                        entry = entry,
                        knowledge = knowledgeById[entry.sourceId].takeIf { entry.kindEnum == FlowEntryKind.KNOWLEDGE },
                        track = trackById[entry.sourceId].takeIf { entry.kindEnum == FlowEntryKind.TRACK },
                        announcement = annById[entry.sourceId].takeIf { entry.kindEnum == FlowEntryKind.ANNOUNCEMENT },
                        mappedTrack = knowledgeById[entry.sourceId]?.trackId?.let(trackById::get),
                        playlistById = playlistById,
                        onToggle = {
                            val changed = entries.toMutableList()
                            changed[index] = entry.copy(isEnabled = !entry.isEnabled)
                            draft = draft.withEntries(changed)
                        },
                        onMove = { delta ->
                            val to = (index + delta).coerceIn(0, entries.lastIndex)
                            if (to != index) {
                                val changed = entries.toMutableList()
                                val item = changed.removeAt(index)
                                changed.add(to, item)
                                draft = draft.withEntries(changed)
                            }
                        },
                        onDelete = {
                            draft = draft.withEntries(entries.toMutableList().also { it.removeAt(index) })
                        },
                        onEditKnowledge = { knowledgeById[entry.sourceId]?.let(onEditKnowledge) },
                        onPreviewKnowledge = { knowledgeById[entry.sourceId]?.let(onPreviewKnowledge) },
                        onStopPreview = onStopPreview,
                        onPlayTrack = { (trackById[entry.sourceId] ?: knowledgeById[entry.sourceId]?.trackId?.let(trackById::get))?.let(onPlayTrack) }
                    )
                }
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Text(
                    "Öneri paneli",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Konuya göre kaynak, cihaz sesi veya anons ekleyin.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VSpace(6)
                OutlinedTextField(
                    suggestionQuery,
                    { suggestionQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Önerilerde ara") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Kaynaklar", "Sesler", "Anonslar").forEachIndexed { i, label ->
                        FilterChip(
                            selected = suggestionType == i,
                            onClick = { suggestionType = i },
                            label = { Text(label) }
                        )
                    }
                }
            }

            when (suggestionType) {
                0 -> {
                    val suggestions = catalog.filter { item ->
                        entries.none { it.kindEnum == FlowEntryKind.KNOWLEDGE && it.sourceId == item.id } &&
                            matches(item.searchableText(), suggestionQuery)
                    }.sortedByDescending { it.isReady }.take(12)
                    items(suggestions, key = { "suggest-k-${it.id}" }) { item ->
                        SuggestionRow(
                            title = "${item.typeEnum.emoji} ${item.title}",
                            subtitle = item.reference.ifBlank { item.statusText },
                            ready = item.isReady,
                            onAdd = {
                                draft = draft.withEntries(
                                    entries + SmartFlowEntry(
                                        FlowEntryKind.KNOWLEDGE.name, item.id, item.isReady
                                    )
                                )
                            }
                        )
                    }
                }
                1 -> {
                    val suggestions = tracks.filter { track ->
                        entries.none { it.kindEnum == FlowEntryKind.TRACK && it.sourceId == track.id } &&
                            matches(track.title + " " + track.artist, suggestionQuery)
                    }.take(12)
                    items(suggestions, key = { "suggest-t-${it.id}" }) { track ->
                        SuggestionRow(
                            title = "🎧 ${track.title}",
                            subtitle = track.artist.ifBlank { playlistById[track.playlistId]?.name.orEmpty() },
                            ready = true,
                            onAdd = { draft = draft.withEntries(entries + SmartFlowEntry(FlowEntryKind.TRACK.name, track.id)) }
                        )
                    }
                }
                else -> {
                    val suggestions = announcements.filter { ann ->
                        entries.none { it.kindEnum == FlowEntryKind.ANNOUNCEMENT && it.sourceId == ann.id } &&
                            matches(ann.label + " " + ann.text, suggestionQuery)
                    }.take(12)
                    items(suggestions, key = { "suggest-a-${it.id}" }) { ann ->
                        SuggestionRow(
                            title = "📢 ${ann.label.ifBlank { "Anons" }}",
                            subtitle = if (ann.isAudio) "Ses kaydı" else ann.text.take(70),
                            ready = ann.isEnabled && (ann.isAudio || ann.text.isNotBlank()),
                            onAdd = { draft = draft.withEntries(entries + SmartFlowEntry(FlowEntryKind.ANNOUNCEMENT.name, ann.id)) }
                        )
                    }
                }
            }

            item {
                VSpace(6)
                if (readyCount == 0) {
                    Text(
                        "Yayına almak için en az bir hazır öğe gerekir. Risale sesini eşleştirin veya ayet/hadis metnini kontrol edip onaylayın.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    VSpace(6)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSave(draft) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(11.dp)
                    ) { Text("Kaydet") }
                    Button(
                        onClick = { onActivate(draft) },
                        enabled = readyCount > 0,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(11.dp)
                    ) {
                        Icon(Icons.Default.Podcasts, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Yayına al")
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowEntryRow(
    index: Int,
    total: Int,
    entry: SmartFlowEntry,
    knowledge: KnowledgeItem?,
    track: Track?,
    announcement: Announcement?,
    mappedTrack: Track?,
    playlistById: Map<Long, Playlist>,
    onToggle: () -> Unit,
    onMove: (Int) -> Unit,
    onDelete: () -> Unit,
    onEditKnowledge: () -> Unit,
    onPreviewKnowledge: () -> Unit,
    onStopPreview: () -> Unit,
    onPlayTrack: () -> Unit
) {
    val title = when (entry.kindEnum) {
        FlowEntryKind.KNOWLEDGE -> knowledge?.let { "${it.typeEnum.emoji} ${it.title}" } ?: "Eksik kaynak"
        FlowEntryKind.TRACK -> "🎧 ${track?.title ?: "Eksik ses"}"
        FlowEntryKind.ANNOUNCEMENT -> "📢 ${announcement?.label?.ifBlank { "Anons" } ?: "Eksik anons"}"
    }
    val subtitle = when (entry.kindEnum) {
        FlowEntryKind.KNOWLEDGE -> knowledge?.reference.orEmpty().ifBlank { knowledge?.statusText.orEmpty() }
        FlowEntryKind.TRACK -> track?.artist?.ifBlank { playlistById[track.playlistId]?.name.orEmpty() }.orEmpty()
        FlowEntryKind.ANNOUNCEMENT -> if (announcement?.isAudio == true) "Ses kaydı" else announcement?.text.orEmpty().take(60)
    }
    val ready = when (entry.kindEnum) {
        FlowEntryKind.KNOWLEDGE -> knowledge?.let {
            it.isReady && (it.typeEnum != KnowledgeType.RISALE || mappedTrack != null)
        } == true
        FlowEntryKind.TRACK -> track != null
        FlowEntryKind.ANNOUNCEMENT -> announcement?.isEnabled == true && (announcement.isAudio || announcement.text.isNotBlank())
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        color = if (!entry.isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${index + 1}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(26.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (!ready) Text(knowledge?.statusText ?: "Kullanılamıyor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    if (knowledge?.typeEnum == KnowledgeType.RISALE && mappedTrack != null) {
                        Text("Ses: ${mappedTrack.title}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                    }
                }
                Switch(checked = entry.isEnabled, onCheckedChange = { onToggle() }, enabled = ready)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onMove(-1) }, enabled = index > 0, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, "Yukarı", Modifier.size(20.dp))
                }
                IconButton(onClick = { onMove(1) }, enabled = index < total - 1, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.KeyboardArrowDown, "Aşağı", Modifier.size(20.dp))
                }
                if (entry.kindEnum == FlowEntryKind.KNOWLEDGE) {
                    IconButton(onClick = onEditKnowledge, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, "Düzenle", Modifier.size(18.dp))
                    }
                    if (knowledge?.typeEnum == KnowledgeType.RISALE) {
                        IconButton(onClick = onPlayTrack, enabled = ready, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.PlayArrow, "Dinle", Modifier.size(20.dp))
                        }
                    } else {
                        IconButton(onClick = onPreviewKnowledge, enabled = knowledge?.text?.isNotBlank() == true, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.RecordVoiceOver, "Seslendir", Modifier.size(19.dp))
                        }
                        IconButton(onClick = onStopPreview, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Stop, "Durdur", Modifier.size(18.dp))
                        }
                    }
                } else if (entry.kindEnum == FlowEntryKind.TRACK) {
                    IconButton(onClick = onPlayTrack, enabled = ready, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.PlayArrow, "Dinle", Modifier.size(20.dp))
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.DeleteOutline, "Akıştan çıkar", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun SuggestionRow(title: String, subtitle: String, ready: Boolean, onAdd: () -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = if (ready) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onAdd, Modifier.size(34.dp)) { Icon(Icons.Default.Add, "Ekle", Modifier.size(19.dp)) }
        }
    }
}

@Composable
private fun SwitchLine(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun BedPlaylistPicker(playlists: List<Playlist>, selected: Long, onSelect: (Long) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val beds = playlists.filter { it.kindEnum == PlaylistKind.BED }
    Box {
        TextButton(onClick = { open = true }) {
            Text("Fon listesi: ${beds.firstOrNull { it.id == selected }?.name ?: "Varsayılan"}")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(text = { Text("Varsayılan fon") }, onClick = { onSelect(-1L); open = false })
            beds.forEach { p ->
                DropdownMenuItem(text = { Text(p.name) }, onClick = { onSelect(p.id); open = false })
            }
        }
    }
}

/** Ayarlar sekmesinde kullanilan API ve gizlilik paneli. */
@Composable
fun SmartAiSettingsCard(
    settings: AiSettings,
    keyConfigured: Boolean,
    onSave: (AiSettings, String?) -> Unit,
    onClearKey: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenCatalog: () -> Unit,
    onAutoMap: () -> Unit
) {
    var enabled by remember(settings) { mutableStateOf(settings.enabled) }
    var endpoint by remember(settings) { mutableStateOf(settings.endpoint) }
    var model by remember(settings) { mutableStateOf(settings.model) }
    var imageModel by remember(settings) { mutableStateOf(settings.imageModel) }
    var imageQuality by remember(settings) { mutableStateOf(settings.imageQuality) }
    var visualStyle by remember(settings) { mutableStateOf(settings.visualStyle) }
    var key by remember { mutableStateOf("") }
    var advanced by rememberSaveable { mutableStateOf(false) }

    Surface(shape = RoundedCornerShape(14.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text("Akıllı yayın ve kaynaklar", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(
                "API kapalıyken de konu fihristiyle yerel taslak oluşturulur. Akıllı sıralamada ses gönderilmez. Bir ses dosyası yalnızca siz 'AI zaman kodu' veya metinsiz 'Ayet geçişlerini bul' işlemini ayrıca onaylarsanız gönderilir.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            VSpace(8)
            SwitchLine("Yapay zekâ ile sıralama", enabled) { enabled = it }
            if (enabled) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text(if (keyConfigured) "Yeni API anahtarı (isteğe bağlı)" else "API anahtarı") },
                    placeholder = { Text(if (keyConfigured) "Kayıtlı anahtar korunacak" else "sk-...") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    if (keyConfigured) "✓ Şifreli anahtar kayıtlı" else "Anahtar henüz kayıtlı değil",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (keyConfigured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                TextButton(onClick = { advanced = !advanced }) {
                    Text(if (advanced) "Gelişmiş ayarları gizle" else "Model ve API adresi")
                }
                if (advanced) {
                    OutlinedTextField(model, { model = it }, label = { Text("Model") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    VSpace(6)
                    OutlinedTextField(endpoint, { endpoint = it }, label = { Text("HTTPS API adresi") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    VSpace(6)
                    OutlinedTextField(
                        imageModel,
                        { imageModel = it },
                        label = { Text("Tefekkür görsel modeli") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    VSpace(6)
                    Text("Görsel kalitesi", style = MaterialTheme.typography.labelMedium)
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("low" to "Ekonomik", "medium" to "Dengeli", "high" to "Yüksek").forEach { (value, label) ->
                            FilterChip(
                                selected = imageQuality == value,
                                onClick = { imageQuality = value },
                                label = { Text(label) }
                            )
                        }
                    }
                    OutlinedTextField(
                        visualStyle,
                        { visualStyle = it },
                        label = { Text("Tefekkür görsel üslubu") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Kutsal veya gaybî varlıklar resmedilmez; doğa, ışık, mimari ve geometrik motiflerle temsilî sahneler hazırlanır.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onOpenUrl("https://platform.openai.com/api-keys") }) { Text("Anahtar sayfası") }
                    if (keyConfigured) TextButton(onClick = onClearKey) { Text("Anahtarı sil") }
                }
            }
            Button(
                onClick = {
                    onSave(
                        AiSettings(
                            enabled = enabled,
                            endpoint = endpoint.trim(),
                            model = model.trim(),
                            imageModel = imageModel.trim(),
                            imageQuality = imageQuality,
                            visualStyle = visualStyle.trim()
                        ),
                        key.trim().takeIf { it.isNotBlank() }
                    )
                    key = ""
                },
                enabled = !enabled || (
                    endpoint.startsWith("https://") && model.isNotBlank() &&
                        imageModel.isNotBlank() && visualStyle.isNotBlank()
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Akıllı yayın ayarlarını kaydet") }
            VSpace(8)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenCatalog, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.MenuBook, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Katalog")
                }
                OutlinedButton(onClick = onAutoMap, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.AutoFixHigh, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Ses eşleştir")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeCatalogSheet(
    catalog: List<KnowledgeItem>,
    tracks: List<Track>,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSave: (KnowledgeItem) -> Unit,
    onDelete: (KnowledgeItem) -> Unit,
    onPreview: (KnowledgeItem) -> Unit,
    onStopPreview: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<KnowledgeItem?>(null) }
    var creating by remember { mutableStateOf(false) }
    val filtered = remember(query, type, catalog) {
        catalog.filter {
            (type == 0 || it.typeEnum.ordinal == type - 1) && matches(it.searchableText(), query)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxHeight(0.92f).padding(horizontal = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Kaynak kataloğu", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("${catalog.count { it.isReady }} hazır / ${catalog.size} kart", style = MaterialTheme.typography.labelSmall)
                }
                Button(onClick = { creating = true }) { Icon(Icons.Default.Add, null); Text("Yeni") }
            }
            VSpace(8)
            OutlinedTextField(query, { query = it }, leadingIcon = { Icon(Icons.Default.Search, null) }, label = { Text("Başlık, konu veya referans ara") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("Tümü", "Risale", "Ayet", "Hadis").forEachIndexed { i, label ->
                    FilterChip(selected = type == i, onClick = { type = i }, label = { Text(label) })
                }
            }
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                items(filtered, key = { it.id }) { item ->
                    Surface(shape = RoundedCornerShape(11.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().clickable { editing = item }) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(item.typeEnum.emoji, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(item.reference, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(item.statusText, style = MaterialTheme.typography.labelSmall, color = if (item.isReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            }
                            Icon(Icons.Default.ChevronRight, null)
                        }
                    }
                }
            }
        }
    }

    if (creating || editing != null) {
        KnowledgeEditorSheet(
            existing = editing,
            tracks = tracks,
            playlists = playlists,
            onDismiss = { creating = false; editing = null },
            onSave = { onSave(it); creating = false; editing = null },
            onDelete = editing?.let { item -> { onDelete(item); editing = null } },
            onPreview = onPreview,
            onStopPreview = onStopPreview,
            onOpenUrl = onOpenUrl
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeEditorSheet(
    existing: KnowledgeItem?,
    tracks: List<Track>,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSave: (KnowledgeItem) -> Unit,
    onDelete: (() -> Unit)?,
    onPreview: (KnowledgeItem) -> Unit,
    onStopPreview: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    val base = existing ?: KnowledgeItem(sortOrder = Int.MAX_VALUE)
    var kind by remember(existing?.id) { mutableStateOf(base.typeEnum) }
    var title by remember(existing?.id) { mutableStateOf(base.title) }
    var collection by remember(existing?.id) { mutableStateOf(base.collection) }
    var section by remember(existing?.id) { mutableStateOf(base.section) }
    var reference by remember(existing?.id) { mutableStateOf(base.reference) }
    var summary by remember(existing?.id) { mutableStateOf(base.summary) }
    var body by remember(existing?.id) { mutableStateOf(base.text) }
    var topics by remember(existing?.id) { mutableStateOf(base.topics.joinToString(", ")) }
    var keywords by remember(existing?.id) { mutableStateOf(base.keywords.joinToString(", ")) }
    var url by remember(existing?.id) { mutableStateOf(base.sourceUrl) }
    var trackId by remember(existing?.id) { mutableLongStateOf(base.trackId) }
    var reviewed by remember(existing?.id) { mutableStateOf(base.isReviewed) }
    var enabled by remember(existing?.id) { mutableStateOf(base.isEnabled) }
    var speed by remember(existing?.id) { mutableFloatStateOf(base.voiceSpeed) }
    var pitch by remember(existing?.id) { mutableFloatStateOf(base.voicePitch) }
    var trackQuery by remember { mutableStateOf("") }
    val playlistById = remember(playlists) { playlists.associateBy { it.id } }

    fun build() = base.copy(
        type = kind.name,
        title = title.trim(), collection = collection.trim(), section = section.trim(),
        reference = reference.trim(), summary = summary.trim(), text = body.trim(),
        topicsJson = KnowledgeItem.csvToJson(topics),
        keywordsJson = KnowledgeItem.csvToJson(keywords),
        sourceUrl = url.trim(), trackId = if (kind == KnowledgeType.RISALE) trackId else -1L,
        isReviewed = reviewed, isEnabled = enabled, voiceSpeed = speed, voicePitch = pitch
    )

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxHeight(0.94f).verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp).padding(bottom = 32.dp)
        ) {
            Text(if (existing == null) "Yeni kaynak kartı" else "Kaynağı düzenle", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            VSpace(8)
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                KnowledgeType.entries.forEach { t ->
                    FilterChip(selected = kind == t, onClick = { kind = t }, label = { Text("${t.emoji} ${t.label}") })
                }
            }
            OutlinedTextField(title, { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(collection, { collection = it }, label = { Text("Kitap / külliyat") }, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(section, { section = it }, label = { Text("Bölüm / sure") }, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(reference, { reference = it }, label = { Text("Kaynak referansı") }, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(summary, { summary = it }, label = { Text("Konu özeti (kaynak metni değildir)") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(topics, { topics = it }, label = { Text("Konular (virgülle)") }, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(keywords, { keywords = it }, label = { Text("Anahtar kavramlar (virgülle)") }, modifier = Modifier.fillMaxWidth())
            VSpace(6)
            OutlinedTextField(url, { url = it }, label = { Text("Kaynak bağlantısı") }, modifier = Modifier.fillMaxWidth())
            if (url.startsWith("http")) TextButton(onClick = { onOpenUrl(url) }) { Icon(Icons.Default.OpenInNew, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Kaynağı aç ve kontrol et") }

            if (kind == KnowledgeType.RISALE) {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Cihazdaki sesle eşleştir", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                val selectedTrack = tracks.firstOrNull { it.id == trackId }
                Text(selectedTrack?.let { "Seçili: ${it.title}" } ?: "Henüz ses seçilmedi", style = MaterialTheme.typography.bodySmall, color = if (selectedTrack == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                OutlinedTextField(trackQuery, { trackQuery = it }, label = { Text("Seslerde ara") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                tracks.filter { matches(it.title + " " + it.artist, trackQuery) }.take(8).forEach { track ->
                    TextButton(onClick = { trackId = track.id }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth()) {
                            Text(track.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(playlistById[track.playlistId]?.name.orEmpty(), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (trackId > 0) TextButton(onClick = { trackId = -1L }) { Text("Eşleşmeyi kaldır") }
            } else {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Yayında okunacak metin", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(
                    "Metni kaynak sayfasından kontrol ederek buraya ekleyin. Yapay zekâ bu alanı doldurmaz.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(body, { body = it; reviewed = false }, label = { Text(if (kind == KnowledgeType.VERSE) "Kontrol edilmiş meal" else "Kontrol edilmiş hadis metni") }, minLines = 5, maxLines = 12, modifier = Modifier.fillMaxWidth())
                SwitchLine("Kaynakla karşılaştırdım; yayında okunabilir", reviewed) { reviewed = it }
                Text("Okuma hızı: ${"%.1f".format(speed)}x", style = MaterialTheme.typography.labelSmall)
                Slider(speed, { speed = it }, valueRange = 0.6f..1.5f)
                Text("Ses perdesi: ${"%.1f".format(pitch)}x", style = MaterialTheme.typography.labelSmall)
                Slider(pitch, { pitch = it }, valueRange = 0.6f..1.5f)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onPreview(build()) }, enabled = body.isNotBlank()) { Icon(Icons.Default.RecordVoiceOver, null); Text("Seslendir") }
                    OutlinedButton(onClick = onStopPreview) { Icon(Icons.Default.Stop, null); Text("Dur") }
                }
            }
            SwitchLine("Bu kart önerilerde görünsün", enabled) { enabled = it }
            VSpace(10)
            Button(onClick = { onSave(build()) }, enabled = title.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Kaydet") }
            if (onDelete != null) {
                TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.DeleteOutline, null); Text("Kaynak kartını sil", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

private fun matches(text: String, query: String): Boolean {
    if (query.isBlank()) return true
    val haystack = normalizeSearch(text)
    return normalizeSearch(query).split(' ').filter { it.length >= 2 }.all(haystack::contains)
}

private fun normalizeSearch(value: String): String = value.lowercase()
    .replace('ı', 'i').replace('ğ', 'g').replace('ü', 'u')
    .replace('ş', 's').replace('ö', 'o').replace('ç', 'c')
    .replace(Regex("[^a-z0-9]+"), " ").trim()

private fun entryReady(
    entry: SmartFlowEntry,
    knowledge: Map<Long, KnowledgeItem>,
    tracks: Map<Long, Track>,
    announcements: Map<Long, Announcement>
): Boolean = when (entry.kindEnum) {
    FlowEntryKind.KNOWLEDGE -> knowledge[entry.sourceId]?.let {
        it.isReady && (it.typeEnum != KnowledgeType.RISALE || tracks.containsKey(it.trackId))
    } == true
    FlowEntryKind.TRACK -> tracks.containsKey(entry.sourceId)
    FlowEntryKind.ANNOUNCEMENT -> announcements[entry.sourceId]?.let { it.isEnabled && (it.isAudio || it.text.isNotBlank()) } == true
}

private fun estimateDuration(
    entries: List<SmartFlowEntry>,
    knowledge: Map<Long, KnowledgeItem>,
    tracks: Map<Long, Track>,
    announcements: Map<Long, Announcement>
): Long = entries.filter { it.isEnabled }.sumOf { entry ->
    when (entry.kindEnum) {
        FlowEntryKind.TRACK -> tracks[entry.sourceId]?.durationMs ?: 0L
        FlowEntryKind.ANNOUNCEMENT -> announcements[entry.sourceId]?.let {
            if (it.isAudio) it.durationMs else (it.text.split(Regex("\\s+")).size * 430L)
        } ?: 0L
        FlowEntryKind.KNOWLEDGE -> knowledge[entry.sourceId]?.let { item ->
            if (item.typeEnum == KnowledgeType.RISALE) tracks[item.trackId]?.durationMs ?: 0L
            else item.text.split(Regex("\\s+")).size * 430L
        } ?: 0L
    }
}
