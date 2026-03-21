package com.jhosue.cursosapuntes.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.jhosue.cursosapuntes.data.model.BackupData
import com.jhosue.cursosapuntes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class BackupStatus {
    object Idle : BackupStatus()
    object Loading : BackupStatus()
    data class Success(val message: String) : BackupStatus()
    data class Error(val message: String) : BackupStatus()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _backupStatus = MutableStateFlow<BackupStatus>(BackupStatus.Idle)
    val backupStatus: StateFlow<BackupStatus> = _backupStatus.asStateFlow()

    fun exportDatabase(context: Context) {
        viewModelScope.launch {
            try {
                _backupStatus.value = BackupStatus.Loading
                val sections = repository.getAllSectionsSync()
                val notes = repository.getAllNotesSync()
                val backupData = BackupData(sections, notes)
                val jsonStr = Gson().toJson(backupData)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateString = dateFormat.format(Date())
                val fileName = "mynotes_backup_$dateString.json"

                val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveUsingMediaStore(context, fileName, jsonStr)
                } else {
                    saveToLegacyDownloads(fileName, jsonStr)
                }

                if (success) {
                    _backupStatus.value = BackupStatus.Success("Backup saved to Downloads: $fileName")
                } else {
                    _backupStatus.value = BackupStatus.Error("Failed to save backup")
                }
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun saveUsingMediaStore(context: Context, fileName: String, content: String): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveToLegacyDownloads(fileName: String, content: String): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            file.writeText(content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importDatabase(context: Context, uri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _backupStatus.value = BackupStatus.Loading
                
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = InputStreamReader(inputStream)
                    val backupData = Gson().fromJson(reader, BackupData::class.java)
                    
                    if (backupData.sections != null && backupData.notes != null) {
                        repository.insertBackup(backupData.sections, backupData.notes)
                        _backupStatus.value = BackupStatus.Success("Restore successful")
                        onSuccess()
                    } else {
                        _backupStatus.value = BackupStatus.Error("Invalid backup file format")
                    }
                } ?: run {
                    _backupStatus.value = BackupStatus.Error("Could not open file")
                }
            } catch (e: JsonSyntaxException) {
                _backupStatus.value = BackupStatus.Error("Invalid JSON structure in backup file.")
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.Error("Error reading file: ${e.message}")
            }
        }
    }

    fun resetStatus() {
        _backupStatus.value = BackupStatus.Idle
    }
}
