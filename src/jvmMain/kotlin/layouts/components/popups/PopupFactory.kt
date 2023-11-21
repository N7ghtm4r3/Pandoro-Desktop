package layouts.components.popups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import helpers.BACKGROUND_COLOR
import kotlinx.coroutines.CoroutineScope

/**
 * **sectionScaffoldState** -> the scaffold state for the scaffold of the popup
 */
lateinit var scaffoldState: ScaffoldState

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
    columnModifier: Modifier = Modifier.fillMaxSize().padding(top = 10.dp).width(250.dp),
    content: @Composable ColumnScope.() -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    scaffoldState = rememberScaffoldState()
    coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Popup(
            offset = IntOffset(x = 250.dp.dpToPx().toInt(), y = 0),
            alignment = Alignment.BottomStart,
            focusable = true
        ) {
            Card(
                modifier = Modifier.size(width = width, height = height),
                shape = RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp),
                elevation = 10.dp
            ) {
                Scaffold(
                    scaffoldState = scaffoldState,
                    snackbarHost = {
                        SnackbarHost(it) { data ->
                            Snackbar(
                                backgroundColor = BACKGROUND_COLOR,
                                snackbarData = data
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row {
                            Column(
                                modifier = Modifier.weight(10f).fillMaxWidth().padding(start = 15.dp, top = 15.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = titleSize
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            ) {
                                IconButton(
                                    modifier = Modifier.size(25.dp).padding(top = 5.dp, end = 5.dp)
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