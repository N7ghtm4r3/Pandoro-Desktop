@file:OptIn(ExperimentalResourceApi::class)

package layouts.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isContentNoteValid
import com.tecknobit.pandorocore.records.Note
import com.tecknobit.pandorocore.records.ProjectUpdate
import helpers.showSnack
import helpers.spaceContent
import layouts.components.PandoroTextField
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.showCreateNotePopup
import layouts.ui.screens.Home.Companion.showNoteInfoPopup
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

/**
 * **clipboard** -> the clipboard where save the content of note copied
 */
val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

/**
 * Function to show the popup to create a new [Note]
 *
 * @param update: the update where add the new [Note], if **null** will be created for the [User]
 */
@Composable
fun showCreateNotePopup(update: ProjectUpdate?) {
    createPopup(
        width = 300.dp,
        height = 200.dp,
        flag = showCreateNotePopup,
        title = stringResource(Res.string.create_a_new_note),
        columnModifier = Modifier,
        titleSize = 15.sp,
        content = {
            val content = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                PandoroTextField(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(55.dp),
                    label = Res.string.content,
                    isError = !isContentNoteValid(content.value),
                    value = content
                )
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            if (isContentNoteValid(content.value)) {
                                /*if (update != null) {
                                    requester!!.execAddChangeNote(currentProject.value.id, update.id, content)
                                    if (requester!!.successResponse())
                                        showCreateNotePopup.value = false
                                    else
                                        showSnack(coroutineScope, snackbarHostState, requester!!.errorMessage())
                                } else {
                                    requester!!.execAddNote(content)
                                    if (requester!!.successResponse())
                                        showCreateNotePopup.value = false
                                    else
                                        showSnack(coroutineScope, snackbarHostState, requester!!.errorMessage())
                                }*/
                            } else
                                showSnack(coroutineScope, snackbarHostState, Res.string.insert_a_correct_content)
                    },
                    text = stringResource(Res.string.create),
                    fontSize = 14.sp
                )
            }
        }
    )
}

/**
 * Function to show the popup to show the info details of a [Note]
 *
 * @param note: the note where get the info details to show
 * @param update: the update where get the [Note], if **null** will be got from the [User]
 */
@Composable
fun showNoteInfoPopup(
    note: Note,
    update: ProjectUpdate?
) {
    val contentLength = note.content.length
    createPopup(
        height =
        if (!note.isMarkedAsDone && contentLength < 50)
            200.dp
        else if (note.isMarkedAsDone && contentLength > 50) {
            if (note.author != null)
                350.dp
            else
                270.dp
        } else
            270.dp,
        flag = showNoteInfoPopup,
        title = stringResource(Res.string.note_info),
        content = {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                if (update != null) {
                    Text(
                        text = stringResource(Res.string.update_version) + " v. ${update.targetVersion}",
                        fontSize = 14.sp
                    )
                }
                spaceContent(5.dp, end = 10.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .weight(10f)
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = stringResource(Res.string.content) + ": ${note.content}",
                        fontSize = 14.sp
                    )
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(
                                top = 5.dp
                            )
                            .size(16.dp),
                        onClick = {
                            clipboard.setContents(StringSelection(note.content), null)
                            showSnack(coroutineScope, snackbarHostState, Res.string.content_copied)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null
                        )
                    }
                }
                spaceContent(5.dp, end = 10.dp)
                val showUsers = update != null && currentProject.value.hasGroups()
                val author = note.author
                if (showUsers && author != null) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(Res.string.author) + " ${author.completeName}",
                        fontSize = 14.sp
                    )
                    spaceContent(5.dp, end = 10.dp)
                }
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = stringResource(Res.string.creation_date) + " ${note.creationDate}",
                    fontSize = 14.sp
                )
                spaceContent(5.dp, end = 10.dp)
                if (note.isMarkedAsDone) {
                    val markedAsDoneBy = note.markedAsDoneBy
                    if (showUsers && markedAsDoneBy != null) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = stringResource(Res.string.marked_as_done_by) + " ${markedAsDoneBy.completeName}",
                            fontSize = 14.sp
                        )
                        spaceContent(5.dp, end = 10.dp)
                    }
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(Res.string.date_of_mark) + " ${note.markedAsDoneDate}",
                        fontSize = 14.sp
                    )
                    spaceContent(5.dp, end = 10.dp)
                }
            }
        }
    )
}