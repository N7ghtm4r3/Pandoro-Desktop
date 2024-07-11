@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.pandoro.layouts.components.popups

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinox.inputs.InputValidator.isEmailValid
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import com.tecknobit.pandoro.helpers.RED_COLOR
import com.tecknobit.pandoro.helpers.showSnack
import com.tecknobit.pandoro.layouts.ui.screens.Home
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentGroup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showAddMembersPopup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showEditProjectGroupPopup
import com.tecknobit.pandoro.layouts.ui.sections.Section.Companion.snackbarHostState
import com.tecknobit.pandoro.viewmodels.GroupSectionViewModel
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.checkMembersValidity
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.Project
import layouts.components.PandoroTextField
import layouts.components.popups.CreatePopup
import layouts.components.popups.coroutineScope
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

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
 * Function to show the popup to add members to a [Group]
 *
 * @param group: the group where add the members
 */
@Composable
fun ShowAddMembersPopup(
    group: Group
) {
    val members = mutableStateListOf("")
    CreatePopup(
        height = 400.dp,
        flag = showAddMembersPopup,
        title = stringResource(Res.string.add_new_members),
        columnModifier = Modifier,
        titleSize = 15.sp,
        content = {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                showMembersSection(
                    height = 275.dp,
                    members = members
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable {
                        if (checkMembersValidity(members)) {
                            viewModel.addMembers(
                                members = members,
                                onSuccess = {
                                    showAddMembersPopup.value = false
                                }
                            )
                        } else
                            showSnack(coroutineScope, snackbarHostState, Res.string.wrong_group_list)
                    },
                    text = stringResource(Res.string.add),
                    fontSize = 14.sp
                )
            }
        }
    )
}

/**
 * Function to show the members section
 *
 * @param height: the height of the members section
 * @param members: the list of the members
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showMembersSection(
    height: Dp,
    members: SnapshotStateList<String>
) {
    LazyColumn(
        modifier = Modifier
            .padding(
                top = 10.dp,
                start = 15.dp
            )
            .height(height)
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 5.dp
                    )
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .size(30.dp),
                    onClick = { members.add("") },
                    content = { Icon(Icons.Filled.Add, null) }
                )
            }
        }
        items(members.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val member = mutableStateOf(members[index])
                PandoroTextField(
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            bottom = 10.dp,
                            start = 10.dp
                        )
                        .width(220.dp)
                        .height(55.dp),
                    label = Res.string.email_of_the_member,
                    isError = !isEmailValid(member.value),
                    onValueChange = {
                        if (it.isNotEmpty()) {
                            member.value = it
                            members.removeAt(index)
                            members.add(index, it)
                        }
                    },
                    value = member,
                    textFieldModifier = Modifier
                        .width(220.dp)
                )
                IconButton(
                    onClick = { members.removeAt(index) }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(18.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = RED_COLOR
                    )
                }
            }
        }
    }
}

/**
 * Function to show the popup to edit a [Project] of a [Group]
 *
 * No-any params required
 */
@Composable
fun showEditProjectGroupPopup() {
    val uProjects = Home.viewModel.projects.collectAsState().value
    CreatePopup(
        height = 400.dp,
        flag = showEditProjectGroupPopup,
        title = stringResource(Res.string.edit_the_groups_projects),
        columnModifier = Modifier,
        titleSize = 15.sp,
        content = {
            val projects = ArrayList<String>()
            val groupProjects = arrayListOf<String>()
            currentGroup.value.projects.forEach { project ->
                groupProjects.add(project.id)
            }
            LazyVerticalGrid(
                modifier = Modifier
                    .height(310.dp)
                    .padding(
                        top = 10.dp
                    ),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uProjects,
                    key = { project ->
                        project.id
                    }
                ) { project ->
                    var selected by remember { mutableStateOf(groupProjects.contains(project.id)) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            colors = CheckboxDefaults.colors(
                                checkedColor = PRIMARY_COLOR,
                                checkmarkColor = BACKGROUND_COLOR
                            ),
                            checked = selected,
                            onCheckedChange = {
                                selected = it
                                if (it)
                                    projects.add(project.id)
                                else
                                    projects.remove(project.id)
                            }
                        )
                        Text(
                            text = project.name,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(20.dp)
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        viewModel.editProjects(
                            projects = projects,
                            onSuccess = {
                                showEditProjectGroupPopup.value = false
                            }
                        )
                    },
                text = stringResource(Res.string.edit),
                fontSize = 14.sp
            )
        }
    )
}