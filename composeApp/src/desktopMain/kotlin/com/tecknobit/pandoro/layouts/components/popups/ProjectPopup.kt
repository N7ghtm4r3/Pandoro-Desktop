@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.pandoro.layouts.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import com.tecknobit.pandoro.viewmodels.HomeScreenViewModel
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidProjectDescription
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidProjectName
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidProjectShortDescription
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidRepository
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidVersion
import com.tecknobit.pandorocore.records.Project
import layouts.components.PandoroTextField
import layouts.components.popups.CreatePopup
import layouts.components.popups.snackbarHostState
import layouts.ui.screens.Home.Companion.showAddProjectPopup
import layouts.ui.screens.Home.Companion.showEditPopup
import layouts.ui.screens.SplashScreen.Companion.user
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

private val viewModel = HomeScreenViewModel(
    snackbarHostState = snackbarHostState
)

/**
 * Function to show the popup to add a new project
 *
 * No-any params required
 */
@Wrapper
@Composable
fun ShowAddProjectPopup() {
    ShowProjectPopup(stringResource(Res.string.add_a_new_project), stringResource(Res.string.add), showAddProjectPopup)
}

/**
 * Function to show the popup to edit an old project
 *
 * @param project: the project to edit
 */
@Wrapper
@Composable
fun ShowEditProjectPopup(project: Project) {
    ShowProjectPopup(stringResource(Res.string.edit_project), stringResource(Res.string.edit), showEditPopup, project)
}

/**
 * Function to show a popup for a project's operation
 *
 * @param title: the title of the popup
 * @param buttonText: the text for the confirm button
 * @param flag: the flag whether show the popup
 * @param project: the project for the operation
 */
@Composable
private fun ShowProjectPopup(title: String, buttonText: String, flag: MutableState<Boolean>, project: Project? = null) {
    viewModel.name = remember { mutableStateOf(if (project != null) project.name else "") }
    viewModel.description = remember { mutableStateOf(if (project != null) project.description else "") }
    viewModel.shortDescription = remember { mutableStateOf(if (project != null) project.shortDescription else "") }
    viewModel.version = remember { mutableStateOf(if (project != null) project.version else "") }
    viewModel.projectRepository = remember { mutableStateOf(if (project != null) project.projectRepo else "") }
    viewModel.projectGroups = remember { mutableListOf() }
    if (project != null)
        viewModel.projectGroups.addAll(project.groups)
    CreatePopup(
        height = if (user.adminGroups.isNotEmpty())
            665.dp
        else
            465.dp,
        flag = flag,
        title = title,
        content = {
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.name),
                isError = !isValidProjectName(viewModel.name.value),
                value = viewModel.name
            )
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.description),
                isError = !isValidProjectDescription(viewModel.description.value),
                value = viewModel.description
            )
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.short_description),
                isError = !isValidProjectShortDescription(viewModel.shortDescription.value),
                value = viewModel.shortDescription
            )
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.version),
                isError = !isValidVersion(viewModel.version.value),
                value = viewModel.version
            )
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.project_repository),
                isError = !isValidRepository(viewModel.projectRepository.value),
                value = viewModel.projectRepository
            )
            if (user.adminGroups.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 15.dp,
                            top = 10.dp
                        ),
                    textAlign = TextAlign.Start,
                    text = stringResource(Res.string.add_to_a_group)
                )
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                        .height(160.dp),
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = user.adminGroups,
                        key = { group ->
                            group.id
                        }
                    ) { group ->
                        var selected by remember { mutableStateOf(viewModel.projectGroups.contains(group)) }
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
                                        viewModel.projectGroups.add(group)
                                    else
                                        viewModel.projectGroups.remove(group)
                                }
                            )
                            Text(
                                text = group.name,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )
            }
            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.workWithProject(
                            project = project,
                            onSuccess = { flag.value = false }
                        )
                    },
                text = buttonText,
                fontSize = 14.sp
            )
        }
    )
}