package com.tecknobit.pandoro.layouts.ui.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import com.tecknobit.pandoro.helpers.RED_COLOR
import com.tecknobit.pandoro.helpers.spaceContent
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentNote
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentUpdate
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showNoteInfoPopup
import com.tecknobit.pandoro.viewmodels.NotesSectionViewModel
import com.tecknobit.pandorocore.records.Note
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.notes
import pandoro.composeapp.generated.resources.notes_number

/**
 * This is the layout for the notes section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class NotesSection : Section() {

    private lateinit var myNotes: List<Note>

    private val viewModel = NotesSectionViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * Function to show the content of the [NotesSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun ShowSection() {
        viewModel.refreshValues()
        myNotes = viewModel.notes.collectAsState().value
        var markModeEnabled by remember { mutableStateOf(false) }
        ShowSection {
            Column {
                Row(
                    modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 20.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.notes),
                        fontSize = 25.sp
                    )
                    IconButton(
                        modifier = Modifier
                            .padding(
                                top = 5.dp
                            ),
                        onClick = { markModeEnabled = !markModeEnabled }
                    ) {
                        Icon(
                            imageVector = if (!markModeEnabled)
                                Icons.Default.EditNote
                            else
                                Icons.Default.Notes,
                            contentDescription = null
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 20.dp
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 5.dp
                            ),
                        text = stringResource(Res.string.notes_number) + " ${myNotes.size}",
                        fontSize = 14.sp
                    )
                    spaceContent()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = 10.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(
                            bottom = 10.dp
                        )
                    ) {
                        items(
                            items = myNotes,
                            key = { note ->
                                note.id
                            }
                        ) { note ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(
                                        min = 65.dp
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                ),
                                onClick = {
                                    currentNote = note
                                    currentUpdate = null
                                    showNoteInfoPopup.value = true
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            start = 20.dp,
                                            end = 10.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val markAsDone = mutableStateOf(note.isMarkedAsDone)
                                    if (markModeEnabled) {
                                        Checkbox(
                                            modifier = Modifier
                                                .size(20.dp),
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = PRIMARY_COLOR,
                                                checkmarkColor = BACKGROUND_COLOR
                                            ),
                                            checked = markAsDone.value,
                                            onCheckedChange = {
                                                viewModel.manageNote(
                                                    markAsDone = markAsDone,
                                                    note = note
                                                )
                                            }
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .width(10.dp)
                                        )
                                    }
                                    val content = note.content
                                    if (content != null) {
                                        Text(
                                            modifier = Modifier
                                                .weight(15f)
                                                .fillMaxWidth(),
                                            text = content,
                                            textAlign = TextAlign.Justify,
                                            fontSize = 14.sp,
                                            textDecoration = if (markAsDone.value)
                                                TextDecoration.LineThrough
                                            else
                                                null
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteNote(
                                                    note = note
                                                )
                                            }
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .size(20.dp),
                                                imageVector = Icons.Default.Delete,
                                                tint = RED_COLOR,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}