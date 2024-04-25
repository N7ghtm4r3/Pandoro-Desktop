package layouts.ui.screens

import Routes.connect
import Routes.home
import UpdaterDialog
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.pandorocore.helpers.Requester
import com.tecknobit.pandorocore.records.users.User
import fontFamily
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import kotlinx.coroutines.delay
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.sections.Section
import navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.app_name
import pandoro.composeapp.generated.resources.app_version
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * This is the layout for the splash screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class SplashScreen : UIScreen() {

    companion object {

        /**
         * **isRefreshing** -> whether is current allowed refresh the lists
         */
        lateinit var isRefreshing: MutableState<Boolean>

        /**
         * **localAuthHelper** -> the instance to manage the auth credentials in local
         */
        val localAuthHelper = Connect().LocalAuthHelper()

        /**
         * **user** -> the user connected for the session
         */
        var user = User()

        /**
         * **requester** -> the stance to manage the requests with the backend
         */
        var requester: Requester? = null

    }

    /**
     * Function to show the content of the [SplashScreen]
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun showScreen() {
        activeScreen = remember { mutableStateOf(Section.Sections.Projects) }
        isRefreshing = rememberSaveable { mutableStateOf(false) }
        localAuthHelper.initUserCredentials()
        val blink = remember { Animatable(0f) }
        var launch by remember { mutableStateOf(true) }
        var checkForUpdates by remember { mutableStateOf(true) }
        LaunchedEffect(true) {
            blink.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            delay(500)
        }
        if(checkForUpdates) {
            checkForUpdates = false
            UpdaterDialog(
                locale = Locale.UK,
                appName = stringResource(Res.string.app_name),
                currentVersion = stringResource(Res.string.app_version),
                onUpdateAvailable = {
                    launch = false
                },
                dismissAction = {
                    launch = true
                }
            )
        }
        TimeFormatter.changeDefaultPattern("dd/MM/yyyy HH:mm:ss")
        if(launch) {
            if (requester != null)
                navigator.navigate(home.name)
            else
                navigator.navigate(connect.name)
        }
        Box(
            modifier = Modifier
                .background(PRIMARY_COLOR)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                color = BACKGROUND_COLOR.copy(blink.value),
                fontSize = 75.sp,
                fontFamily = fontFamily
            )
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(25.dp),
                    text = "by Tecknobit",
                    color = BACKGROUND_COLOR,
                    fontFamily = fontFamily
                )
            }
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

}