package com.benimradyom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
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
import com.benimradyom.app.data.model.Playlist
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.data.model.Program
import com.benimradyom.app.data.model.Rule
import com.benimradyom.app.data.model.RuleAction
import com.benimradyom.app.data.model.RuleTrigger
import com.benimradyom.app.data.model.TimedSpot
import com.benimradyom.app.data.model.TrackOrder
import com.benimradyom.app.ui.components.VSpace

val DAY_LABELS = listOf("Pt", "Sa", "Ca", "Pe", "Cu", "Ct", "Pz")

/**
 * Bir programin butun ayarlari. Sade kalmasi icin katmanlara bolundu:
 * Zaman · Icerik · Ses · Anons · Kurallar
 */
@Composable
fun ProgramEditor(
    program: Program,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    allPrograms: List<Program>,
    onSave: (Program) -> Unit,
    onDelete: (Program) -> Unit,
    onClose: () -> Unit
) {
    var draft by remember(program.id) { mutableStateOf(program) }
    var layer by rememberSaveable { mutableIntStateOf(0) }
    var confirmDelete by remember { mutableStateOf(false) }

    // Her degisiklik aninda kaydedilir; ayri bir "kaydet" adimi yok.
    fun update(block: (Program) -> Program) {
        draft = block(draft)
        onSave(draft)
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClose, Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", Modifier.size(20.dp))
            }
            OutlinedTextField(
                value = draft.name,
                onValueChange = { v -> update { it.copy(name = v) } },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(6.dp))
            IconButton({ confirmDelete = true }, Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.DeleteOutline, "Sil", Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        ScrollableTabRow(selectedTabIndex = layer, edgePadding = 8.dp) {
            listOf("Zaman", "Icerik", "Ses", "Anons", "Kurallar").forEachIndexed { i, label ->
                Tab(layer == i, { layer = i }, text = { Text(label) })
            }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(14.dp)) {
            when (layer) {
                0 -> TimeLayer(draft) { block -> update(block) }
                1 -> ContentLayer(draft, playlists) { block -> update(block) }
                2 -> SoundLayer(draft, playlists) { block -> update(block) }
                3 -> SpotLayer(draft, playlists, announcements) { block -> update(block) }
                else -> RulesLayer(draft, playlists, announcements, allPrograms) { block -> update(block) }
            }
            VSpace(32)
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Programi sil") },
            text = { Text("\"${draft.name}\" silinecek. Listeler ve sesler etkilenmez.") },
            confirmButton = {
                TextButton({ confirmDelete = false; onDelete(draft); onClose() }) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton({ confirmDelete = false }) { Text("Vazgec") } }
        )
    }
}

// ── Katman 1: Zaman ─────────────────────────────────────────────────────────

@Composable
private fun TimeLayer(p: Program, update: ((Program) -> Program) -> Unit) {
    Card("Gunler", "Bu program haftanin hangi gunleri gecerli") {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            DAY_LABELS.forEachIndexed { i, d ->
                val on = p.days.getOrNull(i) == '1'
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (on) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            val chars = p.days.padEnd(7, '0').toCharArray()
                            chars[i] = if (on) '0' else '1'
                            update { it.copy(days = String(chars)) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        d,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (on) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    VSpace(12)
    Card("Saat araligi", "Bos birakilirsa program sadece elle baslatilir") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Baslangic", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(90.dp))
            HourMinutePicker(p.startHour, p.startMinute) { h, m ->
                update { it.copy(startHour = h, startMinute = m) }
            }
        }
        VSpace(8)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Bitis", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(90.dp))
            HourMinutePicker(p.endHour, p.endMinute) { h, m ->
                update { it.copy(endHour = h, endMinute = m) }
            }
        }
        VSpace(10)
        SwitchRow(
            "Saati gelince kendiliginden basla",
            "Kapaliysa program sadece siz baslattiginizda calisir",
            p.autoStart
        ) { v -> update { it.copy(autoStart = v) } }
    }

    VSpace(12)
    Card("Sessiz saatler", "Bu aralikta radyo tamamen susar") {
        SwitchRow("Sessiz saatler acik", null, p.quietStartHour >= 0) { on ->
            update {
                if (on) it.copy(quietStartHour = 0, quietEndHour = 6)
                else it.copy(quietStartHour = -1, quietEndHour = -1)
            }
        }
        if (p.quietStartHour >= 0) {
            VSpace(8)
            Row(verticalAlignment = Alignment.CenterVertically) {
                HourPicker(p.quietStartHour) { h -> update { it.copy(quietStartHour = h) } }
                Text("  -  ", style = MaterialTheme.typography.bodySmall)
                HourPicker(p.quietEndHour) { h -> update { it.copy(quietEndHour = h) } }
            }
        }
    }

    VSpace(12)
    SwitchRow("Program etkin", "Kapatirsaniz bu program hic kullanilmaz", p.isEnabled) { v ->
        update { it.copy(isEnabled = v) }
    }
}

