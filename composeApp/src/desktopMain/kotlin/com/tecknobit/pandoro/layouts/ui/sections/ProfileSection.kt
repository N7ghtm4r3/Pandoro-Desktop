package com.tecknobit.pandoro.layouts.ui.sections

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.tecknobit.equinox.environment.records.EquinoxUser.PROFILE_PIC_KEY
import com.tecknobit.equinox.inputs.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.pandoro.helpers.Logo
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import com.tecknobit.pandoro.helpers.RED_COLOR
import com.tecknobit.pandoro.helpers.spaceContent
import com.tecknobit.pandoro.layouts.components.ChangeLanguage
import com.tecknobit.pandoro.layouts.components.DeleteGroup
import com.tecknobit.pandoro.layouts.ui.screens.Home
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.changelogs
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showEditPasswordPopup
import com.tecknobit.pandoro.viewmodels.ProfileSectionViewModel
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.users.GroupMember.Role.*
import currentProfilePic
import kotlinx.coroutines.flow.StateFlow
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.user
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*
import pandoro.composeapp.generated.resources.Res.string

/**
 * This is the layout for the profile section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
@OptIn(ExperimentalResourceApi::class)
class ProfileSection : Section() {

    companion object {

        /**
         * **HIDE_PASSWORD** -> the string to show when the password has been hidden
         */
        const val HIDE_PASSWORD = "********"

        /**
         * **groups** -> the list of the groups
         */
        lateinit var groups: StateFlow<List<Group>>

        /**
         * **hideLeaveGroup** -> whether show the leave group button in [GroupSection]
         */
        var hideLeaveGroup: Boolean = false

        /**
         * **passwordProperty** -> the password displayed in the screen
         */
        lateinit var passwordProperty: MutableState<String>
    }

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = ProfileSectionViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * Function to show the content of the [ProfileSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun ShowSection() {
        val myChangelogs = changelogs.collectAsState().value
        val myGroups = groups.collectAsState().value
        hideLeaveGroup = false
        passwordProperty = remember { mutableStateOf(HIDE_PASSWORD) }
        val showChangeLanguage = remember { mutableStateOf(false) }
        ShowSection {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 10.dp,
                        bottom = 20.dp
                    )
                    .fillMaxSize(),
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(string.profile),
                            fontSize = 25.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            ) {
                                Card(
                                    modifier = Modifier
                                        .height(405.dp)
                                        .padding(
                                            top = 10.dp
                                        ),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(20.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally),
                                        ) {
                                            var showFilePicker by remember { mutableStateOf(false) }
                                            Logo(
                                                url = currentProfilePic.value,
                                                size = 130.dp
                                            )
                                            IconButton(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                                    .align(Alignment.TopEnd),
                                                onClick = { showFilePicker = true }
                                            ) {
                                                Icon(
                                                    modifier = Modifier.size(20.dp),
                                                    imageVector = Icons.Default.Edit,
                                                    tint = Color.Black,
                                                    contentDescription = null
                                                )
                                            }
                                            FilePicker(
                                                showFilePicker,
                                                fileExtensions = listOf("jpeg", "jpg", "png")
                                            ) { path ->
                                                if (path != null) {
                                                    showFilePicker = false
                                                    viewModel.changeProfilePic(
                                                        imagePath = path.path,
                                                        onSuccess = { response ->
                                                            localAuthHelper.storeProfilePic(
                                                                response.getString(PROFILE_PIC_KEY),
                                                                true
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        Column(
                                            modifier = Modifier
                                                .padding(
                                                    top = 20.dp
                                                )
                                        ) {
                                            if (user.name != null) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(
                                                            bottom = 5.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = stringResource(string.name) + ":"
                                                    )
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        text = user.name,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier
                                                        .padding(
                                                            bottom = 5.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = stringResource(string.surname) + ":"
                                                    )
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        text = user.surname,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = stringResource(string.email) + ":"
                                                    )
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        text = user.email,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    IconButton(
                                                        modifier = Modifier
                                                            .size(30.dp)
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        onClick = { Home.showEditEmailPopup.value = true }
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier
                                                                .size(20.dp),
                                                            imageVector = Icons.Default.Edit,
                                                            tint = RED_COLOR,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = stringResource(string.password) + ":"
                                                    )
                                                    Text(
                                                        modifier = Modifier.padding(
                                                            start = 5.dp,
                                                            top = if (passwordProperty.value == HIDE_PASSWORD)
                                                                5.dp
                                                            else
                                                                0.dp
                                                        ).onClick { visualizePassword() },
                                                        text = passwordProperty.value,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    IconButton(
                                                        modifier = Modifier
                                                            .size(30.dp)
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        onClick = { showEditPasswordPopup.value = true }
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier
                                                                .size(20.dp),
                                                            imageVector = Icons.Default.Edit,
                                                            tint = RED_COLOR,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = stringResource(string.language) + ":"
                                                    )
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        text = LANGUAGES_SUPPORTED[user.language]!!,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    IconButton(
                                                        modifier = Modifier
                                                            .size(30.dp)
                                                            .padding(
                                                                start = 5.dp
                                                            ),
                                                        onClick = { showChangeLanguage.value = true }
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier
                                                                .size(20.dp),
                                                            imageVector = Icons.Default.Edit,
                                                            tint = RED_COLOR,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                                ChangeLanguage(
                                                    viewModel = viewModel,
                                                    show = showChangeLanguage
                                                )
                                            }
                                            spaceContent(space = 0.dp)
                                            Row(
                                                modifier = Modifier
                                                    .padding(
                                                        top = 5.dp
                                                    ),
                                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                                            ) {
                                                Button(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    shape = RoundedCornerShape(10.dp),
                                                    onClick = { localAuthHelper.logout() }
                                                ) {
                                                    Text(
                                                        text = stringResource(string.logout)
                                                    )
                                                }
                                                Button(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = RED_COLOR,
                                                        contentColor = Color.White
                                                    ),
                                                    onClick = { viewModel.deleteAccount() }
                                                ) {
                                                    Text(
                                                        text = stringResource(string.delete)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .fillMaxWidth()
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(405.dp)
                                        .padding(
                                            top = 10.dp
                                        ),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    )
                                ) {
                                    if (myChangelogs.isEmpty()) {
                                        Column(
                                            modifier = Modifier.
                                                fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = stringResource(string.no_any_changelogs),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        LazyColumn {
                                            items(
                                                items = myChangelogs,
                                                key = { changelog ->
                                                    changelog.id
                                                }
                                            ) { changelog ->
                                                Column(
                                                    modifier = Modifier
                                                        .clickable {
                                                            val project = changelog.project
                                                            if (project != null)
                                                                navToProject(Sections.Profile, project)
                                                            else {
                                                                hideLeaveGroup = true
                                                                navToGroup(Sections.Profile, changelog.group)
                                                            }
                                                        }
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .padding(20.dp)
                                                            .height(50.dp)
                                                    ) {
                                                        Column(
                                                            modifier = Modifier
                                                                .weight(10f)
                                                        ) {
                                                            Row(
                                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                val isRed = changelog.isRed
                                                                if (!isRed) {
                                                                    Badge(
                                                                        modifier = Modifier
                                                                            .padding(
                                                                                end = 5.dp
                                                                            )
                                                                            .size(10.dp)
                                                                            .clickable {
                                                                                viewModel.readChangelog(
                                                                                    changelog = changelog
                                                                                )
                                                                            }
                                                                    ) {
                                                                        Text(text = "")
                                                                    }
                                                                }
                                                                Text(
                                                                    modifier = Modifier
                                                                        .clickable(
                                                                            enabled = !isRed
                                                                        ) {
                                                                            viewModel.readChangelog(
                                                                                changelog = changelog
                                                                            )
                                                                        },
                                                                    text = changelog.title,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                            Text(
                                                                modifier = Modifier
                                                                    .padding(
                                                                        top = 5.dp
                                                                    ),
                                                                text = changelog.content
                                                            )
                                                        }
                                                        Column(
                                                            modifier = Modifier
                                                                .weight(1f),
                                                            horizontalAlignment = Alignment.End,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            IconButton(
                                                                onClick = {
                                                                    viewModel.deleteChangelog(
                                                                        changelog = changelog
                                                                    )
                                                                }
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Delete,
                                                                    tint = RED_COLOR,
                                                                    contentDescription = null
                                                                )
                                                            }
                                                        }
                                                    }
                                                    spaceContent(space = 0.dp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val itemHeight = 95.dp
                        if (myGroups.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        top = 10.dp
                                    )
                            ) {
                                spaceContent()
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            top = 10.dp
                                        ),
                                    text = stringResource(string.groups),
                                    fontSize = 20.sp
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            top = 10.dp
                                        ),
                                    text = stringResource(string.groups_number) + " ${myGroups.size}",
                                    fontSize = 14.sp
                                )
                                spaceContent()
                                var units = myGroups.size
                                if (units < 4)
                                    units = 4
                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .padding(
                                            top = 20.dp
                                        )
                                        .fillMaxWidth()
                                        .height(((units / 4) * (itemHeight.value) * 1).dp),
                                    columns = GridCells.Fixed(4),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    items(
                                        items = myGroups,
                                        key = { group ->
                                            group.id
                                        }
                                    ) { group ->
                                        val isAdmin = group.isUserAdmin(user)
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(itemHeight)
                                                .padding(
                                                    bottom = 20.dp
                                                ),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 2.dp
                                            ),
                                            onClick = { navToGroup(Sections.Profile, group) }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column (
                                                    modifier = Modifier
                                                        .weight(5f)
                                                        .padding(
                                                            15.dp
                                                        )
                                                ) {
                                                    Text(
                                                        text = group.name,
                                                        fontSize = 16.sp
                                                    )
                                                    Row {
                                                        Text(
                                                            text = stringResource(string.role) + " ",
                                                            fontSize = 16.sp
                                                        )
                                                        Text(
                                                            text = if (group.isUserMaintainer(user)) {
                                                                if (isAdmin)
                                                                    ADMIN.toString()
                                                                else
                                                                    MAINTAINER.toString()
                                                            } else
                                                                DEVELOPER.toString(),
                                                            color = if (isAdmin)
                                                                RED_COLOR
                                                            else
                                                                PRIMARY_COLOR,
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                }
                                                if (isAdmin) {
                                                    val showDeleteDialog = mutableStateOf(false)
                                                    DeleteGroup(
                                                        show = showDeleteDialog,
                                                        group = group,
                                                        onDismissRequest = {
                                                            showDeleteDialog.value = false
                                                            Home.viewModel.restartRefresher()
                                                        }
                                                    )
                                                    Column (
                                                        modifier = Modifier
                                                            .weight(1f),
                                                        horizontalAlignment = Alignment.End
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                Home.viewModel.suspendRefresher()
                                                                showDeleteDialog.value = true
                                                            }
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier
                                                                    .size(20.dp),
                                                                imageVector = Icons.Default.Delete,
                                                                tint = RED_COLOR,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                }
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxHeight(),
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(PRIMARY_COLOR)
                                                            .fillMaxHeight()
                                                            .width(8.dp),
                                                        content = { Text("") }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to show or hide the [User]'s password
     *
     * No-any params required
     * @return the password to visualize as [String]
     */
    private fun visualizePassword() {
        passwordProperty.value = if (!passwordProperty.value.contains(HIDE_PASSWORD))
            HIDE_PASSWORD
        else
            user.password
    }

}