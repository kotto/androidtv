package ai.maatcore.maatcore_android_tv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // Importer Color pour les couleurs d'erreur
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
// Importer MaatTypography explicitement
import ai.maatcore.maatcore_android_tv.ui.theme.MaatTypography

// Définition du ColorScheme MaâtCore pour le thème sombre
private val MaatDarkColorScheme = darkColorScheme(
    primary = MaatColorOrangeSolaire,       // Accent 1 (CTA, hover)
    onPrimary = MaatColorNoirProfond,       // Texte sur Primary (assurer contraste)
    primaryContainer = MaatColorOrangeSolaire, // Peut être ajusté
    onPrimaryContainer = MaatColorNoirProfond, // Peut être ajusté

    secondary = MaatColorCuivreAncien,      // Accent 2 (soulignement)
    onSecondary = MaatColorOrSable,         // Texte sur Secondary
    secondaryContainer = MaatColorCuivreAncien, // Peut être ajusté
    onSecondaryContainer = MaatColorOrSable,    // Peut être ajusté

    tertiary = MaatColorCuivreClair,        // Texte secondaire / accent subtil
    onTertiary = MaatColorNoirProfond,      // Texte sur Tertiary
    tertiaryContainer = MaatColorCuivreClair,   // Peut être ajusté
    onTertiaryContainer = MaatColorNoirProfond, // Peut être ajusté

    background = MaatColorNoirProfond,      // Fond principal
    onBackground = MaatColorOrSable,        // Texte sur Background

    surface = MaatColorNoirProfond,         // Surface par défaut (peut être identique au background)
    onSurface = MaatColorOrSable,           // Texte sur Surface

    surfaceVariant = MaatColorGrisTresFonce,// Fond secondaire carte
    onSurfaceVariant = MaatColorOrSable,    // Texte sur SurfaceVariant

    error = Color(0xFFCF6679),              // Couleur d'erreur standard pour thème sombre
    onError = MaatColorNoirProfond,         // Texte sur Erreur
    
    outline = MaatColorCuivreAncien,         // Pour les bordures (utilisé par Card, etc.)
    outlineVariant = MaatColorGrisTresFonce  // Variante de bordure
)

// Pour l'instant, le thème clair est identique au thème sombre pour maintenir l'esthétique MaâtCore.
// Des spécifications pour un thème clair pourront être ajoutées ultérieurement si besoin.
private val MaatLightColorScheme = lightColorScheme(
    primary = MaatColorOrangeSolaire,
    onPrimary = MaatColorNoirProfond,
    primaryContainer = MaatColorOrangeSolaire,
    onPrimaryContainer = MaatColorNoirProfond,

    secondary = MaatColorCuivreAncien,
    onSecondary = MaatColorOrSable,
    secondaryContainer = MaatColorCuivreAncien,
    onSecondaryContainer = MaatColorOrSable,

    tertiary = MaatColorCuivreClair,
    onTertiary = MaatColorNoirProfond,
    tertiaryContainer = MaatColorCuivreClair,
    onTertiaryContainer = MaatColorNoirProfond,

    background = MaatColorNoirProfond, // Maintenir le fond sombre pour l'esthétique TV
    onBackground = MaatColorOrSable,

    surface = MaatColorNoirProfond,    // Maintenir la surface sombre
    onSurface = MaatColorOrSable,

    surfaceVariant = MaatColorGrisTresFonce,
    onSurfaceVariant = MaatColorOrSable,

    error = Color(0xFFB00020),              // Couleur d'erreur standard pour thème clair
    onError = Color.White,
    
    outline = MaatColorCuivreAncien,         // Pour les bordures (utilisé par Card, etc.)
    outlineVariant = MaatColorGrisTresFonce  // Variante de bordure
    // Les couleurs spécifiques au thème clair (si différentes du sombre) seraient définies ici.
    // Par exemple, un fond clair : background = Color.White, onBackground = MaatColorNoirProfond etc.
)

@Composable
fun MaatcoreandroidtvTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        MaatDarkColorScheme
    } else {
        // Pour l'instant, on utilise MaatDarkColorScheme aussi pour le thème clair
        // pour respecter l'identité "Netflix-like" sombre.
        // Remplacer par MaatLightColorScheme si un thème clair distinct est implémenté.
        MaatDarkColorScheme // Ou MaatLightColorScheme si vous voulez tester les couleurs claires définies
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaatTypography, // Utiliser notre typographie MaâtCore personnalisée
        content = content
    )
}