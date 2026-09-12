package com.karnama.app.ui.quicktexts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karnama.app.R
import com.karnama.app.data.repository.QuickText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTextsScreen(
    viewModel: QuickTextViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quick_texts_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::startAdding) {
                Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.quick_text_add))
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)
        ) {
            items(uiState.items, key = { it.id }) { item ->
                QuickTextRow(
                    item = item,
                    onEdit = { viewModel.startEditing(item) },
                    onDelete = { viewModel.delete(item) }
                )
                Divider(color = MaterialTheme.colorScheme.outline)
            }
        }
    }

    uiState.editor?.let { editor ->
        QuickTextEditorDialog(
            isNew = editor.editingId == null,
            text = editor.text,
            keywords = editor.keywords,
            onTextChange = viewModel::onEditorTextChange,
            onKeywordsChange = viewModel::onEditorKeywordsChange,
            onDismiss = viewModel::closeEditor,
            onSave = viewModel::saveEditor
        )
    }
}

@Composable
private fun QuickTextRow(item: QuickText, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.text, style = MaterialTheme.typography.bodyLarge)
            if (item.keywords.isNotEmpty()) {
                Text(
                    text = item.keywords.joinToString("، "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Outlined.Edit, contentDescription = null)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun QuickTextEditorDialog(
    isNew: Boolean,
    text: String,
    keywords: String,
    onTextChange: (String) -> Unit,
    onKeywordsChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(if (isNew) R.string.quick_text_add else R.string.quick_text_edit))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = { Text(stringResource(R.string.quick_text_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = keywords,
                    onValueChange = onKeywordsChange,
                    placeholder = { Text(stringResource(R.string.quick_text_keywords_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
