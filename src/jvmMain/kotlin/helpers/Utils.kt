package helpers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarDuration.Short
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI

/**
 * app name constant
 */
const val appName: String = "Pandoro"

// TODO: IMPORT COLORS FROM THE LIBRARY
/**
 * the primary color value
 */
val PRIMARY_COLOR: Color = fromHexToColor("#07020d")

/**
 * the background color value
 */
val BACKGROUND_COLOR: Color = fromHexToColor("#f9f6f0")

/**
 * the green color value
 */

val GREEN_COLOR: Color = fromHexToColor("#61892f")

/**
 * the yellow color value
 */

val YELLOW_COLOR: Color = fromHexToColor("#bfae19")

/**
 * the red color value
 */
val RED_COLOR: Color = fromHexToColor("#A81515")

/**
 * Function to create a [Color] from an hex [String]
 * @param hex: hex value to transform
 *
 * @return color as [Color]
 */
fun fromHexToColor(hex: String): Color {
    return Color(("ff" + hex.removePrefix("#").lowercase()).toLong(16))
}

/**
 * Function to show a snackbar from a view
 *
 * @param scope: scope manager
 * @param scaffoldState: state of the [Scaffold]
 * @param message: message to show
 */
fun showSnack(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    message: String,
    duration: SnackbarDuration = Short
) {
    scope.launch {
        scaffoldState.snackbarHostState.showSnackbar(
            message = message,
            duration = duration,
        )
    }
}

/**
 * Function to space the content shown with a [Spacer] and a [Divider]
 *
 * @param space: the space between the [Spacer] and the [Divider]
 * @param start: the start padding value
 * @param top: the top padding value
 * @param end: the end padding value
 * @param bottom: the bottom padding value
 * @param color: the color of the [Divider]
 */
@Composable
fun spaceContent(
    space: Dp = 10.dp,
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
    color: Color = Color.LightGray
) {
    Spacer(Modifier.padding(top = space))
    createDivider(
        start = start,
        top = top,
        end = end,
        bottom = bottom,
        color = color
    )
}

/**
 * Function to create a [Divider]
 *
 * @param start: the start padding value
 * @param top: the top padding value
 * @param end: the end padding value
 * @param bottom: the bottom padding value
 * @param color: the color of the [Divider]
 */
@Composable
fun createDivider(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
    color: Color = Color.LightGray
) {
    Divider(
        color = color,
        modifier = Modifier.fillMaxWidth().height(1.dp).padding(
            start = start,
            top = top,
            end = end,
            bottom = bottom
        )
    )
}

/**
 * Function to open an url from the application
 *
 * @param url: the url to open
 */
fun openUrl(url: String) {
    Desktop.getDesktop().browse(URI(url))
}