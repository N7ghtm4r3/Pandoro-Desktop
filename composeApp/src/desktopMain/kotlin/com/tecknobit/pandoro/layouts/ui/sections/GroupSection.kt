package com.tecknobit.pandoro.layouts.ui.sections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.*
import com.tecknobit.pandoro.layouts.components.LeaveGroup
import com.tecknobit.pandoro.layouts.components.RemoveUser
import com.tecknobit.pandoro.layouts.ui.screens.Home
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentGroup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showEditProjectGroupPopup
import com.tecknobit.pandoro.layouts.ui.sections.ProfileSection.Companion.hideLeaveGroup
import com.tecknobit.pandoro.viewmodels.GroupSectionViewModel
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.users.GroupMember
import com.tecknobit.pandorocore.records.users.GroupMember.InvitationStatus.PENDING
import com.tecknobit.pandorocore.records.users.GroupMember.Role
import com.tecknobit.pandorocore.records.users.GroupMember.Role.ADMIN
import layouts.ui.screens.SplashScreen.Companion.user
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*
import kotlin.math.ceil

/**
 * This is the layout for the group section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class GroupSection : Section() {

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel by lazy { 
        GroupSectionViewModel(
            initialGroup = currentGroup.value,
            snackbarHostState = snackbarHostState
        )
    }

    /**
     * *group* -> the current group displayed
     */
    private lateinit var group: Group
    
    /**
     * Function to show the content of the [GroupSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun ShowSection() {
        val userProjects = Home.viewModel.projects.collectAsState().value
        group = viewModel.group.collectAsState().value
        val isCurrentUserAnAdmin = group.isUserAdmin(user)
        val authorId = group.author.id
        val isCurrentUserAMaintainer = group.isUserMaintainer(user)
        viewModel.setActiveContext(this::class.java)
        viewModel.refreshGroup {}
        ShowSection {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .background(BACKGROUND_COLOR),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navBack() }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                        Text(
                            text = group.name,
                            fontSize = 25.sp
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = stringResource(Res.string.author) + " ${group.author.completeName}",
                            textAlign = TextAlign.Justify,
                            fontSize = 20.sp
                        )
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = group.description,
                            textAlign = TextAlign.Justify,
                            fontSize = 14.sp
                        )
                        spaceContent()
                        var showMembersSection by remember { mutableStateOf(true) }
                        var showMembersIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.members),
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
                            members.addAll(group.members)
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
                                text = stringResource(Res.string.total_members) + " $totalMembers",
                                fontSize = 14.sp
                            )
                            if (members.isNotEmpty()) {
                                spaceContent()
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier
                                        .height(
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
                                    items(
                                        items = members,
                                        key = { member ->
                                            member.id
                                        }
                                    ) { member ->
                                        val isMemberPending = member.invitationStatus == PENDING
                                        if ((isCurrentUserAMaintainer && isMemberPending) || !isMemberPending) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(65.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color.White
                                                ),
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = 2.dp
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(
                                                            start = 20.dp,
                                                            top = 10.dp,
                                                            end = 20.dp,
                                                            bottom = 10.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Logo(
                                                        url = member.profilePic
                                                    )
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                start = 20.dp
                                                            ),
                                                        text = member.completeName,
                                                        fontSize = 18.sp
                                                    )
                                                    var modifier = Modifier
                                                        .padding(
                                                            start = 20.dp
                                                        )
                                                    if (!member.isLoggedUser(user) && isCurrentUserAMaintainer &&
                                                        !isMemberPending && member.id != authorId
                                                    ) {
                                                        var showRoleMenu by remember { mutableStateOf(false) }
                                                        if (isCurrentUserAnAdmin || !member.isAdmin) {
                                                            modifier = modifier.clickable { showRoleMenu = true }
                                                        }
                                                        DropdownMenu(
                                                            modifier = Modifier
                                                                .background(BACKGROUND_COLOR),
                                                            expanded = showRoleMenu,
                                                            onDismissRequest = { showRoleMenu = false },
                                                            offset = DpOffset(150.dp, 0.dp)
                                                        ) {
                                                            Role.entries.forEach { role ->
                                                                DropdownMenuItem(
                                                                    onClick = {
                                                                        viewModel.changeMemberRole(
                                                                            member = member,
                                                                            role = role,
                                                                            onSuccess = {
                                                                                showRoleMenu = false
                                                                            }
                                                                        )
                                                                    },
                                                                    text = {
                                                                        Text(
                                                                            text = role.toString(),
                                                                            color = if (role == ADMIN)
                                                                                RED_COLOR
                                                                            else
                                                                                PRIMARY_COLOR
                                                                        )
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Text(
                                                        modifier = modifier,
                                                        text = if (isMemberPending)
                                                            PENDING.toString()
                                                        else
                                                            member.role.toString(),
                                                        textAlign = TextAlign.Center,
                                                        color = if (member.role == ADMIN)
                                                            RED_COLOR
                                                        else {
                                                            if (isMemberPending)
                                                                YELLOW_COLOR
                                                            else
                                                                PRIMARY_COLOR
                                                        }
                                                    )
                                                    if (!member.isLoggedUser(user) && isCurrentUserAMaintainer &&
                                                        member.id != authorId
                                                    ) {
                                                        val showRemoveDialog = mutableStateOf(false)
                                                        if (isCurrentUserAnAdmin || !member.isAdmin) {
                                                            Column(
                                                                modifier = Modifier
                                                                    .fillMaxWidth(),
                                                                horizontalAlignment = Alignment.End
                                                            ) {
                                                                IconButton(
                                                                    onClick = {
                                                                        viewModel.suspendRefresher()
                                                                        showRemoveDialog.value = true
                                                                    }
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
                                                            group = group,
                                                            memberId = member.id,
                                                            onDismissRequest = {
                                                                showRemoveDialog.value = false
                                                                viewModel.restartRefresher()
                                                            }
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
                        val projects = group.projects
                        val areProjectsEmpty = projects.isEmpty()
                        if (!areProjectsEmpty || isCurrentUserAnAdmin) {
                            var showProjectsSection by remember { mutableStateOf(true) }
                            var showProjectsIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(Res.string.projects),
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
                                if (showProjectsSection && isCurrentUserAnAdmin && userProjects.isNotEmpty()) {
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
                                    modifier = Modifier
                                        .padding(
                                            top = 10.dp
                                        ),
                                    text = stringResource(Res.string.projects_number) + " ${projects.size}",
                                    fontSize = 14.sp
                                )
                                spaceContent()
                                if (!areProjectsEmpty) {
                                    LazyVerticalGrid(
                                        modifier = Modifier
                                            .padding(
                                                top = 20.dp
                                            )
                                            .height(50.dp),
                                        columns = GridCells.Adaptive(130.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(
                                            start = 10.dp,
                                            end = 10.dp
                                        )
                                    ) {
                                        items(
                                            items = projects,
                                            key = { project ->
                                                project.id
                                            }
                                        ) { project ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp),
                                                shape = RoundedCornerShape(15),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color.White
                                                ),
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = 2.dp
                                                ),
                                                onClick = { navToProject(Sections.Group, project) },
                                            ) {
                                                Row {
                                                    Column(
                                                        modifier = Modifier
                                                            .weight(5f)
                                                            .fillMaxSize(),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = project.name,
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    Column(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .fillMaxHeight(),
                                                        horizontalAlignment = Alignment.End,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(PRIMARY_COLOR)
                                                                .fillMaxHeight()
                                                                .width(5.dp)
                                                        )
                                                    }
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
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextButton(
                                modifier = Modifier
                                    .fillMaxSize(),
                                onClick = {
                                    viewModel.suspendRefresher()
                                    showLeaveDialog.value = true
                                }
                            ) {
                                Text(
                                    modifier = Modifier
                                        .wrapContentSize(),
                                    text = stringResource(Res.string.leave_group),
                                    color = RED_COLOR,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        LeaveGroup(
                            show = showLeaveDialog,
                            group = group,
                            onDismissRequest = {
                                showLeaveDialog.value = false
                                viewModel.restartRefresher()
                            }
                        )
                    }
                }
            }
        }
    }

}