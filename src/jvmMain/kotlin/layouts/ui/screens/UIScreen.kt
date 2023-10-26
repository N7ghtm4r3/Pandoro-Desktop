package layouts.ui.screens

import androidx.compose.runtime.Composable
import com.tecknobit.apimanager.annotations.Structure

/**
 * The [UIScreen] class is useful to give the base structure that a Pandoro's screen must have
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Structure
abstract class UIScreen {

    /**
     * Function to show the content of the screen
     *
     * No-any params required
     */
    @Composable
    abstract fun showScreen()

}