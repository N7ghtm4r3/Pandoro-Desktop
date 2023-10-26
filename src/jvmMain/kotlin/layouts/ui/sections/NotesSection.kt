package layouts.ui.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Notes
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.RED_COLOR
import helpers.spaceContent
import layouts.ui.screens.Home.Companion.currentNote
import layouts.ui.screens.Home.Companion.currentUpdate
import layouts.ui.screens.Home.Companion.showNoteInfoPopup
import layouts.ui.screens.SplashScreen.Companion.user

/**
 * This is the layout for the notes section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class NotesSection : Section() {

    /**
     * Function to show the content of the [NotesSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun showSection() {
        var markModeEnabled by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notes",
                fontSize = 25.sp
            )
            IconButton(
                modifier = Modifier.padding(top = 5.dp),
                onClick = { markModeEnabled = !markModeEnabled }
            ) {
                Icon(
                    imageVector = if (!markModeEnabled) Icons.Default.EditNote else Icons.Default.Notes,
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = "Notes number: ${user.notes.size}",
                fontSize = 14.sp
            )
            spaceContent()
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(user.notes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().height(65.dp),
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        elevation = 2.dp,
                        onClick = {
                            currentNote = note
                            currentUpdate = null
                            showNoteInfoPopup.value = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 20.dp, end = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var markAsDone by remember { mutableStateOf(note.isMarkedAsDone) }
                            if (markModeEnabled) {
                                Checkbox(
                                    modifier = Modifier.size(20.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PRIMARY_COLOR,
                                        checkmarkColor = BACKGROUND_COLOR
                                    ),
                                    checked = markAsDone,
                                    onCheckedChange = {
                                        // TODO: MAKE REQUEST THEN
                                        markAsDone = it
                                    }
                                )
                                Spacer(Modifier.width(10.dp))
                            }
                            Text(
                                modifier = Modifier.weight(15f).fillMaxWidth(),
                                text = note.content,
                                textAlign = TextAlign.Justify,
                                fontSize = 14.sp,
                                textDecoration = if (markAsDone) TextDecoration.LineThrough else null
                            )
                            Column(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                IconButton(
                                    onClick = {
                                        // TODO: MAKE REQUEST THEN
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
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