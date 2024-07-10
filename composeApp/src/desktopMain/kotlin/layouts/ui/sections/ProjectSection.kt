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
import androidx.compose.material.icons.Icons
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
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.areAllChangeNotesDone
import com.tecknobit.pandorocore.records.Note
import com.tecknobit.pandorocore.records.Project.RepositoryPlatform.GitLab
import com.tecknobit.pandorocore.records.Project.RepositoryPlatform.Github
import com.tecknobit.pandorocore.records.ProjectUpdate
import com.tecknobit.pandorocore.records.ProjectUpdate.Status.PUBLISHED
import com.tecknobit.pandorocore.records.ProjectUpdate.Status.SCHEDULED
import com.tecknobit.pandorocore.ui.SingleItemManager
import com.tecknobit.pandorocore.ui.formatNotesAsMarkdown
import helpers.*
import layouts.components.DeleteUpdate
import layouts.components.PublishUpdate
import layouts.components.popups.clipboard
import layouts.components.showProjectChart
import layouts.ui.screens.Home.Companion.currentNote
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.currentUpdate
import layouts.ui.screens.Home.Companion.showCreateNotePopup
import layouts.ui.screens.Home.Companion.showNoteInfoPopup
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
class ProjectSection : Section(), SingleItemManager {

    /**
     * Function to show the content of the [ProjectSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun ShowSection() {
        var isGitHub = false
        val hasGroup = currentProject.value.hasGroups()
        refreshItem()
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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                        Row(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {
                            Text(
                                text = currentProject.value.name,
                                fontSize = 25.sp
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = 10.dp,
                                        bottom = 5.dp
                                    )
                                    .align(Alignment.Bottom),
                                text = "v. " + currentProject.value.version,
                                fontSize = 12.sp
                            )
                        }
                        var logoPath = ""
                        when (currentProject.value.repositoryPlatform) {
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
                                onClick = { openUrl(currentProject.value.projectRepo) }
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
                        if (currentProject.value.hasGroups()) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = if (isGitHub)
                                            5.dp
                                        else
                                            0.dp
                                    ),
                                text = stringResource(Res.string.author) + "${currentProject.value.author.completeName}",
                                textAlign = TextAlign.Justify,
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = currentProject.value.description,
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
                                text = stringResource(Res.string.updates_number) + ": ${currentProject.value.updatesNumber}",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = 5.dp
                                    ),
                                text = stringResource(Res.string.last_update) + " ${currentProject.value.lastUpdateDate}",
                                fontSize = 14.sp
                            )
                            val updates = currentProject.value.updates
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
                                        val showDeleteDialog = mutableStateOf(false)
                                        val changeNotes = update.notes
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
                                                                    var isMarkedAsDone by remember { mutableStateOf(note.isMarkedAsDone) }
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
                                                                            checked = isMarkedAsDone,
                                                                            onCheckedChange = {
                                                                                /*if (it) {
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
                                                                                }*/
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
                                                            onClick = { showMenu = true }
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
                        if (currentProject.value.publishedUpdates.isNotEmpty()) {
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
                            val publishedUpdates = currentProject.value.publishedUpdates
                            if (showStatsSection) {
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = stringResource(Res.string.total_development_days) + " ${currentProject.value.totalDevelopmentDays}",
                                    fontSize = 14.sp
                                )
                                Text(
                                    modifier = Modifier.padding(top = 5.dp),
                                    text = stringResource(Res.string.average_development_time) + " ${currentProject.value.averageDevelopmentTime} "
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
     * Function to refresh an item to display in the UI
     *
     * No-any params required
     */
    override fun refreshItem() {
        /*CoroutineScope(Dispatchers.Default).launch {
            while (user.id != null && activeScreen.value == Sections.Project) {
                try {
                    val response = requester!!.execGetSingleProject(currentProject.value.id)
                    if (requester!!.successResponse()) {
                        val tmpProject = Project(response)
                        if (needToRefresh(currentProject.value, tmpProject))
                            currentProject.value = tmpProject
                    } else
                        showSnack(requester!!.errorMessage())
                } catch (_: JSONException) {
                }
                delay(1000)
            }
        }*/
    }

    /**
     * Function to perform a [ProjectUpdate] start
     *
     * @param projectUpdate: the update to start
     */
    private fun startUpdate(projectUpdate: ProjectUpdate) {
        /*requester!!.execStartUpdate(currentProject.value.id, projectUpdate.id)
        if (!requester!!.successResponse())
            showSnack(requester!!.errorMessage())*/
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
        /*requester!!.execPublishUpdate(currentProject.value.id, projectUpdate.id)
        if (requester!!.successResponse()) {
            showPublishUpdate.value = false
            showNotes.value = false
        } else
            showSnack(requester!!.errorMessage())*/
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
                /*requester!!.execDeleteChangeNote(currentProject.value.id, update.id, note.id)
                if (!requester!!.successResponse())
                    showSnack(requester!!.errorMessage())*/
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