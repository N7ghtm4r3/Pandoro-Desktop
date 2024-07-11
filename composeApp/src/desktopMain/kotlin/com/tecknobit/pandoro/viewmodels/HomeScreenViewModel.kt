package com.tecknobit.pandoro.viewmodels

import Routes.connect
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.pandoro.layouts.ui.screens.Home
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.activeScreen
import com.tecknobit.pandoro.layouts.ui.screens.Home.Companion.changelogs
import com.tecknobit.pandoro.layouts.ui.sections.ProfileSection.Companion.groups
import com.tecknobit.pandoro.layouts.ui.sections.Section.Sections.*
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidProjectDescription
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidProjectName
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidProjectShortDescription
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidRepository
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidVersion
import com.tecknobit.pandorocore.records.Changelog
import com.tecknobit.pandorocore.records.Group
import com.tecknobit.pandorocore.records.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pandoro.composeapp.generated.resources.*

/**
 * The **HomeScreenViewModel** class is the support class used by the [MainActivity]
 * to execute the different requests to refresh the user data to the backend
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see PandoroViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
@OptIn(ExperimentalResourceApi::class)
class HomeScreenViewModel(
    override var snackbarHostState: SnackbarHostState? = null
) : PandoroViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **_projects** -> list of the projects of the user
     */
    private val _projects = MutableStateFlow<MutableList<Project>>(mutableListOf())
    val projects: StateFlow<MutableList<Project>> = _projects

    /**
     * **_groups** -> list of the groups of the user
     */
    private val _groups = MutableStateFlow<MutableList<Group>>(mutableListOf())

    /**
     * **_changelogs** -> list of the changelogs of the user
     */
    private val _changelogs = MutableStateFlow<MutableList<Changelog>>(mutableListOf())

    /**
     * **_isServerOffline** -> whether the server is currently offline
     */
    private val _isServerOffline = MutableStateFlow(false)
    val isServerOffline = _isServerOffline

    /**
     * **name** -> the name of the project
     */
    lateinit var name: MutableState<String>

    /**
     * **description** -> the description of the project
     */
    lateinit var description: MutableState<String>

    /**
     * **shortDescription** -> the short description of the project
     */
    lateinit var shortDescription: MutableState<String>

    /**
     * **version** -> the current version of the project
     */
    lateinit var version: MutableState<String>

    /**
     * **projectRepository** -> the project repository of the project
     */
    lateinit var projectRepository: MutableState<String>

    /**
     * **projectGroups** -> the list of groups where the project is shared
     */
    lateinit var projectGroups: MutableList<String>

    init {
        groups = _groups
        changelogs = _changelogs
    }

    /**
     * Function to execute the request to refresh the [_projects], [_groups], [_notes] and [_changelogs] lists
     *
     * No-any params required
     */
    fun refreshValues() {
        execRefreshingRoutine(
            currentContext = Home::class.java,
            routine = {
                if(activeScreen.value == Projects || activeScreen.value == Overview || activeScreen.value == Group) {
                    requester.sendRequest(
                        request = { requester.getProjectsList() },
                        onSuccess = { response ->
                            _projects.value = Project.getInstances(
                                response.getJSONArray(RESPONSE_MESSAGE_KEY)
                            )
                        },
                        onFailure = { showSnack(it) }
                    )
                } else if(activeScreen.value == Profile || activeScreen.value == Projects) {
                    requester.sendRequest(
                        request = { requester.getGroupsList() },
                        onSuccess = { response ->
                            _groups.value = Group.getInstances(
                                response.getJSONArray(RESPONSE_MESSAGE_KEY)
                            )
                            groups = _groups
                        },
                        onFailure = { showSnack(it) }
                    )
                }
                requester.sendRequest(
                    request = { requester.getChangelogsList() },
                    onSuccess = { response ->
                        _isServerOffline.value = false
                        _changelogs.value = Changelog.getInstances(
                            response.getJSONArray(RESPONSE_MESSAGE_KEY)
                        )
                        changelogs = _changelogs
                    },
                    onFailure = {
                        suspendRefresher()
                        localAuthHelper.logout()
                        navigator.navigate(connect.name)
                    },
                    onConnectionError = { _isServerOffline.value = true }
                )
            }
        )
    }

    /**
     * Function to work ([addProject] or [editProject]) a project
     *
     * @param project: the project to work on
     * @param onSuccess: the action to execute whether the request has been successful
     */
    fun workWithProject(
        project: Project?,
        onSuccess: () -> Unit
    ) {
        if (isValidProjectName(name.value)) {
            if (isValidProjectDescription(description.value)) {
                if (isValidProjectShortDescription(shortDescription.value)) {
                    if (isValidVersion(version.value)) {
                        if (isValidRepository(projectRepository.value)) {
                            if(project == null) {
                                addProject(
                                    groupIds = projectGroups,
                                    onSuccess = onSuccess
                                )
                            } else {
                                editProject(
                                    project = project,
                                    groupIds = projectGroups,
                                    onSuccess = onSuccess
                                )
                            }
                        } else
                            showSnack(Res.string.insert_a_correct_repository_url)
                    } else
                        showSnack(Res.string.insert_a_correct_version)
                } else
                    showSnack(Res.string.insert_a_correct_short_description)
            } else
                showSnack(Res.string.insert_a_correct_description)
        } else
            showSnack(Res.string.insert_a_correct_name)
    }

    /**
     * Function to execute the request to add a new project
     *
     * @param groupIds: the list of group identifiers where the project is shared
     * @param onSuccess: the action to execute whether the request has been successful
     */
    private fun addProject(
        groupIds: List<String>,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.addProject(
                    name = name.value,
                    projectDescription = description.value,
                    projectShortDescription = shortDescription.value,
                    projectVersion = version.value,
                    groups = groupIds,
                    projectRepository = projectRepository.value
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to edit an existing project
     *
     * @param project: the existing project to edit
     * @param groupIds: the list of group identifiers where the project is shared
     * @param onSuccess: the action to execute whether the request has been successful
     */
    private fun editProject(
        project: Project,
        groupIds: List<String>,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.editProject(
                    projectId = project.id,
                    name = name.value,
                    projectDescription = description.value,
                    projectShortDescription = shortDescription.value,
                    projectVersion = version.value,
                    groups = groupIds,
                    projectRepository = projectRepository.value
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to delete an existing project
     *
     * @param project: the existing project to delete
     * @param onSuccess: the action to execute whether the request has been successful
     */
    fun deleteProject(
        project: Project,
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.deleteProject(
                    projectId = project.id
                )
            },
            onSuccess = { onSuccess.invoke() },
            onFailure = { showSnack(it) }
        )
    }

}