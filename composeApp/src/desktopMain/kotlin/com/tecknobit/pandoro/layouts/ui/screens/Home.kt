package layouts.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.GREEN_COLOR
import com.tecknobit.pandoro.helpers.RED_COLOR
import com.tecknobit.pandoro.helpers.spaceContent
import com.tecknobit.pandoro.layouts.components.popups.*
import com.tecknobit.pandoro.layouts.ui.theme.PrimaryLight
import com.tecknobit.pandoro.viewmodels.HomeScreenViewModel
import com.tecknobit.pandoro.viewmodels.ProfileSectionViewModel
import com.tecknobit.pandorocore.records.Changelog
import com.tecknobit.pandorocore.records.Changelog.ChangelogEvent.INVITED_GROUP
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.Note
import com.tecknobit.pandorocore.records.ProjectUpdate
import com.tecknobit.pandorocore.ui.ListManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import layouts.components.Sidebar
import layouts.components.Sidebar.Companion.SIDEBAR_WIDTH
import layouts.components.popups.*
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.*
import layouts.ui.sections.Section.*
import layouts.ui.sections.Section.Sections.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.decline
import pandoro.composeapp.generated.resources.join
import java.util.*

/**
 * This is the layout for the home screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 * @see ListManager
 */
//TODO: TO COMMENT
@OptIn(ExperimentalResourceApi::class)
class Home : UIScreen() {

    companion object {

        /**
         * **activeScreen** -> the instance of the active screen of the application to show
         */
        var activeScreen = mutableStateOf(Projects)

        /**
         * **changelogs** -> list of [Changelog] as changelogs for the [User]
         */
        lateinit var changelogs: StateFlow<List<Changelog>>

        /**
         * **ShowAddProjectPopup** -> flag whether show the [ShowAddProjectPopup]
         */
        lateinit var showAddProjectPopup: MutableState<Boolean>

        /**
         * **showEditPopup** -> flag whether show the [ShowEditProjectPopup]
         */
        lateinit var showEditPopup: MutableState<Boolean>

        /**
         * **showScheduleUpdatePopup** -> flag whether show the [showScheduleUpdatePopup]
         */
        lateinit var showScheduleUpdatePopup: MutableState<Boolean>

        /**
         * **ShowCreateNotePopup** -> flag whether show the [ShowCreateNotePopup]
         */
        lateinit var showCreateNotePopup: MutableState<Boolean>

        /**
         * **showNoteInfoPopup** -> flag whether show the [showNoteInfoPopup]
         */
        lateinit var showNoteInfoPopup: MutableState<Boolean>

        /**
         * **ShowAddMembersPopup** -> flag whether show the [ShowAddMembersPopup]
         */
        lateinit var showAddMembersPopup: MutableState<Boolean>

        /**
         * **showEditProjectGroupPopup** -> flag whether show the [showEditProjectGroupPopup]
         */
        lateinit var showEditProjectGroupPopup: MutableState<Boolean>

        /**
         * **ShowEditEmailPopup** -> flag whether show the [ShowEditEmailPopup]
         */
        lateinit var showEditEmailPopup: MutableState<Boolean>

        /**
         * **ShowEditPasswordPopup** -> flag whether show the [ShowEditPasswordPopup]
         */
        lateinit var showEditPasswordPopup: MutableState<Boolean>

        /**
         * **ShowAddGroupPopup** -> flag whether show the [ShowAddGroupPopup]
         */
        lateinit var showAddGroupPopup: MutableState<Boolean>

        /**
         * **currentProject** -> active [Project] instance
         */
        lateinit var currentProject: MutableState<com.tecknobit.pandorocore.records.Project>

        /**
         * **currentNote** -> active [Note] instance
         */
        lateinit var currentNote: Note

        /**
         * **currentUpdate** -> active [ProjectUpdate] instance
         */
        var currentUpdate: ProjectUpdate? = null

        /**
         * **currentGroup.value** -> active [Group] instance
         */
        lateinit var currentGroup: MutableState<Group>

    }

    /**
     * **projects** -> instance to show the [ProjectsSection]
     */
    private val projects = ProjectsSection()

    /**
     * **project** -> instance to show the [ProjectSection]
     */
    private val project = ProjectSection()

    /**
     * **group** -> instance to show the [GroupSection]
     */
    private val group = GroupSection()

    /**
     * **notes** -> instance to show the [NotesSection]
     */
    private val notes = NotesSection()

    /**
     * **overview** -> instance to show the [OverviewSection]
     */
    private val overview = OverviewSection()

    /**
     * **profile** -> instance to show the [Profile]
     */
    private val profile = ProfileSection()

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = HomeScreenViewModel()

    /**
     * *snackbarHostState* -> the host to launch the snackbar messages
     */
    private val snackbarHostState = SnackbarHostState()

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val profileViewModel = ProfileSectionViewModel(
        snackbarHostState = snackbarHostState
    )

    private lateinit var myChangelogs: List<Changelog>

