package com.karnama.app.ui.quicktexts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karnama.app.data.repository.QuickText
import com.karnama.app.data.repository.QuickTextRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuickTextEditorState(
    val editingId: Long? = null,
    val text: String = "",
    val keywords: String = ""
)

data class QuickTextsUiState(
    val items: List<QuickText> = emptyList(),
    val editor: QuickTextEditorState? = null
)

class QuickTextViewModel(
    private val repository: QuickTextRepository
) : ViewModel() {

    private val editorState = MutableStateFlow<QuickTextEditorState?>(null)

    val uiState: StateFlow<QuickTextsUiState> = combine(
        repository.observeAll(), editorState
    ) { items, editor ->
        QuickTextsUiState(items = items, editor = editor)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuickTextsUiState())

    fun startAdding() {
        editorState.value = QuickTextEditorState()
    }

    fun startEditing(quickText: QuickText) {
        editorState.value = QuickTextEditorState(
            editingId = quickText.id,
            text = quickText.text,
            keywords = quickText.keywords.joinToString(", ")
        )
    }

    fun closeEditor() {
        editorState.value = null
    }

    fun onEditorTextChange(text: String) {
        editorState.value = editorState.value?.copy(text = text)
    }

    fun onEditorKeywordsChange(keywords: String) {
        editorState.value = editorState.value?.copy(keywords = keywords)
    }

    fun saveEditor() {
        val state = editorState.value ?: return
        if (state.text.isBlank()) return
        val keywordList = state.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        viewModelScope.launch {
            if (state.editingId == null) {
                repository.add(state.text, keywordList)
            } else {
                repository.update(state.editingId, state.text, keywordList)
            }
            editorState.value = null
        }
    }

    fun delete(quickText: QuickText) {
        viewModelScope.launch { repository.delete(quickText) }
    }
}
