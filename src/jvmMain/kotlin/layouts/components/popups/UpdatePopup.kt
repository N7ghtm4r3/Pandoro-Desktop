package layouts.components.popups

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helpers.RED_COLOR
import helpers.showSnack
import layouts.components.PandoroTextField
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.showScheduleUpdatePopup
import toImportFromLibrary.Update.TARGET_VERSION_MAX_LENGTH

/**
 * Function to show the popup to schedule a new [Update]
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
        title = "Schedule an update for ${currentProject.name} project",
        content = {
            var targetVersion by remember { mutableStateOf("") }
            PandoroTextField(
                modifier = Modifier.padding(10.dp).height(55.dp),
                label = "Target version",
                isError = !isValidTargetVersion(targetVersion),
                onValueChange = { targetVersion = it },
                value = targetVersion
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 15.dp),
                text = "Notes for the update",
                textAlign = TextAlign.Start
            )
            LazyColumn(
                modifier = Modifier.padding(top = 10.dp, start = 15.dp).height(225.dp)
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier.size(30.dp),
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
                            modifier = Modifier.padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 10.dp
                            ).width(220.dp).height(55.dp),
                            label = "Content of the note",
                            isError = !isContentNoteValid(content.value),
                            onValueChange = {
                                if (it.isNotEmpty()) {
                                    content.value = it
                                    notes.removeAt(index)
                                    notes.add(index, it)
                                }
                            },
                            value = content.value,
                            textFieldModifier = Modifier.width(220.dp)
                        )
                        IconButton(
                            onClick = { notes.removeAt(index) }
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = RED_COLOR
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(15.dp))
            Text(
                modifier = Modifier.clickable {
                    if (isValidTargetVersion(targetVersion)) {
                        if (notes.isNotEmpty()) {
                            var notesCorrect = true
                            for (note in notes) {
                                notesCorrect = isContentNoteValid(note)
                                if (!notesCorrect)
                                    break
                            }
                            if (notesCorrect) {
                                // TODO: MAKE REQUEST THEN
                                showScheduleUpdatePopup.value = false
                            } else
                                showSnack(coroutineScope, scaffoldState, "You must insert correct notes")
                        } else
                            showSnack(coroutineScope, scaffoldState, "You must insert one note at least")
                    } else
                        showSnack(coroutineScope, scaffoldState, "Insert a correct target version")
                },
                text = "Schedule update",
                fontSize = 14.sp
            )
        }
    )
}

/**
 * Function to check the validity of a target version
 *
 * @param targetVersion: target version to check
 * @return whether the target version is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isValidTargetVersion(targetVersion: String): Boolean {
    return targetVersion.length in 1..TARGET_VERSION_MAX_LENGTH
}