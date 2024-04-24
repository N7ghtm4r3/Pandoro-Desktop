package layouts.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.appVersion
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.screens.SplashScreen.Companion.userProfilePic
import layouts.ui.sections.Section

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
                    if (userProfilePic.value != null) {
                        Image(
                            bitmap = userProfilePic.value!!,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = BACKGROUND_COLOR,
                                    shape = CircleShape
                                )
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                bottom = 5.dp
                            ),
                        text = user.completeName,
                        fontSize = 14.sp
                    )
                    Text(
                        text = user.email,
                        fontSize = 12.sp
                    )
                }
                Divider(Modifier.padding(top = 30.dp), color = BACKGROUND_COLOR, thickness = 1.dp)
                for (item in Section.sidebarMenu()) {
                    Column(
                        modifier = Modifier.padding(top = 10.dp).fillMaxWidth().height(30.dp).clickable {
                            activeScreen.value = item
                        },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.toString(),
                            fontSize = 16.sp
                        )
                    }
                    Divider(Modifier.padding(top = 10.dp), color = BACKGROUND_COLOR, thickness = 1.dp)
                }
                Column(
                    modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pandoro Desktop",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "v. $appVersion",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

}