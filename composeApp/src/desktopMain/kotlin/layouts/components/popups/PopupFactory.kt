package layouts.components.popups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import kotlinx.coroutines.CoroutineScope


lateinit var snackbarHostState: SnackbarHostState

/**
 * **sectionCoroutineScope** -> the coroutine scope to manage the coroutines of the [Scaffold]
 */
lateinit var coroutineScope: CoroutineScope

/**
 * Function to create a [Popup]
 *
 * @param width: the width of the [Popup], default 400.[dp]
 * @param height: the height of the [Popup]
 * @param flag: the flag whether show the [Popup]
 * @param title: the title of the [Popup]
 * @param titleSize: the title size, default 16.[sp]
 * @param columnModifier: the modifier to be applied to the content column
 * @param content: the content of the popup
 * @param horizontalAlignment: the [Alignment] for the content column
 */
@Composable
fun createPopup(
    width: Dp = 400.dp,
    height: Dp,
    flag: MutableState<Boolean>,
    title: String,
    titleSize: TextUnit = 16.sp,
    columnModifier: Modifier = Modifier
        .fillMaxSize()
        .padding(
            top = 10.dp
        )
        .width(250.dp),
    content: @Composable ColumnScope.() -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    snackbarHostState = remember { SnackbarHostState() }
    coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Popup(
            alignment = Alignment.BottomStart,
            offset = IntOffset(
                x = 250.dp.dpToPx().toInt(),
                y = 0
            ),
            properties = PopupProperties(
                focusable = true
            ), onPreviewKeyEvent = { false }, onKeyEvent = { false }) {
            Card(
                modifier = Modifier
                    .size(
                        width = width,
                        height = height
                    ),
                shape = RoundedCornerShape(
                    topEnd = 15.dp,
                    bottomEnd = 15.dp
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) {
                            Snackbar(
                                containerColor = PRIMARY_COLOR,
                                contentColor = BACKGROUND_COLOR,
                                snackbarData = it
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row {
                            Column(
                                modifier = Modifier
                                    .weight(10f).
                                    fillMaxWidth()
                                        .padding(
                                            start = 15.dp,
                                            top = 15.dp
                                        )
                            ) {
                                Text(
                                    text = title,
                                    fontSize = titleSize
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(
                                            top = 5.dp,
                                            end = 5.dp
                                        )
                                        .align(alignment = Alignment.End),
                                    onClick = { flag.value = false }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        Column(
                            modifier = columnModifier,
                            horizontalAlignment = horizontalAlignment,
                            content = content
                        )
                    }
                }
            }
        }
    }
}

/**
 * Function to convert from [Dp] to a pixels value as [Float]
 */
@Composable
private fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }