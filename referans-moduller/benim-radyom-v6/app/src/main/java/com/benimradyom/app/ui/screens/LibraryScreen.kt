package com.benimradyom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.benimradyom.app.data.media.DeviceTrack
import com.benimradyom.app.data.model.LinkedFolder
import com.benimradyom.app.data.model.Playlist
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.Track
import com.benimradyom.app.data.model.TrackOrder
import com.benimradyom.app.ui.components.ColorDot
import com.benimradyom.app.ui.components.EmptyState
import com.benimradyom.app.ui.components.KindBadge
import com.benimradyom.app.ui.components.VSpace
import com.benimradyom.app.ui.components.formatDurationLong
import com.benimradyom.app.ui.components.formatMs
import com.benimradyom.app.ui.components.kindColor
import com.benimradyom.app.ui.theme.playlistPalette
import com.benimradyom.app.viewmodel.LibraryState

/**
 * Kutuphane: cihazdaki sesler + kendi listeleriniz tek ekranda.
 *
 * Eski surumde muzik eklemek icin Android dosya secicisine cikmak,
 * klasor klasor gezmek ve her liste icin bastan tekrarlamak gerekiyordu.
 * Burada hepsi uygulamanin icinde.
 */
@Composable
fun LibraryScreen(
    library: LibraryState,
    playlists: List<Playlist>,
    linkedFolders: List<LinkedFolder>,
    selectedPlaylist: Playlist?,
    selectedTracks: List<Track>,
    onRequestPermission: () -> Unit,
    onRefresh: () -> Unit,
    onQuery: (String) -> Unit,
    onOpenFolder: (String?) -> Unit,
    onToggleSelect: (String) -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onAddSelectedTo: (Long) -> Unit,
    onAddFolderTo: (String, Long, Boolean) -> Unit,
    onAddMediaLink: (Long, String, String) -> Unit,
    onUnlinkFolder: (Long, String) -> Unit,
    onCreatePlaylist: (String, PlaylistKind, Int) -> Unit,
    onSelectPlaylist: (Playlist?) -> Unit,
    onUpdatePlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Playlist) -> Unit,
    onDeleteTrack: (Track) -> Unit,
    onPlayTrack: (Track) -> Unit,
    onSortPlaylist: (Long, TrackOrder) -> Unit,
    onShiftTrack: (Track, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var pickerFor by remember { mutableStateOf<PickTarget?>(null) }
    var showCreate by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(tab == 0, { tab = 0; onOpenFolder(null) }, text = { Text("Klasorler") })
            Tab(tab == 1, { tab = 1 }, text = { Text("Sesler") })
            Tab(tab == 2, { tab = 2 }, text = { Text("Listelerim") })
        }

        when (tab) {
            0, 1 -> DeviceSection(
                library = library,
                isFolderTab = tab == 0,
                linkedFolders = linkedFolders,
                onRequestPermission = onRequestPermission,
                onRefresh = onRefresh,
                onQuery = onQuery,
                onOpenFolder = onOpenFolder,
                onToggleSelect = onToggleSelect,
                onSelectAll = onSelectAll,
                onClearSelection = onClearSelection,
                onAddSelected = { pickerFor = PickTarget.Selection },
                onAddFolder = { key -> pickerFor = PickTarget.Folder(key) }
            )

            else -> PlaylistsSection(
                playlists = playlists,
                linkedFolders = linkedFolders,
                selected = selectedPlaylist,
                tracks = selectedTracks,
                onSelect = onSelectPlaylist,
                onCreate = { showCreate = true },
                onUpdate = onUpdatePlaylist,
                onDelete = onDeletePlaylist,
                onDeleteTrack = onDeleteTrack,
                onPlayTrack = onPlayTrack,
                onUnlinkFolder = onUnlinkFolder,
                onSortPlaylist = onSortPlaylist,
                onShiftTrack = onShiftTrack,
                onAddMediaLink = onAddMediaLink
            )
        }
    }

    pickerFor?.let { target ->
        PlaylistPickerSheet(
            playlists = playlists,
            title = when (target) {
                is PickTarget.Selection -> "${library.selected.size} ses nereye eklensin?"
                is PickTarget.Folder -> "Klasor nereye eklensin?"
            },
            showSyncOption = target is PickTarget.Folder,
            onCreateNew = { showCreate = true },
            onPicked = { playlistId, keepInSync ->
                when (target) {
                    is PickTarget.Selection -> onAddSelectedTo(playlistId)
                    is PickTarget.Folder -> onAddFolderTo(target.key, playlistId, keepInSync)
                }
                pickerFor = null
            },
            onDismiss = { pickerFor = null }
        )
    }

    if (showCreate) {
        CreatePlaylistDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, kind, color ->
                onCreatePlaylist(name, kind, color)
                showCreate = false
            }
        )
    }
}

