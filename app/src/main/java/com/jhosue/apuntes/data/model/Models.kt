package com.jhosue.apuntes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey val id: String,
    val name: String,
    val noteCount: Int,
    val position: Int = 0
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String,
    val sectionId: String,
    val title: String,
    val date: String,
    val type: String,
    val description: String,
    val exampleCode: String? = null,
    val position: Int = 0
)
