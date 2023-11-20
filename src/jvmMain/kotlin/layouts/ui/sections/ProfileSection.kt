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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.tecknobit.pandoro.records.Changelog
import com.tecknobit.pandoro.records.users.GroupMember.Role.*
import helpers.PRIMARY_COLOR
import helpers.RED_COLOR
import helpers.spaceContent
import layouts.components.DeleteGroup
import layouts.ui.screens.Home.Companion.showEditEmailPopup
import layouts.ui.screens.Home.Companion.showEditPasswordPopup
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.Section.Sections.Profile

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
        private const val HIDE_PASSWORD = "********"

    }

    /**
     * Function to show the content of the [ProfileSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun showSection() {
        LazyColumn(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp).fillMaxSize(),
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
                                        // TODO: USE REAL USER ICON user.profilePic
                                        Image(
                                            modifier = Modifier.size(150.dp).clip(CircleShape),
                                            painter = painterResource("pillars-of-creation.jpg"),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop
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
                                            fileExtensions = listOf("jpg", "jpeg", "png")
                                        ) { path ->
                                            // TODO: MAKE REQUEST if path is not null THEN
                                            showFilePicker = false
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.padding(top = 20.dp)
                                    ) {
                                        listOf(
                                            Pair("Name", user.name),
                                            Pair("Surname", user.surname),
                                            Pair("Email", user.email),
                                            Pair("Password", HIDE_PASSWORD)
                                        ).forEach { details ->
                                            val isEmail = details.first == "Email"
                                            val isPassword = details.first == "Password"
                                            var bottom = 10.dp
                                            if (isEmail || isPassword)
                                                bottom = 0.dp
                                            Row(
                                                modifier = Modifier.padding(bottom = bottom),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                var property by remember { mutableStateOf(details.second) }
                                                Text(
                                                    text = "${details.first}:"
                                                )
                                                Text(
                                                    modifier = Modifier.then(
                                                        if (isPassword) {
                                                            var top: Dp = 0.dp
                                                            if (property.equals(HIDE_PASSWORD))
                                                                top = 5.dp
                                                            Modifier.padding(start = 5.dp, top = top).onClick {
                                                                property = visualizePassword(property)
                                                            }
                                                        } else
                                                            Modifier.padding(start = 5.dp)
                                                    ),
                                                    text = property,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (isEmail || isPassword) {
                                                    IconButton(
                                                        modifier = Modifier.size(30.dp).padding(start = 5.dp),
                                                        onClick = {
                                                            if (isEmail)
                                                                showEditEmailPopup.value = true
                                                            else
                                                                showEditPasswordPopup.value = true
                                                        }
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
                                        }
                                        spaceContent(space = 0.dp)
                                        Row(
                                            modifier = Modifier.padding(top = 20.dp),
                                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                                        ) {
                                            Button(
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    // TODO: MAKE REQUEST THEN
                                                }
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
                                                    // TODO: MAKE REQUEST THEN
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
                                val changelogs = user.changelogs
                                if (changelogs.isEmpty()) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "No any changelogs",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    LazyColumn {
                                        items(changelogs) { changelog ->
                                            Column(
                                                modifier = Modifier.clickable {
                                                    val project = changelog.project
                                                    if (project != null)
                                                        navToProject(Profile, project)
                                                    else
                                                        navToGroup(Profile, changelog.group)
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
                                                                // TODO: MAKE REQUEST THEN
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
                    val groups = user.groups
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
                            LazyVerticalGrid(
                                modifier = Modifier.padding(top = 20.dp)
                                    .fillMaxWidth()
                                    .height(((groups.size / 4) * itemHeight.value).dp),
                                columns = GridCells.Fixed(5),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                items(groups) { group ->
                                    val isAdmin = group.isUserAdmin(user)
                                    Card(
                                        modifier = Modifier.fillMaxWidth().height(itemHeight).padding(bottom = 20.dp),
                                        backgroundColor = Color.White,
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 2.dp,
                                        onClick = { navToGroup(Profile, group) }
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

    /**
     * Function to show or hide the [User]'s password
     *
     * @param password: the password to show or hide
     *
     * @return the password to visualize as [String]
     */
    private fun visualizePassword(password: String): String {
        return if (!password.contains(HIDE_PASSWORD))
            HIDE_PASSWORD
        else
            "password from the preferences"
    }

    private fun readChangelog(changelog: Changelog) {
        // TODO: MAKE REQUEST THEN
    }

}