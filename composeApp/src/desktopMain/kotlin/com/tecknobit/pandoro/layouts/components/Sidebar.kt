package layouts.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import currentProfilePic
import fontFamily
import com.tecknobit.pandoro.helpers.BACKGROUND_COLOR
import com.tecknobit.pandoro.helpers.Logo
import com.tecknobit.pandoro.helpers.PRIMARY_COLOR
import layouts.ui.screens.Home
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.sections.Section
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.Res
import pandoro.composeapp.generated.resources.app_version

/**
 * This is the layout for the sidebar component
 *
 * @author Tecknobit - N7ghtm4r3
 */
class Sidebar {

    companion object {

        /**
         * **SIDEBAR_WIDTH** -> the width of the sidebar
         */
        val SIDEBAR_WIDTH = 250.dp

    }

    /**
     * Function to create the sidebar
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun createSidebar() {
        Scaffold(
            backgroundColor = PRIMARY_COLOR,
            contentColor = BACKGROUND_COLOR
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 10.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Logo(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = BACKGROUND_COLOR,
                                shape = CircleShape
                            ),
                        url = currentProfilePic.value,
                        size = 100.dp
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                bottom = 5.dp
                            ),
                        fontFamily = fontFamily,
                        text = user.completeName,
                        fontSize = 14.sp
                    )
                    Text(
                        text = user.email,
                        fontFamily = fontFamily,
                        fontSize = 12.sp
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(
                            top = 30.dp
                        ),
                    color = BACKGROUND_COLOR
                )
                Section.sidebarMenu().forEach { item ->
                    Column(
                        modifier = Modifier
                            .padding(
                                top = 10.dp
                            )
                            .fillMaxWidth()
                            .height(30.dp)
                            .clickable {
                                Home.activeScreen.value = item.first
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.second,
                            fontFamily = fontFamily,
                            fontSize = 16.sp
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(
                                top = 10.dp
                            ),
                        color = BACKGROUND_COLOR
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = 20.dp
                        ),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pandoro Desktop",
                        fontFamily = fontFamily,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "v. ${stringResource(Res.string.app_version)}",
                        fontFamily = fontFamily,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

}