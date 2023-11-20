package layouts.ui.screens

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
import com.tecknobit.pandoro.records.Changelog
import com.tecknobit.pandoro.records.Changelog.ChangelogEvent.INVITED_GROUP
import com.tecknobit.pandoro.records.Group
import com.tecknobit.pandoro.records.Note
import com.tecknobit.pandoro.records.ProjectUpdate
import helpers.BACKGROUND_COLOR
import helpers.GREEN_COLOR
import helpers.RED_COLOR
import helpers.spaceContent
import layouts.components.Sidebar
import layouts.components.Sidebar.Companion.activeScreen
import layouts.components.popups.*
import layouts.ui.screens.Home.Companion.showAddGroupPopup
import layouts.ui.screens.Home.Companion.showAddProjectPopup
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.*
import layouts.ui.sections.Section.Sections.*

/**
 * This is the layout for the home screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class Home : UIScreen() {

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
         * **notifies** -> list of [Changelog] as notifies for the [User]
         */
        // TODO: CHECK TO CHANGE
        val notifies = mutableStateListOf<Changelog>()

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
        lateinit var currentProject: com.tecknobit.pandoro.records.Project

        /**
         * **currentNote** -> active [Note] instance
         */
        lateinit var currentNote: Note

        /**
         * **currentUpdate** -> active [ProjectUpdate] instance
         */
        var currentUpdate: ProjectUpdate? = null

        /**
         * **currentGroup** -> active [Group] instance
         */
        lateinit var currentGroup: Group

    }

    /**
     * Function to show the content of the [Home]
     *
     * No-any params required
     */
    @Composable
    override fun showScreen() {
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
                if (notifies.isNotEmpty())
                    showNotifies()
                else {
                    when (activeScreen.value) {
                        Projects, Project, Notes, Profile -> createFab()
                        Group -> {
                            if (currentGroup.isUserMaintainer(user))
                                createFab()
                        }

                        else -> {}
                    }
                }
            }
        ) {
            Box {
                // TODO: REMOVE THIS SNIPPET OF THE LAUNCH
                /*rememberCoroutineScope().launch {
                    while (true) {
                        delay(Random().nextLong(40000))
                        when (Random().nextInt(3)) {
                            0 -> {
                                notifies.add(
                                    Changelog(
                                        "",
                                        Changelog.ChangelogEvent.UPDATE_DELETED,
                                        System.currentTimeMillis(),
                                        Project("TecknobitCiaoaa"),
                                        "1.0.1.0.10",
                                        true
                                    )
                                )
                            }

                            1 -> {
                                notifies.add(
                                    Changelog(
                                        "",
                                        INVITED_GROUP,
                                        System.currentTimeMillis(),
                                        toImportFromLibrary.Group(
                                            "",
                                            "TecknobitCiaoaa",
                                            "ciao",
                                            ArrayList(
                                                listOf(
                                                    toImportFromLibrary.Group.GroupMember(
                                                        "manu0",
                                                        "Manuel",
                                                        "Maurizio",
                                                        toImportFromLibrary.Group.Role.ADMIN
                                                    ),
                                                    toImportFromLibrary.Group.GroupMember(
                                                        "Gabriele",
                                                        "Marengo",
                                                        toImportFromLibrary.Group.Role.MAINTAINER
                                                    )
                                                )
                                            ),
                                            ArrayList()
                                        ),
                                        true
                                    )
                                )
                            }

                            2 -> {
                                notifies.add(
                                    Changelog(
                                        "",
                                        Changelog.ChangelogEvent.GROUP_DELETED,
                                        System.currentTimeMillis(),
                                        toImportFromLibrary.Group(
                                            "",
                                            "TecknobitJOIN",
                                            "ciao",
                                            ArrayList(
                                                listOf(
                                                    toImportFromLibrary.Group.GroupMember(
                                                        "manu0",
                                                        "Manuel",
                                                        "Maurizio",
                                                        toImportFromLibrary.Group.Role.ADMIN
                                                    ),
                                                    toImportFromLibrary.Group.GroupMember(
                                                        "Gabriele",
                                                        "Marengo",
                                                        toImportFromLibrary.Group.Role.MAINTAINER
                                                    )
                                                )
                                            ),
                                            ArrayList()
                                        ),
                                        false
                                    )
                                )
                            }
                        }
                    }
                }*/
                Row {
                    Column(modifier = Modifier.width(250.dp).fillMaxHeight()) {
                        Sidebar().createSidebar()
                    }
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp).background(BACKGROUND_COLOR)
                    ) {
                        when (activeScreen.value) {
                            Projects -> projects.showSection()
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
                showEditProjectPopup(currentProject)
            if (showScheduleUpdatePopup.value)
                showScheduleUpdatePopup()
            if (showCreateNotePopup.value)
                showCreateNotePopup(currentUpdate)
            if (showNoteInfoPopup.value)
                showNoteInfoPopup(currentNote, currentUpdate)
            if (showAddMembersPopup.value)
                showAddMembersPopup(currentGroup)
            if (showEditProjectGroupPopup.value)
                showEditProjectGroupPopup(currentGroup)
            if (showEditEmailPopup.value)
                showEditEmailPopup()
            if (showEditPasswordPopup.value)
                showEditPasswordPopup()
            if (showAddGroupPopup.value)
                showAddGroupPopup()
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
     * Function to show the [notifies] list
     *
     * No-any params required
     */
    @Composable
    private fun showNotifies() {
        LazyColumn(
            contentPadding = PaddingValues(20.dp)
        ) {
            items(notifies) { notify ->
                val isJoinRequest = notify.changelogEvent == INVITED_GROUP
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
                        IconButton(
                            modifier = Modifier.size(25.dp)
                                .padding(
                                    top = 5.dp,
                                    end = 5.dp
                                ).align(alignment = Alignment.End),
                            onClick = { notifies.remove(notify) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                        Column(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                        ) {
                            Text(
                                text = notify.title,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = notify.content,
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
                                            // TODO: MAKE REQUEST THEN
                                            notifies.remove(notify)
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
                                            // TODO: MAKE REQUEST THEN
                                            notifies.remove(notify)
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