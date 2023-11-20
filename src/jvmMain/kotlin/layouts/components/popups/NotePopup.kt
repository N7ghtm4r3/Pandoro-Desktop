package layouts.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.isContentNoteValid
import com.tecknobit.pandoro.records.Note
import com.tecknobit.pandoro.records.ProjectUpdate
import helpers.showSnack
import helpers.spaceContent
import layouts.components.PandoroTextField
import layouts.ui.screens.Home.Companion.showCreateNotePopup
import layouts.ui.screens.Home.Companion.showNoteInfoPopup

/**
 * Function to show the popup to create a new [Note]
 *
 * @param update: the update where add the new [Note], if **null** will be create for the [User]
 */
@Composable
fun showCreateNotePopup(update: ProjectUpdate?) {
    createPopup(
        width = 250.dp,
        height = 200.dp,
        flag = showCreateNotePopup,
        title = if (update != null) "Create a note for the ${update.targetVersion} update" else "Create a new note",
        columnModifier = Modifier,
        titleSize = 15.sp,
        content = {
            var content by remember { mutableStateOf("") }
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                PandoroTextField(
                    modifier = Modifier.padding(10.dp).height(55.dp),
                    label = "Content",
                    isError = !isContentNoteValid(content),
                    onValueChange = { content = it },
                    value = content
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable {
                        if (isContentNoteValid(content)) {
                            // TODO: MAKE REQUEST THEN
                            showCreateNotePopup.value = false
                        } else
                            showSnack(coroutineScope, scaffoldState, "Insert a correct content")
                    },
                    text = "Create note",
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
fun showNoteInfoPopup(note: Note, update: ProjectUpdate?) {
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
        title = "Note info",
        content = {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                if (update != null) {
                    Text(
                        text = "ProjectUpdate version: v. ${update.targetVersion}",
                        fontSize = 14.sp
                    )
                }
                spaceContent(5.dp, end = 10.dp)
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = "Note: ${note.content}",
                    fontSize = 14.sp
                )
                spaceContent(5.dp, end = 10.dp)
                val author = note.author
                if (author != null) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Author: ${author.completeName}",
                        fontSize = 14.sp
                    )
                    spaceContent(5.dp, end = 10.dp)
                }
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = "Creation date: ${note.creationDate}",
                    fontSize = 14.sp
                )
                spaceContent(5.dp, end = 10.dp)
                if (note.isMarkedAsDone) {
                    val doneAuthor = note.markedAsDoneBy
                    if (doneAuthor != null) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = "Marked as done by: ${doneAuthor.completeName}",
                            fontSize = 14.sp
                        )
                        spaceContent(5.dp, end = 10.dp)
                    }
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Date of mark: ${note.markedAsDoneDate}",
                        fontSize = 14.sp
                    )
                    spaceContent(5.dp, end = 10.dp)
                }
            }
        }
    )
}