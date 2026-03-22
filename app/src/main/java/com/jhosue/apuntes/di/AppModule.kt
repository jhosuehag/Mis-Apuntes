package com.jhosue.apuntes.di

import android.content.Context
import androidx.room.Room
import com.jhosue.apuntes.data.local.AppDatabase
import com.jhosue.apuntes.data.local.MIGRATION_1_2
import com.jhosue.apuntes.data.local.NoteDao
import com.jhosue.apuntes.data.local.SectionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes_db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideSectionDao(appDatabase: AppDatabase): SectionDao {
        return appDatabase.sectionDao()
    }

    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao {
        return appDatabase.noteDao()
    }
}
