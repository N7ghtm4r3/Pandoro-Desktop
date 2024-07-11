@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.pandoro.layouts.components.popups

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.RED_COLOR
import com.tecknobit.pandoro.viewmodels.ProjectSectionViewModel
import com.tecknobit.pandorocore.helpers.*
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isContentNoteValid
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isValidVersion
import layouts.components.PandoroTextField
import layouts.components.popups.CreatePopup
import layouts.components.popups.snackbarHostState
import layouts.ui.screens.Home.Companion.currentProject
import layouts.ui.screens.Home.Companion.showScheduleUpdatePopup
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

private val viewModel = ProjectSectionViewModel(
    initialProject = currentProject.value,
    snackbarHostState =  snackbarHostState
)

/**
 * Function to show the popup to schedule a new [ProjectUpdate]
 *
 * No-any params required
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showScheduleUpdatePopup() {
    val notes = mutableStateListOf("")
    CreatePopup(
        height = 450.dp,
        flag = showScheduleUpdatePopup,
        title = stringResource(Res.string.schedule_update),
        content = {
            viewModel.targetVersion = remember { mutableStateOf("") }
            PandoroTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .height(55.dp),
                label = stringResource(Res.string.target_version),
                isError = !isValidVersion(viewModel.targetVersion.value),
                value = viewModel.targetVersion
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        top = 15.dp
                    ),
                text = stringResource(Res.string.change_notes),
                textAlign = TextAlign.Start
            )
            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        start = 15.dp
                    )
                    .height(225.dp)
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 5.dp
                            )
                    ) {
                        FloatingActionButton(
                            modifier = Modifier
                                .size(30.dp),
                            onClick = { notes.add("") },
                            content = { Icon(Icons.Filled.Add, null) }
                        )
                    }
                }
                items(notes.size) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val content = mutableStateOf(notes[index])
                        PandoroTextField(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    start = 10.dp
                                )
                                .width(220.dp)
                                .height(55.dp),
                            label = stringResource(Res.string.content),
                            isError = !isContentNoteValid(content.value),
                            onValueChange = {
                                if (it.isNotEmpty()) {
                                    content.value = it
                                    notes.removeAt(index)
                                    notes.add(index, it)
                                }
                            },
                            value = content,
                            textFieldModifier = Modifier
                                .width(220.dp)
                        )
                        IconButton(
                            onClick = { notes.removeAt(index) }
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
                }
            }
            Spacer(
                modifier = Modifier
                    .height(15.dp)
            )
            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.scheduleUpdate(
                            project = currentProject.value,
                            notes = notes,
                            onSuccess = {
                                showScheduleUpdatePopup.value = false
                            }
                        )
                    },
                text = stringResource(Res.string.schedule),
                fontSize = 14.sp
            )
        }
    )
}