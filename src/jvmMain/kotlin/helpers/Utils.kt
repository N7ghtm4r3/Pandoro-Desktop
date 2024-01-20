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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecknobit.pandoro.helpers.ui.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.ui.GREEN_COLOR
import com.tecknobit.pandoro.helpers.ui.PRIMARY_COLOR
import com.tecknobit.pandoro.helpers.ui.YELLOW_COLOR
import com.tecknobit.pandoro.services.UsersHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import java.awt.Desktop
import java.net.URI
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.imageio.IIOException
import javax.imageio.ImageIO
import javax.net.ssl.*

/**
 * app name constant
 */
const val appName: String = "Pandoro"

/**
 * the primary color value
 */
val PRIMARY_COLOR: Color = fromHexToColor(PRIMARY_COLOR)

/**
 * the background color value
 */
val BACKGROUND_COLOR: Color = fromHexToColor(BACKGROUND_COLOR)

/**
 * the green color value
 */

val GREEN_COLOR: Color = fromHexToColor(GREEN_COLOR)

/**
 * the yellow color value
 */

val YELLOW_COLOR: Color = fromHexToColor(YELLOW_COLOR)

/**
 * the red color value
 */
val RED_COLOR: Color = fromHexToColor(com.tecknobit.pandoro.helpers.ui.RED_COLOR)

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
fun openUrl(url: String) = Desktop.getDesktop().browse(URI(url))

/**
 * Function to load an image from an ul
 *
 * @param url: the url from load the image
 */
fun loadImageBitmap(url: String): ImageBitmap {
    var iUrl = url
    if (!iUrl.startsWith(localAuthHelper.host!!))
        iUrl = localAuthHelper.host + "/$url"
    if (iUrl.startsWith("https")) {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
        })
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _: String?, _: SSLSession? -> true }
        } catch (ignored: Exception) {
        }
    }
    var bitmap: ImageBitmap? = null
    try {
        bitmap = ImageIO.read(URL(iUrl)).toComposeImageBitmap()
    } catch (e: IIOException) {
        try {
            bitmap =
                ImageIO.read(URL(localAuthHelper.host!! + "/" + UsersHelper.DEFAULT_PROFILE_PIC)).toComposeImageBitmap()
        } catch (e: IIOException) {
            localAuthHelper.logout()
        }
    }
    return bitmap!!
}