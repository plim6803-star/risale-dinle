package com.benimradyom.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.benimradyom.app.data.media.MediaStoreScanner
import com.benimradyom.app.ui.screens.BroadcastScreen
import com.benimradyom.app.ui.screens.LibraryScreen
import com.benimradyom.app.ui.screens.RadioScreen
import com.benimradyom.app.ui.theme.BenimRadyomTheme
import com.benimradyom.app.viewmodel.RadioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { BenimRadyomTheme { AppRoot() } }
    }
}

private enum class Tab(val label: String) {
    RADIO("Radyo"), LIBRARY("Kutuphane"), BROADCAST("Yayin")
}

@Composable
private fun AppRoot(vm: RadioViewModel = viewModel()) {
    val ctx = LocalContext.current
    var tab by rememberSaveable { mutableIntStateOf(0) }

    val radioState by vm.radioState.collectAsState()
    val library by vm.library.collectAsState()
    val playlists by vm.playlists.collectAsState()
    val announcements by vm.announcements.collectAsState()
    val programs by vm.programs.collectAsState()
    val smartFlows by vm.smartFlows.collectAsState()
    val knowledgeItems by vm.knowledgeItems.collectAsState()
    val allTracks by vm.allTracks.collectAsState()
    val aiSettings by vm.aiSettings.collectAsState()
    val apiKeyConfigured by vm.apiKeyConfigured.collectAsState()
    val smartGeneration by vm.smartGeneration.collectAsState()
    val transcriptSegments by vm.transcriptSegments.collectAsState()
    val contemplationImages by vm.contemplationImages.collectAsState()
    val bedMuteCues by vm.bedMuteCues.collectAsState()
    val mediaAi by vm.mediaAi.collectAsState()
    val linkedFolders by vm.linkedFolders.collectAsState()
    val prayer by vm.prayerSettings.collectAsState()
    val hasTracks by vm.hasTracks.collectAsState()
    val selectedPlaylist by vm.selectedPlaylist.collectAsState()
    val selectedTracks by vm.selectedTracks.collectAsState()
    val isRecording by vm.recording.collectAsState()
    val toast by vm.toast.collectAsState()

    val snackbar = remember { SnackbarHostState() }

    // ── Izinler ─────────────────────────────────────────────────────────────
    val audioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> vm.onPermissionResult(granted) }

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) vm.startRecording() }

    // ── Anonsa ses dosyasi secme ────────────────────────────────────────────
    var audioPickCallback by remember { mutableStateOf<((String, Long) -> Unit)?>(null) }
    val audioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val cb = audioPickCallback
        audioPickCallback = null
        if (uri != null && cb != null) {
            // Secilen dosyanin izni kalici olsun; yoksa yeniden baslatinca calmaz.
            runCatching {
                ctx.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            cb(uri.toString(), durationOf(ctx, uri))
        }
    }

    // ── Calan sese zaman kodlu metin baglama ───────────────────────────────
    var transcriptTrackId by remember { mutableStateOf<Long?>(null) }
    val transcriptPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val trackId = transcriptTrackId
        transcriptTrackId = null
        if (uri != null && trackId != null) {
            vm.importTranscript(trackId, uri)
        }
    }

    // ── Yedekleme dosyalari ─────────────────────────────────────────────────
    val backupSaver = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let(vm::exportBackup)
    }

    val backupLoader = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let(vm::importBackup)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (!MediaStoreScanner.hasPermission(ctx)) {
            audioPermission.launch(MediaStoreScanner.audioPermission)
        }
    }

    LaunchedEffect(toast) {
        toast?.let {
            snackbar.showSnackbar(it.text)
            vm.consumeToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { index, t ->
                    val selected = tab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { tab = index },
                        icon = {
                            Icon(
                                when (t) {
                                    Tab.RADIO ->
                                        if (selected) Icons.Filled.Radio else Icons.Outlined.Radio
                                    Tab.LIBRARY ->
                                        if (selected) Icons.Filled.LibraryMusic
                                        else Icons.Outlined.LibraryMusic
                                    Tab.BROADCAST ->
                                        if (selected) Icons.Filled.Tune else Icons.Outlined.Tune
                                },
                                t.label
                            )
                        },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (Tab.entries[tab]) {
            Tab.RADIO -> RadioScreen(
                state = radioState,
                programs = programs,
                hasContent = hasTracks || knowledgeItems.any { it.isReady },
                transcriptSegments = transcriptSegments,
                contemplationImages = contemplationImages,
                bedMuteCues = bedMuteCues,
                apiKeyConfigured = apiKeyConfigured,
                mediaAi = mediaAi,
                onPlayPause = vm::togglePlay,
                onNext = vm::next,
                onPrev = vm::previous,
                onStop = vm::stopRadio,
                onSeek = vm::seek,
                onSelectProgram = vm::activateProgram,
                onGoToLibrary = { tab = 1 },
                onSleepTimer = vm::setSleepTimer,
                onImportTranscript = { track ->
                    transcriptTrackId = track.id
                    transcriptPicker.launch(
                        arrayOf("application/x-subrip", "text/vtt", "text/plain", "application/octet-stream")
                    )
                },
                onTranscribeTrack = vm::transcribeTrack,
                onGenerateContemplationImages = vm::generateContemplationImages,
                onAnalyzeQuranCues = vm::analyzeQuranCues,
                onSaveBedMuteCues = vm::saveBedMuteCues,
                onDeleteBedMuteCues = vm::deleteBedMuteCues,
                onDeleteTranscript = vm::deleteTranscript,
                onDeleteContemplationImages = vm::deleteContemplationImages,
                modifier = Modifier.padding(padding)
            )

            Tab.LIBRARY -> LibraryScreen(
                library = library,
                playlists = playlists,
                linkedFolders = linkedFolders,
                selectedPlaylist = selectedPlaylist,
                selectedTracks = selectedTracks,
                onRequestPermission = { audioPermission.launch(MediaStoreScanner.audioPermission) },
                onRefresh = { vm.refreshLibrary(); vm.syncLinkedFolders() },
                onQuery = vm::setQuery,
                onOpenFolder = vm::openFolder,
                onToggleSelect = vm::toggleSelection,
                onSelectAll = vm::selectAllVisible,
                onClearSelection = vm::clearSelection,
                onAddSelectedTo = vm::addSelectedTo,
                onAddFolderTo = vm::addFolderTo,
                onAddMediaLink = vm::addMediaLink,
                onUnlinkFolder = vm::unlinkFolder,
                onCreatePlaylist = { name, kind, color -> vm.createPlaylist(name, kind, color) },
                onSelectPlaylist = vm::selectPlaylist,
                onUpdatePlaylist = vm::updatePlaylist,
                onDeletePlaylist = vm::deletePlaylist,
                onDeleteTrack = vm::deleteTrack,
                onPlayTrack = vm::playTrack,
                onSortPlaylist = vm::sortPlaylist,
                onShiftTrack = vm::shiftTrack,
                modifier = Modifier.padding(padding)
            )

            Tab.BROADCAST -> BroadcastScreen(
                programs = programs,
                smartFlows = smartFlows,
                knowledgeItems = knowledgeItems,
                allTracks = allTracks,
                playlists = playlists,
                announcements = announcements,
                prayer = prayer,
                aiSettings = aiSettings,
                apiKeyConfigured = apiKeyConfigured,
                smartGeneration = smartGeneration,
                isRecording = isRecording,
                onSaveProgram = vm::saveProgram,
                onActivateProgram = vm::activateProgram,
                onDeleteProgram = vm::deleteProgram,
                onCreateProgram = vm::createProgram,
                onGenerateSmartFlow = vm::generateSmartFlow,
                onSaveSmartFlow = vm::saveSmartFlow,
                onActivateSmartFlow = vm::activateSmartFlow,
                onDeleteSmartFlow = vm::deleteSmartFlow,
                onSaveKnowledge = vm::saveKnowledge,
                onDeleteKnowledge = vm::deleteKnowledge,
                onPreviewKnowledge = vm::previewKnowledge,
                onPlayTrack = vm::playTrack,
                onSaveAnnouncement = vm::saveAnnouncement,
                onToggleAnnouncement = vm::toggleAnnouncement,
                onDeleteAnnouncement = vm::deleteAnnouncement,
                onPreviewText = vm::previewText,
                onStopPreview = vm::stopPreview,
                onPickAudioForAnnouncement = { cb ->
                    audioPickCallback = cb
                    audioPicker.launch(arrayOf("audio/*"))
                },
                onStartRecording = { micPermission.launch(Manifest.permission.RECORD_AUDIO) },
                onStopRecording = { cb ->
                    vm.stopRecording { uri, dur -> cb(uri?.toString(), dur) }
                },
                onSavePrayer = vm::savePrayerSettings,
                onSaveAiConfiguration = vm::saveAiConfiguration,
                onClearApiKey = vm::clearApiKey,
                onAutoMapKnowledge = vm::autoMapKnowledge,
                onOpenUrl = { url ->
                    runCatching {
                        ctx.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }.onFailure { vm.showToast("Bağlantı açılamadı") }
                },
                onExportBackup = {
                    backupSaver.launch(defaultBackupName())
                },
                onImportBackup = {
                    backupLoader.launch(arrayOf("application/json", "text/plain", "*/*"))
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

private fun defaultBackupName(): String {
    val stamp = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    return "benim-radyom-yedek-$stamp.json"
}

/** Secilen ses dosyasinin suresini MediaStore'dan okur. */
private fun durationOf(ctx: Context, uri: Uri): Long = runCatching {
    ctx.contentResolver.query(
        uri, arrayOf(MediaStore.Audio.Media.DURATION), null, null, null
    )?.use { c ->
        if (c.moveToFirst()) c.getLong(0) else 0L
    } ?: 0L
}.getOrDefault(0L)
