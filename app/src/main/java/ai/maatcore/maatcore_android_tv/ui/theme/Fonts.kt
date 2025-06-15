package ai.maatcore.maatcore_android_tv.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight // IMPORT AJOUTÉ
import ai.maatcore.maatcore_android_tv.R // IMPORT AJOUTÉ (pour R.font.*)

// Définition pour Montserrat
val MontserratFamily = FontFamily(
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold)
    // Ajoutez d'autres épaisseurs/styles si nécessaire, par exemple :
    // Font(R.font.montserrat_regular, FontWeight.Normal),
    // Font(R.font.montserrat_bold, FontWeight.Bold)
)

// Définition pour Poppins
val PoppinsFamily = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
    // Ajoutez d'autres épaisseurs/styles si nécessaire
    // Font(R.font.poppins_regular, FontWeight.Normal),
    // Font(R.font.poppins_medium, FontWeight.Medium)
)

// Définition pour Inter
val InterFamily = FontFamily(
    Font(R.font.inter_24pt_regular, FontWeight.Normal)
    // Ajoutez d'autres épaisseurs/styles si nécessaire
    // Font(R.font.inter_medium, FontWeight.Medium),
    // Font(R.font.inter_bold, FontWeight.Bold)
)
