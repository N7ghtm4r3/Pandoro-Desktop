package layouts.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import layouts.ui.screens.SplashScreen.Companion.user
import toImportFromLibrary.Group
import toImportFromLibrary.Group.GroupMember
import toImportFromLibrary.Group.GroupMember.InvitationStatus.JOINED
import toImportFromLibrary.Group.GroupMember.InvitationStatus.PENDING
import toImportFromLibrary.ProjectUpdate

/**
 * Function to delete an update
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param update: the update to delete
 */
@Wrapper
@Composable
fun DeleteUpdate(show: MutableState<Boolean>, update: ProjectUpdate) {
    AlertDialogContainer(
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
    AlertDialogContainer(
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
@OptIn(ExperimentalMaterialApi::class)
@Wrapper
@Composable
fun LeaveGroup(
    show: MutableState<Boolean>,
    group: Group
) {
    val members = group.members
    val showNextAdmin = remember { mutableStateOf(false) }
    var nextAdmin by remember { mutableStateOf<GroupMember?>(null) }
    for (member in members) {
        if (!member.isLoggedUser(user) && member.invitationStatus == JOINED) {
            nextAdmin = member
            break
        }
    }
    if (showNextAdmin.value) {
        AlertDialog(
            modifier = Modifier.size(width = 400.dp, height = 280.dp),
            shape = RoundedCornerShape(25.dp),
            backgroundColor = BACKGROUND_COLOR,
            onDismissRequest = { show.value = false },
            title = {},
            text = {},
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Choose the next admin",
                        fontSize = 18.sp
                    )
                    Column(
                        modifier = Modifier
                            .padding(
                                top = 20.dp,
                                bottom = 10.dp
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .height(150.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            members.forEach { member ->
                                if (member.invitationStatus == JOINED && !member.isLoggedUser(user)) {
                                    ListItem(
                                        icon = {
                                            // TODO: USE REAL USER ICON member.profilePic
                                            Image(
                                                modifier = Modifier.size(45.dp).clip(CircleShape),
                                                painter = painterResource("pillars-of-creation.jpg"),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop
                                            )
                                        },
                                        text = {
                                            Text(
                                                text = member.completeName,
                                                fontSize = 18.sp
                                            )
                                        },
                                        secondaryText = {
                                            val role = member.role
                                            Text(
                                                text = role.toString(),
                                                color = PRIMARY_COLOR
                                            )
                                        },
                                        trailing = {
                                            RadioButton(
                                                selected = member == nextAdmin,
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = PRIMARY_COLOR
                                                ),
                                                onClick = { nextAdmin = member }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    // TODO: MAKE REQUEST THEN
                                    show.value = false
                                    showNextAdmin.value = false
                                },
                                content = { Text(text = "Dismiss") }
                            )
                            TextButton(
                                onClick = {
                                    leaveGroup(show, group, nextAdmin)
                                    showNextAdmin.value = false
                                },
                                content = { Text(text = "Confirm") }
                            )
                        }
                    }
                }
            }
        )
    }
    AlertDialogContainer(
        show = show,
        title = "Leave the ${group.name} group",
        text = "If you confirm this action, you will be removed from the group and will no longer have access to the" +
                " contents of that group",
        confirmButton = {
            TextButton(
                onClick = {
                    if (group.isUserAdmin(user)) {
                        var pendingMembers = 0
                        members.forEach { member ->
                            if (member.invitationStatus == PENDING)
                                pendingMembers++
                        }
                        if ((members.size - 1 - pendingMembers) != 0) {
                            var hasOtherAdmins = false
                            for (member in members) {
                                if (member.isAdmin && member.invitationStatus == JOINED && !member.isLoggedUser(user)) {
                                    hasOtherAdmins = true
                                    break
                                }
                            }
                            if (!hasOtherAdmins)
                                showNextAdmin.value = true
                            else
                                leaveGroup(show, group)
                        } else
                            leaveGroup(show, group)
                    } else
                        leaveGroup(show, group)
                },
                content = { Text(text = "Confirm") }
            )
        }
    )
}

private fun leaveGroup(
    show: MutableState<Boolean>,
    group: Group,
    nextAdmin: GroupMember? = null
) {
    // TODO: MAKE REQUEST THEN
    show.value = false
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
    AlertDialogContainer(
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
 * Function to publish a new [ProjectUpdate]
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param confirmButton: the confirm button and its action to execute
 */
@Composable
fun PublishUpdate(
    show: MutableState<Boolean>,
    confirmButton: @Composable () -> Unit
) {
    AlertDialogContainer(
        show = show,
        title = "Not all the change notes are done",
        text = "You are publishing an update where not all the change notes are marked as done, do you want anyway " +
                "publish the update?",
        confirmButton = confirmButton
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
private fun AlertDialogContainer(
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