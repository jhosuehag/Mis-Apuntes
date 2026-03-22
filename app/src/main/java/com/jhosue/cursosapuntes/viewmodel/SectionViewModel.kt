package com.jhosue.cursosapuntes.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosue.cursosapuntes.data.model.Note
import com.jhosue.cursosapuntes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SectionViewModel @Inject constructor(
    private val repository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sectionId: String = savedStateHandle.get<String>("sectionId") ?: ""
    private val sectionName: String = savedStateHandle.get<String>("sectionName") ?: "Section"

    val title: StateFlow<String> = MutableStateFlow(sectionName)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    
    val notes: StateFlow<List<Note>> = combine(_allNotes, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true) 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getNotes(sectionId).collect { list ->
                _allNotes.value = list
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(function: String, usedFor: String, example: String) {
        viewModelScope.launch {
            repository.addNote(sectionId, function, usedFor, example, _allNotes.value.size)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId, sectionId)
        }
    }

    fun deleteNotes(noteIds: Set<String>) {
        viewModelScope.launch {
            noteIds.forEach { repository.deleteNote(it, sectionId) }
        }
    }

    fun updateNoteOrder(reorderedNotes: List<Note>) {
        val updated = reorderedNotes.mapIndexed { index, note ->
            note.copy(position = index)
        }
        _allNotes.value = updated
        viewModelScope.launch {
            repository.updateNotes(updated)
        }
    }

    fun moveNote(from: Int, to: Int) {
        val currentList = _allNotes.value.toMutableList()
        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)
            val updated = currentList.mapIndexed { index, note ->
                note.copy(position = index)
            }
            _allNotes.value = updated
            viewModelScope.launch {
                repository.updateNotes(updated)
            }
        }
    }
}
