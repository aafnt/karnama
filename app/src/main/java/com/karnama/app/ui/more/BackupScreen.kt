package com.karnama.app.ui.more

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karnama.app.R
import com.karnama.app.data.repository.BackupRepository
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    backupRepository: BackupRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val json = backupRepository.exportToJson()
                writeToUri(context, uri, json)
                statusMessage = "پشتیبان با موفقیت ذخیره شد."
            } catch (e: Exception) {
                statusMessage = "خطا در ذخیره‌ی پشتیبان."
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val json = readFromUri(context, uri)
                backupRepository.importFromJson(json)
                statusMessage = "بازیابی با موفقیت انجام شد."
            } catch (e: Exception) {
                statusMessage = "خطا در بازیابی. فایل معتبر نیست."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.more_backup)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "از تمام کارها، اقدامات و متن‌های آماده یک فایل پشتیبان تهیه کنید یا از یک فایل قبلی بازیابی کنید.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { exportLauncher.launch("karnama-backup.json") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تهیه پشتیبان")
            }

            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("بازیابی از فایل")
            }

            statusMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun writeToUri(context: Context, uri: Uri, content: String) {
    context.contentResolver.openOutputStream(uri)?.use { stream ->
        OutputStreamWriter(stream).use { it.write(content) }
    }
}

private fun readFromUri(context: Context, uri: Uri): String {
    context.contentResolver.openInputStream(uri)?.use { stream ->
        return BufferedReader(InputStreamReader(stream)).readText()
    }
    throw IllegalStateException("Unable to open input stream")
}
