@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.pandoro.layouts.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.pandoro.helpers.showSnack
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showAddGroupPopup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showEditEmailPopup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showEditPasswordPopup
import com.tecknobit.pandoro.layouts.ui.sections.ProfileSection.Companion.HIDE_PASSWORD
import com.tecknobit.pandoro.layouts.ui.sections.ProfileSection.Companion.passwordProperty
import com.tecknobit.pandoro.viewmodels.ProfileSectionViewModel
import com.tecknobit.pandorocore.helpers.*
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isGroupDescriptionValid
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isGroupNameValid
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isInputValid
import kotlinx.coroutines.launch
import layouts.components.PandoroTextField
import layouts.components.popups.CreatePopup
import layouts.components.popups.coroutineScope
import layouts.components.popups.snackbarHostState
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.user
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

/**
 * *viewModel* -> the support view model to manage the requests to the backend
 */
private val viewModel = ProfileSectionViewModel(
    snackbarHostState = snackbarHostState
)

/**
 * Function to show the popup to edit the email of the [User]
 *
 * No-any params required
 */
@Wrapper
@Composable
fun ShowEditEmailPopup() {
    ShowEditProfilePopup(
        show = showEditEmailPopup,
        label = stringResource(Res.string.email),
        item = "email"
    )
}

/**
 * Function to show the popup to edit the password of the [User]
 *
 * No-any params required
 */
@Wrapper
@Composable
fun ShowEditPasswordPopup() {
    ShowEditProfilePopup(
        show = showEditPasswordPopup,
        label = stringResource(Res.string.password),
        item = "password"
    )
}

/**
 * Function to show the popup to edit a profile info of the [User]
 *
 * @param show: the flag whether show the [Popup]
 * @param label: the label of the [PandoroTextField]
 * @param item: the item between **email** and **password**
 */
@Composable
private fun ShowEditProfilePopup(
    show: MutableState<Boolean>,
    label: String,
    item: String
) {
    CreatePopup(
        width = 250.dp,
        height = 200.dp,
        flag = show,
        title = stringResource(Res.string.edit) + " $item",
        columnModifier = Modifier,
        titleSize = 15.sp,
        content = {
            val isEditingEmail = label == "E-mail"
            val profileInfo = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                PandoroTextField(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(55.dp),
                    label = label,
                    isError = !isInputValid(item, profileInfo.value),
                    value = profileInfo
                )
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            if (isInputValid(item, profileInfo.value)) {
                                if (isEditingEmail) {
                                    viewModel.changeEmail(
                                        newEmail = profileInfo.value,
                                        onSuccess = {
                                            localAuthHelper.storeEmail(
                                                email = profileInfo.value,
                                                refreshUser = true
                                            )
                                        }
                                    )
                                } else {
                                    viewModel.changePassword(
                                        newPassword = profileInfo.value,
                                        onSuccess = {
                                            localAuthHelper.storePassword(
                                                password = profileInfo.value,
                                                refreshUser = true
                                            )
                                            if (passwordProperty.value != HIDE_PASSWORD)
                                                passwordProperty.value = user.password
                                        }
                                    )
                                }
                                show.value = false
                            } else {
                                coroutineScope.launch {
                                    showSnack(
                                        scope = coroutineScope,
                                        snackbarHostState = snackbarHostState,
                                        message = getString(Res.string.insert_a_correct) + " $item"
                                    )
                                }
                            }
                        },
                    text = stringResource(Res.string.edit) + " $item",
                    fontSize = 14.sp
                )
            }
        }
    )
}

/**
 * Function to show the popup to create and add a new [Group]
 *
 * No-any params required
 */
@Composable
fun ShowAddGroupPopup() {
    val members = mutableStateListOf("")
    CreatePopup(
        height = 500.dp,
        flag = showAddGroupPopup,
        title = stringResource(Res.string.create_a_new_group),
        columnModifier = Modifier,
        content = {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                viewModel.name = remember { mutableStateOf("") }
                PandoroTextField(
                    modifier = Modifier
                        .height(55.dp)
                        .align(Alignment.CenterHorizontally),
                    label = Res.string.name,
                    isError = !isGroupNameValid(viewModel.name.value),
                    value = viewModel.name
                )
                viewModel.description = remember { mutableStateOf("") }
                PandoroTextField(
                    modifier = Modifier
                        .height(55.dp)
                        .align(Alignment.CenterHorizontally),
                    label = stringResource(Res.string.description),
                    isError = !isGroupDescriptionValid(viewModel.description.value),
                    value = viewModel.description
                )
                showMembersSection(
                    height = 230.dp,
                    members = members
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            viewModel.createGroup(
                                members = members,
                                showCreateGroup = showAddGroupPopup
                            )
                        },
                    text = stringResource(Res.string.create),
                    fontSize = 14.sp
                )
            }
        }
    )
}