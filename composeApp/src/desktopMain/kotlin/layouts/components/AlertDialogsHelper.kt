@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package layouts.components

import Routes.splashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.ProjectUpdate
import com.tecknobit.pandorocore.records.users.GroupMember
import com.tecknobit.pandorocore.records.users.GroupMember.InvitationStatus.JOINED
import com.tecknobit.pandorocore.records.users.GroupMember.InvitationStatus.PENDING
import helpers.BACKGROUND_COLOR
import helpers.Logo
import helpers.PRIMARY_COLOR
import helpers.showSnack
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.Section
import layouts.ui.sections.Section.Companion.navBack
import layouts.ui.sections.Section.Companion.sectionCoroutineScope
import layouts.ui.sections.Section.Companion.snackbarHostState
import navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

/**
 * Function to delete an update
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param update: the update to delete
 */
@OptIn(ExperimentalResourceApi::class)
@Wrapper
@Composable
fun DeleteUpdate(show: MutableState<Boolean>, update: ProjectUpdate) {
    AlertDialogContainer(
        show = show,
        title = stringResource(Res.string.delete_update) + " ${update.targetVersion}",
        text = stringResource(Res.string.delete_update_text),
        confirmButton = {
            TextButton(
                onClick = {
                    /*requester!!.execDeleteUpdate(currentProject.value.id, update.id)
                    show.value = false
                    if (!requester!!.successResponse())
                        showSnack(sectionCoroutineScope, snackbarHostState, requester!!.errorMessage())*/
                },
                content = {
                    Text(
                        text = stringResource(Res.string.confirm)
                    )
                }
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
        title = stringResource(Res.string.remove_the_user_from) + " ${group.name}",
        text = stringResource(Res.string.remove_user_text),
        confirmButton = {
            TextButton(
                onClick = {
                    /*requester!!.execRemoveMember(group.id, memberId)
                    show.value = false
                    if (!requester!!.successResponse())
                        showSnack(sectionCoroutineScope, snackbarHostState, requester!!.errorMessage())*/
                },
                content = {
                    Text(
                        text = stringResource(Res.string.confirm)
                    )
                }
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
        androidx.compose.material.AlertDialog(
            modifier = Modifier
                .size(
                    width = 400.dp,
                    height = 280.dp
                ),
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
                        text = stringResource(Res.string.choose_the_next_admin),
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
                                        leadingContent = {
                                            Logo(
                                                url = member.profilePic
                                            )
                                        },
                                        headlineContent = {
                                            Text(
                                                text = member.completeName,
                                                fontSize = 18.sp
                                            )
                                        },
                                        supportingContent = {
                                            val role = member.role
                                            Text(
                                                text = role.toString(),
                                                color = PRIMARY_COLOR
                                            )
                                        },
                                        trailingContent = {
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
                                    show.value = false
                                    showNextAdmin.value = false
                                },
                                content = {
                                    Text(
                                        text = stringResource(Res.string.dismiss)
                                    )
                                }
                            )
                            TextButton(
                                onClick = {
                                    leaveGroup(show, group, nextAdmin)
                                    showNextAdmin.value = false
                                },
                                content = {
                                    Text(
                                        text = stringResource(Res.string.confirm)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
    AlertDialogContainer(
        show = show,
        title = stringResource(Res.string.leave_group),
        text = stringResource(Res.string.leave_group_text),
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
                content = {
                    Text(
                        text = stringResource(Res.string.confirm)
                    )
                }
            )
        }
    )
}

/**
 * Function to execute the request to leave a [Group]
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param group: the group to leave
 * @param nextAdmin: the next admin chosen whan a ADMIN leaves
 */
private fun leaveGroup(
    show: MutableState<Boolean>,
    group: Group,
    nextAdmin: GroupMember? = null
) {
    var nextAdminId: String? = null
    if (nextAdmin != null)
        nextAdminId = nextAdmin.id
    /*requester!!.execLeaveGroup(group.id, nextAdminId)
    show.value = false
    if (requester!!.successResponse()) {
        navBack()
        activeScreen.value = Section.Sections.Projects
    } else
        showSnack(sectionCoroutineScope, snackbarHostState, requester!!.errorMessage())*/
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
        title = stringResource(Res.string.delete_group),
        text = stringResource(Res.string.delete_group_text),
        confirmButton = {
            TextButton(
                onClick = {
                    /*requester!!.execDeleteGroup(group.id)
                    show.value = false
                    if (!requester!!.successResponse())
                        showSnack(sectionCoroutineScope, snackbarHostState, requester!!.errorMessage())*/
                },
                content = {
                    Text(
                        text = stringResource(Res.string.confirm)
                    )
                }
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
        title = stringResource(Res.string.not_all_the_change_notes_are_done),
        text = stringResource(Res.string.check_change_notes_message),
        confirmButton = confirmButton
    )
}

/**
 * Function to show the dialog to change the language of the user
 *
 * @param show: whether show the dialog or not
 */
@Composable
fun ChangeLanguage(
    show: MutableState<Boolean>
) {
    var selectedLanguage by remember { mutableStateOf(user.language) }
    if(show.value) {
        AlertDialog(
            onDismissRequest = { show.value = false },
            title = {
                Text(
                    text = stringResource(Res.string.change_language)
                )
            },
            text = {
                LazyColumn {
                    items(LANGUAGES_SUPPORTED.keys.toList()) { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == language,
                                onClick = { selectedLanguage = language }
                            )
                            Text(
                                text = LANGUAGES_SUPPORTED[language]!!
                            )
                        }
                        HorizontalDivider()
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        show.value = false
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.dismiss)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        /*requester!!.execChangeLanguage(
                            newLanguage = selectedLanguage
                        )
                        if(requester!!.successResponse()) {
                            localAuthHelper.storeLanguage(
                                language = selectedLanguage,
                                refreshUser = true
                            )
                            navigator.navigate(splashScreen.name)
                        }
                        show.value = false*/
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.confirm)
                    )
                }
            }
        )
    } else
        selectedLanguage = user.language
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
@Composable
private fun AlertDialogContainer(
    show: MutableState<Boolean>,
    title: String,
    text: String,
    dismissButton: @Composable (() -> Unit)? = {
        TextButton(
            onClick = { show.value = false },
            content = {
                Text(
                    text = stringResource(Res.string.dismiss)
                )
            }
        )
    },
    confirmButton: @Composable () -> Unit
) {
    if (show.value) {
        AlertDialog(
            shape = RoundedCornerShape(25.dp),
            containerColor = BACKGROUND_COLOR,
            onDismissRequest = { show.value = false },
            title = { Text(text = title) },
            text = { Text(text = text) },
            dismissButton = dismissButton,
            confirmButton = confirmButton
        )
    }
}