private sealed interface PickTarget {
    data object Selection : PickTarget
    data class Folder(val key: String) : PickTarget
}

// ── Cihaz bolumu ────────────────────────────────────────────────────────────

@Composable
private fun DeviceSection(
    library: LibraryState,
    isFolderTab: Boolean,
    linkedFolders: List<LinkedFolder>,
    onRequestPermission: () -> Unit,
    onRefresh: () -> Unit,
    onQuery: (String) -> Unit,
    onOpenFolder: (String?) -> Unit,
    onToggleSelect: (String) -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onAddSelected: () -> Unit,
    onAddFolder: (String) -> Unit
) {
    if (!library.permissionGranted) {
        EmptyState(
            icon = Icons.Default.LibraryMusic,
            title = "Muzik erisimi gerekli",
            subtitle = "Telefonunuzdaki ses dosyalarini uygulama icinde gosterebilmek icin " +
                "muzik ve ses iznine ihtiyac var.",
            action = {
                Button(onClick = onRequestPermission, shape = RoundedCornerShape(12.dp)) {
                    Text("Izin ver")
                }
            }
        )
        return
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        VSpace(10)
        SearchRow(library.query, onQuery, onRefresh)
        VSpace(8)

        val inFolder = library.openFolderKey != null
        if (inFolder) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton({ onOpenFolder(null) }, Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", Modifier.size(18.dp))
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    library.openFolder?.name ?: "Klasor",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                TextButton(onClick = onSelectAll) { Text("Tumunu sec") }
            }
            VSpace(4)
        }

        if (library.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            VSpace(8)
        }

        Box(Modifier.weight(1f)) {
            when {
                library.allTracks.isEmpty() && !library.loading -> EmptyState(
                    icon = Icons.Default.Audiotrack,
                    title = "Ses dosyasi bulunamadi",
                    subtitle = "Telefonunuzda okunabilir bir ses dosyasi yok gibi gorunuyor."
                )

                isFolderTab && !inFolder -> FolderList(
                    library = library,
                    linkedKeys = linkedFolders.map { it.folderKey }.toSet(),
                    onOpen = onOpenFolder,
                    onAddFolder = onAddFolder
                )

                else -> TrackList(
                    tracks = library.visibleTracks,
                    selected = library.selected,
                    onToggle = onToggleSelect
                )
            }
        }

        if (library.selected.isNotEmpty()) {
            SelectionBar(
                count = library.selected.size,
                onClear = onClearSelection,
                onAdd = onAddSelected
            )
        }
    }
}

@Composable
private fun SearchRow(query: String, onQuery: (String) -> Unit, onRefresh: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            placeholder = { Text("Ara...", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(18.dp)) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton({ onQuery("") }, Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, "Temizle", Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(6.dp))
        IconButton(onRefresh) {
            Icon(Icons.Default.Refresh, "Yenile", Modifier.size(20.dp))
        }
    }
}

