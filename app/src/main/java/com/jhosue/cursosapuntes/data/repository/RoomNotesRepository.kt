package com.jhosue.cursosapuntes.data.repository

import com.jhosue.cursosapuntes.data.local.NoteDao
import com.jhosue.cursosapuntes.data.local.SectionDao
import com.jhosue.cursosapuntes.data.model.Note
import com.jhosue.cursosapuntes.data.model.Section
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomNotesRepository @Inject constructor(
    private val sectionDao: SectionDao,
    private val noteDao: NoteDao
) : NotesRepository {

    override fun getSections(): Flow<List<Section>> {
        return sectionDao.getAllSections()
    }

    override fun getNotes(sectionId: String): Flow<List<Note>> {
        return noteDao.getNotesBySectionId(sectionId)
    }

    override fun getNote(noteId: String): Flow<Note?> {
        return noteDao.getNoteById(noteId)
    }

    override suspend fun addSection(name: String, position: Int) {
        withContext(Dispatchers.IO) {
            val section = Section(
                id = UUID.randomUUID().toString(),
                name = name,
                noteCount = 0,
                position = position
            )
            sectionDao.insertSection(section)
        }
    }

    override suspend fun addNote(sectionId: String, function: String, usedFor: String, example: String, position: Int) {
        withContext(Dispatchers.IO) {
            val typeId = if (example.isBlank()) "Theory" else "Algorithm"
            val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
            val dateString = dateFormat.format(Date())

            val note = Note(
                id = UUID.randomUUID().toString(),
                sectionId = sectionId,
                title = function,
                date = dateString,
                type = typeId,
                description = usedFor,
                exampleCode = example.takeIf { it.isNotBlank() },
                position = position
            )
            
            noteDao.insertNote(note)
            sectionDao.incrementNoteCount(sectionId)
        }
    }

    override suspend fun updateSection(section: Section) {
        withContext(Dispatchers.IO) {
            sectionDao.updateSectionName(section.id, section.name)
            sectionDao.updateNoteCount(section.id, section.noteCount)
        }
    }

    override suspend fun deleteSection(sectionId: String) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNotesBySectionId(sectionId)
            sectionDao.deleteSection(sectionId)
        }
    }

    override suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.updateNote(
                noteId = note.id,
                title = note.title,
                description = note.description,
                exampleCode = note.exampleCode,
                type = note.type
            )
        }
    }

    override suspend fun deleteNote(noteId: String, sectionId: String) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNote(noteId)
            sectionDao.decrementNoteCount(sectionId)
        }
    }

    override suspend fun getAllSectionsSync(): List<Section> {
        return withContext(Dispatchers.IO) {
            sectionDao.getAllSectionsSync()
        }
    }

    override suspend fun getAllNotesSync(): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getAllNotesSync()
        }
    }

    override suspend fun insertBackup(sections: List<Section>, notes: List<Note>) {
        withContext(Dispatchers.IO) {
            sectionDao.deleteAllSections()
            noteDao.deleteAllNotes()

            sectionDao.insertSections(sections)
            noteDao.insertNotes(notes)
        }
    }

    override suspend fun updateSections(sections: List<Section>) {
        withContext(Dispatchers.IO) {
            sectionDao.updateSections(sections)
        }
    }

    override suspend fun updateNotes(notes: List<Note>) {
        withContext(Dispatchers.IO) {
            noteDao.updateNotes(notes)
        }
    }
}
