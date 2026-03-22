package com.jhosue.apuntes.data.repository

import com.jhosue.apuntes.data.model.Note
import com.jhosue.apuntes.data.model.Section
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface NotesRepository {
    fun getSections(): Flow<List<Section>>
    fun getNotes(sectionId: String): Flow<List<Note>>
    fun getNote(noteId: String): Flow<Note?>
    suspend fun addSection(name: String, position: Int)
    suspend fun addNote(sectionId: String, function: String, usedFor: String, example: String, position: Int)
    suspend fun updateSection(section: Section)
    suspend fun deleteSection(sectionId: String)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String, sectionId: String)
    suspend fun getAllSectionsSync(): List<Section>
    suspend fun getAllNotesSync(): List<Note>
    suspend fun insertBackup(sections: List<Section>, notes: List<Note>)
    suspend fun updateSections(sections: List<Section>)
    suspend fun updateNotes(notes: List<Note>)
}

@Singleton
class MockNotesRepository @Inject constructor() : NotesRepository {
    private val _sections = MutableStateFlow<List<Section>>(listOf(
        Section("1", "Data Structures", 5, 0),
        Section("2", "Web Development", 3, 1),
        Section("3", "Computer Networks", 2, 2)
    ))

    private val _notes = MutableStateFlow<List<Note>>(listOf(
        Note("1", "1", "Arrays & Linked Lists", "Mar 3", "Theory", "Almacenar colecciones de elementos en memoria de forma secuencial o enlazada.", exampleCode = "// Array en JavaScript\nconst arr = [10, 20, 30, 40];\nconsole.log(arr[2]); // 30", position = 0),
        Note("2", "1", "Binary Search Trees", "Mar 2", "Algorithm", "BST property: left child < parent < right child...", position = 1),
        Note("3", "1", "Hash Tables", "Feb 28", "Theory", "Hash functions map keys to indices in an array...", position = 2)
    ))

    override fun getSections(): Flow<List<Section>> = _sections

    override fun getNotes(sectionId: String): Flow<List<Note>> = _notes.map { list ->
        list.filter { it.sectionId == sectionId }
    }

    override fun getNote(noteId: String): Flow<Note?> = _notes.map { list ->
        list.find { it.id == noteId }
    }

    override suspend fun addSection(name: String, position: Int) {
        val currentLists = _sections.value.toMutableList()
        currentLists.add(Section(UUID.randomUUID().toString(), name, 0, position))
        _sections.value = currentLists
    }

    override suspend fun addNote(sectionId: String, function: String, usedFor: String, example: String, position: Int) {
        val currentNotes = _notes.value.toMutableList()
        val typeId = if (example.isBlank()) "Theory" else "Algorithm"
        currentNotes.add(Note(id = UUID.randomUUID().toString(), sectionId = sectionId, title = function, date = "Today", type = typeId, description = usedFor, exampleCode = example.takeIf { it.isNotBlank() }, position = position))
        _notes.value = currentNotes
        
        val sectionList = _sections.value.toMutableList()
        val index = sectionList.indexOfFirst { it.id == sectionId }
        if (index != -1) {
            val s = sectionList[index]
            sectionList[index] = s.copy(noteCount = s.noteCount + 1)
            _sections.value = sectionList
        }
    }

    override suspend fun updateSection(section: Section) {
        val list = _sections.value.toMutableList()
        val index = list.indexOfFirst { it.id == section.id }
        if (index != -1) {
            list[index] = section
            _sections.value = list
        }
    }

    override suspend fun deleteSection(sectionId: String) {
        val notesToDelete = _notes.value.filter { it.sectionId == sectionId }
        _notes.value = _notes.value.filter { it.sectionId != sectionId }
        _sections.value = _sections.value.filter { it.id != sectionId }
    }

    override suspend fun updateNote(note: Note) {
        val list = _notes.value.toMutableList()
        val index = list.indexOfFirst { it.id == note.id }
        if (index != -1) {
            list[index] = note
            _notes.value = list
        }
    }

    override suspend fun deleteNote(noteId: String, sectionId: String) {
        _notes.value = _notes.value.filter { it.id != noteId }
        val sectionList = _sections.value.toMutableList()
        val index = sectionList.indexOfFirst { it.id == sectionId }
        if (index != -1) {
            val s = sectionList[index]
            sectionList[index] = s.copy(noteCount = maxOf(0, s.noteCount - 1))
            _sections.value = sectionList
        }
    }

    override suspend fun getAllSectionsSync(): List<Section> = _sections.value
    
    override suspend fun getAllNotesSync(): List<Note> = _notes.value
    
    override suspend fun insertBackup(sections: List<Section>, notes: List<Note>) {
        _sections.value = sections
        _notes.value = notes
    }

    override suspend fun updateSections(sections: List<Section>) {
        _sections.value = sections
    }

    override suspend fun updateNotes(notes: List<Note>) {
        _notes.value = notes
    }
}
