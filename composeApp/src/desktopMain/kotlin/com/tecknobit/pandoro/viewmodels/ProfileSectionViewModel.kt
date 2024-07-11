package com.tecknobit.pandoro.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.checkMembersValidity
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isGroupDescriptionValid
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isGroupNameValid
import com.tecknobit.pandorocore.records.Changelog
import com.tecknobit.pandorocore.records.Group
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pandoro.composeapp.generated.resources.*
import java.io.File

/**
 * The **ProfileSectionViewModel** class is the support class used by the [ProfileSection]
 * to manage the account operations of the user
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see PandoroViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
@OptIn(ExperimentalResourceApi::class)
class ProfileSectionViewModel(
    override var snackbarHostState: SnackbarHostState?
): PandoroViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **name** -> the name of the group
     */
    lateinit var name: MutableState<String>

    /**
     * **description** -> the description of the group
     */
    lateinit var description: MutableState<String>

    /**
     * Function to execute the profile pic change
     *
     * @param imagePath: the path of the image to set
     * @param onSuccess: the action to execute whether the request has been successful
     */
    fun changeProfilePic(
        imagePath: String,
        onSuccess: (JsonHelper) -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.changeProfilePic(
                    profilePic = File(imagePath)
                )
            },
            onSuccess = { response ->
                onSuccess.invoke(response)
            },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the email change
     *
     * @param newEmail: the new email of the user
     * @param onSuccess: the action to execute whether the request has been successful
     */
    fun changeEmail(
        newEmail: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.changeEmail(
                    newEmail = newEmail
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it ) }
        )
    }

    /**
     * Function to execute the password change
     *
     * @param newPassword: the new password of the user
     * @param onSuccess: the action to execute whether the request has been successful
     */
    fun changePassword(
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.changePassword(
                    newPassword = newPassword
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the language change
     *
     * @param newLanguage: the new language of the user
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun changeLanguage(
        newLanguage: String,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.changeLanguage(
                    newLanguage = newLanguage
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the account deletion
     *
     * No-any params required
     */
    fun deleteAccount() {
        requester.sendRequest(
            request = {
                requester.deleteAccount()
            },
            onSuccess = { localAuthHelper.logout() },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to decline a group invitation
     *
     * @param group: the group where the user has been invited
     * @param changelog: the related changelog of the invitation
     */
    fun declineInvitation(
        group: Group,
        changelog: Changelog
    ) {
        requester.sendRequest(
            request = {
                requester.declineInvitation(
                    groupId = group.id,
                    changelogId = changelog.id
                )
            },
            onSuccess = { },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to accept a group invitation
     *
     * @param group: the group where the user has been invited
     * @param changelog: the related changelog of the invitation
     */
    fun acceptInvitation(
        group: Group,
        changelog: Changelog
    ) {
        requester.sendRequest(
            request = {
                requester.acceptInvitation(
                    groupId = group.id,
                    changelogId = changelog.id
                )
            },
            onSuccess = { },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to read a changelog
     *
     * @param changelog: the changelog to read
     */
    fun readChangelog(
        changelog: Changelog
    ) {
        requester.sendRequest(
            request = {
                requester.readChangelog(
                    changelogId = changelog.id,
                )
            },
            onSuccess = { },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to delete a changelog
     *
     * @param changelog: the changelog to delete
     */
    fun deleteChangelog(
        changelog: Changelog
    ) {
        requester.sendRequest(
            request = {
                requester.deleteChangelog(
                    groupId = if (changelog.group != null)
                        changelog.group.id
                    else
                        null,
                    changelogId = changelog.id,
                )
            },
            onSuccess = { },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to create a new group
     *
     * @param members: the list of the members to add to the group
     * @param showCreateGroup: whether show the dialog for the creation of the group
     */
    fun createGroup(
        members: List<String>,
        showCreateGroup: MutableState<Boolean>
    ) {
        if (isGroupNameValid(name.value)) {
            if (isGroupDescriptionValid(description.value)) {
                if (members.isNotEmpty()) {
                    if (checkMembersValidity(members)) {
                        requester.sendRequest(
                            request = {
                                requester.createGroup(
                                    name = name.value,
                                    groupDescription = description.value,
                                    members = members
                                )
                            },
                            onSuccess = {
                                showCreateGroup.value = false
                                name.value = ""
                                description.value = ""
                            },
                            onFailure = { showSnack(it) }
                        )
                    } else
                        showSnack(Res.string.you_must_insert_a_correct_members_list)
                } else
                    showSnack(Res.string.you_must_insert_one_member_at_least)
            } else
                showSnack(Res.string.you_must_insert_a_correct_group_description)
        } else
            showSnack(Res.string.you_must_insert_a_correct_group_name)
    }

}