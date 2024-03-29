package layouts.ui.sections

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.pandoro.records.Changelog
import com.tecknobit.pandoro.records.Changelog.ChangelogEvent.INVITED_GROUP
import com.tecknobit.pandoro.records.Group
import com.tecknobit.pandoro.records.users.GroupMember.Role.*
import com.tecknobit.pandoro.services.UsersHelper.PROFILE_PIC_KEY
import helpers.*
import layouts.components.DeleteGroup
import layouts.ui.screens.Home
import layouts.ui.screens.Home.Companion.changelogs
import layouts.ui.screens.Home.Companion.showEditPasswordPopup
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.requester
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.screens.SplashScreen.Companion.userProfilePic
import java.io.File

/**
 * This is the layout for the profile section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class ProfileSection : Section() {

    companion object {

        /**
         * **HIDE_PASSWORD** -> the string to show when the password has been hidden
         */
        const val HIDE_PASSWORD = "********"

        /**
         * **groups** -> the list of the groups
         */
        val groups: SnapshotStateList<Group> = mutableStateListOf()

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
     * Function to show the content of the [ProfileSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun showSection() {
        hideLeaveGroup = false
        passwordProperty = remember { mutableStateOf(HIDE_PASSWORD) }
        showSection {
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
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Profile",
                            fontSize = 25.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f).fillMaxSize()
                            ) {
                                Card(
                                    modifier = Modifier.height(405.dp).padding(top = 10.dp),
                                    backgroundColor = Color.White,
                                    shape = RoundedCornerShape(15.dp),
                                    elevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                        ) {
                                            var showFilePicker by remember { mutableStateOf(false) }
                                            if (userProfilePic.value != null) {
                                                Image(
                                                    modifier = Modifier
                                                        .size(150.dp)
                                                        .clip(CircleShape),
                                                    bitmap = userProfilePic.value!!,
                                                    contentDescription = null
                                                )
                                            }
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
                                                    val response = requester!!.execChangeProfilePic(File(path.path))
                                                    if (requester!!.successResponse()) {
                                                        localAuthHelper.storeProfilePic(
                                                            JsonHelper(response)
                                                                .getString(PROFILE_PIC_KEY)
                                                        )
                                                    } else
                                                        showSnack(requester!!.errorMessage())
                                                }
                                            }
                                        }
                                        Column(
                                            modifier = Modifier.padding(top = 20.dp)
                                        ) {
                                            if (user.name != null) {
                                                Row(
                                                    modifier = Modifier.padding(bottom = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Name:"
                                                    )
                                                    Text(
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        text = user.name,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.padding(bottom = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Surname:"
                                                    )
                                                    Text(
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        text = user.surname,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Email:"
                                                    )
                                                    Text(
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        text = user.email,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    IconButton(
                                                        modifier = Modifier.size(30.dp).padding(start = 5.dp),
                                                        onClick = { Home.showEditEmailPopup.value = true }
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.size(20.dp),
                                                            imageVector = Icons.Default.Edit,
                                                            tint = RED_COLOR,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Password:"
                                                    )
                                                    Text(
                                                        modifier = Modifier.padding(
                                                            start = 5.dp,
                                                            top = if (passwordProperty.value == HIDE_PASSWORD) 5.dp else 0.dp
                                                        ).onClick { visualizePassword() },
                                                        text = passwordProperty.value,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    IconButton(
                                                        modifier = Modifier.size(30.dp).padding(start = 5.dp),
                                                        onClick = { showEditPasswordPopup.value = true }
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.size(20.dp),
                                                            imageVector = Icons.Default.Edit,
                                                            tint = RED_COLOR,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            }
                                            spaceContent(space = 0.dp)
                                            Row(
                                                modifier = Modifier.padding(top = 20.dp),
                                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                                            ) {
                                                Button(
                                                    modifier = Modifier.weight(1f),
                                                    onClick = { localAuthHelper.logout() }
                                                ) {
                                                    Text(
                                                        text = "Logout"
                                                    )
                                                }
                                                Button(
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        backgroundColor = RED_COLOR,
                                                        contentColor = Color.White
                                                    ),
                                                    onClick = {
                                                        requester!!.execDeleteAccount()
                                                        if (requester!!.successResponse())
                                                            localAuthHelper.logout()
                                                        else
                                                            showSnack(requester!!.errorMessage())
                                                    }
                                                ) {
                                                    Text(
                                                        text = "Delete"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1.5f).fillMaxWidth()
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().height(405.dp).padding(top = 10.dp),
                                    backgroundColor = Color.White,
                                    shape = RoundedCornerShape(15.dp),
                                    elevation = 2.dp
                                ) {
                                    if (changelogs.isEmpty()) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "No-any changelogs",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        LazyColumn {
                                            items(
                                                items = changelogs,
                                                key = { changelog ->
                                                    changelog.id
                                                }
                                            ) { changelog ->
                                                Column(
                                                    modifier = Modifier.clickable {
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
                                                        modifier = Modifier.padding(20.dp).height(50.dp)
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.weight(10f)
                                                        ) {
                                                            Row(
                                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                val isRed = changelog.isRed
                                                                if (!isRed) {
                                                                    Badge(
                                                                        modifier = Modifier
                                                                            .padding(end = 5.dp)
                                                                            .size(10.dp)
                                                                            .clickable { readChangelog(changelog) }
                                                                    ) {
                                                                        Text(text = "")
                                                                    }
                                                                }
                                                                Text(
                                                                    modifier =
                                                                    if (!isRed) {
                                                                        Modifier.clickable {
                                                                            readChangelog(changelog)
                                                                        }
                                                                    } else
                                                                        Modifier,
                                                                    text = changelog.title,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                            Text(
                                                                modifier = Modifier.padding(top = 5.dp),
                                                                text = changelog.content
                                                            )
                                                        }
                                                        Column(
                                                            modifier = Modifier.weight(1f),
                                                            horizontalAlignment = Alignment.End,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            IconButton(
                                                                onClick = {
                                                                    var groupId: String? = null
                                                                    if (changelog.changelogEvent == INVITED_GROUP) {
                                                                        val changelogGroup = changelog.group
                                                                        if (changelogGroup != null)
                                                                            groupId = changelogGroup.id
                                                                    }
                                                                    requester!!.execDeleteChangelog(
                                                                        changelog.id,
                                                                        groupId
                                                                    )
                                                                    if (!requester!!.successResponse())
                                                                        showSnack(requester!!.errorMessage())
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
                        if (groups.isNotEmpty()) {
                            Column(
                                modifier = Modifier.padding(top = 10.dp)
                            ) {
                                spaceContent()
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = "Groups",
                                    fontSize = 20.sp
                                )
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = "Groups number: ${groups.size}",
                                    fontSize = 14.sp
                                )
                                spaceContent()
                                var units = groups.size
                                if (units < 4)
                                    units = 4
                                LazyVerticalGrid(
                                    modifier = Modifier.padding(top = 20.dp)
                                        .fillMaxWidth()
                                        .height(((units / 4) * (itemHeight.value) * 1).dp),
                                    columns = GridCells.Fixed(5),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    items(
                                        items = groups,
                                        key = { group ->
                                            group.id
                                        }
                                    ) { group ->
                                        val isAdmin = group.isUserAdmin(user)
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(itemHeight)
                                                .padding(bottom = 20.dp),
                                            backgroundColor = Color.White,
                                            shape = RoundedCornerShape(10.dp),
                                            elevation = 2.dp,
                                            onClick = { navToGroup(Sections.Profile, group) }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize().padding(15.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    modifier = Modifier.weight(if (isAdmin) 2f else 1f),
                                                    text = group.name,
                                                    fontSize = 18.sp
                                                )
                                                Text(
                                                    modifier = Modifier.weight(1f),
                                                    text =
                                                    if (group.isUserMaintainer(user)) {
                                                        if (isAdmin)
                                                            ADMIN.toString()
                                                        else
                                                            MAINTAINER.toString()
                                                    } else
                                                        DEVELOPER.toString(),
                                                    color = if (isAdmin) RED_COLOR else PRIMARY_COLOR,
                                                    fontSize = 15.sp
                                                )
                                                if (isAdmin) {
                                                    val showDeleteDialog = mutableStateOf(false)
                                                    IconButton(
                                                        modifier = Modifier.weight(1f),
                                                        onClick = { showDeleteDialog.value = true }
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.size(20.dp),
                                                            imageVector = Icons.Default.Delete,
                                                            tint = RED_COLOR,
                                                            contentDescription = null
                                                        )
                                                    }
                                                    DeleteGroup(
                                                        show = showDeleteDialog,
                                                        group = group
                                                    )
                                                }
                                            }
                                            if (group.author.id == user.id) {
                                                Column(
                                                    modifier = Modifier.fillMaxHeight(),
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier.background(PRIMARY_COLOR).fillMaxHeight()
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

    /**
     * Function to read a changelog
     *
     * @param changelog: the changelog to read
     */
    private fun readChangelog(changelog: Changelog) {
        requester!!.execReadChangelog(changelog.id)
        if (!requester!!.successResponse())
            showSnack(requester!!.errorMessage())
    }

}