package layouts.ui.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandorocore.records.Project
import com.tecknobit.pandorocore.ui.filterProjects
import com.tecknobit.pandorocore.ui.populateFrequentProjects
import helpers.BACKGROUND_COLOR
import helpers.RED_COLOR
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.showEditPopup
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

/**
 * This is the layout for the projects section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
@OptIn(ExperimentalResourceApi::class)
class ProjectsSection : Section() {

    companion object {

        /**
         * **projectsList** -> the list of the projects
         */
        val projectsList: SnapshotStateList<Project> = mutableStateListOf()

    }

    /**
     * **maxHeight** -> the max height of the [BoxWithConstraints] where are nested the [LazyVerticalGrid]
     */
    private var maxHeight = 0.dp

    /**
     * Function to show the content of the [ProjectsSection]
     *
     * No-any params required
     */
    @Composable
    override fun showSection() {
        showSection {
            BoxWithConstraints {
                this@ProjectsSection.maxHeight = maxHeight
                Spacer(Modifier.height(10.dp))
                LazyColumn {
                    item {
                        populateLazyGrid(
                            stringResource(Res.string.frequent_projects),
                            populateFrequentProjects(projectsList).toMutableStateList()
                        )
                    }
                    item {
                        populateLazyGrid(
                            stringResource(Res.string.current_projects),
                            projectsList
                        )
                    }
                }
            }
        }
    }

    /**
     * Function to create the [LazyVerticalGrid] with the projects list
     *
     * @param title: the title of the [LazyVerticalGrid]
     * @param list: the list of the [Project] to populate the [LazyVerticalGrid]
     *
     */
    @Composable
    private fun populateLazyGrid(
        title: String,
        list: SnapshotStateList<Project>
    ) {
        var query by remember { mutableStateOf("") }
        val projects = filterProjects(query = query, list).toMutableStateList()
        Text(
            modifier = Modifier
                .padding(
                    start = 20.dp
                ),
            text = title,
            fontSize = 25.sp
        )
        /*PandoroTextField(
            modifier = Modifier
                .padding(
                    top = 20.dp,
                    start = 20.dp,
                    bottom = 0.dp
                ).size(
                    width = 250.dp,
                    height = 55.dp
                ),
            label = stringResource(Res.string.search),
            onValueChange = {
                query = it
            },
            value = query,
            trailingIcon = {
                Icon(
                    modifier = if (query.isNotEmpty()) {
                        Modifier
                            .clickable {
                                query = ""
                            }
                    } else Modifier,
                    imageVector = if (query.isEmpty()) Icons.Default.Search else Icons.Default.Clear,
                    contentDescription = null,
                )
            }
        )*/
        if (projects.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(
                        start = 25.dp
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_projects_found),
                    fontSize = 15.sp
                )
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .heightIn(max = maxHeight)
                    .padding(20.dp),
                columns = GridCells.Adaptive(175.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = projects,
                    key = { project ->
                        project.id
                    }
                ) { project ->
                    var actionsSelected by remember { mutableStateOf(false) }
                    var showDeleteAlertDialog by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .padding(
                                bottom = 8.dp
                            )
                            .height(115.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        onClick = {
                            previousSections.add(Sections.Projects)
                            activeScreen.value = Sections.Project
                            currentProject.value = project
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp)
                        ) {
                            Row {
                                Column(
                                    modifier = Modifier
                                        .weight(8f)
                                        .fillMaxWidth()
                                ) {
                                    if (!actionsSelected) {
                                        Text(
                                            text = project.name,
                                            fontSize = 16.sp
                                        )
                                    } else {
                                        Row {
                                            Column {
                                                IconButton(
                                                    modifier = Modifier.size(18.dp),
                                                    onClick = {
                                                        currentProject.value = project
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
                                            Column(
                                                modifier = Modifier.padding(
                                                    start = 20.dp
                                                )
                                            ) {
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
                                                        shape = RoundedCornerShape(25.dp),
                                                        containerColor = BACKGROUND_COLOR,
                                                        onDismissRequest = { showDeleteAlertDialog = false },
                                                        title = {
                                                            Text(
                                                                text = stringResource(Res.string.delete_project)
                                                            )
                                                        },
                                                        text = {
                                                            Text(
                                                                text = stringResource(Res.string.delete_text_dialog)
                                                            )
                                                        },
                                                        dismissButton = {
                                                            TextButton(
                                                                onClick = { showDeleteAlertDialog = false },
                                                                content = {
                                                                    Text(
                                                                        text = stringResource(Res.string.dismiss)
                                                                    )
                                                                }
                                                            )
                                                        },
                                                        confirmButton = {
                                                            TextButton(
                                                                onClick = {
                                                                    /*requester!!.execDeleteProject(project.id)
                                                                    showDeleteAlertDialog = false
                                                                    actionsSelected = false
                                                                    if (!requester!!.successResponse())
                                                                        showSnack(requester!!.errorMessage())*/
                                                                },
                                                                content = {
                                                                    Text(
                                                                        text = stringResource(Res.string.confirm)
                                                                    )
                                                                }
                                                            )
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    IconButton(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.End),
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
                                modifier = Modifier
                                    .padding(
                                        top = 7.dp
                                    ),
                                text = project.shortDescription,
                                fontSize = 14.sp
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = 7.dp
                                    ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "v. " + project.version,
                                        fontSize = 12.sp
                                    )
                                }
                                if (project.hasGroups()) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .align(Alignment.End),
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

}