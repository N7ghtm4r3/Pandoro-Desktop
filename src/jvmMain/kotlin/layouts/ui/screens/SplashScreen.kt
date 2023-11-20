package layouts.ui.screens

import Routes.connect
import Routes.home
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.pandoro.helpers.Requester
import com.tecknobit.pandoro.records.users.User
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.appName
import kotlinx.coroutines.delay
import navigator

/**
 * This is the layout for the splash screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class SplashScreen : UIScreen() {

    companion object {

        val localAuthHelper = Connect().LocalAuthHelper()

        /**
         * **user** -> the user connected for the session
         */
        var user = User()

        var requester: Requester? = null

        lateinit var userProfilePic: MutableState<ImageBitmap?>

    }

    /**
     * Function to show the content of the [SplashScreen]
     *
     * No-any params required
     */
    @Composable
    override fun showScreen() {
        userProfilePic = rememberSaveable { mutableStateOf(null) }
        localAuthHelper.initUserCredentials()
        val blink = remember { Animatable(0f) }
        LaunchedEffect(key1 = true, block = {
            blink.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            delay(500)
            if (requester != null)
                navigator.navigate(home.name)
            else
                navigator.navigate(connect.name)
        })
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(PRIMARY_COLOR).fillMaxSize()
        ) {
            Text(
                text = appName,
                color = BACKGROUND_COLOR.copy(blink.value),
                fontSize = 75.sp
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "by Tecknobit",
                    color = BACKGROUND_COLOR,
                    modifier = Modifier.padding(25.dp)
                )
            }
        }
    }

}