package com.tecknobit.pandoro.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.pandorocore.records.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import layouts.ui.screens.Home
import layouts.ui.sections.NotesSection
import layouts.ui.sections.Section.Sections.*

/**
 * The **NotesSectionViewModel** class is the support class used by the [NotesSection]
 * to manage the notes of the user
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see PandoroViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
//TODO: TO COMMENT
class NotesSectionViewModel(
    override var snackbarHostState: SnackbarHostState?
): PandoroViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **_projects** -> list of the projects of the user
     */
    private val _notes = MutableStateFlow<MutableList<Note>>(mutableListOf())
    val notes: StateFlow<MutableList<Note>> = _notes

    fun refreshValues() {
        execRefreshingRoutine(
            currentContext = Home::class.java,
            routine = {
                requester.sendRequest(
                    request = { requester.getNotesList() },
                    onSuccess = { response ->
                        _notes.value = Note.getInstances(
                            response.getJSONArray(RESPONSE_MESSAGE_KEY)
                        )
                    },
                    onFailure = { showSnack(it) }
                )
            }
        )
    }

    /**
     * Function to execute the request to add a new note
     *
     * @param content: the content of the note
     * @param onSuccess: the action to execute whether the request has been successful
     */
    fun addNote(
        content: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.addNote(
                    contentNote = content
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to mark as to do or mark as done a note
     *
     * @param markAsDone: whether the change note is currently marked as done
     * @param note: the note to manage
     */
    fun manageNote(
        markAsDone: MutableState<Boolean>,
        note: Note
    ) {
        requester.sendRequest(
            request = {
                if(markAsDone.value) {
                    requester.markNoteAsToDo(
                        noteId = note.id
                    )
                } else {
                    requester.markNoteAsDone(
                        noteId = note.id
                    )
                }
            },
            onSuccess = { markAsDone.value = !markAsDone.value},
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to delete a note
     *
     * @param note: the note to delete
     */
    fun deleteNote(
        note: Note
    ) {
        requester.sendRequest(
            request = {
                requester.deleteNote(
                    noteId = note.id
                )
            },
            onSuccess = {},
            onFailure = { showSnack(it) }
        )
    }

}