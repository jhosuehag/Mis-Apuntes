package com.jhosue.apuntes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jhosue.apuntes.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE sectionId = :sectionId ORDER BY position ASC")
    fun getNotesBySectionId(sectionId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY position ASC")
    suspend fun getAllNotesSync(): List<Note>

    @androidx.room.Update
    suspend fun updateNotes(notes: List<Note>)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteById(noteId: String): Flow<Note?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>)

    @Query("UPDATE notes SET title = :title, description = :description, exampleCode = :exampleCode, type = :type WHERE id = :noteId")
    fun updateNote(noteId: String, title: String, description: String, exampleCode: String?, type: String)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("DELETE FROM notes WHERE sectionId = :sectionId")
    suspend fun deleteNotesBySectionId(sectionId: String)
}