    /**
     * Function to show the content of the [Home]
     *
     * No-any params required
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ShowScreen() {
        viewModel.setActiveContext(this::class.java)
        viewModel.refreshValues()
        myChangelogs = changelogs.collectAsState().value
        currentProject = remember { mutableStateOf(com.tecknobit.pandorocore.records.Project()) }
        currentGroup = remember { mutableStateOf(Group()) }
        showAddProjectPopup = remember { mutableStateOf(false) }
        showEditPopup = remember { mutableStateOf(false) }
        showScheduleUpdatePopup = remember { mutableStateOf(false) }
        showCreateNotePopup = remember { mutableStateOf(false) }
        showNoteInfoPopup = remember { mutableStateOf(false) }
        showAddMembersPopup = remember { mutableStateOf(false) }
        showEditProjectGroupPopup = remember { mutableStateOf(false) }
        showEditEmailPopup = remember { mutableStateOf(false) }
        showEditPasswordPopup = remember { mutableStateOf(false) }
        showAddGroupPopup = remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryLight
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
             },
            floatingActionButton = {
                var showNotifies = false
                changelogs.value.forEach { changelog ->
                    if (!changelog.isRed) {
                        showNotifies = true
                        return@forEach
                    }
                }
                //TODO: TO FIX BECAUSE IS NOT SHOWN
                if (showNotifies && activeScreen.value != Profile)
                    ShowNotifies()
                else {
                    when (activeScreen.value) {
                        Projects, Project, Notes, Profile -> CreateFab()
                        Group -> {
                            if (currentGroup.value.isUserMaintainer(user))
                                CreateFab()
                        }

                        else -> {}
                    }
                }
            }
        ) {
            Box (
                modifier = Modifier
                    .padding(
                        top = it.calculateTopPadding()
                    )
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .width(SIDEBAR_WIDTH)
                            .fillMaxHeight()
                    ) {
                        Sidebar().createSidebar()
                    }
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(20.dp)
                            .background(BACKGROUND_COLOR)
                    ) {
                        when (activeScreen.value) {
                            Projects -> projects.ShowSection()
                            Notes -> notes.ShowSection()
                            Overview -> overview.ShowSection()
                            Profile -> profile.ShowSection()
                            Project -> project.ShowSection()
                            Group -> group.ShowSection()
                        }
                    }
                }
            }
            ShowAddProjectPopup()
            if(showEditPopup.value)
                ShowEditProjectPopup(currentProject.value)
            showScheduleUpdatePopup()
            if(showCreateNotePopup.value)
                ShowCreateNotePopup(currentUpdate)
            if(showNoteInfoPopup.value)
                showNoteInfoPopup(currentNote, currentUpdate)
            ShowAddMembersPopup(currentGroup.value)
            showEditProjectGroupPopup()
            ShowEditEmailPopup()
            ShowEditPasswordPopup()
            ShowAddGroupPopup()
        }
    }

    /**
     * Function to create a [FloatingActionButton] to manage the popups
     *
     * No-any params required
     */
    @Composable
    private fun CreateFab() {
        FloatingActionButton(
            onClick = {
                when (activeScreen.value) {
                    Projects -> showAddProjectPopup.value = true
                    Project -> showScheduleUpdatePopup.value = true
                    Group -> showAddMembersPopup.value = true
                    Notes -> {
                        currentUpdate = null
                        showCreateNotePopup.value = true
                    }

                    Profile -> showAddGroupPopup.value = true
                    else -> {}
                }
            },
            content = { Icon(Icons.Filled.Add, "") }
        )
    }

    /**
     * Function to show the [changelogs] list
     *
     * No-any params required
     */
    @Composable
    private fun ShowNotifies() {
        LazyColumn(
            contentPadding = PaddingValues(20.dp)
        ) {
            items(
                items = myChangelogs,
                key = { changelog ->
                    changelog.id
                }
            ) { changelog ->
                val isJoinRequest = changelog.changelogEvent == INVITED_GROUP
                if (!changelog.isRed) {
                    Card(
                        modifier = Modifier
                            .padding(
                                bottom = 10.dp
                            )
                            .width(500.dp)
                            .wrapContentHeight(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        ),
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            if (!isJoinRequest) {
                                IconButton(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(
                                            top = 5.dp,
                                            end = 5.dp
                                        ).align(alignment = Alignment.End),
                                    onClick = {
                                        profileViewModel.readChangelog(
                                            changelog = changelog
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .height(25.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = 20.dp
                                    )
                            ) {
                                Text(
                                    text = changelog.title,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            top = 10.dp
                                        ),
                                    text = changelog.content,
                                    fontSize = 15.sp
                                )
                                if (isJoinRequest) {
                                    spaceContent()
                                    Row(
                                        modifier = Modifier
                                            .padding(
                                                top = 10.dp
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        Button(
                                            modifier = Modifier
                                                .weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = RED_COLOR,
                                                contentColor = Color.White
                                            ),
                                            onClick = {
                                                profileViewModel.declineInvitation(
                                                    group = changelog.group,
                                                    changelog = changelog
                                                )
                                            }
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.decline)
                                            )
                                        }
                                        Button(
                                            modifier = Modifier
                                                .weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = GREEN_COLOR,
                                                contentColor = Color.White
                                            ),
                                            onClick = {
                                                profileViewModel.acceptInvitation(
                                                    group = changelog.group,
                                                    changelog = changelog
                                                )
                                            }
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.join)
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

}