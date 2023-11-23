package layouts.ui.sections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.ui.SingleItemManager
import com.tecknobit.pandoro.records.Group
import com.tecknobit.pandoro.records.users.GroupMember
import com.tecknobit.pandoro.records.users.GroupMember.InvitationStatus.PENDING
import com.tecknobit.pandoro.records.users.GroupMember.Role
import com.tecknobit.pandoro.records.users.GroupMember.Role.ADMIN
import helpers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import layouts.components.LeaveGroup
import layouts.components.RemoveUser
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentGroup
import layouts.ui.screens.Home.Companion.showEditProjectGroupPopup
import layouts.ui.screens.SplashScreen.Companion.requester
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.ProfileSection.Companion.hideLeaveGroup
import org.json.JSONException
import kotlin.math.ceil

/**
 * This is the layout for the group section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 * @see SingleItemManager
 */
class GroupSection : Section(), SingleItemManager {

    /**
     * Function to show the content of the [GroupSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun showSection() {
        val isCurrentUserAnAdmin = currentGroup.value.isUserAdmin(user)
        val isCurrentUserAMaintainer = currentGroup.value.isUserMaintainer(user)
        refreshItem()
        showSection {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier.fillParentMaxWidth().background(BACKGROUND_COLOR),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navBack() }
                        ) {
                            Icon(
                                modifier = Modifier.size(22.dp),
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                        Text(
                            text = currentGroup.value.name,
                            fontSize = 25.sp
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "Author: ${currentGroup.value.author.completeName}",
                            textAlign = TextAlign.Justify,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = currentGroup.value.description,
                            textAlign = TextAlign.Justify,
                            fontSize = 14.sp
                        )
                        spaceContent()
                        var showMembersSection by remember { mutableStateOf(true) }
                        var showMembersIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Members",
                                fontSize = 20.sp
                            )
                            IconButton(
                                onClick = {
                                    showMembersSection = !showMembersSection
                                    showMembersIcon =
                                        if (showMembersSection)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility
                                }
                            ) {
                                Icon(
                                    imageVector = showMembersIcon,
                                    contentDescription = null
                                )
                            }
                        }
                        if (showMembersSection) {
                            val members = mutableListOf<GroupMember>()
                            members.addAll(currentGroup.value.members)
                            if (!isCurrentUserAMaintainer) {
                                val membersToHide = mutableListOf<GroupMember>()
                                members.forEach { member ->
                                    if (member.invitationStatus == PENDING)
                                        membersToHide.add(member)
                                }
                                members.removeAll(membersToHide)
                            }
                            val totalMembers = members.size
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = "Total members: $totalMembers",
                                fontSize = 14.sp
                            )
                            if (members.isNotEmpty()) {
                                spaceContent()
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.height(
                                        if (totalMembers < 3)
                                            90.dp
                                        else if (totalMembers <= 15)
                                            (ceil(totalMembers / 3.toFloat()) * 90).dp
                                        else
                                            400.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(
                                        top = 20.dp,
                                        start = 10.dp,
                                        bottom = 10.dp,
                                        end = 10.dp
                                    )
                                ) {
                                    items(members) { member ->
                                        val isMemberPending = member.invitationStatus == PENDING
                                        if ((isCurrentUserAMaintainer && isMemberPending) || !isMemberPending) {
                                            Card(
                                                modifier = Modifier.fillMaxWidth().height(65.dp),
                                                backgroundColor = Color.White,
                                                shape = RoundedCornerShape(10.dp),
                                                elevation = 2.dp
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        start = 20.dp,
                                                        top = 10.dp,
                                                        end = 20.dp,
                                                        bottom = 10.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Image(
                                                        modifier = Modifier.size(45.dp).clip(CircleShape),
                                                        bitmap = loadImageBitmap(member.profilePic),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    Text(
                                                        modifier = Modifier.padding(start = 20.dp),
                                                        text = member.completeName,
                                                        fontSize = 18.sp
                                                    )
                                                    var modifier = Modifier.padding(start = 20.dp)
                                                    if (!member.isLoggedUser(user) && isCurrentUserAMaintainer &&
                                                        !isMemberPending
                                                    ) {
                                                        var showRoleMenu by remember { mutableStateOf(false) }
                                                        if (isCurrentUserAnAdmin || !member.isAdmin) {
                                                            modifier = modifier.clickable { showRoleMenu = true }
                                                        }
                                                        DropdownMenu(
                                                            modifier = Modifier.background(BACKGROUND_COLOR),
                                                            expanded = showRoleMenu,
                                                            onDismissRequest = { showRoleMenu = false },
                                                            offset = DpOffset(150.dp, 0.dp)
                                                        ) {
                                                            Role.values().forEach { role ->
                                                                DropdownMenuItem(
                                                                    onClick = {
                                                                        requester!!.execChangeMemberRole(
                                                                            currentGroup.value.id,
                                                                            member.id,
                                                                            role
                                                                        )
                                                                        if (requester!!.successResponse())
                                                                            showRoleMenu = false
                                                                        else
                                                                            showSnack(requester!!.errorMessage())
                                                                    }
                                                                ) {
                                                                    Text(
                                                                        text = role.toString(),
                                                                        color = if (role == ADMIN) RED_COLOR else PRIMARY_COLOR
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Text(
                                                        modifier = modifier,
                                                        text = if (isMemberPending) PENDING.toString() else member.role.toString(),
                                                        textAlign = TextAlign.Center,
                                                        color = if (member.role == ADMIN) RED_COLOR
                                                        else {
                                                            if (isMemberPending)
                                                                YELLOW_COLOR
                                                            else
                                                                PRIMARY_COLOR
                                                        }
                                                    )
                                                    if (!member.isLoggedUser(user) && isCurrentUserAMaintainer) {
                                                        val showRemoveDialog = mutableStateOf(false)
                                                        if (isCurrentUserAnAdmin || !member.isAdmin) {
                                                            Column(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalAlignment = Alignment.End
                                                            ) {
                                                                IconButton(
                                                                    onClick = { showRemoveDialog.value = true }
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.GroupRemove,
                                                                        contentDescription = null
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        RemoveUser(
                                                            show = showRemoveDialog,
                                                            group = currentGroup.value,
                                                            memberId = member.id
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        spaceContent()
                        val projects = currentGroup.value.projects
                        val areProjectsEmpty = projects.isEmpty()
                        if (!areProjectsEmpty || isCurrentUserAnAdmin) {
                            var showProjectsSection by remember { mutableStateOf(true) }
                            var showProjectsIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Projects",
                                    fontSize = 20.sp
                                )
                                IconButton(
                                    onClick = {
                                        showProjectsSection = !showProjectsSection
                                        showProjectsIcon =
                                            if (showProjectsSection)
                                                Icons.Default.VisibilityOff
                                            else
                                                Icons.Default.Visibility
                                    }
                                ) {
                                    Icon(
                                        imageVector = showProjectsIcon,
                                        contentDescription = null
                                    )
                                }
                                if (showProjectsSection && isCurrentUserAnAdmin) {
                                    IconButton(
                                        onClick = { showEditProjectGroupPopup.value = true }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ModeEditOutline,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                            if (showProjectsSection) {
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = "Projects number: ${projects.size}",
                                    fontSize = 14.sp
                                )
                                spaceContent()
                                if (!areProjectsEmpty) {
                                    LazyVerticalGrid(
                                        modifier = Modifier
                                            .padding(top = 20.dp)
                                            .height(50.dp),
                                        columns = GridCells.Adaptive(100.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
                                    ) {
                                        items(projects) { project ->
                                            Card(
                                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                                shape = RoundedCornerShape(15),
                                                backgroundColor = Color.White,
                                                elevation = 2.dp,
                                                onClick = { navToProject(Sections.Group, project) },
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = project.name,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                Column(
                                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier.background(PRIMARY_COLOR).fillMaxHeight()
                                                            .width(3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    spaceContent()
                                }
                            }
                        }
                    }
                }
                if (!hideLeaveGroup) {
                    item {
                        val showLeaveDialog = mutableStateOf(false)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextButton(
                                modifier = Modifier.fillMaxSize(),
                                onClick = { showLeaveDialog.value = true }
                            ) {
                                Text(
                                    modifier = Modifier.wrapContentSize(),
                                    text = "Leave group",
                                    color = RED_COLOR,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        LeaveGroup(
                            show = showLeaveDialog,
                            group = currentGroup.value,
                        )
                    }
                }
            }
        }
    }

    /**
     * Function to refresh an item to display in the UI
     *
     * No-any params required
     */
    override fun refreshItem() {
        CoroutineScope(Dispatchers.Default).launch {
            while (user.id != null && activeScreen.value == Sections.Group) {
                val response = requester!!.execGetSingleGroup(currentGroup.value.id)
                if (requester!!.successResponse()) {
                    try {
                        val tmpGroup = Group(response)
                        if (needToRefresh(currentGroup.value, tmpGroup))
                            currentGroup.value = tmpGroup
                    } catch (_: JSONException) {
                    }
                }
                delay(1000)
            }
        }
    }

}