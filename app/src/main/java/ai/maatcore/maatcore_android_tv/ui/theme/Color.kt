package ai.maatcore.maatcore_android_tv.ui.theme

import androidx.compose.ui.graphics.Color

// MaâtCore Palette
val MaatColorNoirProfond = Color(0xFF0D0D0D)         // Fond principal (dark mode)
val MaatColorOrSable = Color(0xFFF5D487)            // Texte principal
val MaatColorOrangeSolaire = Color(0xFFFF8C42)       // Accent 1 (CTA, hover)
val MaatColorCuivreAncien = Color(0xFFA65C2E)        // Accent 2 (soulignement)
val MaatColorGrisTresFonce = Color(0xFF1E1E1E)       // Fond secondaire carte
val MaatColorCuivreClair = Color(0xFFE0C28A)         // Texte secondaire
val MaatColorGrisClair = Color(0xFFCCCCCC)           // Added for placeholder backgrounds

// Service Specific Accents
val MaatColorVertSante = Color(0xFF46B17B)           // Accent MaâtCare
val MaatColorJauneEnergie = Color(0xFFFFD23F)         // Accent MaâtFoot
val MaatColorBleuCeruleen = Color(0xFF00A3FF)         // Accent MaâtClass

// Gradient Colors for Netflix UI (These are not the MaatCare specific animation colors)
val MenuGradientStart = Color(0xFF0D0700)
val MenuGradientEnd = Color(0xFF160A00)
val CardSectionGradientStart = Color(0xFF0D0700) // Same as MenuGradientStart for consistency
val CardSectionGradientEnd = Color(0xFF250F00)

// Colors for MaatCare Screen Animations (ADD THESE)
// IMPORTANT: Replace the Color(0x...) values with the actual hex codes you want.
val MaatColorViolet = Color(0xFF8A2BE2)    // Example: BlueViolet - CHOOSE YOUR ACTUAL COLOR
val MaatColorRose = Color(0xFFFFC0CB)      // Example: Pink - CHOOSE YOUR ACTUAL COLOR
val MaatColorBleuNuit = Color(0xFF191970)  // Example: MidnightBlue - CHOOSE YOUR ACTUAL COLOR

// You can add more semantic color names here if needed, for example:
// val Primary = MaatColorOrangeSolaire
// val OnPrimary = Color.Black // Or MaatColorNoirProfond if contrast is good
// val Background = MaatColorNoirProfond
// val OnBackground = MaatColorOrSable
// val Surface = MaatColorGrisTresFonce
// val OnSurface = MaatColorOrSable
// val Secondary = MaatColorCuivreAncien
// val OnSecondary = MaatColorOrSable