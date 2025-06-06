package ai.maatcore.maatcore_android_tv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// It's common to define a specific DarkColorScheme, e.g., using colors from Color.kt
// For now, we'll use the default darkColorScheme.
// If you have custom colors, they should be defined in Color.kt and used to create a specific scheme.
private val MaatDarkColorScheme = darkColorScheme(
    // Define your custom dark theme colors here if needed, for example:
    // primary = Purple80,
    // secondary = PurpleGrey80,
    // tertiary = Pink80
    // If not specified, default M3 dark colors will be used.
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaatDarkColorScheme, // Using a default dark scheme for now
        typography = MaatTypography, // Using MaatTypography from Typography.kt
        content = content
    )
}
