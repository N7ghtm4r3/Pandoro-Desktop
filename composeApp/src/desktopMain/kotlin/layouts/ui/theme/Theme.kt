package layouts.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * **LightColorScheme** -> the Pandoro's application color scheme
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    primaryContainer = DwarfWhiteColor,
    secondary = ErrorLight,
    secondaryContainer = IceGrayColor,
    onSecondaryContainer = PrimaryLight,
    background = BackgroundLight,
    surface = DwarfWhiteColor,
    onSurfaceVariant = PrimaryLight,
    error = ErrorLight
)

/**
 * Function to create the Pandoro's theme
 *
 * @param content: the content of the UI to create with the theme
 */
@Composable
fun PandoroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}