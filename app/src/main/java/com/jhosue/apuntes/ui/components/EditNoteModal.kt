package com.jhosue.apuntes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditNoteModal(
    currentTitle: String,
    currentDescription: String,
    currentExample: String?,
    onDismissRequest: () -> Unit,
    onSave: (title: String, description: String, example: String) -> Unit
) {
    var functionName by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentTitle,
                selection = TextRange(currentTitle.length)
            )
        )
    }
    var usedForDescription by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentDescription,
                selection = TextRange(currentDescription.length)
            )
        )
    }
    val currentExampleText = currentExample ?: ""
    var exampleCode by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentExampleText,
                selection = TextRange(currentExampleText.length)
            )
        )
    }

    // FocusRequesters for cyclic navigation
    val functionFR = remember { FocusRequester() }
    val usedForFR = remember { FocusRequester() }
    val exampleFR = remember { FocusRequester() }
    val focusRequesters = remember { listOf(functionFR, usedForFR, exampleFR) }
    // -1 = no field focused yet; first button press → index 0 (Function)
    // 0 = Function, 1 = Used for, 2 = Example
    var currentFocusIndex by remember { mutableIntStateOf(-1) }

    // Track focus state of each field that may be hidden by the keyboard.
    var usedForFocusState by remember { mutableStateOf<FocusState?>(null) }
    var exampleFocusState by remember { mutableStateOf<FocusState?>(null) }

    val usedForBIVR = remember { BringIntoViewRequester() }
    val exampleBIVR = remember { BringIntoViewRequester() }

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime

    // ── IME-stable scroll ────────────────────────────────────────────────────
    // Problem: bringIntoView() called at focus time runs before the keyboard
    // finishes animating, so it calculates coordinates against the old layout.
    // Solution: observe WindowInsets.ime.getBottom() with snapshotFlow.
    //   • distinctUntilChanged() emits only when the value actually changes.
    //   • We check whether the bottom value has STABILISED by collecting two
    //     consecutive equal non-zero values; that is the signal that the
    //     keyboard animation has completed and layout is final.
    // When stable AND a target field has focus we call bringIntoView().
    LaunchedEffect(Unit) {
        var previous = 0
        snapshotFlow { imeInsets.getBottom(density) }
            .distinctUntilChanged()
            .collect { current ->
                if (current > 0 && current == previous) {
                    // IME stable at its final height — layout is settled.
                    when {
                        usedForFocusState?.isFocused == true ->
                            launch { usedForBIVR.bringIntoView() }
                        exampleFocusState?.isFocused == true ->
                            launch { exampleBIVR.bringIntoView() }
                    }
                }
                previous = current
            }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // ── Header ────────────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit Note",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    // Cycle: Function(0) → UsedFor(1) → Example(2) → Function(0)
                                    val next = (currentFocusIndex + 1) % focusRequesters.size
                                    focusRequesters[next].requestFocus()
                                    currentFocusIndex = next
                                    // Scroll is handled by the IME LaunchedEffect once
                                    // the keyboard finishes animating.
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.size(width = 44.dp, height = 36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Cycle focus between fields",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // ── Form fields ───────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Function",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = functionName,
                            onValueChange = { functionName = it },
                            placeholder = {
                                Text("e.g., Hash Map", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    usedForFR.requestFocus()
                                    currentFocusIndex = 1
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(functionFR)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Used for",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = usedForDescription,
                            onValueChange = { usedForDescription = it },
                            placeholder = {
                                Text(
                                    "Describe the primary use case...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .focusRequester(usedForFR)
                                .bringIntoViewRequester(usedForBIVR)
                                .onFocusChanged { usedForFocusState = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Example",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = exampleCode,
                            onValueChange = { exampleCode = it },
                            placeholder = {
                                Text(
                                    "Map<String, Integer> map = ...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .focusRequester(exampleFR)
                                .bringIntoViewRequester(exampleBIVR)
                                .onFocusChanged { exampleFocusState = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ── Save button ───────────────────────────────────────────
                    Button(
                        onClick = { onSave(functionName.text, usedForDescription.text, exampleCode.text) },
                        enabled = functionName.text.isNotBlank() && usedForDescription.text.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
