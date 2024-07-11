package layouts.ui.screens

import UpdaterDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinox.environment.records.EquinoxUser.DEFAULT_PROFILE_PIC
import Routes.connect
import Routes.home
import currentProfilePic
import fontFamily
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import com.tecknobit.pandoro.layouts.ui.screens.Connect
import navigator
import com.tecknobit.pandorocore.records.users.User
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.sections.Section
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.app_name
import pandoro.composeapp.generated.resources.app_version
import java.util.*

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

    }

    /**
     * Function to show the content of the [SplashScreen]
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun ShowScreen() {
        activeScreen = remember { mutableStateOf(Section.Sections.Projects) }
        isRefreshing = rememberSaveable { mutableStateOf(false) }
        localAuthHelper.initUserCredentials()
        if(user.language != null) {
            Locale.setDefault(Locale.forLanguageTag(user.language))
        }
        currentProfilePic = remember {
            mutableStateOf(
                if(user.profilePic != null)
                    user.profilePic
                else
                    DEFAULT_PROFILE_PIC
            )
        }
        var launch by remember { mutableStateOf(true) }
        var checkForUpdates by remember { mutableStateOf(true) }
        Box(
            modifier = Modifier
                .background(PRIMARY_COLOR)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                color = BACKGROUND_COLOR,
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
        if(checkForUpdates) {
            checkForUpdates = false
            UpdaterDialog(
                locale = Locale.getDefault(),
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
        if(launch) {
            if (user.id != null)
                navigator.navigate(home.name)
            else
                navigator.navigate(connect.name)
        }
    }

}