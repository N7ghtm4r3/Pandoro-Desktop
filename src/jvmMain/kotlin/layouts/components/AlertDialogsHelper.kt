package layouts.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.apimanager.annotations.Wrapper
import helpers.BACKGROUND_COLOR
import toImportFromLibrary.Group
import toImportFromLibrary.Update

/**
 * Function to delete an update
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param update: the update to delete
 */
@Wrapper
@Composable
fun DeleteUpdate(show: MutableState<Boolean>, update: Update) {
    alertDialogContainer(
        show = show,
        title = "Delete update ${update.targetVersion}",
        text = "If you confirm this action the update and its all "
                + "information will be deleted and no more recoverable, "
                + "confirm?",
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: MAKE REQUEST THEN
                    show.value = false
                },
                content = { Text(text = "Confirm") }
            )
        }
    )
}

/**
 * Function to remove a user from a [Group]
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param group: the group from remove the member
 * @param memberId: the identifier of the member to remove
 */
@Wrapper
@Composable
fun RemoveUser(
    show: MutableState<Boolean>,
    group: Group,
    memberId: String
) {
    alertDialogContainer(
        show = show,
        title = "Remove the user from ${group.name}",
        text = "If you confirm this action, the user will be removed from the group and will no longer have access to the" +
                " contents of that group",
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: MAKE REQUEST THEN
                    show.value = false
                },
                content = { Text(text = "Confirm") }
            )
        }
    )
}

/**
 * Function to leave a [Group]
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param group: the group to leave
 */
@Wrapper
@Composable
fun LeaveGroup(
    show: MutableState<Boolean>,
    group: Group
) {
    alertDialogContainer(
        show = show,
        title = "Leave the ${group.name} group",
        text = "If you confirm this action, you will be removed from the group and will no longer have access to the" +
                " contents of that group",
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: MAKE REQUEST THEN
                    show.value = false
                },
                content = { Text(text = "Confirm") }
            )
        }
    )
}

/**
 * Function to delete a [Group]
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param group: the group to delete
 */
@Wrapper
@Composable
fun DeleteGroup(
    show: MutableState<Boolean>,
    group: Group
) {
    alertDialogContainer(
        show = show,
        title = "Delete the ${group.name} group",
        text = "If you confirm this action, the group and all its information will be deleted and no more recoverable",
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: MAKE REQUEST THEN
                    show.value = false
                },
                content = { Text(text = "Confirm") }
            )
        }
    )
}

/**
 * Function to create an [AlertDialog]
 *
 * @param show: the flag whether show the [AlertDialog]
 * @param title: the title of the [AlertDialog]
 * @param text: the text of the [AlertDialog]
 * @param dismissButton: the dismiss button and its action of the [AlertDialog]
 * @param confirmButton: the confirm button and its action of the [AlertDialog]
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun alertDialogContainer(
    show: MutableState<Boolean>,
    title: String,
    text: String,
    dismissButton: @Composable (() -> Unit)? = {
        TextButton(
            onClick = { show.value = false },
            content = { Text(text = "Dismiss") }
        )
    },
    confirmButton: @Composable () -> Unit
) {
    if (show.value) {
        AlertDialog(
            modifier = Modifier.size(width = 400.dp, height = 200.dp),
            shape = RoundedCornerShape(25.dp),
            backgroundColor = BACKGROUND_COLOR,
            onDismissRequest = { show.value = false },
            title = { Text(text = title) },
            text = { Text(text = text) },
            dismissButton = dismissButton,
            confirmButton = confirmButton
        )
    }
}