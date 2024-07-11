package com.tecknobit.pandoro.layouts.ui.sections

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.tecknobit.pandoro.helpers.*
import com.tecknobit.pandoro.layouts.components.DeleteUpdate
import com.tecknobit.pandoro.layouts.components.PublishUpdate
import com.tecknobit.pandoro.layouts.components.popups.clipboard
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentNote
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentProject
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.currentUpdate
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showCreateNotePopup
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.showNoteInfoPopup
import com.tecknobit.pandoro.viewmodels.ProjectSectionViewModel
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.areAllChangeNotesDone
import com.tecknobit.pandorocore.records.Note
import com.tecknobit.pandorocore.records.Project
import com.tecknobit.pandorocore.records.Project.RepositoryPlatform.GitLab
import com.tecknobit.pandorocore.records.Project.RepositoryPlatform.Github
import com.tecknobit.pandorocore.records.ProjectUpdate
import com.tecknobit.pandorocore.records.ProjectUpdate.Status.PUBLISHED
import com.tecknobit.pandorocore.records.ProjectUpdate.Status.SCHEDULED
import com.tecknobit.pandorocore.ui.SingleItemManager
import com.tecknobit.pandorocore.ui.formatNotesAsMarkdown
import layouts.components.showProjectChart
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*
import java.awt.datatransfer.StringSelection

/**
 * This is the layout for the project section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 * @see SingleItemManager
 */
//TODO: TO COMMENT
class ProjectSection : Section() {

    private val viewModel by lazy {
        ProjectSectionViewModel(
            initialProject = currentProject.value,
            snackbarHostState = snackbarHostState
        )
    }

    private lateinit var project: Project

