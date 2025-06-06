package ai.maatcore.maatcore_android_tv.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import ai.maatcore.maatcore_android_tv.R

// Utilisation de polices système en attendant d'ajouter les fichiers de police personnalisés
val Montserrat = FontFamily.SansSerif
val Poppins = FontFamily.SansSerif
val Inter = FontFamily.SansSerif

// Note: Pour utiliser des polices personnalisées, vous devrez ajouter les fichiers TTF/OTF dans res/font
// et décommenter le code ci-dessous:
/*
val Montserrat = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.montserrat_regular, FontWeight.Normal),
    androidx.compose.ui.text.font.Font(R.font.montserrat_bold, FontWeight.Bold),
    androidx.compose.ui.text.font.Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

val Poppins = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.poppins_regular, FontWeight.Normal),
    androidx.compose.ui.text.font.Font(R.font.poppins_medium, FontWeight.Medium),
    androidx.compose.ui.text.font.Font(R.font.poppins_semibold, FontWeight.SemiBold),
    androidx.compose.ui.text.font.Font(R.font.poppins_bold, FontWeight.Bold)
)

val Inter = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.inter_regular, FontWeight.Normal),
    androidx.compose.ui.text.font.Font(R.font.inter_medium, FontWeight.Medium),
    androidx.compose.ui.text.font.Font(R.font.inter_semibold, FontWeight.SemiBold)
)
*/

// Définition des styles de typographie MaâtCore compatible avec Material3 standard
val MaatTypography = Typography(
    displayLarge = TextStyle( // H1: Titres H1
        fontFamily = Montserrat, // Ou Poppins
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 80.sp, // Ajustez si nécessaire
        letterSpacing = 0.sp // Ajustez si nécessaire
    ),
    displayMedium = TextStyle( // H2: Titres H2
        fontFamily = Montserrat, // Ou Poppins
        fontWeight = FontWeight.SemiBold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle( // Sous-titres
        fontFamily = Poppins, // Ou Inter
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle( // Texte courant
        fontFamily = Inter, // Ou Noto Sans
        fontWeight = FontWeight.Normal, // Regular
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp // Ajustez si nécessaire
    ),
    labelLarge = TextStyle( // Boutons
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    )
    // Vous pouvez définir d'autres styles comme titleLarge, bodyMedium, etc. si nécessaire
    // en vous basant sur les classes de Material 3 Typography et vos spécifications.
    // Par exemple, pour un texte plus petit ou des légendes :
    /*
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
    */
)
