@file:OptIn(ExperimentalResourceApi::class)

package layouts.components.popups

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandorocore.helpers.*
import helpers.RED_COLOR
import helpers.showSnack
import layouts.components.PandoroTextField
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.showScheduleUpdatePopup
import layouts.ui.screens.SplashScreen.Companion.requester
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

/**
 * Function to show the popup to schedule a new [ProjectUpdate]
 *
 * No-any params required
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showScheduleUpdatePopup() {
    val notes = mutableStateListOf("")
    createPopup(
        height = 450.dp,
        flag = showScheduleUpdatePopup,
        title = stringResource(Res.string.schedule_update),
        content = {
            var targetVersion by remember { mutableStateOf("") }
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.target_version),
                isError = !isValidVersion(targetVersion),
                onValueChange = { targetVersion = it },
                value = targetVersion
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        top = 15.dp
                    ),
                text = stringResource(Res.string.change_notes),
                textAlign = TextAlign.Start
            )
            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        start = 15.dp
                    )
                    .height(225.dp)
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 5.dp
                            )
                    ) {
                        FloatingActionButton(
                            modifier = Modifier
                                .size(30.dp),
                            onClick = { notes.add("") },
                            content = { Icon(Icons.Filled.Add, null) }
                        )
                    }
                }
                items(notes.size) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val content = mutableStateOf(notes[index])
                        PandoroTextField(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    start = 10.dp
                                )
                                .width(220.dp)
                                .height(55.dp),
                            label = stringResource(Res.string.content),
                            isError = !isContentNoteValid(content.value),
                            onValueChange = {
                                if (it.isNotEmpty()) {
                                    content.value = it
                                    notes.removeAt(index)
                                    notes.add(index, it)
                                }
                            },
                            value = content.value,
                            textFieldModifier = Modifier
                                .width(220.dp)
                        )
                        IconButton(
                            onClick = { notes.removeAt(index) }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(18.dp),
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = RED_COLOR
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(15.dp)
            )
            Text(
                modifier = Modifier
                    .clickable {
                    if (isValidVersion(targetVersion)) {
                        if (notes.isNotEmpty()) {
                            var notesCorrect = true
                            for (note in notes) {
                                notesCorrect = isContentNoteValid(note)
                                if (!notesCorrect)
                                    break
                            }
                            if (notesCorrect) {
                                requester!!.execScheduleUpdate(currentProject.value.id, targetVersion, notes)
                                if (requester!!.successResponse())
                                    showScheduleUpdatePopup.value = false
                                else
                                    showSnack(coroutineScope, snackbarHostState, requester!!.errorMessage())
                            } else
                                showSnack(coroutineScope, snackbarHostState, Res.string.you_must_insert_correct_notes)
                        } else
                            showSnack(coroutineScope, snackbarHostState, Res.string.you_must_insert_one_note_at_least)
                    } else
                        showSnack(coroutineScope, snackbarHostState, Res.string.insert_a_correct_target_version)
                },
                text = stringResource(Res.string.schedule),
                fontSize = 14.sp
            )
        }
    )
}