import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import coil3.ImageLoader
import coil3.addLastModifiedToFileCacheKey
import coil3.compose.LocalPlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import Routes.*
import com.tecknobit.pandoro.layouts.ui.screens.Connect
import com.tecknobit.pandoro.layouts.ui.screens.Home
import layouts.ui.screens.SplashScreen
import com.tecknobit.pandoro.layouts.ui.theme.PandoroTheme
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import okhttp3.OkHttpClient
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.app_name
import pandoro.composeapp.generated.resources.rem
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * **navigator** -> the navigator instance is useful to manage the navigation between the screens of the application
 */
lateinit var navigator: Navigator

/**
 * List of available routes of the app
 */
enum class Routes {

    /**
     * **splashScreen** -> the first screen of the app where are loaded the user data for the session
     * and managed the first page to open
     */
    splashScreen,

    /**
     * **connect** -> the screen where the user can connect with own session to use in the app
     */
    connect,

    /**
     * **home** -> the screen where the user can navigate in the application
     */
    home

}

/**
 * **fontFamily** -> the Pandoro's font family
 */
lateinit var fontFamily: FontFamily

/**
 * **sslContext** -> the context helper to TLS protocols
 */
private val sslContext = SSLContext.getInstance("TLS")

/**
 * **imageLoader** -> the image loader used by coil library to load the image and by-passing the https self-signed certificates
 */
lateinit var imageLoader: ImageLoader

/**
 * **currentProfilePic** -> the current profile pic of the user
 */
lateinit var currentProfilePic: MutableState<String>

/**
 * Method to sho the layout of **Pandoro** desktop app.
 * No-any params required
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    fontFamily = FontFamily(Font(Res.font.rem))
    sslContext.init(null, validateSelfSignedCertificate(), SecureRandom())
    imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
            .components {
                add(
                    OkHttpNetworkFetcherFactory {
                        OkHttpClient.Builder()
                            .sslSocketFactory(
                                sslContext.socketFactory,
                                validateSelfSignedCertificate()[0] as X509TrustManager
                            )
                            .hostnameVerifier { _: String?, _: SSLSession? -> true }
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .build()
                    }
                )
            }
            .addLastModifiedToFileCacheKey(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    PreComposeApp {
        navigator = rememberNavigator()
        PandoroTheme {
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = splashScreen.name,
            ) {
                scene(
                    route = splashScreen.name,
                    navTransition = NavTransition(),
                ) {
                    SplashScreen().ShowScreen()
                }
                scene(
                    route = connect.name,
                    navTransition = NavTransition(),
                ) {
                    Connect().ShowScreen()
                }
                scene(
                    route = home.name,
                    navTransition = NavTransition(),
                ) {
                    Home().ShowScreen()
                }
            }
        }
    }
}

/**
 * Method to start the of **Pandoro** desktop app.
 * No-any params required
 */
@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        title = stringResource(Res.string.app_name),
        icon = painterResource("icons/logo.png"),
        state = WindowState(placement = WindowPlacement.Maximized),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

/**
 * Method to validate a self-signed SLL certificate and bypass the checks of its validity<br></br>
 * No-any params required
 *
 * @return list of trust managers as [Array] of [TrustManager]
 * @apiNote this method disable all checks on the SLL certificate validity, so is recommended to
 * use for test only or in a private distribution on own infrastructure
 */
private fun validateSelfSignedCertificate(): Array<TrustManager> {
    return arrayOf(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
    })
}
