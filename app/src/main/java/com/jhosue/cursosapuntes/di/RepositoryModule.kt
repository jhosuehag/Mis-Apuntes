package com.jhosue.cursosapuntes.di

import com.jhosue.cursosapuntes.data.repository.RoomNotesRepository
import com.jhosue.cursosapuntes.data.repository.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindNotesRepository(
        roomNotesRepository: RoomNotesRepository
    ): NotesRepository
}
