import Routes.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.RED_COLOR
import helpers.appName
import layouts.ui.screens.Connect
import layouts.ui.screens.Home
import layouts.ui.screens.SplashScreen
import moe.tlaster.precompose.PreComposeWindow
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

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
 * Method to sho the layout of **Pandoro** desktop app.
 * No-any params required
 */
@Composable
@Preview
fun App() {
    navigator = rememberNavigator()
    MaterialTheme(
        typography = Typography(defaultFontFamily = FontFamily(Font(resource = "REM-Medium.ttf"))),
        colors = Colors(
            primary = PRIMARY_COLOR,
            primaryVariant = PRIMARY_COLOR,
            secondary = BACKGROUND_COLOR,
            secondaryVariant = PRIMARY_COLOR,
            background = BACKGROUND_COLOR,
            surface = Color.Transparent,
            error = RED_COLOR,
            onPrimary = BACKGROUND_COLOR,
            onSecondary = PRIMARY_COLOR,
            onBackground = BACKGROUND_COLOR,
            onSurface = PRIMARY_COLOR,
            onError = RED_COLOR,
            isLight = true
        )
    ) {
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

/**
 * Method to start the of **Pandoro** desktop app.
 * No-any params required
 */
fun main() = application {
    PreComposeWindow(
        title = appName,
        //icon = painterResource("logo.png"),
        state = WindowState(placement = WindowPlacement.Maximized),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
