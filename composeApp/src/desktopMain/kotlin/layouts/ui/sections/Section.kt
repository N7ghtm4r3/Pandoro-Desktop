package layouts.ui.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.Project
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentGroup
import layouts.ui.screens.Home.Companion.currentProject
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import pandoro.composeapp.generated.resources.*

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
         * **snackbarHostState** -> the state to display the [Snackbar]
         */
        val snackbarHostState = SnackbarHostState()

        /**
         * **sectionCoroutineScope** -> the coroutine scope to manage the coroutines of the [Scaffold]
         */
        lateinit var sectionCoroutineScope: CoroutineScope

        /**
         * Function to create the items list for the sidebar
         *
         * No-any params required
         */
        @OptIn(ExperimentalResourceApi::class)
        fun sidebarMenu(): MutableList<Pair<Sections, String>> {
            val sections = mutableListOf<Pair<Sections, String>>()
            runBlocking {
                async {
                    sections.add(Pair(Sections.Projects, getString(Res.string.projects)))
                    sections.add(Pair(Sections.Notes, getString(Res.string.notes)))
                    sections.add(Pair(Sections.Overview, getString(Res.string.overview)))
                    sections.add(Pair(Sections.Profile, getString(Res.string.profile)))
                }.await()
            }
            return sections
        }

        /**
         * Function to navigate back and show the last item of [previousSections]
         *
         * No-any params required
         */
        fun navBack() {
            activeScreen.value = previousSections[previousSections.lastIndex]
            previousSections.removeLast()
        }

    }

    /**
     * Function to show the content of the section
     *
     * No-any params required
     */
    @Composable
    abstract fun ShowSection()

    /**
     * Function to show the content of the section
     *
     * @param content: the content of the section to show
     */
    @Composable
    protected fun ShowSection(content: @Composable (PaddingValues) -> Unit) {
        sectionCoroutineScope = rememberCoroutineScope()
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(
                        containerColor = PRIMARY_COLOR,
                        contentColor = BACKGROUND_COLOR,
                        snackbarData = it
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
    @OptIn(ExperimentalResourceApi::class)
    protected fun showSnack(
        message: StringResource
    ) {
        sectionCoroutineScope.launch {
            showSnack(getString(message))
        }
    }

    /**
     * Function to show a snackbar from a section
     *
     * @param message: message to show
     */
    protected fun showSnack(
        message: String
    ) {
        helpers.showSnack(sectionCoroutineScope, snackbarHostState, message)
    }

    /**
     * Function to navigate to the [ProjectSection]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param project: the project to show
     */
    protected fun navToProject(previousSection: Sections, project: Project) {
        currentProject.value = project
        navToSection(previousSection, Sections.Project)
    }

    /**
     * Function to navigate to the [GroupSection]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param group: the group to show
     */
    protected fun navToGroup(previousSection: Sections, group: Group) {
        currentGroup.value = group
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

}