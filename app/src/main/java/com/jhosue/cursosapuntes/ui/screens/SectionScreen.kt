package com.jhosue.cursosapuntes.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.luminance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Share
import android.content.Intent
import androidx.activity.compose.BackHandler
import com.jhosue.cursosapuntes.data.model.Note
import com.jhosue.cursosapuntes.ui.components.CreateNoteModal
import com.jhosue.cursosapuntes.ui.components.DeleteConfirmationDialog
import com.jhosue.cursosapuntes.ui.components.EditNoteModal
import com.jhosue.cursosapuntes.viewmodel.SectionViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SectionScreen(
    viewModel: SectionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNoteDetail: (String, String, Int, Int) -> Unit
) {
    val title by viewModel.title.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showCreateNoteModal by remember { mutableStateOf(false) }

    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var selectedNotes by remember { mutableStateOf(setOf<String>()) }
    var isSelectionModeActive by remember { mutableStateOf(false) }
    var showMultipleDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = isSelectionModeActive) {
        selectedNotes = emptySet()
        isSelectionModeActive = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (selectedNotes.isEmpty()) {
                FloatingActionButton(
                    onClick = { showCreateNoteModal = true },
                    containerColor = Color(0xFF2872EB),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        }
    ) { padding ->
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = padding.calculateTopPadding() + 24.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            onClick = onNavigateBack
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Back", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${notes.size} notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        placeholder = { Text("Search in section...", color = Color.White.copy(alpha = 0.6f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            cursorColor = Color.White,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }

            if (notes.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notes found matching \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8F9BB3),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notes in this section yet.\nTap + to add one.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8F9BB3),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        if (isSelectionModeActive && notes.isNotEmpty()) {
                            val allSelected = selectedNotes.size == notes.size
                            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), contentAlignment = Alignment.CenterEnd) {
                                Text(
                                    text = if (allSelected) "Deseleccionar todo" else "Seleccionar todo",
                                    color = Color(0xFF2563EB),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .clickable {
                                            if (allSelected) {
                                                selectedNotes = emptySet()
                                                isSelectionModeActive = false
                                            } else {
                                                selectedNotes = notes.map { it.id }.toSet()
                                            }
                                        }
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }
                    items(notes, key = { it.id }) { note ->
                        val isSelected = selectedNotes.contains(note.id)
                        val isSelectionMode = isSelectionModeActive
                        
                        NoteItem(
                            note = note,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode,
                            dragHandleModifier = Modifier, // Restored plain modifier
                            onClick = { 
                                if (isSelectionMode) {
                                    val newSelection = if (isSelected) selectedNotes - note.id else selectedNotes + note.id
                                    selectedNotes = newSelection
                                    if (newSelection.isEmpty()) isSelectionModeActive = false
                                } else {
                                    onNavigateToNoteDetail(note.id, title, notes.indexOf(note) + 1, notes.size)
                                }
                            },
                            onLongClick = {
                                if (!isSelectionModeActive) {
                                    isSelectionModeActive = true
                                    selectedNotes = selectedNotes + note.id
                                }
                            },
                            onEdit = { noteToEdit = note },
                            onDelete = { noteToDelete = note }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        
        if (isSelectionModeActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding() + 16.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedNotes.size} sel.", 
                                fontWeight = FontWeight.Bold, 
                                color = if (isSystemInDarkTheme()) Color.White else Color(0xFF1E1E1E),
                                maxLines = 1,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            TextButton(
                                onClick = { 
                                    selectedNotes = emptySet() 
                                    isSelectionModeActive = false
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Cancelar", color = Color.Gray, maxLines = 1)
                            }
                            if (selectedNotes.size == 1) {
                                TextButton(
                                    onClick = { 
                                        noteToEdit = notes.find { it.id == selectedNotes.first() } 
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp)
                                ) {
                                    Text("Editar", color = Color(0xFF2563EB), maxLines = 1)
                                }
                            }
                            TextButton(
                                onClick = { 
                                    val selectedNotesObjects = notes.filter { selectedNotes.contains(it.id) }
                                    val shareTexts = selectedNotesObjects.map { currentNote ->
                                        buildString {
                                            append("📌 ${currentNote.title}\n\n")
                                            append("📖 ¿Para qué sirve?\n${currentNote.description}")
                                            if (!currentNote.exampleCode.isNullOrBlank()) {
                                                append("\n\n💻 Ejemplo:\n${currentNote.exampleCode}")
                                            }
                                        }
                                    }
                                    val finalShareText = shareTexts.joinToString(separator = "\n\n----------------------------\n\n")
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, finalShareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir notas"))
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Compartir", color = Color(0xFF2872EB), maxLines = 1)
                            }
                            TextButton(
                                onClick = { showMultipleDeleteDialog = true },
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Eliminar", color = Color.Red, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMultipleDeleteDialog) {
        DeleteConfirmationDialog(
            title = "¿Eliminar ${selectedNotes.size} notas?",
            message = "Esta acción no se puede deshacer.",
            onDismissRequest = { showMultipleDeleteDialog = false },
            onConfirm = {
                viewModel.deleteNotes(selectedNotes)
                selectedNotes = emptySet()
                isSelectionModeActive = false
                showMultipleDeleteDialog = false
            }
        )
    }

    if (showCreateNoteModal) {
            CreateNoteModal(
                onDismissRequest = { showCreateNoteModal = false },
                onCreate = { function, usedFor, example ->
                    viewModel.addNote(function, usedFor, example)
                    showCreateNoteModal = false
                }
            )
        }

        noteToEdit?.let { note ->
            EditNoteModal(
                currentTitle = note.title,
                currentDescription = note.description,
                currentExample = note.exampleCode,
                onDismissRequest = { noteToEdit = null },
                onSave = { newTitle, newDescription, newExample ->
                    val newType = if (newExample.isBlank()) "Theory" else "Algorithm"
                    viewModel.updateNote(
                        note.copy(
                            title = newTitle,
                            description = newDescription,
                            exampleCode = newExample.takeIf { it.isNotBlank() },
                            type = newType
                        )
                    )
                    noteToEdit = null
                    selectedNotes = emptySet()
                    isSelectionModeActive = false
                }
            )
        }

        noteToDelete?.let { note ->
            DeleteConfirmationDialog(
                title = "Delete Note?",
                message = "This action cannot be undone.",
                onDismissRequest = { noteToDelete = null },
                onConfirm = {
                    viewModel.deleteNote(note.id)
                    noteToDelete = null
                }
            )
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    dragHandleModifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scale by animateFloatAsState(
        targetValue = if (showMenu) 0.97f else 1f,
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { 
                        if (!isSelectionMode) onLongClick()
                    }
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF1E3A8A) else Color(0xFFE8F0FE)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2563EB)) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isSelectionMode) {
                    Box(modifier = dragHandleModifier) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Reordenar",
                            tint = Color.LightGray,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(20.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = note.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (!isSelectionMode) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { showMenu = true }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = if (note.type == "Theory") Color(0xFFEDF1F7) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = note.type,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (note.type == "Theory") Color(0xFF2872EB) else Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier
                .width((LocalConfiguration.current.screenWidthDp * 0.5f).dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
            offset = DpOffset(x = 8.dp, y = (-60).dp)
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF2872EB),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Edit", color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                onClick = {
                    showMenu = false
                    onEdit()
                },
                modifier = Modifier.height(52.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Delete", color = Color(0xFFE53935))
                    }
                },
                onClick = {
                    showMenu = false
                    onDelete()
                },
                modifier = Modifier.height(52.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF2872EB),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Share", color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                onClick = {
                    showMenu = false
                    val shareText = buildString {
                        append("📌 ${note.title}\n\n")
                        append("📖 ¿Para qué sirve?\n${note.description}")
                        if (!note.exampleCode.isNullOrBlank()) {
                            append("\n\n💻 Ejemplo:\n${note.exampleCode}")
                        }
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir nota"))
                },
                modifier = Modifier.height(52.dp)
            )
        }
    }
}