    /**
     * Function to show the content of the [ProjectSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun ShowSection() {
        viewModel.setActiveContext(this::class.java)
        var isGitHub = false
        var hasGroup = currentProject.value.hasGroups()
        viewModel.refreshProject {
           hasGroup = project.hasGroups()
        }
        project = viewModel.project.collectAsState().value
        ShowSection {
            LazyColumn {
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
                        Row(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {
                            Text(
                                text = project.name,
                                fontSize = 25.sp
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = 10.dp,
                                        bottom = 5.dp
                                    )
                                    .align(Alignment.Bottom),
                                text = "v. " + project.version,
                                fontSize = 12.sp
                            )
                        }
                        var logoPath = ""
                        when (project.repositoryPlatform) {
                            Github -> logoPath = "github.svg"
                            GitLab -> logoPath = "gitlab.svg"
                            else -> {}
                        }
                        isGitHub = logoPath.contains("github")
                        if (logoPath.isNotEmpty()) {
                            IconButton(
                                modifier = if (isGitHub) {
                                    Modifier
                                        .padding(
                                            start = 5.dp
                                        )
                                } else
                                    Modifier,
                                onClick = { openUrl(project.projectRepo) }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(
                                            if (isGitHub)
                                                35.dp
                                            else
                                                60.dp
                                        ),
                                    painter = painterResource(logoPath),
                                    contentDescription = null
                                )
                            }
                        }
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
                        if (project.hasGroups()) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = if (isGitHub)
                                            5.dp
                                        else
                                            0.dp
                                    ),
                                text = stringResource(Res.string.author) + project.author.completeName,
                                textAlign = TextAlign.Justify,
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = project.description,
                            textAlign = TextAlign.Justify,
                            fontSize = 14.sp
                        )
                        spaceContent()
                        var showUpdatesSection by remember { mutableStateOf(true) }
                        var showUpdatesIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.updates),
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
                                modifier = Modifier
                                    .padding(
                                        top = 10.dp
                                    ),
                                text = stringResource(Res.string.updates_number) + ": ${project.updatesNumber}",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = 5.dp
                                    ),
                                text = stringResource(Res.string.last_update) + " ${project.lastUpdateDate}",
                                fontSize = 14.sp
                            )
                            val updates = project.updates
                            if (updates.isNotEmpty()) {
                                spaceContent()
                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .padding(
                                            top = 20.dp
                                        )
                                        .heightIn(
                                            min = 0.dp,
                                            max = 820.dp
                                        ),
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(
                                        items = updates,
                                        key = { update ->
                                            update.id
                                        }
                                    ) { update ->
                                        val isPublished = update.status == PUBLISHED
                                        val isScheduled = update.status == SCHEDULED
                                        var showMenu by remember { mutableStateOf(false) }
                                        val showDeleteDialog = remember { mutableStateOf(false) }
                                        val changeNotes = update.notes
                                        if(!showDeleteDialog.value)
                                            viewModel.restartRefresher()
                                        DeleteUpdate(
                                            show = showDeleteDialog,
                                            update = update
                                        )
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(400.dp)
                                                .padding(
                                                    bottom = 10.dp
                                                ),
                                            shape = RoundedCornerShape(7),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 2.dp
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(20.dp)
                                                        .weight(10f)
                                                        .fillMaxHeight()
                                                ) {
                                                    Text(
                                                        text = "v. ${update.targetVersion}",
                                                        fontSize = 18.sp
                                                    )
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                top = 5.dp
                                                            ),
                                                        text = stringResource(Res.string.update_id) + " ${update.id}",
                                                        fontSize = 14.sp
                                                    )
                                                    spaceContent()
                                                    val author = update.author
                                                    if (hasGroup && author != null) {
                                                        Text(
                                                            modifier = Modifier
                                                                .padding(
                                                                    top = 5.dp
                                                                ),
                                                            text = stringResource(Res.string.author) + " ${author.completeName}",
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(
                                                                top = 5.dp
                                                            ),
                                                        text = stringResource(Res.string.creation_date) + " ${update.createDate}",
                                                        fontSize = 14.sp
                                                    )
                                                    spaceContent()
                                                    if (!isScheduled) {
                                                        val startedBy = update.startedBy
                                                        if (hasGroup && startedBy != null) {
                                                            Text(
                                                                modifier = Modifier
                                                                    .padding(
                                                                        top = 5.dp
                                                                    ),
                                                                text = stringResource(Res.string.started_by) + " ${startedBy.completeName}",
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                        Text(
                                                            modifier = Modifier
                                                                .padding(
                                                                    top = 5.dp
                                                                ),
                                                            text = stringResource(Res.string.start_date) + " ${update.startDate}",
                                                            fontSize = 14.sp
                                                        )
                                                        spaceContent()
                                                    }
                                                    if (isPublished) {
                                                        val publishedBy = update.publishedBy
                                                        if (hasGroup && publishedBy != null) {
                                                            Text(
                                                                modifier = Modifier
                                                                    .padding(
                                                                        top = 5.dp
                                                                    ),
                                                                text = stringResource(Res.string.published_by) + " ${publishedBy.completeName}",
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                        Text(
                                                            modifier = Modifier
                                                                .padding(
                                                                    top = 5.dp
                                                                ),
                                                            text = stringResource(Res.string.publish_date) + " ${update.publishDate}",
                                                            fontSize = 14.sp
                                                        )
                                                        spaceContent()
                                                        var timeGap = stringResource(Res.string.days)
                                                        if (update.developmentDuration == 1)
                                                            timeGap = stringResource(Res.string.day)
                                                        Text(
                                                            modifier = Modifier
                                                                .padding(
                                                                    top = 5.dp
                                                                ),
                                                            text = stringResource(Res.string.development_duration) +
                                                                    ": ${update.developmentDuration} $timeGap",
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    if (!isPublished || isScheduled) {
                                                        Row(
                                                            modifier = Modifier
                                                                .height(30.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = stringResource(Res.string.change_notes)
                                                                        + ": ${changeNotes.size}",
                                                                fontSize = 18.sp
                                                            )
                                                            IconButton(
                                                                onClick = {
                                                                    currentUpdate = update
                                                                    showCreateNotePopup.value = true
                                                                }
                                                            ) {
                                                                Icon(
                                                                    modifier = Modifier
                                                                        .size(20.dp),
                                                                    imageVector = Icons.Default.Add,
                                                                    contentDescription = null,
                                                                    tint = RED_COLOR
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Text(
                                                            modifier = Modifier
                                                                .padding(
                                                                    top = 5.dp
                                                                ),
                                                            text = stringResource(Res.string.change_notes)
                                                                    + ": ${changeNotes.size}",
                                                            fontSize = 18.sp
                                                        )
                                                    }
                                                    LazyColumn(
                                                        modifier = Modifier
                                                            .padding(
                                                                top = 5.dp
                                                            )
                                                    ) {
                                                        items(
                                                            items = changeNotes,
                                                            key = { changeNote ->
                                                                changeNote.id
                                                            }
                                                        ) { note ->
                                                            if (!isPublished) {
                                                                if (isScheduled) {
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .height(55.dp),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Row(
                                                                            modifier = Modifier
                                                                                .weight(4f)
                                                                                .fillMaxWidth(),
                                                                            verticalAlignment = Alignment.CenterVertically
                                                                        ) {
                                                                            Text(
                                                                                modifier = Modifier
                                                                                    .padding(
                                                                                        end = 5.dp
                                                                                    ),
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
                                                                    val isMarkedAsDone = mutableStateOf(note.isMarkedAsDone)
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .height(50.dp),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Checkbox(
                                                                            colors = CheckboxDefaults.colors(
                                                                                checkedColor = PRIMARY_COLOR,
                                                                                checkmarkColor = Color.White
                                                                            ),
                                                                            checked = isMarkedAsDone.value,
                                                                            onCheckedChange = {
                                                                                viewModel.manageChangeNote(
                                                                                    markedAsDone = isMarkedAsDone,
                                                                                    update = update,
                                                                                    changeNote = note
                                                                                )
                                                                            }
                                                                        )
                                                                        Text(
                                                                            modifier = Modifier
                                                                                .weight(4f)
                                                                                .fillMaxWidth()
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
                                                                                if (isMarkedAsDone.value)
                                                                                    TextDecoration.LineThrough
                                                                                else
                                                                                    null
                                                                            )
                                                                        )
                                                                        if (!isMarkedAsDone.value)
                                                                            deleteNoteButton(update, note)
                                                                    }
                                                                }
                                                            } else {
                                                                Row(
                                                                    modifier = Modifier
                                                                        .padding(
                                                                            bottom = 5.dp
                                                                        )
                                                                        .wrapContentHeight(),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        modifier = Modifier
                                                                            .padding(
                                                                                end = 5.dp
                                                                            ),
                                                                        text = "-",
                                                                        fontSize = 14.sp
                                                                    )
                                                                    Text(
                                                                        modifier = Modifier
                                                                            .clickable {
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
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight(),
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
                                                                        content = {
                                                                            Text(
                                                                                text = stringResource(Res.string.confirm)
                                                                            )
                                                                        }
                                                                    )
                                                                }
                                                            }
                                                            DropdownMenu(
                                                                modifier = Modifier
                                                                    .background(Color.White)
                                                                    .width(200.dp),
                                                                expanded = showMenu,
                                                                onDismissRequest = { showMenu = false }
                                                            ) {
                                                                if(update.notes.isNotEmpty()) {
                                                                    DropdownMenuItem(
                                                                        text = {
                                                                            Text(
                                                                                text = stringResource(Res.string.export_notes)
                                                                            )
                                                                        },
                                                                        trailingIcon = {
                                                                            Icon(
                                                                                imageVector = Icons.Default.ContentPaste,
                                                                                contentDescription = null
                                                                            )
                                                                        },
                                                                        onClick = {
                                                                            showMenu = false
                                                                            clipboard.setContents(
                                                                                StringSelection(
                                                                                    formatNotesAsMarkdown(
                                                                                        update = update
                                                                                    )
                                                                            ), null)
                                                                            showSnack(
                                                                                Res.string.notes_formatted_in_markdown_copied
                                                                            )
                                                                        }
                                                                    )
                                                                }
                                                                if(!isPublished) {
                                                                    DropdownMenuItem(
                                                                        onClick = {
                                                                            showMenu = false
                                                                            if (isScheduled)
                                                                                startUpdate(update)
                                                                            else {
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
                                                                        text = {
                                                                            Text(
                                                                                text = stringResource(
                                                                                    if (isScheduled)
                                                                                        Res.string.start_update
                                                                                    else
                                                                                        Res.string.publish
                                                                                ),
                                                                                fontSize = 14.sp
                                                                            )
                                                                            Column(
                                                                                modifier = Modifier
                                                                                    .fillMaxWidth(),
                                                                                horizontalAlignment = Alignment.End
                                                                            ) {
                                                                                Icon(
                                                                                    modifier = Modifier
                                                                                        .size(18.dp),
                                                                                    imageVector =
                                                                                    if (isScheduled)
                                                                                        Icons.Default.PlayArrow
                                                                                    else
                                                                                        Icons.Default.Publish,
                                                                                    contentDescription = null,
                                                                                    tint =
                                                                                    if (isScheduled)
                                                                                        YELLOW_COLOR
                                                                                    else
                                                                                        GREEN_COLOR
                                                                                )
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                                DropdownMenuItem(
                                                                    onClick = {
                                                                        showMenu = false
                                                                        viewModel.suspendRefresher()
                                                                        showDeleteDialog.value = true
                                                                    },
                                                                    text = {
                                                                        Text(
                                                                            text = stringResource(Res.string.delete),
                                                                            fontSize = 14.sp
                                                                        )
                                                                        Column(
                                                                            modifier = Modifier
                                                                                .fillMaxWidth(),
                                                                            horizontalAlignment = Alignment.End
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
                                                                )
                                                            }
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                showMenu = true
                                                                currentUpdate = update
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.MoreVert,
                                                                contentDescription = null,
                                                                tint = PRIMARY_COLOR
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .background(
                                                                    if (isPublished)
                                                                        GREEN_COLOR
                                                                    else if (isScheduled)
                                                                        RED_COLOR
                                                                    else
                                                                        YELLOW_COLOR
                                                                )
                                                                .fillMaxHeight()
                                                                .width(10.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val groups = project.groups
                        if (groups.isNotEmpty()) {
                            spaceContent()
                            var showGroupsSection by remember { mutableStateOf(true) }
                            var showGroupsIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(Res.string.groups),
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
                                    modifier = Modifier
                                        .padding(
                                            top = 10.dp
                                        ),
                                    text = stringResource(Res.string.groups_number) + " ${groups.size}",
                                    fontSize = 14.sp
                                )
                                spaceContent()
                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .padding(
                                            top = 20.dp
                                        )
                                        .height(50.dp),
                                    columns = GridCells.Adaptive(130.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(
                                        items = groups,
                                        key = { group ->
                                            group.id
                                        }
                                    ) { group ->
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
                                            onClick = { navToGroup(Sections.Project, group) },
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
                                                        text = group.name,
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
                            }
                        }
                        if (project.publishedUpdates.isNotEmpty()) {
                            spaceContent()
                            var showStatsSection by remember { mutableStateOf(true) }
                            var showStatsIcon by remember { mutableStateOf(Icons.Default.VisibilityOff) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth().
                                    padding(
                                        top = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(Res.string.stats),
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
                            val publishedUpdates = project.publishedUpdates
                            if (showStatsSection) {
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = stringResource(Res.string.total_development_days) + " ${project.totalDevelopmentDays}",
                                    fontSize = 14.sp
                                )
                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = stringResource(Res.string.average_development_time) + " ${project.averageDevelopmentTime} "
                                            + stringResource(Res.string.days),
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

    /**
     * Function to perform a [ProjectUpdate] start
     *
     * @param projectUpdate: the update to start
     */
    private fun startUpdate(
        projectUpdate: ProjectUpdate
    ) {
        viewModel.startUpdate(
            update = projectUpdate
        )
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
        viewModel.publishUpdate(
            update = projectUpdate,
            onSuccess = {
                showPublishUpdate.value = false
                showNotes.value = false
            }
        )
    }

    /**
     * Function to perform a [Note] deletion
     *
     * @param update: the update where delete the change changeNote
     * @param changeNote: the changeNote to delete
     */
    @Composable
    private fun deleteNoteButton(
        update: ProjectUpdate,
        changeNote: Note
    ) {
        IconButton(
            onClick = {
                viewModel.deleteChangeNote(
                    update = update,
                    changeNote = changeNote
                )
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = Icons.Default.Delete,
                tint = RED_COLOR,
                contentDescription = null
            )
        }
    }

}