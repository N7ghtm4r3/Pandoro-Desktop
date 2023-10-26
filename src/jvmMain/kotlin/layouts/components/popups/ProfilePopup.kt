package layouts.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import helpers.showSnack
import layouts.components.PandoroTextField
import layouts.ui.screens.Connect.Companion.isPasswordValid
import layouts.ui.screens.Home.Companion.showAddGroupPopup
import layouts.ui.screens.Home.Companion.showEditEmailPopup
import layouts.ui.screens.Home.Companion.showEditPasswordPopup
import toImportFromLibrary.Group.GROUP_DESCRIPTION_MAX_LENGTH
import toImportFromLibrary.Group.GROUP_NAME_MAX_LENGTH

/**
 * Function to show the popup to edit the email of the [user]
 *
 * No-any params required
 */
@Wrapper
@Composable
fun showEditEmailPopup() {
    showEditProfilePopup(
        show = showEditEmailPopup,
        label = "Email",
        item = "email"
    )
}

/**
 * Function to show the popup to edit the password of the [user]
 *
 * No-any params required
 */
@Wrapper
@Composable
fun showEditPasswordPopup() {
    showEditProfilePopup(
        show = showEditPasswordPopup,
        label = "Password",
        item = "password"
    )
}

/**
 * Function to show the popup to edit a profile info of the [user]
 *
 * @param show: the flag whether show the [Popup]
 * @param label: the label of the [PandoroTextField]
 * @param item: the item between **email** and **password**
 */
@Composable
private fun showEditProfilePopup(
    show: MutableState<Boolean>,
    label: String,
    item: String
) {
    createPopup(
        width = 250.dp,
        height = 200.dp,
        flag = show,
        title = "Edit $item",
        columnModifier = Modifier,
        titleSize = 15.sp,
        content = {
            var profileInfo by remember { mutableStateOf("") }
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                PandoroTextField(
                    modifier = Modifier.padding(10.dp).height(55.dp),
                    label = label,
                    isError = !isInputValid(item, profileInfo),
                    onValueChange = { profileInfo = it },
                    value = profileInfo
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable {
                        if (isInputValid(item, profileInfo)) {
                            // TODO: MAKE REQUEST THEN
                            show.value = false
                        } else
                            showSnack(coroutineScope, scaffoldState, "Insert a correct $item")
                    },
                    text = "Edit $item",
                    fontSize = 14.sp
                )
            }
        }
    )
}

/**
 * Function to check the validity of an input
 *
 * @param item: the item between **email** and **password**
 * @param input: input to check
 * @return whether the input is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isInputValid(item: String, input: String): Boolean {
    return if (item == "email")
        isEmailValid(input)
    else
        isPasswordValid(input)
}

/**
 * Function to show the popup to create and add a new [Group]
 *
 * No-any params required
 */
@Composable
fun showAddGroupPopup() {
    val members = mutableStateListOf("")
    createPopup(
        height = 400.dp,
        flag = showAddGroupPopup,
        title = "Create a new group",
        columnModifier = Modifier,
        content = {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                var groupName by remember { mutableStateOf("") }
                PandoroTextField(
                    modifier = Modifier.height(55.dp).align(Alignment.CenterHorizontally),
                    label = "Name",
                    isError = !isGroupNameValid(groupName),
                    onValueChange = { groupName = it },
                    value = groupName
                )
                var groupDescription by remember { mutableStateOf("") }
                PandoroTextField(
                    modifier = Modifier.height(55.dp).align(Alignment.CenterHorizontally),
                    label = "Description",
                    isError = !isGroupDescriptionValid(groupDescription),
                    onValueChange = { groupDescription = it },
                    value = groupDescription
                )
                showMembersSection(
                    height = 230.dp,
                    members = members
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable {
                        if (isGroupNameValid(groupName)) {
                            if (isGroupDescriptionValid(groupDescription)) {
                                if (checkMembersValidity(members)) {
                                    // TODO: MAKE REQUEST THEN
                                    showAddGroupPopup.value = false
                                }
                            } else
                                showSnack(coroutineScope, scaffoldState, "You must insert a correct group description")
                        } else
                            showSnack(coroutineScope, scaffoldState, "You must insert a correct group name")
                    },
                    text = "Create group",
                    fontSize = 14.sp
                )
            }
        }
    )
}

/**
 * Function to check the validity of a group name
 *
 * @param groupName: group name to check
 * @return whether the group name is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isGroupNameValid(groupName: String): Boolean {
    return groupName.length in 1..GROUP_NAME_MAX_LENGTH
}

/**
 * Function to check the validity of a group description
 *
 * @param groupDescription: group description to check
 * @return whether the group description is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isGroupDescriptionValid(groupDescription: String): Boolean {
    return groupDescription.length in 1..GROUP_DESCRIPTION_MAX_LENGTH
}