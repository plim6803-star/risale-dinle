package com.benimradyom.app.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.benimradyom.app.data.model.PlaylistKind
import com.benimradyom.app.ui.theme.BedGrey
import com.benimradyom.app.ui.theme.SpeechBlue
import com.benimradyom.app.ui.theme.SpotAmber
import com.benimradyom.app.ui.theme.Teal700

/** 214000 -> "3:34" */
fun formatMs(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val total = ms / 1000
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

/** Toplam sureyi "2 sa 14 dk" gibi yazar. */
fun formatDurationLong(ms: Long): String {
    if (ms <= 0L) return "0 dk"
    val minutes = ms / 60000
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h > 0 && m > 0 -> "$h sa $m dk"
        h > 0 -> "$h sa"
        else -> "$m dk"
    }
}

fun kindColor(kind: PlaylistKind): Color = when (kind) {
    PlaylistKind.MUSIC -> Teal700
    PlaylistKind.SPEECH -> SpeechBlue
    PlaylistKind.SPOT -> SpotAmber
    PlaylistKind.PRAYER -> Teal700
    PlaylistKind.BED -> BedGrey
}

/** Liste turunu gosteren kucuk rozet. */
@Composable
fun KindBadge(kind: PlaylistKind, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = kindColor(kind).copy(alpha = 0.14f)
    ) {
        Text(
            text = kind.emoji + " " + kind.label,
            style = MaterialTheme.typography.labelSmall,
            color = kindColor(kind),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun ColorDot(color: Int, size: Int = 10, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(color))
    )
}

@Composable
fun SectionHeader(title: String, trailing: String? = null) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(
                trailing,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Bos ekranlarda gosterilen aciklama. */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Box(modifier.fillMaxSize(), Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(28.dp)
        ) {
            Icon(
                icon, null,
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (action != null) {
                Spacer(Modifier.height(16.dp))
                action()
            }
        }
    }
}

@Composable
fun VSpace(dp: Int) = Spacer(Modifier.height(dp.dp))

@Composable
fun HSpace(dp: Int) = Spacer(Modifier.width(dp.dp))