@Composable
private fun FolderList(
    library: LibraryState,
    linkedKeys: Set<String>,
    onOpen: (String) -> Unit,
    onAddFolder: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(library.visibleFolders, key = { it.key }) { folder ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth().clickable { onOpen(folder.key) }
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Folder, null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                folder.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (folder.key in linkedKeys) {
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Link, "Bagli",
                                    Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            "${folder.trackCount} dosya - ${formatDurationLong(folder.totalDurationMs)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FilledTonalButton(
                        onClick = { onAddFolder(folder.key) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, null, Modifier.size(15.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Ekle", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<DeviceTrack>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    if (tracks.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Search,
            title = "Sonuc yok",
            subtitle = "Aramanizla eslesen ses bulunamadi."
        )
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(tracks, key = { it.uri }) { t ->
            val isSel = t.uri in selected
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSel) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().clickable { onToggle(t.uri) }
            ) {
                Row(
                    Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSel, onCheckedChange = { onToggle(t.uri) })
                    Column(Modifier.weight(1f)) {
                        Text(
                            t.title,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            listOfNotNull(
                                t.artist.takeIf { it.isNotBlank() },
                                t.folderName
                            ).joinToString(" - "),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        formatMs(t.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionBar(count: Int, onClear: () -> Unit, onAdd: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$count ses secildi",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onClear) { Text("Vazgec") }
            Spacer(Modifier.width(4.dp))
            Button(onClick = onAdd, shape = RoundedCornerShape(10.dp)) {
                Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Listeye ekle")
            }
        }
    }
}

// ── Listelerim bolumu ───────────────────────────────────────────────────────

@Composable
private fun PlaylistsSection(
    playlists: List<Playlist>,
    linkedFolders: List<LinkedFolder>,
    selected: Playlist?,
    tracks: List<Track>,
    onSelect: (Playlist?) -> Unit,
    onCreate: () -> Unit,
    onUpdate: (Playlist) -> Unit,
    onDelete: (Playlist) -> Unit,
    onDeleteTrack: (Track) -> Unit,
    onPlayTrack: (Track) -> Unit,
    onUnlinkFolder: (Long, String) -> Unit,
    onSortPlaylist: (Long, TrackOrder) -> Unit,
    onShiftTrack: (Track, Int) -> Unit,
    onAddMediaLink: (Long, String, String) -> Unit
) {
    var editKind by remember { mutableStateOf<Playlist?>(null) }
    var confirmDelete by remember { mutableStateOf<Playlist?>(null) }
    var sortMenu by remember { mutableStateOf(false) }
    var showMediaLink by remember { mutableStateOf(false) }

    if (selected == null) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Button(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Yeni liste")
            }
            VSpace(10)
            if (playlists.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.LibraryMusic,
                    title = "Henuz liste yok",
                    subtitle = "Once bir liste olusturun, sonra klasorlerden ses ekleyin."
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(playlists, key = { it.id }) { pl ->
                        val links = linkedFolders.count { it.playlistId == pl.id }
                        PlaylistRow(pl, links, onClick = { onSelect(pl) },
                            onKind = { editKind = pl }, onDelete = { confirmDelete = pl })
                    }
                }
            }
        }
    } else {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton({ onSelect(null) }, Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", Modifier.size(18.dp))
                }
                Spacer(Modifier.width(4.dp))
                ColorDot(selected.color, 12)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        selected.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${tracks.size} ses",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                KindBadge(selected.kindEnum)
                Box {
                    IconButton({ sortMenu = true }, Modifier.size(34.dp)) {
                        Icon(Icons.Default.Sort, "Sirala", Modifier.size(18.dp))
                    }
                    DropdownMenu(sortMenu, { sortMenu = false }) {
                        Text(
                            "Listeyi sirala",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        TrackOrder.entries.filter { it != TrackOrder.SIRALI }.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.label) },
                                onClick = {
                                    sortMenu = false
                                    onSortPlaylist(selected.id, order)
                                }
                            )
                        }
                    }
                }
            }
            VSpace(8)

            OutlinedButton(
                onClick = { showMediaLink = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Link, null, Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    if (selected.kindEnum == PlaylistKind.BED) "Fon müziği bağlantısı ekle"
                    else "Medya bağlantısı ekle"
                )
            }
            VSpace(8)

            val links = linkedFolders.filter { it.playlistId == selected.id }
            if (links.isNotEmpty()) {
                links.forEach { link ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, null, Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "\"${link.displayName}\" klasoru bagli - yeni dosyalar otomatik eklenir",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            { onUnlinkFolder(selected.id, link.folderKey) },
                            Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.LinkOff, "Baglantiyi kaldir", Modifier.size(14.dp))
                        }
                    }
                    VSpace(4)
                }
                VSpace(4)
            }

            if (tracks.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Audiotrack,
                    title = "Bu liste bos",
                    subtitle = "Klasorler sekmesinden bu listeye ses ekleyin."
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    itemsIndexed(tracks, key = { _, item -> item.id }) { index, t ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton({ onPlayTrack(t) }, Modifier.size(32.dp)) {
                                    Icon(Icons.Default.PlayArrow, "Cal", Modifier.size(18.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        t.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                    Row {
                                        if (t.artist.isNotBlank()) {
                                            Text(
                                                t.artist,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                        }
                                        if (t.lastPositionMs > 5000L) {
                                            Text(
                                                "kaldigi yer ${formatMs(t.lastPositionMs)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                Text(
                                    formatMs(t.durationMs),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                IconButton(
                                    onClick = { onShiftTrack(t, -1) },
                                    modifier = Modifier.size(28.dp),
                                    enabled = index > 0
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowUp, "Yukari", Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { onShiftTrack(t, 1) },
                                    modifier = Modifier.size(28.dp),
                                    enabled = index < tracks.lastIndex
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown, "Asagi", Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton({ onDeleteTrack(t) }, Modifier.size(32.dp)) {
                                    Icon(
                                        Icons.Default.DeleteOutline, "Sil", Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editKind?.let { pl ->
        KindPickerDialog(pl, onDismiss = { editKind = null }) { updated ->
            onUpdate(updated); editKind = null
        }
    }
    confirmDelete?.let { pl ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = { Text("Listeyi sil") },
            text = { Text("\"${pl.name}\" ve icindeki ses kayitlari listeden silinecek. Telefonunuzdaki dosyalar silinmez.") },
            confirmButton = {
                TextButton({ onDelete(pl); confirmDelete = null }) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton({ confirmDelete = null }) { Text("Vazgec") } }
        )
    }
    if (showMediaLink && selected != null) {
        MediaLinkDialog(
            isBed = selected.kindEnum == PlaylistKind.BED,
            onDismiss = { showMediaLink = false },
            onAdd = { title, url ->
                onAddMediaLink(selected.id, title, url)
                showMediaLink = false
            }
        )
    }
}

@Composable
private fun PlaylistRow(
    pl: Playlist,
    linkCount: Int,
    onClick: () -> Unit,
    onKind: () -> Unit,
    onDelete: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(pl.color).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(pl.kindEnum.emoji, style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    pl.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    buildString {
                        append(pl.kindEnum.label)
                        if (
                            pl.kindEnum != PlaylistKind.BED &&
                            pl.kindEnum != PlaylistKind.PRAYER
                        ) append(" - agirlik %${pl.weight}")
                        if (linkCount > 0) append(" - $linkCount bagli klasor")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = kindColor(pl.kindEnum)
                )
            }
            Box {
                IconButton({ menu = true }, Modifier.size(32.dp)) {
                    Icon(Icons.Default.MoreVert, "Secenekler", Modifier.size(18.dp))
                }
                DropdownMenu(menu, { menu = false }) {
                    DropdownMenuItem(
                        text = { Text("Tur ve agirligi degistir") },
                        onClick = { menu = false; onKind() }
                    )
                    DropdownMenuItem(
                        text = { Text("Sil", color = MaterialTheme.colorScheme.error) },
                        onClick = { menu = false; onDelete() }
                    )
                }
            }
        }
    }
}

// ── Diyaloglar ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistPickerSheet(
    playlists: List<Playlist>,
    title: String,
    showSyncOption: Boolean,
    onCreateNew: () -> Unit,
    onPicked: (Long, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var keepInSync by remember { mutableStateOf(showSyncOption) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            VSpace(10)
            if (showSyncOption) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { keepInSync = !keepInSync }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(keepInSync, { keepInSync = it })
                    Column(Modifier.weight(1f)) {
                        Text("Klasoru bagli tut", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Bu klasore sonradan attiginiz dosyalar otomatik eklenir",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                VSpace(8)
            }

            if (playlists.isEmpty()) {
                Text(
                    "Once bir liste olusturun.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                VSpace(10)
            } else {
                playlists.forEach { pl ->
                    ListItem(
                        headlineContent = { Text(pl.name) },
                        supportingContent = {
                            Text(
                                pl.kindEnum.emoji + " " + pl.kindEnum.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingContent = { ColorDot(pl.color, 14) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onPicked(pl.id, keepInSync) }
                    )
                }
                VSpace(6)
            }

            OutlinedButton(
                onClick = { onDismiss(); onCreateNew() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Yeni liste olustur")
            }
        }
    }
}

@Composable
private fun MediaLinkDialog(
    isBed: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    val valid = url.trim().startsWith("https://", ignoreCase = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Link, null) },
        title = { Text(if (isBed) "Fon müziği bağlantısı" else "Medya bağlantısı") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Doğrudan çalınabilir bir HTTPS ses adresi ekleyin. YouTube veya web sayfası adresi değil; MP3, M4A, AAC, OGG ya da yayın akışı adresi olmalıdır.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Görünen ad") },
                    placeholder = { Text(if (isBed) "Huzurlu fon 1" else "Ses kaydı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("HTTPS medya adresi") },
                    placeholder = { Text("https://.../ses.mp3") },
                    singleLine = true,
                    isError = url.isNotBlank() && !valid,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isBed) {
                    Text(
                        "Bağlantı Fon Müzik listesine eklenir; seçili program veya akıllı akış bu listeyi kullanabilir.",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(title.trim(), url.trim()) }, enabled = valid) {
                Text("Listeye ekle")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Vazgeç") } }
    )
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String, PlaylistKind, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(PlaylistKind.MUSIC) }
    var color by remember { mutableIntStateOf(playlistPalette[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni liste") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    name, { name = it },
                    label = { Text("Liste adi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Tur",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PlaylistKind.entries.forEach { k ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { kind = k }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(kind == k, { kind = k })
                        Column {
                            Text(
                                k.emoji + " " + k.label,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                k.hint,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    playlistPalette.forEach { c ->
                        Box(
                            Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(c))
                                .clickable { color = c }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                { onCreate(name.trim(), kind, color) },
                enabled = name.isNotBlank()
            ) { Text("Olustur") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Vazgec") } }
    )
}

@Composable
private fun KindPickerDialog(
    playlist: Playlist,
    onDismiss: () -> Unit,
    onSave: (Playlist) -> Unit
) {
    var kind by remember { mutableStateOf(playlist.kindEnum) }
    var weight by remember { mutableIntStateOf(playlist.weight) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(playlist.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PlaylistKind.entries.forEach { k ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { kind = k },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(kind == k, { kind = k })
                        Column {
                            Text(k.emoji + " " + k.label, style = MaterialTheme.typography.bodySmall)
                            Text(
                                k.hint,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (kind != PlaylistKind.BED && kind != PlaylistKind.PRAYER) {
                    HorizontalDivider()
                    Text(
                        "Yayindaki agirligi: %$weight",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = weight.toFloat(),
                        onValueChange = { weight = it.toInt() },
                        valueRange = 0f..100f,
                        steps = 19
                    )
                    Text(
                        "0 yaparsaniz bu liste yayina hic girmez.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button({ onSave(playlist.copy(kind = kind.name, weight = weight)) }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Vazgec") } }
    )
}
