@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package com.tecknobit.pandoro.layouts.components

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
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.Logo
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.activeScreen
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentGroup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentProject
import com.tecknobit.pandoro.layouts.ui.sections.Section
import com.tecknobit.pandoro.layouts.ui.sections.Section.Companion.navBack
import com.tecknobit.pandoro.viewmodels.GroupSectionViewModel
import com.tecknobit.pandoro.viewmodels.ProfileSectionViewModel
import com.tecknobit.pandoro.viewmodels.ProjectSectionViewModel
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.ProjectUpdate
import com.tecknobit.pandorocore.records.users.GroupMember
import com.tecknobit.pandorocore.records.users.GroupMember.InvitationStatus.JOINED
import com.tecknobit.pandorocore.records.users.GroupMember.InvitationStatus.PENDING
import layouts.components.popups.snackbarHostState
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.user
import navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

/**
 * *viewModel* -> the support view model to manage the requests to the backend
 */
private val viewModel = ProjectSectionViewModel(
    initialProject = currentProject.value,
    snackbarHostState = snackbarHostState
)

/**
 * *groupViewModel* -> the support view model to manage the requests to the backend
 */
private val groupViewModel = GroupSectionViewModel(
    initialGroup = currentGroup.value,
    snackbarHostState = Section.snackbarHostState
)

/**
 * Function to delete an update
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param update: the update to delete
 */
@OptIn(ExperimentalResourceApi::class)
@Wrapper
@Composable
fun DeleteUpdate(
    show: MutableState<Boolean>,
    update: ProjectUpdate
) {
    AlertDialogContainer(
        show = show,
        title = stringResource(Res.string.delete_update) + " ${update.targetVersion}",
        text = stringResource(Res.string.delete_update_text),
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteUpdate(
                        update = update
                    )
                    show.value = false
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
    onDismissRequest: () -> Unit,
    group: Group,
    memberId: String
) {
    AlertDialogContainer(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(Res.string.remove_the_user_from) + " ${group.name}",
        text = stringResource(Res.string.remove_user_text),
        confirmButton = {
            TextButton(
                onClick = {
                    groupViewModel.removeMember(
                        show = show,
                        group = group,
                        memberId = memberId,
                        onSuccess = onDismissRequest
                    )
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
 * @param onDismissRequest: the action to execute when the dialog has been dismissed
 * @param group: the group to leave
 */
@Wrapper
@Composable
fun LeaveGroup(
    show: MutableState<Boolean>,
    onDismissRequest: () -> Unit,
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
            onDismissRequest = onDismissRequest,
            shape = RoundedCornerShape(25.dp),
            backgroundColor = BACKGROUND_COLOR,
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
        onDismissRequest = onDismissRequest,
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
    groupViewModel.leaveFromGroup(
        group = group,
        nextAdmin = nextAdmin,
        onSuccess = {
            navBack()
            activeScreen.value = Section.Sections.Projects
        }
    )
    show.value = false
}

/**
 * Function to delete a [Group]
 *
 * @param show: the flaw whether show the [AlertDialog]
 * @param onDismissRequest: the action to execute when the dialog has been dismissed
 * @param group: the group to delete
 */
@Wrapper
@Composable
fun DeleteGroup(
    show: MutableState<Boolean>,
    group: Group,
    onDismissRequest: () -> Unit,
) {
    AlertDialogContainer(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(Res.string.delete_group),
        text = stringResource(Res.string.delete_group_text),
        confirmButton = {
            TextButton(
                onClick = {
                    groupViewModel.deleteGroup(
                        group = group,
                        onSuccess = onDismissRequest
                    )
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
 * @param viewModel: the support view model to manage the requests to the backend
 * @param show: whether show the dialog or not
 */
@Composable
fun ChangeLanguage(
    viewModel: ProfileSectionViewModel,
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
                        viewModel.changeLanguage(
                            newLanguage = selectedLanguage,
                            onSuccess = {
                                localAuthHelper.storeLanguage(
                                    language = selectedLanguage,
                                    refreshUser = true
                                )
                                navigator.navigate(splashScreen.name)
                            }
                        )
                        show.value = false
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
 * @param onDismissRequest: the action to execute when the dialog has been dismissed
 * @param dismissButton: the dismiss button and its action of the [AlertDialog]
 * @param confirmButton: the confirm button and its action of the [AlertDialog]
 */
@Composable
private fun AlertDialogContainer(
    show: MutableState<Boolean>,
    title: String,
    text: String,
    onDismissRequest: () -> Unit = { show.value = false },
    dismissButton: @Composable (() -> Unit)? = {
        TextButton(
            onClick = onDismissRequest,
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
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),

            shape = RoundedCornerShape(25.dp),
            containerColor = BACKGROUND_COLOR,
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = { Text(text = text) },
            dismissButton = dismissButton,
            confirmButton = confirmButton
        )
    }
}