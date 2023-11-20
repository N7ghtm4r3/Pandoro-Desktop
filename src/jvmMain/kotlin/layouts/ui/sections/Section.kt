package layouts.ui.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.tecknobit.pandoro.records.Group
import com.tecknobit.pandoro.records.Project
import helpers.BACKGROUND_COLOR
import kotlinx.coroutines.CoroutineScope
import layouts.components.Sidebar.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentGroup
import layouts.ui.screens.Home.Companion.currentProject

/**
 * The [Section] class is useful to give the base structure that a Pandoro's section must have
 *
 * @author Tecknobit - N7ghtm4r3
 */
abstract class Section {

    /**
     * **Sections** -> list of available sections
     */
    enum class Sections {

        /**
         * **Projects** -> section
         */
        Projects,

        /**
         * **Notes** -> section
         */
        Notes,

        /**
         * **Overview** -> section
         */
        Overview,

        /**
         * **Profile** -> section
         */
        Profile,

        /**
         * **Project** -> section
         */
        Project,

        /**
         * **Group** -> section
         */
        Group

    }

    companion object {

        /**
         * **previousSections** -> the previous sections list to show
         */
        var previousSections: MutableList<Sections> = mutableListOf()

        /**
         * Function to create the items list for the sidebar
         *
         * No-any params required
         */
        fun sidebarMenu(): List<Sections> {
            val sections = mutableListOf<Sections>()
            sections.add(Sections.Projects)
            sections.add(Sections.Notes)
            sections.add(Sections.Overview)
            sections.add(Sections.Profile)
            return sections
        }

    }

    /**
     * **scaffoldState** -> the scaffold state for the scaffold of the popup
     */
    private lateinit var scaffoldState: ScaffoldState

    /**
     * **coroutineScope** -> the coroutine scope to manage the coroutines of the [Scaffold]
     */
    private lateinit var coroutineScope: CoroutineScope

    /**
     * Function to show the content of the section
     *
     * No-any params required
     */
    @Composable
    abstract fun showSection()

    /**
     * Function to show the content of the section
     *
     * @param content: the content of the section to show
     */
    @Composable
    protected fun showSection(content: @Composable (PaddingValues) -> Unit) {
        coroutineScope = rememberCoroutineScope()
        scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(it) { data ->
                    Snackbar(
                        backgroundColor = BACKGROUND_COLOR,
                        snackbarData = data
                    )
                }
            },
            content = content
        )
    }

    /**
     * Function to show a snackbar from a section
     *
     * @param message: message to show
     */
    protected fun showSnack(message: String) {
        helpers.showSnack(coroutineScope, scaffoldState, message)
    }

    /**
     * Function to navigate to the [ProjectSection]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param project: the project to show
     */
    protected fun navToProject(previousSection: Sections, project: Project) {
        currentProject = project
        navToSection(previousSection, Sections.Project)
    }

    /**
     * Function to navigate to the [GroupSection]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param group: the group to show
     */
    protected fun navToGroup(previousSection: Sections, group: Group) {
        currentGroup = group
        navToSection(previousSection, Sections.Group)
    }

    /**
     * Function to navigate to a [Sections]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param destinationSection: the destination section to show
     */
    private fun navToSection(previousSection: Sections, destinationSection: Sections) {
        previousSections.add(previousSection)
        activeScreen.value = destinationSection
    }

    /**
     * Function to navigate back and show the last item of [previousSections]
     *
     * No-any params required
     */
    protected fun navBack() {
        activeScreen.value = previousSections[previousSections.lastIndex]
        previousSections.removeLast()
    }

}