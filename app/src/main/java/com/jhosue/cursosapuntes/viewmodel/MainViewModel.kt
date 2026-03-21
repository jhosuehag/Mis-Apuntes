package com.jhosue.cursosapuntes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosue.cursosapuntes.data.model.Section
import com.jhosue.cursosapuntes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {
    private val _sections = MutableStateFlow<List<Section>>(emptyList())
    val sections: StateFlow<List<Section>> = _sections

    init {
        viewModelScope.launch {
            repository.getSections().collect { list ->
                _sections.value = list
            }
        }
    }

    fun addSection(name: String) {
        viewModelScope.launch {
            repository.addSection(name)
        }
    }

    fun updateSection(section: Section) {
        viewModelScope.launch {
            repository.updateSection(section)
        }
    }

    fun deleteSection(sectionId: String) {
        viewModelScope.launch {
            repository.deleteSection(sectionId)
        }
    }

    fun deleteSections(sectionIds: Set<String>) {
        viewModelScope.launch {
            sectionIds.forEach { repository.deleteSection(it) }
        }
    }

    fun updateSectionOrder(reorderedSections: List<Section>) {
        val updated = reorderedSections.mapIndexed { index, section ->
            section.copy(position = index)
        }
        _sections.value = updated
        viewModelScope.launch {
            repository.updateSections(updated)
        }
    }

    fun moveSection(from: Int, to: Int) {
        val currentList = _sections.value.toMutableList()
        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)
            val updated = currentList.mapIndexed { index, section ->
                section.copy(position = index)
            }
            _sections.value = updated
            viewModelScope.launch {
                repository.updateSections(updated)
            }
        }
    }
}
