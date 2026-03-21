package com.jhosue.cursosapuntes.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.luminance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.jhosue.cursosapuntes.data.model.Section
import com.jhosue.cursosapuntes.ui.components.CreateSectionModal
import com.jhosue.cursosapuntes.ui.components.DeleteConfirmationDialog
import com.jhosue.cursosapuntes.ui.components.EditSectionModal
import com.jhosue.cursosapuntes.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSection: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val sections by viewModel.sections.collectAsState()
    var showCreateModal by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var sectionToEdit by remember { mutableStateOf<Section?>(null) }
    var sectionToDelete by remember { mutableStateOf<Section?>(null) }
    var selectedSections by remember { mutableStateOf(setOf<String>()) }
    var isSelectionModeActive by remember { mutableStateOf(false) }
    var showMultipleDeleteDialog by remember { mutableStateOf(false) }

    var backPressedOnce by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        if (isSelectionModeActive) {
            selectedSections = emptySet()
            isSelectionModeActive = false
        } else {
            if (backPressedOnce) {
                (context as? Activity)?.finish()
            } else {
                backPressedOnce = true
                Toast.makeText(context, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            delay(2000L)
            backPressedOnce = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (selectedSections.isEmpty()) {
                FloatingActionButton(
                    onClick = { showCreateModal = true },
                    containerColor = Color(0xFF2872EB),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Section")
                }
            }
        }
    ) { padding ->
        val filteredSections = sections.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 80.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MY WORKSPACE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8F9BB3)
                                )
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            onClick = onNavigateToSettings,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "My Notes",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search sections...", color = Color(0xFFA6B0C3)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFA6B0C3)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "SECTIONS • ${filteredSections.size}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8F9BB3)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                if (isSelectionModeActive && filteredSections.isNotEmpty()) {
                    val allSelected = selectedSections.size == filteredSections.size
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text(
                            text = if (allSelected) "Deseleccionar todo" else "Seleccionar todo",
                            color = Color(0xFF2563EB),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable {
                                    if (allSelected) {
                                        selectedSections = emptySet()
                                        isSelectionModeActive = false
                                    } else {
                                        selectedSections = filteredSections.map { it.id }.toSet()
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }

                if (filteredSections.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No sections found." else "No sections yet.\nTap + to create one.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF8F9BB3),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            
            items(filteredSections, key = { it.id }) { section ->
                val isSelected = selectedSections.contains(section.id)
                val isSelectionMode = isSelectionModeActive

                SectionItem(
                    section = section,
                    isSelected = isSelected,
                    isSelectionMode = isSelectionMode,
                    dragHandleModifier = Modifier, // Restored plain modifier
                    onClick = { 
                        if (isSelectionMode) {
                            val newSelection = if (isSelected) selectedSections - section.id else selectedSections + section.id
                            selectedSections = newSelection
                            if (newSelection.isEmpty()) isSelectionModeActive = false
                        } else {
                            onNavigateToSection(section.id, section.name)
                        }
                    },
                    onLongClick = {
                        if (!isSelectionModeActive) {
                            isSelectionModeActive = true
                            selectedSections = selectedSections + section.id
                        }
                    },
                    onEdit = { sectionToEdit = section },
                    onDelete = { sectionToDelete = section }
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedSections.size} sel.", 
                                fontWeight = FontWeight.Bold, 
                                color = if (isSystemInDarkTheme()) Color.White else Color(0xFF1E1E1E),
                                maxLines = 1,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { 
                                selectedSections = emptySet() 
                                isSelectionModeActive = false
                            }) {
                                Text("Cancelar", color = Color.Gray, maxLines = 1)
                            }
                            if (selectedSections.size == 1) {
                                TextButton(onClick = { 
                                    sectionToEdit = filteredSections.find { it.id == selectedSections.first() }
                                }) {
                                    Text("Editar", color = Color(0xFF2563EB), maxLines = 1)
                                }
                            }
                            TextButton(onClick = { showMultipleDeleteDialog = true }) {
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
            title = "¿Eliminar ${selectedSections.size} secciones?",
            message = "Se eliminarán también todas las notas dentro de cada sección seleccionada. Esta acción no se puede deshacer.",
            onDismissRequest = { showMultipleDeleteDialog = false },
            onConfirm = {
                viewModel.deleteSections(selectedSections)
                selectedSections = emptySet()
                isSelectionModeActive = false
                showMultipleDeleteDialog = false
            }
        )
    }

    if (showCreateModal) {
        CreateSectionModal(
            onDismissRequest = { showCreateModal = false },
            onCreate = { name ->
                viewModel.addSection(name)
                showCreateModal = false
            }
        )
    }

    sectionToEdit?.let { section ->
        EditSectionModal(
            currentName = section.name,
            onDismissRequest = { sectionToEdit = null },
            onSave = { newName ->
                viewModel.updateSection(section.copy(name = newName))
                sectionToEdit = null
                selectedSections = emptySet()
                isSelectionModeActive = false
            }
        )
    }

    sectionToDelete?.let { section ->
        DeleteConfirmationDialog(
            title = "Delete Section?",
            message = "This will also delete all ${section.noteCount} notes inside this section. This action cannot be undone.",
            onDismissRequest = { sectionToDelete = null },
            onConfirm = {
                viewModel.deleteSection(section.id)
                sectionToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SectionItem(
    section: Section,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    dragHandleModifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
            border = if (isSelected) BorderStroke(2.dp, Color(0xFF2563EB)) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
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
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF2872EB), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${section.noteCount} notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                                detectTapGestures(onTap = { showMenu = true })
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
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
        }
    }
}
