package layouts.ui.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helpers.BACKGROUND_COLOR
import helpers.RED_COLOR
import layouts.components.PandoroTextField
import layouts.components.Sidebar.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.showEditPopup
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.Section.Sections.*
import toImportFromLibrary.Project
import kotlin.math.ceil

/**
 * This is the layout for the projects section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class ProjectsSection : Section() {

    companion object {

        /**
         * **projectsList** -> the list of the projects
         */
        lateinit var projectsList: SnapshotStateList<Project>

    }

    /**
     * Function to show the content of the [ProjectsSection]
     *
     * No-any params required
     */
    @Composable
    override fun showSection() {
        projectsList = mutableStateListOf()
        projectsList.addAll(user.projects)
        Spacer(Modifier.height(10.dp))
        LazyColumn {
            item {
                populateLazyGrid("Frequent projects", populateFrequentProjects())
            }
            item {
                populateLazyGrid("Current projects", projectsList)
            }
        }
    }

    /**
     * Function to populate the frequent projects list
     *
     * No-any params required
     * @return frequent projects list as [SnapshotStateList] of [Project]
     */
    private fun populateFrequentProjects(): SnapshotStateList<Project> {
        val frequentProjects = mutableStateListOf<Project>()
        val updatesNumber = ArrayList<Int>()
        for (project in projectsList)
            updatesNumber.add(project.updatesNumber)
        updatesNumber.sortDescending()
        for (updates in updatesNumber) {
            if (frequentProjects.size < 9) {
                for (project in projectsList) {
                    if (project.updatesNumber == updates && !frequentProjects.contains(project)) {
                        frequentProjects.add(project)
                        break
                    }
                }
            }
        }
        if (frequentProjects.size > 9)
            frequentProjects.removeRange(8, frequentProjects.size - 1)
        return frequentProjects
    }

    /**
     * Function to create the [LazyVerticalGrid] with the projects list
     *
     * @param title: the title of the [LazyVerticalGrid]
     * @param list: the list of the [Project] to populate the [LazyVerticalGrid]
     *
     */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun populateLazyGrid(title: String, list: SnapshotStateList<Project>) {
        var query by remember { mutableStateOf("") }
        val projects = filterProjects(query = query, list)
        Text(
            modifier = Modifier.padding(start = 20.dp),
            text = title,
            fontSize = 25.sp
        )
        PandoroTextField(
            modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 0.dp).size(width = 250.dp, height = 55.dp),
            label = "Search",
            onValueChange = {
                query = it
            },
            value = query,
            trailingIcon = {
                Icon(
                    modifier = if (query.isNotEmpty()) {
                        Modifier.clickable { query = "" }
                    } else Modifier,
                    imageVector = if (query.isEmpty()) Icons.Default.Search else Icons.Default.Clear,
                    contentDescription = null,
                )
            }
        )
        if (projects.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(start = 25.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No projects found",
                    fontSize = 15.sp
                )
            }
        } else {
            val times = projects.size / 9f
            val height =
                if (times <= 1)
                    150.dp
                else
                    (ceil(times.toDouble()) * 150 + 60).dp
            LazyVerticalGrid(
                modifier = Modifier.height(height).padding(20.dp),
                columns = GridCells.Adaptive(150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(projects) { project ->
                    var actionsSelected by remember { mutableStateOf(false) }
                    var showDeleteAlertDialog by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.padding(bottom = 8.dp),
                        backgroundColor = White,
                        shape = RoundedCornerShape(15.dp),
                        elevation = 2.dp,
                        onClick = {
                            previousSections.add(Projects)
                            activeScreen.value = Project
                            currentProject = project
                        }
                    ) {
                        Column(Modifier.size(100.dp).padding(15.dp)) {
                            Row {
                                Column(Modifier.weight(1f).fillMaxWidth()) {
                                    if (!actionsSelected) {
                                        Text(
                                            text = project.name,
                                            fontSize = 16.sp
                                        )
                                    } else {
                                        Row {
                                            Column(modifier = Modifier.weight(1f)) {
                                                IconButton(
                                                    modifier = Modifier.size(18.dp),
                                                    onClick = {
                                                        currentProject = project
                                                        showEditPopup.value = true
                                                        actionsSelected = false
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                IconButton(
                                                    modifier = Modifier.size(18.dp),
                                                    onClick = { showDeleteAlertDialog = true }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = RED_COLOR
                                                    )
                                                }
                                                if (showDeleteAlertDialog) {
                                                    AlertDialog(
                                                        modifier = Modifier.size(width = 400.dp, height = 200.dp),
                                                        shape = RoundedCornerShape(25.dp),
                                                        backgroundColor = BACKGROUND_COLOR,
                                                        onDismissRequest = { showDeleteAlertDialog = false },
                                                        title = {
                                                            Text(text = "Delete project")
                                                        },
                                                        text = {
                                                            Text(
                                                                text = "If you confirm this action the project and its all "
                                                                        + "information will be deleted and no more recoverable, "
                                                                        + "confirm?"
                                                            )
                                                        },
                                                        dismissButton = {
                                                            TextButton(
                                                                onClick = { showDeleteAlertDialog = false },
                                                                content = { Text(text = "Dismiss") }
                                                            )
                                                        },
                                                        confirmButton = {
                                                            TextButton(
                                                                onClick = {
                                                                    // TODO: MAKE REQUEST THEN
                                                                    user.projects.remove(project)
                                                                    showDeleteAlertDialog = false
                                                                    actionsSelected = false
                                                                },
                                                                content = { Text(text = "Confirm") }
                                                            )
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Column(Modifier.weight(1f).fillMaxWidth()) {
                                    IconButton(
                                        modifier = Modifier.size(18.dp).align(alignment = Alignment.End),
                                        onClick = { actionsSelected = !actionsSelected }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                            Text(
                                modifier = Modifier.padding(top = 7.dp),
                                text = project.shortDescription,
                                fontSize = 14.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxSize().padding(top = 7.dp),
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "v. " + project.version,
                                        fontSize = 12.sp
                                    )
                                }
                                if (project.groups.isNotEmpty()) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(18.dp).align(alignment = Alignment.End),
                                            imageVector = Icons.Default.Group,
                                            contentDescription = null
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

    /**
     * Function to filter the projects list
     *
     * @param query: the query to filter the projects list
     * @param list: the list of the [Project] to filter
     *
     * @return projects list filtered as [SnapshotStateList] of [Project]
     */
    private fun filterProjects(
        query: String,
        list: SnapshotStateList<Project>
    ): SnapshotStateList<Project> {
        return if (query.isEmpty())
            list
        else {
            val checkQuery = query.uppercase()
            val filteredList = mutableStateListOf<Project>()
            for (project in list) {
                if (project.name.uppercase().contains(checkQuery) || project.shortDescription.uppercase()
                        .contains(checkQuery) || project.description.uppercase().contains(checkQuery) || project.version
                        .uppercase().contains(checkQuery) || groupMatch(project.groups, checkQuery)
                ) {
                    filteredList.add(project)
                }
            }
            filteredList
        }
    }

    /**
     * Function to check whether name match with a group of the list
     *
     * @param groups: the groups of the project
     * @param name: the name to do the check
     *
     * @return whether name match with a group of the list as [Boolean]
     */
    private fun groupMatch(
        groups: ArrayList<toImportFromLibrary.Group>,
        name: String
    ): Boolean {
        groups.forEach { group: toImportFromLibrary.Group ->
            if (group.name.uppercase().contains(name))
                return true
        }
        return false
    }

}