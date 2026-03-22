package com.jhosue.apuntes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhosue.apuntes.data.model.Section
import com.jhosue.apuntes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    val sections: StateFlow<List<Section>> = repository.getSections()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSection(name: String) {
        viewModelScope.launch {
            repository.addSection(name, sections.value.size)
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
        viewModelScope.launch {
            val updated = reorderedSections.mapIndexed { index, section ->
                section.copy(position = index)
            }
            repository.updateSections(updated)
        }
    }

    fun moveSection(from: Int, to: Int) {
        viewModelScope.launch {
            val currentList = sections.value.toMutableList()
            if (from in currentList.indices && to in currentList.indices) {
                val item = currentList.removeAt(from)
                currentList.add(to, item)
                val updated = currentList.mapIndexed { index, section ->
                    section.copy(position = index)
                }
                repository.updateSections(updated)
            }
        }
    }
}