// ── Katman 2: Icerik ────────────────────────────────────────────────────────

@Composable
private fun ContentLayer(
    p: Program,
    playlists: List<Playlist>,
    update: ((Program) -> Program) -> Unit
) {
    val mixable = playlists.filter {
        it.kindEnum != PlaylistKind.BED && it.kindEnum != PlaylistKind.PRAYER
    }

    Card("Karisim", "Her listenin bu programda ne siklikta cikacagi") {
        if (mixable.isEmpty()) {
            Text("Once liste olusturun.", style = MaterialTheme.typography.bodySmall)
        }
        mixable.forEach { pl ->
            val current = p.mix[pl.id] ?: pl.weight
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pl.kindEnum.emoji + " " + pl.name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "%$current",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = current.toFloat(),
                onValueChange = { v ->
                    update { prog ->
                        prog.copy(mixJson = Program.mixToJson(prog.mix + (pl.id to v.toInt())))
                    }
                },
                valueRange = 0f..100f,
                steps = 19
            )
        }
        Text(
            "0 yaptiginiz liste bu programda hic calmaz.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    VSpace(12)
    Card("Calma sirasi", "Ders ve Kur'an listelerinde sira her zaman korunur") {
        TrackOrder.entries.forEach { order ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { update { it.copy(trackOrder = order.name) } },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(p.orderEnum == order, { update { it.copy(trackOrder = order.name) } })
                Column {
                    Text(order.label, style = MaterialTheme.typography.bodySmall)
                    Text(
                        order.hint,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Katman 3: Ses ───────────────────────────────────────────────────────────

@Composable
private fun SoundLayer(
    p: Program,
    playlists: List<Playlist>,
    update: ((Program) -> Program) -> Unit
) {
    val bedLists = playlists.filter { it.kindEnum == PlaylistKind.BED }
    val others = playlists.filter {
        it.kindEnum != PlaylistKind.BED && it.kindEnum != PlaylistKind.PRAYER
    }

    Card("Ana ses", "Bu programin genel ses seviyesi") {
        Text(
            "%${(p.masterVolume * 100).toInt()}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = p.masterVolume,
            onValueChange = { v -> update { it.copy(masterVolume = v) } },
            valueRange = 0.1f..1f
        )
    }

    VSpace(12)
    Card("Liste basina ses", "Kimi kayitlar digerlerinden kisik geliyorsa burada dengeleyin") {
        others.forEach { pl ->
            val v = p.volumes[pl.id] ?: 1f
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pl.name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "%${(v * 100).toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = v,
                onValueChange = { nv ->
                    update { prog ->
                        prog.copy(volumesJson = Program.volumesToJson(prog.volumes + (pl.id to nv)))
                    }
                },
                valueRange = 0.1f..1.5f
            )
        }
    }

    VSpace(12)
    Card("Fon muzik", "Ders / Kur'an okunurken altinda kisik enstrumantal calar") {
        SwitchRow("Konusma altinda fon", null, p.bedEnabled) { v ->
            update { it.copy(bedEnabled = v) }
        }
        if (p.bedEnabled) {
            VSpace(8)
            PlaylistDropdown("Fon listesi", bedLists, p.bedPlaylistId, allowNone = true) { id ->
                update { it.copy(bedPlaylistId = id) }
            }
            Text(
                "Fon ekleme: Kütüphane → Listelerim → Fon Müzik. Cihaz dosyası veya doğrudan HTTPS ses bağlantısı eklenebilir.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            VSpace(8)
            Text(
                "Fon sesi: %${(p.bedVolume * 100).toInt()}",
                style = MaterialTheme.typography.labelSmall
            )
            Slider(
                value = p.bedVolume,
                onValueChange = { v -> update { it.copy(bedVolume = v) } },
                valueRange = 0.02f..0.6f
            )
        }
    }

    VSpace(12)
    Card("Gecisler", null) {
        Text("Gecis suresi: ${p.fadeMs} ms", style = MaterialTheme.typography.labelSmall)
        Slider(
            value = p.fadeMs.toFloat(),
            onValueChange = { v -> update { it.copy(fadeMs = v.toInt()) } },
            valueRange = 0f..5000f
        )
        SwitchRow(
            "Capraz gecis",
            "Acikken parcalar ust uste binerek gecer, kapaliyken kisilip acilir",
            p.crossfade
        ) { v -> update { it.copy(crossfade = v) } }
        SwitchRow(
            "Ses dengeleme",
            "Kisik kayitlari yukseltir; seviye farkini azaltir",
            p.normalizeVolume
        ) { v -> update { it.copy(normalizeVolume = v) } }
    }
}

// ── Katman 4: Anons ─────────────────────────────────────────────────────────

@Composable
private fun SpotLayer(
    p: Program,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    update: ((Program) -> Program) -> Unit
) {
    var addingSpot by remember { mutableStateOf(false) }

    Card("Araya anons", "Kac parcada bir anons girsin") {
        Text(
            if (p.spotEveryN <= 0) "Kapali" else "${p.spotEveryN} parcada bir",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = p.spotEveryN.toFloat(),
            onValueChange = { v -> update { it.copy(spotEveryN = v.toInt()) } },
            valueRange = 0f..10f,
            steps = 9
        )
    }

    VSpace(12)
    Card("Saat basi", null) {
        SwitchRow("Saat anonsu", "\"Saat 14\" diye seslendirir", p.announceClock) { v ->
            update { it.copy(announceClock = v) }
        }
        VSpace(8)
        PlaylistDropdown(
            "Saat basi calacak liste",
            playlists.filter { it.kindEnum == PlaylistKind.SPEECH },
            p.hourlyPlaylistId,
            allowNone = true
        ) { id -> update { it.copy(hourlyPlaylistId = id) } }
    }

    VSpace(12)
    Card("Sabit saatli anonslar", "Belirli saatte calacak anonslar") {
        val spots = p.timedSpots
        if (spots.isEmpty()) {
            Text(
                "Henuz yok.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        spots.forEachIndexed { index, spot ->
            val ann = announcements.firstOrNull { it.id == spot.announcementId }
            Row(
                Modifier.fillMaxWidth().padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "%02d:%02d".format(spot.hour, spot.minute),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    ann?.label?.ifBlank { ann.text.take(24) } ?: "(silinmis anons)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    {
                        update { prog ->
                            prog.copy(
                                timedSpotsJson = Program.timedSpotsToJson(
                                    prog.timedSpots.filterIndexed { i, _ -> i != index }
                                )
                            )
                        }
                    },
                    Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline, "Sil", Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        VSpace(6)
        OutlinedButton(
            onClick = { addingSpot = true },
            enabled = announcements.isNotEmpty(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Add, null, Modifier.size(15.dp))
            Spacer(Modifier.width(4.dp))
            Text("Saat ekle", style = MaterialTheme.typography.labelSmall)
        }
        if (announcements.isEmpty()) {
            Text(
                "Once Anonslar sekmesinden anons olusturun.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (addingSpot) {
        TimedSpotDialog(
            announcements = announcements,
            onDismiss = { addingSpot = false },
            onAdd = { spot ->
                update { prog ->
                    prog.copy(
                        timedSpotsJson = Program.timedSpotsToJson(prog.timedSpots + spot)
                    )
                }
                addingSpot = false
            }
        )
    }
}

@Composable
private fun TimedSpotDialog(
    announcements: List<Announcement>,
    onDismiss: () -> Unit,
    onAdd: (TimedSpot) -> Unit
) {
    var hour by remember { mutableIntStateOf(8) }
    var minute by remember { mutableIntStateOf(0) }
    var annId by remember { mutableLongStateOf(announcements.firstOrNull()?.id ?: -1L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sabit saatli anons") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Saat", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(60.dp))
                    HourPicker(hour) { hour = it }
                    Spacer(Modifier.width(6.dp))
                    MinutePicker(minute) { minute = it }
                }
                Text("Anons", style = MaterialTheme.typography.labelMedium)
                Column(Modifier.heightIn(max = 220.dp).verticalScroll(rememberScrollState())) {
                    announcements.forEach { a ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { annId = a.id },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(annId == a.id, { annId = a.id })
                            Text(
                                a.label.ifBlank { a.text.take(30).ifBlank { "Anons" } },
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button({ onAdd(TimedSpot(hour, minute, annId)) }, enabled = annId > 0) { Text("Ekle") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Vazgec") } }
    )
}

// ── Katman 5: Kurallar ──────────────────────────────────────────────────────

@Composable
private fun RulesLayer(
    p: Program,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    allPrograms: List<Program>,
    update: ((Program) -> Program) -> Unit
) {
    var adding by remember { mutableStateOf(false) }
    val rules = p.rules

    Card("Sartli gorevler", "\"Su olursa sunu yap\" seklinde kurallar") {
        if (rules.isEmpty()) {
            Text(
                "Henuz kural yok.\nOrnek: Ders listesinden bir bolum calinca arkasindan motivasyon anonsu cal.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        rules.forEachIndexed { index, rule ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            ) {
                Row(
                    Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "EGER  " + describeTrigger(rule, playlists),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "ISE  " + describeAction(rule, playlists, announcements, allPrograms),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = rule.isEnabled,
                        onCheckedChange = { on ->
                            update { prog ->
                                prog.copy(
                                    rulesJson = Program.rulesToJson(
                                        prog.rules.mapIndexed { i, r ->
                                            if (i == index) r.copy(isEnabled = on) else r
                                        }
                                    )
                                )
                            }
                        }
                    )
                    IconButton(
                        {
                            update { prog ->
                                prog.copy(
                                    rulesJson = Program.rulesToJson(
                                        prog.rules.filterIndexed { i, _ -> i != index }
                                    )
                                )
                            }
                        },
                        Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline, "Sil", Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        VSpace(8)
        Button(onClick = { adding = true }, shape = RoundedCornerShape(10.dp)) {
            Icon(Icons.Default.Add, null, Modifier.size(16.dp))
            Spacer(Modifier.width(5.dp))
            Text("Kural ekle")
        }
    }

    if (adding) {
        RuleDialog(
            playlists = playlists,
            announcements = announcements,
            allPrograms = allPrograms.filter { it.id != p.id },
            onDismiss = { adding = false },
            onAdd = { rule ->
                update { prog -> prog.copy(rulesJson = Program.rulesToJson(prog.rules + rule)) }
                adding = false
            }
        )
    }
}

private fun describeTrigger(rule: Rule, playlists: List<Playlist>): String =
    when (rule.triggerEnum) {
        RuleTrigger.LISTE_CALINCA -> {
            val name = playlists.firstOrNull { it.id.toString() == rule.triggerValue }?.name ?: "?"
            "\"$name\" listesinden bir parca calinca"
        }
        RuleTrigger.HER_N_PARCA -> "her ${rule.triggerValue} parcada bir"
        RuleTrigger.SAAT_GELINCE -> "saat ${rule.triggerValue} olunca"
        else -> rule.triggerEnum.label.lowercase()
    }

private fun describeAction(
    rule: Rule,
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    programs: List<Program>
): String = when (rule.actionEnum) {
    RuleAction.LISTE_CAL -> {
        val name = playlists.firstOrNull { it.id.toString() == rule.actionValue }?.name ?: "?"
        "\"$name\" listesinden bir parca cal"
    }
    RuleAction.ANONS_CAL -> {
        val a = announcements.firstOrNull { it.id.toString() == rule.actionValue }
        "\"${a?.label?.ifBlank { a.text.take(20) } ?: "?"}\" anonsunu cal"
    }
    RuleAction.SES_AYARLA -> "sesi %${rule.actionValue} yap"
    RuleAction.FON_AC -> "fon muzigi ac"
    RuleAction.FON_KAPAT -> "fon muzigi kapat"
    RuleAction.DURAKLAT -> "${rule.actionValue} dakika duraklat"
    RuleAction.PROGRAM_DEGISTIR -> {
        val name = programs.firstOrNull { it.id.toString() == rule.actionValue }?.name ?: "?"
        "\"$name\" programina gec"
    }
}

@Composable
private fun RuleDialog(
    playlists: List<Playlist>,
    announcements: List<Announcement>,
    allPrograms: List<Program>,
    onDismiss: () -> Unit,
    onAdd: (Rule) -> Unit
) {
    var trigger by remember { mutableStateOf(RuleTrigger.LISTE_CALINCA) }
    var triggerValue by remember { mutableStateOf("") }
    var action by remember { mutableStateOf(RuleAction.ANONS_CAL) }
    var actionValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni kural") },
        text = {
            Column(
                Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("EGER", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                RuleTrigger.entries.filter { it != RuleTrigger.PROGRAM_BITINCE }.forEach { t ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .clickable { trigger = t; triggerValue = "" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(trigger == t, { trigger = t; triggerValue = "" })
                        Text(t.label, style = MaterialTheme.typography.bodySmall)
                    }
                }
                when (trigger) {
                    RuleTrigger.LISTE_CALINCA -> SimplePicker(
                        playlists.map { it.id.toString() to it.name }, triggerValue
                    ) { triggerValue = it }
                    RuleTrigger.HER_N_PARCA -> OutlinedTextField(
                        triggerValue, { triggerValue = it.filter { c -> c.isDigit() } },
                        label = { Text("Kac parcada bir") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    RuleTrigger.SAAT_GELINCE -> OutlinedTextField(
                        triggerValue, { triggerValue = it },
                        label = { Text("Saat (ornek 08:30)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    else -> Unit
                }

                HorizontalDivider()
                Text("ISE", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                RuleAction.entries.forEach { a ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .clickable { action = a; actionValue = "" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(action == a, { action = a; actionValue = "" })
                        Text(a.label, style = MaterialTheme.typography.bodySmall)
                    }
                }
                when (action) {
                    RuleAction.LISTE_CAL -> SimplePicker(
                        playlists.map { it.id.toString() to it.name }, actionValue
                    ) { actionValue = it }
                    RuleAction.ANONS_CAL -> SimplePicker(
                        announcements.map {
                            it.id.toString() to it.label.ifBlank { it.text.take(24) }
                        },
                        actionValue
                    ) { actionValue = it }
                    RuleAction.PROGRAM_DEGISTIR -> SimplePicker(
                        allPrograms.map { it.id.toString() to it.name }, actionValue
                    ) { actionValue = it }
                    RuleAction.SES_AYARLA -> OutlinedTextField(
                        actionValue, { actionValue = it.filter { c -> c.isDigit() } },
                        label = { Text("Ses yuzdesi (0-100)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    RuleAction.DURAKLAT -> OutlinedTextField(
                        actionValue, { actionValue = it.filter { c -> c.isDigit() } },
                        label = { Text("Dakika") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    else -> Unit
                }
            }
        },
        confirmButton = {
            Button(
                {
                    onAdd(
                        Rule(
                            trigger = trigger.name, triggerValue = triggerValue,
                            action = action.name, actionValue = actionValue
                        )
                    )
                },
                enabled = ruleIsComplete(trigger, triggerValue, action, actionValue)
            ) { Text("Ekle") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Vazgec") } }
    )
}

private fun ruleIsComplete(
    trigger: RuleTrigger,
    triggerValue: String,
    action: RuleAction,
    actionValue: String
): Boolean {
    val triggerOk = when (trigger) {
        RuleTrigger.LISTE_CALINCA, RuleTrigger.HER_N_PARCA, RuleTrigger.SAAT_GELINCE ->
            triggerValue.isNotBlank()
        else -> true
    }
    val actionOk = when (action) {
        RuleAction.FON_AC, RuleAction.FON_KAPAT -> true
        else -> actionValue.isNotBlank()
    }
    return triggerOk && actionOk
}

@Composable
private fun SimplePicker(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(Modifier.heightIn(max = 160.dp).verticalScroll(rememberScrollState())) {
        options.forEach { (value, label) ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onSelect(value) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected == value, { onSelect(value) })
                Text(
                    label, style = MaterialTheme.typography.bodySmall,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── Ortak kucuk parcalar ────────────────────────────────────────────────────

@Composable
private fun Card(title: String, hint: String?, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            if (hint != null) {
                Text(
                    hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            VSpace(8)
            content()
        }
    }
}

@Composable
fun SwitchRow(title: String, hint: String?, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            if (hint != null) {
                Text(
                    hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
fun HourPicker(hour: Int, onPick: (Int) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { open = true },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(if (hour < 0) "--" else "%02d".format(hour))
        }
        DropdownMenu(open, { open = false }) {
            (0..23).forEach { h ->
                DropdownMenuItem(text = { Text("%02d".format(h)) }, onClick = { onPick(h); open = false })
            }
        }
    }
}

@Composable
fun MinutePicker(minute: Int, onPick: (Int) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { open = true },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("%02d".format(minute))
        }
        DropdownMenu(open, { open = false }) {
            (0..55 step 5).forEach { m ->
                DropdownMenuItem(text = { Text("%02d".format(m)) }, onClick = { onPick(m); open = false })
            }
        }
    }
}

@Composable
private fun HourMinutePicker(hour: Int, minute: Int, onPick: (Int, Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HourPicker(hour) { onPick(it, minute) }
        Text(" : ", style = MaterialTheme.typography.bodySmall)
        MinutePicker(minute) { onPick(hour, it) }
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = { onPick(-1, 0) }) {
            Text("Temizle", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDropdown(
    label: String,
    options: List<Playlist>,
    selectedId: Long,
    allowNone: Boolean = false,
    onSelect: (Long) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val selected = options.firstOrNull { it.id == selectedId }
    ExposedDropdownMenuBox(open, { open = !open }) {
        OutlinedTextField(
            value = selected?.name ?: if (allowNone) "Kapali" else "Secilmedi",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(open) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(open, { open = false }) {
            if (allowNone) {
                DropdownMenuItem(text = { Text("Kapali") }, onClick = { onSelect(-1L); open = false })
            }
            options.forEach { pl ->
                DropdownMenuItem(text = { Text(pl.name) }, onClick = { onSelect(pl.id); open = false })
            }
        }
    }
}
