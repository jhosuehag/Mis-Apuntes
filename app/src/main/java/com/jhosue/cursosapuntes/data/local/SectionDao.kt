package com.jhosue.cursosapuntes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jhosue.cursosapuntes.data.model.Section
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections ORDER BY position ASC")
    fun getAllSections(): Flow<List<Section>>

    @Query("SELECT * FROM sections ORDER BY position ASC")
    suspend fun getAllSectionsSync(): List<Section>

    @androidx.room.Update
    suspend fun updateSections(sections: List<Section>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: Section): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<Section>)

    @Query("UPDATE sections SET noteCount = noteCount + 1 WHERE id = :sectionId")
    fun incrementNoteCount(sectionId: String): Int

    @Query("UPDATE sections SET name = :name WHERE id = :sectionId")
    fun updateSectionName(sectionId: String, name: String)

    @Query("UPDATE sections SET noteCount = :noteCount WHERE id = :sectionId")
    fun updateNoteCount(sectionId: String, noteCount: Int)

    @Query("DELETE FROM sections WHERE id = :sectionId")
    suspend fun deleteSection(sectionId: String)

    @Query("DELETE FROM sections")
    suspend fun deleteAllSections()

    @Query("UPDATE sections SET noteCount = noteCount - 1 WHERE id = :sectionId AND noteCount > 0")
    fun decrementNoteCount(sectionId: String)
}
