package layouts.ui.sections

import androidx.compose.runtime.Composable
import layouts.components.Sidebar.Companion.activeScreen
import layouts.ui.screens.Home.Companion.currentGroup
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.sections.Section.Sections.*

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
            sections.add(Projects)
            sections.add(Notes)
            sections.add(Overview)
            sections.add(Profile)
            return sections
        }

    }

    /**
     * Function to show the content of the section
     *
     * No-any params required
     */
    @Composable
    abstract fun showSection()

    /**
     * Function to navigate to the [ProjectSection]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param project: the project to show
     */
    protected fun navToProject(previousSection: Sections, project: toImportFromLibrary.Project) {
        currentProject = project
        navToSection(previousSection, Project)
    }

    /**
     * Function to navigate to the [GroupSection]
     *
     * @param previousSection: the previous section to show when navigate back
     * @param group: the group to show
     */
    protected fun navToGroup(previousSection: Sections, group: toImportFromLibrary.Group) {
        currentGroup = group
        navToSection(previousSection, Group)
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