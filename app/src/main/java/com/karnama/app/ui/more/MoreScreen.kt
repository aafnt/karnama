package com.karnama.app.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karnama.app.R

@Composable
fun MoreScreen(
    onQuickTexts: () -> Unit,
    onDoneTasks: () -> Unit,
    onSettings: () -> Unit,
    onBackup: () -> Unit,
    onAbout: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.nav_more),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
            )

            MoreItem(Icons.Outlined.Description, stringResource(R.string.more_quick_texts), onQuickTexts)
            Divider(color = MaterialTheme.colorScheme.outline)
            MoreItem(Icons.Outlined.CheckCircleOutline, stringResource(R.string.more_done_tasks), onDoneTasks)
            Divider(color = MaterialTheme.colorScheme.outline)
            MoreItem(Icons.Outlined.Settings, stringResource(R.string.more_settings), onSettings)
            Divider(color = MaterialTheme.colorScheme.outline)
            MoreItem(Icons.Outlined.Save, stringResource(R.string.more_backup), onBackup)
            Divider(color = MaterialTheme.colorScheme.outline)
            MoreItem(Icons.Outlined.Info, stringResource(R.string.more_about), onAbout)
            Divider(color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun MoreItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        )
        Icon(
            Icons.AutoMirrored.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
