package layouts.ui.screens

import UpdaterDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.pandoro.helpers.ui.ListManager
import com.tecknobit.pandoro.records.Changelog
import com.tecknobit.pandoro.records.Changelog.ChangelogEvent.INVITED_GROUP
import com.tecknobit.pandoro.records.Group
import com.tecknobit.pandoro.records.Note
import com.tecknobit.pandoro.records.ProjectUpdate
import helpers.*
import kotlinx.coroutines.*
import layouts.components.Sidebar
import layouts.components.Sidebar.Companion.SIDEBAR_WIDTH
import layouts.components.popups.*
import layouts.ui.screens.Home.Companion.showAddGroupPopup
import layouts.ui.screens.Home.Companion.showAddProjectPopup
import layouts.ui.screens.SplashScreen.Companion.isRefreshing
import layouts.ui.screens.SplashScreen.Companion.requester
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.*
import layouts.ui.sections.Section.*
import layouts.ui.sections.Section.Companion.sectionCoroutineScope
import layouts.ui.sections.Section.Companion.sectionScaffoldState
import layouts.ui.sections.Section.Sections.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * This is the layout for the home screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 * @see ListManager
 */
class Home : UIScreen(), ListManager {

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

    companion object {

        /**
         * **activeScreen** -> the instance of the active screen of the application to show
         */
        lateinit var activeScreen: MutableState<Sections>

        /**
         * **changelogs** -> list of [Changelog] as changelogs for the [User]
         */
        val changelogs = mutableStateListOf<Changelog>()

        /**
         * **showAddProjectPopup** -> flag whether show the [showAddProjectPopup]
         */
        lateinit var showAddProjectPopup: MutableState<Boolean>

        /**
         * **showEditPopup** -> flag whether show the [showEditProjectPopup]
         */
        lateinit var showEditPopup: MutableState<Boolean>

        /**
         * **showScheduleUpdatePopup** -> flag whether show the [showScheduleUpdatePopup]
         */
        lateinit var showScheduleUpdatePopup: MutableState<Boolean>

        /**
         * **showCreateNotePopup** -> flag whether show the [showCreateNotePopup]
         */
        lateinit var showCreateNotePopup: MutableState<Boolean>

        /**
         * **showNoteInfoPopup** -> flag whether show the [showNoteInfoPopup]
         */
        lateinit var showNoteInfoPopup: MutableState<Boolean>

        /**
         * **showAddMembersPopup** -> flag whether show the [showAddMembersPopup]
         */
        lateinit var showAddMembersPopup: MutableState<Boolean>

        /**
         * **showEditProjectGroupPopup** -> flag whether show the [showEditProjectGroupPopup]
         */
        lateinit var showEditProjectGroupPopup: MutableState<Boolean>

        /**
         * **showEditEmailPopup** -> flag whether show the [showEditEmailPopup]
         */
        lateinit var showEditEmailPopup: MutableState<Boolean>

        /**
         * **showEditPasswordPopup** -> flag whether show the [showEditPasswordPopup]
         */
        lateinit var showEditPasswordPopup: MutableState<Boolean>

        /**
         * **showAddGroupPopup** -> flag whether show the [showAddGroupPopup]
         */
        lateinit var showAddGroupPopup: MutableState<Boolean>

        /**
         * **currentProject** -> active [Project] instance
         */
        lateinit var currentProject: MutableState<com.tecknobit.pandoro.records.Project>

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
     * **currentGroup.value** -> active [Group] instance
     */
    private var checkForUpdates: Boolean = true

    /**
     * Function to show the content of the [Home]
     *
     * No-any params required
     */
    @Composable
    override fun showScreen() {
        currentProject = remember { mutableStateOf(com.tecknobit.pandoro.records.Project()) }
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
            topBar = { TopAppBar(title = {}) },
            floatingActionButton = {
                val notRedChangelogs = mutableListOf<Changelog>()
                changelogs.forEach { changelog ->
                    if (!changelog.isRed)
                        notRedChangelogs.add(changelog)
                }
                if (notRedChangelogs.isNotEmpty() && activeScreen.value != Profile)
                    showNotifies()
                else {
                    when (activeScreen.value) {
                        Projects, Project, Notes, Profile -> createFab()
                        Group -> {
                            if (currentGroup.value.isUserMaintainer(user))
                                createFab()
                        }

                        else -> {}
                    }
                }
            }
        ) {
            Box {
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
                            Projects -> {
                                if (!isRefreshing.value) {
                                    refreshValues()
                                    isRefreshing.value = true
                                }
                                projects.showSection()
                            }
                            Notes -> notes.showSection()
                            Overview -> overview.showSection()
                            Profile -> profile.showSection()
                            Project -> project.showSection()
                            Group -> group.showSection()
                        }
                    }
                }
            }
            if (showAddProjectPopup.value)
                showAddProjectPopup()
            if (showEditPopup.value)
                showEditProjectPopup(currentProject.value)
            if (showScheduleUpdatePopup.value)
                showScheduleUpdatePopup()
            if (showCreateNotePopup.value)
                showCreateNotePopup(currentUpdate)
            if (showNoteInfoPopup.value)
                showNoteInfoPopup(currentNote, currentUpdate)
            if (showAddMembersPopup.value)
                showAddMembersPopup(currentGroup.value)
            if (showEditProjectGroupPopup.value)
                showEditProjectGroupPopup()
            if (showEditEmailPopup.value)
                showEditEmailPopup()
            if (showEditPasswordPopup.value)
                showEditPasswordPopup()
            if (showAddGroupPopup.value)
                showAddGroupPopup()
        }
        if (checkForUpdates) {
            checkForUpdates = false
            MaterialTheme(
                colors = Colors(
                    primary = PRIMARY_COLOR,
                    primaryVariant = PRIMARY_COLOR,
                    secondary = BACKGROUND_COLOR,
                    secondaryVariant = PRIMARY_COLOR,
                    background = BACKGROUND_COLOR,
                    surface = BACKGROUND_COLOR,
                    error = RED_COLOR,
                    onPrimary = BACKGROUND_COLOR,
                    onSecondary = PRIMARY_COLOR,
                    onBackground = BACKGROUND_COLOR,
                    onSurface = PRIMARY_COLOR,
                    onError = RED_COLOR,
                    isLight = true
                )
            ) {
                UpdaterDialog(
                    locale = Locale.UK,
                    appName = appName,
                    currentVersion = appVersion
                )
                TimeFormatter.changeDefaultPattern("dd/MM/yyyy HH:mm:ss")
            }
        }
    }

    /**
     * Function to create a [FloatingActionButton] to manage the popups
     *
     * No-any params required
     */
    @Composable
    private fun createFab() {
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
    private fun showNotifies() {
        LazyColumn(
            contentPadding = PaddingValues(20.dp)
        ) {
            items(
                items = changelogs,
                key = { changelog ->
                    changelog.id
                }
            ) { changelog ->
                val isJoinRequest = changelog.changelogEvent == INVITED_GROUP
                if (!changelog.isRed) {
                    Card(
                        modifier = Modifier.padding(bottom = 10.dp)
                            .size(
                                width = 500.dp,
                                height = if (!isJoinRequest) 115.dp else 155.dp
                            ),
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(5.dp),
                        elevation = 10.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (!isJoinRequest) {
                                IconButton(
                                    modifier = Modifier.size(25.dp)
                                        .padding(
                                            top = 5.dp,
                                            end = 5.dp
                                        ).align(alignment = Alignment.End),
                                    onClick = {
                                        requester!!.execReadChangelog(changelog.id)
                                        if (requester!!.successResponse())
                                            changelogs.remove(changelog)
                                        else
                                            showSnack(
                                                sectionCoroutineScope,
                                                sectionScaffoldState,
                                                requester!!.errorMessage()
                                            )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            } else
                                Spacer(Modifier.height(25.dp))
                            Column(
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                            ) {
                                Text(
                                    text = changelog.title,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = changelog.content,
                                    fontSize = 15.sp
                                )
                                if (isJoinRequest) {
                                    spaceContent()
                                    Row(
                                        modifier = Modifier.padding(top = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        Button(
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = RED_COLOR,
                                                contentColor = Color.White
                                            ),
                                            onClick = {
                                                requester!!.execDeclineInvitation(changelog.group.id, changelog.id)
                                                if (requester!!.successResponse())
                                                    changelogs.remove(changelog)
                                                else {
                                                    showSnack(
                                                        sectionCoroutineScope, sectionScaffoldState,
                                                        requester!!.errorMessage()
                                                    )
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = "Decline"
                                            )
                                        }
                                        Button(
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = GREEN_COLOR,
                                                contentColor = Color.White
                                            ),
                                            onClick = {
                                                requester!!.execAcceptInvitation(changelog.group.id, changelog.id)
                                                if (requester!!.successResponse())
                                                    changelogs.remove(changelog)
                                                else {
                                                    showSnack(
                                                        sectionCoroutineScope, sectionScaffoldState,
                                                        requester!!.errorMessage()
                                                    )
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = "Join"
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

    /**
     * Function to refresh a list of items to display in the UI
     *
     * No-any params required
     */
    override fun refreshValues() {
        CoroutineScope(Dispatchers.Default).launch {
            var response: String
            while (user.id != null) {
                if (activeScreen.value == Projects || activeScreen.value == Group || activeScreen.value == Overview) {
                    val tmpProjectsList = mutableStateListOf<com.tecknobit.pandoro.records.Project>()
                    response = requester!!.execProjectsList()
                    if (requester!!.successResponse()) {
                        try {
                            val jProjects = JSONArray(response)
                            jProjects.forEach { jProject ->
                                tmpProjectsList.add(com.tecknobit.pandoro.records.Project(jProject as JSONObject))
                            }
                            if (needToRefresh(ProjectsSection.projectsList, tmpProjectsList)) {
                                ProjectsSection.projectsList.clear()
                                ProjectsSection.projectsList.addAll(tmpProjectsList)
                                user.setProjects(ProjectsSection.projectsList)
                            }
                        } catch (_: JSONException) {
                        }
                    } else
                        showSnack(sectionCoroutineScope, sectionScaffoldState, requester!!.errorMessage())
                }
                if (activeScreen.value == Profile || activeScreen.value == Project) {
                    val tmpGroups = mutableStateListOf<Group>()
                    response = requester!!.execGroupsList()
                    if (requester!!.successResponse()) {
                        try {
                            val jGroups = JSONArray(response)
                            jGroups.forEach { jGroup ->
                                tmpGroups.add(Group(jGroup as JSONObject))
                            }
                            if (needToRefresh(ProfileSection.groups, tmpGroups)) {
                                ProfileSection.groups.clear()
                                ProfileSection.groups.addAll(tmpGroups)
                                user.setGroups(ProfileSection.groups)
                            }
                        } catch (_: JSONException) {
                        }
                    } else
                        showSnack(sectionCoroutineScope, sectionScaffoldState, requester!!.errorMessage())
                }
                val tmpChangelogs = mutableStateListOf<Changelog>()
                response = requester!!.execChangelogsList()
                if (requester!!.successResponse()) {
                    try {
                        val jChangelogs = JSONArray(response)
                        jChangelogs.forEach { jChangelog ->
                            tmpChangelogs.add(Changelog(jChangelog as JSONObject))
                        }
                        if (needToRefresh(changelogs, tmpChangelogs)) {
                            changelogs.clear()
                            changelogs.addAll(tmpChangelogs)
                        }
                    } catch (_: Exception) {
                    }
                } else
                    showSnack(sectionCoroutineScope, sectionScaffoldState, requester!!.errorMessage())
                delay(1000)
            }
        }
    }

}