package layouts.ui.sections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.areAllChangeNotesDone
import com.tecknobit.pandoro.records.Note
import com.tecknobit.pandoro.records.Project
import com.tecknobit.pandoro.records.Project.RepositoryPlatform.GitLab
import com.tecknobit.pandoro.records.Project.RepositoryPlatform.Github
import com.tecknobit.pandoro.records.ProjectUpdate
import com.tecknobit.pandoro.records.ProjectUpdate.Status.PUBLISHED
import com.tecknobit.pandoro.records.ProjectUpdate.Status.SCHEDULED
import helpers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import layouts.components.DeleteUpdate
import layouts.components.PublishUpdate
import layouts.components.Sidebar.Companion.activeScreen
import layouts.components.showProjectChart
import layouts.ui.screens.Home.Companion.currentNote
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.currentUpdate
import layouts.ui.screens.Home.Companion.showCreateNotePopup
import layouts.ui.screens.Home.Companion.showNoteInfoPopup
import layouts.ui.screens.SplashScreen.Companion.requester
import layouts.ui.screens.SplashScreen.Companion.user

/**
 * This is the layout for the project section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class ProjectSection : Section() {

    /**
     * Function to show the content of the [ProjectSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    override fun showSection() {
        var isGitHub = false
        val hasGroup = currentProject.value.hasGroups()
        refreshItem()
        showSection {
            LazyColumn {
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
                    Row(
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        Text(
                            text = currentProject.value.name,
                            fontSize = 25.sp
                        )
                        Text(
                            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp).align(Alignment.Bottom),
                            text = "v. " + currentProject.value.version,
                            fontSize = 12.sp
                        )
                    }
                    var logoPath = ""
                    when (currentProject.value.repositoryPlatform) {
                        Github -> logoPath = "github-mark.svg"
                        GitLab -> logoPath = "gitlab-logo-500.svg"
                        else -> {}
                    }
                    isGitHub = logoPath.contains("github")
                    if (logoPath.isNotEmpty()) {
                        IconButton(
                            modifier = if (isGitHub) Modifier.padding(start = 5.dp) else Modifier,
                            onClick = { openUrl(currentProject.value.projectRepo) }
                        ) {
                            Icon(
                                modifier = Modifier.size(if (isGitHub) 35.dp else 60.dp),
                                painter = painterResource(logoPath),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                ) {
                    if (currentProject.value.hasGroups()) {
                        Text(
                            modifier = Modifier.padding(top = if (isGitHub) 5.dp else 0.dp),
                            text = "Author: ${currentProject.value.author.completeName}",
                            textAlign = TextAlign.Justify,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = currentProject.value.description,
                        textAlign = TextAlign.Justify,
                        fontSize = 14.sp
                    )
                    spaceContent()
                    var showUpdatesSection by remember { mutableStateOf(true) }
                    var showUpdatesIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Updates",
                            fontSize = 20.sp
                        )
                        IconButton(
                            onClick = {
                                showUpdatesSection = !showUpdatesSection
                                showUpdatesIcon =
                                    if (showUpdatesSection)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility
                            }
                        ) {
                            Icon(
                                imageVector = showUpdatesIcon,
                                contentDescription = null
                            )
                        }
                    }
                    if (showUpdatesSection) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = "Updates number: ${currentProject.value.updatesNumber}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "Last update: ${currentProject.value.lastUpdateDate}",
                            fontSize = 14.sp
                        )
                        val updates = currentProject.value.updates
                        if (updates.isNotEmpty()) {
                            spaceContent()
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .heightIn(0.dp, 820.dp),
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(updates) { update ->
                                    val isPublished = update.status == PUBLISHED
                                    val isScheduled = update.status == SCHEDULED
                                    var showMenu by remember { mutableStateOf(false) }
                                    val showDeleteDialog = mutableStateOf(false)
                                    val changeNotes = update.notes
                                    DeleteUpdate(
                                        show = showDeleteDialog,
                                        update = update
                                    )
                                    Card(
                                        modifier = Modifier.fillMaxWidth().height(400.dp).padding(bottom = 10.dp),
                                        shape = RoundedCornerShape(7),
                                        backgroundColor = Color.White,
                                        elevation = 2.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(20.dp).weight(10f).fillMaxHeight()
                                            ) {
                                                Text(
                                                    text = "v. ${update.targetVersion}",
                                                    fontSize = 18.sp
                                                )
                                                Text(
                                                    modifier = Modifier.padding(top = 5.dp),
                                                    text = "Update ID: ${update.id}",
                                                    fontSize = 14.sp
                                                )
                                                spaceContent()
                                                if (hasGroup) {
                                                    Text(
                                                        modifier = Modifier.padding(top = 5.dp),
                                                        text = "Author: ${update.author.completeName}",
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                Text(
                                                    modifier = Modifier.padding(top = 5.dp),
                                                    text = "Creation date: ${update.createDate}",
                                                    fontSize = 14.sp
                                                )
                                                spaceContent()
                                                if (!isScheduled) {
                                                    if (hasGroup) {
                                                        Text(
                                                            modifier = Modifier.padding(top = 5.dp),
                                                            text = "Started by: ${update.startedBy.completeName}",
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    Text(
                                                        modifier = Modifier.padding(top = 5.dp),
                                                        text = "Start date: ${update.startDate}",
                                                        fontSize = 14.sp
                                                    )
                                                    spaceContent()
                                                }
                                                if (isPublished) {
                                                    if (hasGroup) {
                                                        Text(
                                                            modifier = Modifier.padding(top = 5.dp),
                                                            text = "Published by: ${update.publishedBy.completeName}",
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    Text(
                                                        modifier = Modifier.padding(top = 5.dp),
                                                        text = "Publish date: ${update.publishDate}",
                                                        fontSize = 14.sp
                                                    )
                                                    spaceContent()
                                                    var timeGap = "days"
                                                    if (update.developmentDuration == 1)
                                                        timeGap = "day"
                                                    Text(
                                                        modifier = Modifier.padding(top = 5.dp),
                                                        text = "Development duration: ${update.developmentDuration} $timeGap",
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                if (!isPublished || isScheduled) {
                                                    Row(
                                                        modifier = Modifier.height(30.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "Change notes",
                                                            fontSize = 18.sp
                                                        )
                                                        IconButton(
                                                            onClick = {
                                                                currentUpdate = update
                                                                showCreateNotePopup.value = true
                                                            }
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier.size(20.dp),
                                                                imageVector = Icons.Default.Add,
                                                                contentDescription = null,
                                                                tint = RED_COLOR
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    Text(
                                                        modifier = Modifier.padding(top = 5.dp),
                                                        text = "Change notes",
                                                        fontSize = 18.sp
                                                    )
                                                }
                                                LazyColumn(
                                                    modifier = Modifier.padding(top = 5.dp)
                                                ) {
                                                    items(changeNotes) { note ->
                                                        if (!isPublished) {
                                                            if (isScheduled) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth().height(55.dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Row(
                                                                        modifier = Modifier.weight(4f).fillMaxWidth(),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Text(
                                                                            modifier = Modifier.padding(end = 5.dp),
                                                                            text = "-",
                                                                            fontSize = 14.sp
                                                                        )
                                                                        Text(
                                                                            modifier = Modifier.clickable {
                                                                                currentNote = note
                                                                                currentUpdate = update
                                                                                showNoteInfoPopup.value = true
                                                                            },
                                                                            text = note.content,
                                                                            fontSize = 14.sp,
                                                                            textAlign = TextAlign.Justify
                                                                        )
                                                                    }
                                                                    deleteNoteButton(update, note)
                                                                }
                                                            } else {
                                                                var isMarkedAsDone by remember { mutableStateOf(note.isMarkedAsDone) }
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Checkbox(
                                                                        colors = CheckboxDefaults.colors(
                                                                            checkedColor = PRIMARY_COLOR,
                                                                            checkmarkColor = Color.White
                                                                        ),
                                                                        checked = isMarkedAsDone,
                                                                        onCheckedChange = {
                                                                            if (it) {
                                                                                requester!!.execMarkChangeNoteAsDone(
                                                                                    currentProject.value.id,
                                                                                    update.id,
                                                                                    note.id
                                                                                )
                                                                                if (requester!!.successResponse())
                                                                                    isMarkedAsDone = true
                                                                                else
                                                                                    showSnack(requester!!.errorMessage())
                                                                            } else {
                                                                                requester!!.execMarkChangeNoteAsToDo(
                                                                                    currentProject.value.id,
                                                                                    update.id,
                                                                                    note.id
                                                                                )
                                                                                if (requester!!.successResponse())
                                                                                    isMarkedAsDone = false
                                                                                else
                                                                                    showSnack(requester!!.errorMessage())
                                                                            }
                                                                        }
                                                                    )
                                                                    Text(
                                                                        modifier = Modifier.weight(4f).fillMaxWidth()
                                                                            .clickable {
                                                                                currentNote = note
                                                                                currentUpdate = update
                                                                                showNoteInfoPopup.value = true
                                                                            },
                                                                        text = note.content,
                                                                        fontSize = 14.sp,
                                                                        textAlign = TextAlign.Justify,
                                                                        style = TextStyle(
                                                                            textDecoration =
                                                                            if (isMarkedAsDone)
                                                                                TextDecoration.LineThrough
                                                                            else
                                                                                null
                                                                        )
                                                                    )
                                                                    if (!isMarkedAsDone)
                                                                        deleteNoteButton(update, note)
                                                                }
                                                            }
                                                        } else {
                                                            Row(
                                                                modifier = Modifier.padding(bottom = 5.dp)
                                                                    .wrapContentHeight(),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    modifier = Modifier.padding(end = 5.dp),
                                                                    text = "-",
                                                                    fontSize = 14.sp
                                                                )
                                                                Text(
                                                                    modifier = Modifier.clickable {
                                                                        currentNote = note
                                                                        currentUpdate = update
                                                                        showNoteInfoPopup.value = true
                                                                    },
                                                                    text = note.content,
                                                                    fontSize = 14.sp,
                                                                    textAlign = TextAlign.Justify
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Row {
                                                    val showPublishUpdate = remember { mutableStateOf(false) }
                                                    val showNotes = remember { mutableStateOf(false) }
                                                    Box {
                                                        if (showPublishUpdate.value) {
                                                            PublishUpdate(showNotes) {
                                                                TextButton(
                                                                    onClick = {
                                                                        publishUpdate(
                                                                            showNotes, showPublishUpdate,
                                                                            update
                                                                        )
                                                                    },
                                                                    content = { Text(text = "Confirm") }
                                                                )
                                                            }
                                                        }
                                                        DropdownMenu(
                                                            modifier = Modifier.background(Color.White).width(200.dp),
                                                            expanded = showMenu,
                                                            onDismissRequest = { showMenu = false }
                                                        ) {
                                                            DropdownMenuItem(
                                                                onClick = {
                                                                    showMenu = false
                                                                    if (isScheduled)
                                                                        startUpdate(update)
                                                                    else if (!isPublished) {
                                                                        if (!areAllChangeNotesDone(changeNotes)) {
                                                                            showPublishUpdate.value = true
                                                                            showNotes.value = true
                                                                        } else {
                                                                            publishUpdate(
                                                                                showNotes, showPublishUpdate,
                                                                                update
                                                                            )
                                                                        }
                                                                    }
                                                                },
                                                                content = {
                                                                    Text(
                                                                        text =
                                                                        if (isScheduled)
                                                                            "Start update"
                                                                        else if (!isPublished)
                                                                            "Publish update"
                                                                        else "",
                                                                        fontSize = 14.sp
                                                                    )
                                                                    Column(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalAlignment = Alignment.End
                                                                    ) {
                                                                        Icon(
                                                                            modifier = Modifier.size(18.dp),
                                                                            imageVector =
                                                                            if (isScheduled)
                                                                                Icons.Default.PlayArrow
                                                                            else if (!isPublished)
                                                                                Icons.Default.Publish
                                                                            else
                                                                                Icons.Default.Edit,
                                                                            contentDescription = null,
                                                                            tint =
                                                                            if (isScheduled)
                                                                                YELLOW_COLOR
                                                                            else if (!isPublished)
                                                                                GREEN_COLOR
                                                                            else
                                                                                PRIMARY_COLOR
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                            DropdownMenuItem(
                                                                onClick = {
                                                                    showMenu = false
                                                                    showDeleteDialog.value = true
                                                                },
                                                                content = {
                                                                    Text(
                                                                        text = "Delete",
                                                                        fontSize = 14.sp
                                                                    )
                                                                    Column(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalAlignment = Alignment.End
                                                                    ) {
                                                                        Icon(
                                                                            modifier = Modifier.size(18.dp),
                                                                            imageVector = Icons.Default.Delete,
                                                                            contentDescription = null,
                                                                            tint = RED_COLOR
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            if (isPublished)
                                                                showDeleteDialog.value = true
                                                            else
                                                                showMenu = true
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isPublished) Icons.Default.Delete else Icons.Default.MoreVert,
                                                            contentDescription = null,
                                                            tint = if (isPublished) RED_COLOR else PRIMARY_COLOR
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier.background(
                                                            if (isPublished)
                                                                GREEN_COLOR
                                                            else if (isScheduled)
                                                                RED_COLOR
                                                            else
                                                                YELLOW_COLOR
                                                        )
                                                            .fillMaxHeight().width(10.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    val groups = currentProject.value.groups
                    if (groups.isNotEmpty()) {
                        spaceContent()
                        var showGroupsSection by remember { mutableStateOf(true) }
                        var showGroupsIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Groups",
                                fontSize = 20.sp
                            )
                            IconButton(
                                onClick = {
                                    showGroupsSection = !showGroupsSection
                                    showGroupsIcon =
                                        if (showGroupsSection)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility
                                }
                            ) {
                                Icon(
                                    imageVector = showGroupsIcon,
                                    contentDescription = null
                                )
                            }
                        }
                        if (showGroupsSection) {
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = "Groups number: ${groups.size}",
                                fontSize = 14.sp
                            )
                            spaceContent()
                            LazyVerticalGrid(
                                modifier = Modifier.padding(top = 20.dp).height(50.dp),
                                columns = GridCells.Adaptive(100.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(groups) { group ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        shape = RoundedCornerShape(15),
                                        backgroundColor = Color.White,
                                        elevation = 2.dp,
                                        onClick = { navToGroup(Sections.Project, group) },
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = group.name,
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
                        }
                    }
                    if (currentProject.value.publishedUpdates.isNotEmpty()) {
                        spaceContent()
                        var showStatsSection by remember { mutableStateOf(true) }
                        var showStatsIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stats",
                                fontSize = 20.sp
                            )
                            IconButton(
                                onClick = {
                                    showStatsSection = !showStatsSection
                                    showStatsIcon =
                                        if (showStatsSection)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility
                                }
                            ) {
                                Icon(
                                    imageVector = showStatsIcon,
                                    contentDescription = null
                                )
                            }
                        }
                        val publishedUpdates = currentProject.value.publishedUpdates
                        if (showStatsSection) {
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = "Total development days: ${currentProject.value.totalDevelopmentDays}",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier.padding(top = 5.dp),
                                text = "Average development time: ${currentProject.value.averageDevelopmentTime} days",
                                fontSize = 14.sp
                            )
                            if (publishedUpdates.isNotEmpty()) {
                                spaceContent()
                                Column(
                                    modifier = Modifier.padding(
                                        top = 15.dp,
                                        start = 10.dp,
                                        end = 10.dp,
                                        bottom = 10.dp
                                    ),
                                    content = { showProjectChart(publishedUpdates) }
                                )
                            }
                        }
                        spaceContent()
                    }
                }
            }
            }
        }
    }

    private fun refreshItem() {
        CoroutineScope(Dispatchers.Default).launch {
            while (user.id != null && activeScreen.value == Sections.Project) {
                val response = requester!!.execGetSingleProject(currentProject.value.id)
                if (requester!!.successResponse()) {
                    val tmpProject = Project(response)
                    if (needToRefresh(currentProject.value, tmpProject))
                        currentProject.value = tmpProject
                } else
                    showSnack(requester!!.errorMessage())
                delay(1000)
            }
        }
    }

    /**
     * Function to perform a [ProjectUpdate] start
     *
     * @param projectUpdate: the update to start
     */
    private fun startUpdate(projectUpdate: ProjectUpdate) {
        requester!!.execStartUpdate(currentProject.value.id, projectUpdate.id)
        if (!requester!!.successResponse())
            showSnack(requester!!.errorMessage())
    }

    /**
     * Function to perform a [ProjectUpdate] publishing
     *
     * @param showNotes: whether show the alert dialog for the change notes check
     * @param showPublishUpdate: whether execute the publishing check
     * @param projectUpdate: the update to publish
     */
    private fun publishUpdate(
        showNotes: MutableState<Boolean>,
        showPublishUpdate: MutableState<Boolean>,
        projectUpdate: ProjectUpdate
    ) {
        requester!!.execPublishUpdate(currentProject.value.id, projectUpdate.id)
        if (requester!!.successResponse()) {
            showPublishUpdate.value = false
            showNotes.value = false
        } else
            showSnack(requester!!.errorMessage())
    }

    /**
     * Function to perform a [Note] deletion
     *
     * @param update: the update where delete the change note
     * @param note: the note to delete
     */
    @Composable
    private fun deleteNoteButton(
        update: ProjectUpdate,
        note: Note
    ) {
        IconButton(
            onClick = {
                requester!!.execDeleteChangeNote(currentProject.value.id, update.id, note.id)
                if (!requester!!.successResponse())
                    showSnack(requester!!.errorMessage())
            }
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Delete,
                tint = RED_COLOR,
                contentDescription = null
            )
        }
    }

}