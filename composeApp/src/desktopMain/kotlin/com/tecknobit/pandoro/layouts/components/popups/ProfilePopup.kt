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
import com.tecknobit.pandoro.viewmodels.ProfileSectionViewModel
import com.tecknobit.pandorocore.helpers.*
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isInputValid
import kotlinx.coroutines.launch
import layouts.components.PandoroTextField
import layouts.components.popups.CreatePopup
import layouts.components.popups.coroutineScope
import layouts.components.popups.snackbarHostState
import layouts.ui.screens.Home.Companion.showAddGroupPopup
import layouts.ui.screens.Home.Companion.showEditEmailPopup
import layouts.ui.screens.Home.Companion.showEditPasswordPopup
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.ProfileSection.Companion.HIDE_PASSWORD
import layouts.ui.sections.ProfileSection.Companion.passwordProperty
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

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
            val isEditingEmail = label == "Email"
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
            ) {/*
                var groupName by remember { mutableStateOf("") }
                PandoroTextField(
                    modifier = Modifier
                        .height(55.dp)
                        .align(Alignment.CenterHorizontally),
                    label = Res.string.name,
                    isError = !isGroupNameValid(groupName),
                    value = groupName
                )
                var groupDescription by remember { mutableStateOf("") }
                PandoroTextField(
                    modifier = Modifier
                        .height(55.dp)
                        .align(Alignment.CenterHorizontally),
                    label = stringResource(Res.string.description),
                    isError = !isGroupDescriptionValid(groupDescription),
                    value = groupDescription
                )*/
                showMembersSection(
                    height = 230.dp,
                    members = members
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            /*if (isGroupNameValid(groupName)) {
                                if (isGroupDescriptionValid(groupDescription)) {
                                    if (checkMembersValidity(members)) {
                                        /*requester!!.execCreateGroup(groupName, groupDescription, members)
                                        if (requester!!.successResponse())
                                            ShowAddGroupPopup.value = false
                                        else
                                            showSnack(coroutineScope, snackbarHostState, requester!!.errorMessage())*/
                                    }
                                } else
                                    showSnack(coroutineScope, snackbarHostState, Res.string.you_must_insert_a_correct_group_description)
                            } else
                                showSnack(coroutineScope, snackbarHostState, Res.string.you_must_insert_a_correct_group_name)
                        */},
                    text = stringResource(Res.string.create),
                    fontSize = 14.sp
                )
            }
        }
    )
}