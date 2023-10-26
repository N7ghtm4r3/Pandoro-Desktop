package layouts.components.popups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.showSnack
import layouts.components.PandoroTextField
import layouts.ui.screens.Connect.Companion.urlValidator
import layouts.ui.screens.Home.Companion.showAddProjectPopup
import layouts.ui.screens.Home.Companion.showEditPopup
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.ProjectsSection.Companion.projectsList
import toImportFromLibrary.Group
import toImportFromLibrary.Project
import toImportFromLibrary.Project.*
import toImportFromLibrary.Update
import toImportFromLibrary.Update.TARGET_VERSION_MAX_LENGTH

/**
 * Function to show the popup to add a new project
 *
 * No-any params required
 */
@Wrapper
@Composable
fun showAddProjectPopup() {
    showProjectPopup("Add a new project", "Add project", showAddProjectPopup)
}

/**
 * Function to show the popup to edit an old project
 *
 * No-any params required
 */
@Wrapper
@Composable
fun showEditProjectPopup(project: Project) {
    showProjectPopup("Edit " + project.name + " project", "Edit project", showEditPopup, project)
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
private fun showProjectPopup(title: String, buttonText: String, flag: MutableState<Boolean>, project: Project? = null) {
    var name by remember { mutableStateOf(if (project != null) project.name else "") }
    var description by remember { mutableStateOf(if (project != null) project.description else "") }
    var shortDescription by remember { mutableStateOf(if (project != null) project.shortDescription else "") }
    var version by remember { mutableStateOf(if (project != null) project.version else "") }
    var repository by remember { mutableStateOf(if (project != null) project.projectRepo else "") }
    createPopup(
        height = if (user.adminGroups.isNotEmpty()) 665.dp else 465.dp,
        flag = flag,
        title = title,
        content = {
            PandoroTextField(
                modifier = Modifier.padding(10.dp).height(55.dp),
                label = "Name",
                isError = !isValidProjectName(name),
                onValueChange = { name = it },
                value = name
            )
            PandoroTextField(
                modifier = Modifier.padding(10.dp).height(55.dp),
                label = "Description",
                isError = !isValidDescription(description),
                onValueChange = { description = it },
                value = description
            )
            PandoroTextField(
                modifier = Modifier.padding(10.dp).height(55.dp),
                label = "Short description",
                isError = !isValidShortDescription(shortDescription),
                onValueChange = { shortDescription = it },
                value = shortDescription
            )
            PandoroTextField(
                modifier = Modifier.padding(10.dp).height(55.dp),
                label = "Version",
                isError = !isValidVersion(version),
                onValueChange = { version = it },
                value = version
            )
            PandoroTextField(
                modifier = Modifier.padding(10.dp).height(55.dp),
                label = "Project repository",
                isError = !isValidRepository(repository),
                onValueChange = { repository = it },
                value = repository
            )
            val groups = if (project != null) project.groups else ArrayList<Group>()
            if (user.adminGroups.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 15.dp),
                    textAlign = TextAlign.Start,
                    text = if (name.isNotEmpty()) "Add $name to a group" else "Add to a group"
                )
                LazyVerticalGrid(
                    modifier = Modifier.padding(top = 10.dp).height(175.dp),
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(user.adminGroups) { group ->
                        var selected by remember { mutableStateOf(groups.contains(group)) }
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
                                    // TODO: CREATE THE REAL WORKFLOW
                                    selected = it
                                    if (it)
                                        groups.add(group)
                                    else
                                        groups.remove(group)
                                }
                            )
                            Text(
                                text = group.name,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else
                Spacer(Modifier.height(10.dp))
            Text(
                modifier = Modifier.clickable {
                    if (isValidProjectName(name)) {
                        if (isValidDescription(description)) {
                            if (isValidShortDescription(shortDescription)) {
                                if (isValidVersion(version)) {
                                    version = version.replace("v. ", "")
                                    if (isValidRepository(repository)) {
                                        if (project == null) {
                                            // TODO: MAKE ADD REQUEST THEN
                                            projectsList.add(
                                                Project(
                                                    "id0",
                                                    name,
                                                    shortDescription,
                                                    description,
                                                    version,
                                                    groups,
                                                    arrayListOf<Update>(),
                                                    repository
                                                )
                                            )
                                        } else {
                                            // TODO: MAKE EDIT REQUEST THEN
                                            lateinit var projectToRemove: Project
                                            for (pro in projectsList) {
                                                if (pro.id.equals(project.id)) {
                                                    projectToRemove = pro
                                                    projectsList.add(
                                                        Project(
                                                            pro.id,
                                                            name,
                                                            shortDescription,
                                                            description,
                                                            version,
                                                            groups,
                                                            pro.updates,
                                                            repository
                                                        )
                                                    )
                                                    break
                                                }
                                            }
                                            projectsList.remove(projectToRemove)
                                        }
                                        flag.value = false
                                    } else
                                        showSnack(coroutineScope, scaffoldState, "Insert a correct repository url")
                                } else
                                    showSnack(coroutineScope, scaffoldState, "Insert a correct version")
                            } else
                                showSnack(coroutineScope, scaffoldState, "Insert a correct short description")
                        } else
                            showSnack(coroutineScope, scaffoldState, "Insert a correct description")
                    } else
                        showSnack(coroutineScope, scaffoldState, "Insert a correct name")
                },
                text = buttonText,
                fontSize = 14.sp
            )
        }
    )
}

/**
 * Function to check the validity of a project name
 *
 * @param projectName: project name to check
 * @return whether the project name is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isValidProjectName(projectName: String): Boolean {
    return projectName.length in 1..PROJECT_NAME_MAX_LENGTH
}

/**
 * Function to check the validity of a description
 *
 * @param description: description to check
 * @return whether the description is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isValidDescription(description: String): Boolean {
    return description.length in 1..PROJECT_DESCRIPTION_MAX_LENGTH
}

/**
 * Function to check the validity of a short description
 *
 * @param shorDescription: short description to check
 * @return whether the short description is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isValidShortDescription(shorDescription: String): Boolean {
    return shorDescription.length in 1..PROJECT_SHORT_DESCRIPTION_MAX_LENGTH
}

/**
 * Function to check the validity of a version
 *
 * @param version: version to check
 * @return whether the version is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isValidVersion(version: String): Boolean {
    return version.length in 1..TARGET_VERSION_MAX_LENGTH
}

/**
 * Function to check the validity of a repository url
 *
 * @param repository: repository to check
 * @return whether the repository is valid as [Boolean]
 */
// TODO: PACK IN LIBRARY
private fun isValidRepository(repository: String): Boolean {
    return repository.isEmpty() || (urlValidator.isValid(repository) && RepositoryPlatform.isValidPlatform(repository))
}
