
import Routes.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import layouts.ui.screens.Connect
import layouts.ui.screens.Home
import layouts.ui.screens.SplashScreen
import layouts.ui.theme.PandoroTheme
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.app_name
import pandoro.composeapp.generated.resources.rem

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
 * Method to sho the layout of **Pandoro** desktop app.
 * No-any params required
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    fontFamily = FontFamily(Font(Res.font.rem))
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
                    SplashScreen().showScreen()
                }
                scene(
                    route = connect.name,
                    navTransition = NavTransition(),
                ) {
                    Connect().showScreen()
                }
                scene(
                    route = home.name,
                    navTransition = NavTransition(),
                ) {
                    Home().showScreen()
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
