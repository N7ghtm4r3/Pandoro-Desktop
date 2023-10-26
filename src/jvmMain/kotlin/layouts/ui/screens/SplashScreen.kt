package layouts.ui.screens

import Routes.connect
import Routes.home
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.appName
import kotlinx.coroutines.delay
import navigator
import org.apache.commons.validator.routines.EmailValidator
import toImportFromLibrary.User

/**
 * This is the layout for the splash screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class SplashScreen : UIScreen() {

    companion object {

        /**
         * **validator** -> the validator to check the validity of the emails
         */
        val validator: EmailValidator = EmailValidator.getInstance()

        /**
         * **user** -> the user connected for the session
         */
        val user = User()

    }

    /**
     * Function to show the content of the [SplashScreen]
     *
     * No-any params required
     */
    @Composable
    override fun showScreen() {
        val blink = remember { Animatable(0f) }
        LaunchedEffect(key1 = true, block = {
            blink.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            delay(500)
            // TODO: NAVIGATE CORRECTLY TO THE FIRST SCREEN OF THE APPLICATION
            if (false)
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