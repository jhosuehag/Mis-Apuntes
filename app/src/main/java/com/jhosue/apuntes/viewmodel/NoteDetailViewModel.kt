package com.jhosue.apuntes.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosue.apuntes.data.model.Note
import com.jhosue.apuntes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: String = savedStateHandle.get<String>("noteId") ?: ""
    private val sectionName: String = savedStateHandle.get<String>("sectionName") ?: "Section"
    
    val sectionTitle: String = sectionName

    val note: StateFlow<Note?> = repository.getNote(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
