package helpers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import java.awt.Desktop
import java.net.URI

/**
 * app name constant
 */
const val appName: String = "Pandoro"

/**
 * current app version constant
 */
const val appVersion: String = "1.0.3"

/**
 * the primary color value
 */
val PRIMARY_COLOR: Color = fromHexToColor(com.tecknobit.pandorocore.ui.PRIMARY_COLOR)

/**
 * the background color value
 */
val BACKGROUND_COLOR: Color = fromHexToColor(com.tecknobit.pandorocore.ui.BACKGROUND_COLOR)

/**
 * the green color value
 */

val GREEN_COLOR: Color = fromHexToColor(com.tecknobit.pandorocore.ui.GREEN_COLOR)

/**
 * the yellow color value
 */

val YELLOW_COLOR: Color = fromHexToColor(com.tecknobit.pandorocore.ui.YELLOW_COLOR)

/**
 * the red color value
 */
val RED_COLOR: Color = fromHexToColor(com.tecknobit.pandorocore.ui.RED_COLOR)

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
 * @param snackbarHostState: state of the [Scaffold]
 * @param message: message to show
 */
fun showSnack(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: androidx.compose.material3.SnackbarDuration = androidx.compose.material3.SnackbarDuration.Short
) {
    scope.launch {
        snackbarHostState.showSnackbar(
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
    HorizontalDivider(
        color = color,
        modifier = Modifier.padding(
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
fun openUrl(url: String) = Desktop.getDesktop().browse(URI(url))

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    size: Dp = 45.dp,
    url: String
) {
    var iUrl = url
    if (!iUrl.startsWith(localAuthHelper.host!!))
        iUrl = localAuthHelper.host + "/$url"
    AsyncImage(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(iUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

