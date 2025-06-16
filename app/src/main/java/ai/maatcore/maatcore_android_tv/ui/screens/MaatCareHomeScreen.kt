package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.AnimatedVisibility // Keep: Already present
import androidx.compose.animation.animateColorAsState // ADD THIS for color animations
import androidx.compose.animation.core.LinearEasing // ADD THIS for tween
import androidx.compose.animation.core.animateDpAsState // ADD THIS for dp animations
import androidx.compose.animation.core.animateFloatAsState // ADD THIS for float animations
import androidx.compose.animation.core.spring // ADD THIS for spring animations
import androidx.compose.animation.core.tween // ADD THIS for tween animations
import androidx.compose.animation.fadeIn // Keep: Already present
import androidx.compose.animation.fadeOut // Keep: Already present
import androidx.compose.foundation.Image // Keep: Already present
import androidx.compose.foundation.background // Keep: Already present
import androidx.compose.foundation.border // Keep: Already present
import androidx.compose.foundation.clickable // Keep: Already present
import androidx.compose.foundation.focusable // ADD THIS if you make elements focusable directly
import androidx.compose.foundation.layout.* // Keep: Already present
import androidx.compose.foundation.lazy.grid.GridCells // Keep: Already present
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // Keep: Already present
import androidx.compose.foundation.lazy.grid.items // Keep: Already present
import androidx.compose.foundation.rememberScrollState // Keep: Already present
import androidx.compose.foundation.shape.RoundedCornerShape // Keep: Already present
import androidx.compose.foundation.verticalScroll // Keep: Already present
import androidx.compose.material.icons.Icons // Keep: Already present
import androidx.compose.material.icons.filled.* // Keep: Already present
import androidx.compose.material3.* // Keep: Already present (ensure this is Material 3 Card, Icon, Text etc.)
import androidx.compose.runtime.* // Keep: Already present
import androidx.compose.ui.Alignment // Keep: Already present
import androidx.compose.ui.Modifier // Keep: Already present
import androidx.compose.ui.draw.scale // Keep: Already present
import androidx.compose.ui.focus.FocusRequester // Keep: Already present
import androidx.compose.ui.focus.focusRequester // Keep: Already present
import androidx.compose.ui.focus.onFocusChanged // Keep: Already present
import androidx.compose.ui.geometry.Offset // Keep: Already present
import androidx.compose.ui.graphics.Brush // Keep: Already present
import androidx.compose.ui.graphics.Color // Keep: Already present
import androidx.compose.ui.graphics.vector.ImageVector // Keep: Already present
import androidx.compose.ui.res.painterResource // Keep: Already present
import androidx.compose.ui.text.TextStyle // Keep: Already present
import androidx.compose.ui.text.font.FontWeight // Keep: Already present
import androidx.compose.ui.text.style.TextOverflow // Keep: Already present
import androidx.compose.ui.unit.dp // Keep: Already present
import androidx.compose.ui.unit.sp // Keep: Already present
import androidx.navigation.NavController // Keep: Already present
import ai.maatcore.maatcore_android_tv.R // Keep: Already present

// Theme imports (Ensure these files/objects exist and contain your definitions)
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorBleuNuit // ADD THIS (if defined in theme)
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorRose // ADD THIS (if defined in theme)
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorVertSante // ADD THIS (if defined in theme)
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorViolet // ADD THIS (if defined in theme)
import ai.maatcore.maatcore_android_tv.ui.theme.MontserratFamily // ADD THIS (if defined in theme)
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily // ADD THIS (if defined in theme)
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip

// Potentially other theme colors if Color(0xFFD4AF37) is also a theme color
// import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOr // Example if 0xFFD4AF37 is named

/* ---------- data ---------- */

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val avatarRes: Int, // Assuming R.drawable.placeholder_image
    val iconRes: ImageVector
)

data class ServiceOption(
    val id: String,
    val name: String,
    val icon: ImageVector
)

// VitalSign class seems unused in this specific file, but keeping it as it was present.
data class VitalSign(
    val id: String,
    val name: String,
    val value: String,
    val unit: String,
    val icon: ImageVector
)

@Composable
fun animateGradientColors(isFocused: Boolean): List<Color> {
    // Ensure MaatColorViolet, MaatColorRose, MaatColorBleuNuit are imported or defined
    // and animateColorAsState, tween, LinearEasing are imported.
    val colorStops = if (isFocused) {
        listOf(
            animateColorAsState(targetValue = MaatColorViolet, animationSpec = tween(durationMillis = 300, easing = LinearEasing), label = "color1").value,
            animateColorAsState(targetValue = MaatColorRose, animationSpec = tween(durationMillis = 300, easing = LinearEasing), label = "color2").value
        )
    } else {
        listOf(
            animateColorAsState(targetValue = MaatColorBleuNuit, animationSpec = tween(durationMillis = 300, easing = LinearEasing), label = "color3").value,
            animateColorAsState(targetValue = MaatColorBleuNuit.copy(alpha = 0.7f), animationSpec = tween(durationMillis = 300, easing = LinearEasing), label = "color4").value
        )
    }
    return colorStops
}

/* ---------- screen ---------- */

@Composable
fun MaatCareHomeScreen(navController: NavController) {
    val doctors = listOf(
        Doctor("1", "Dr. Amara", "Médecine Générale", R.drawable.placeholder_image, Icons.Default.MedicalServices),
        Doctor("2", "Dr. Kwame", "Cardiologie", R.drawable.placeholder_image, Icons.Default.Favorite),
        Doctor("3", "Dr. Fatou", "Pédiatrie", R.drawable.placeholder_image, Icons.Default.ChildCare),
        Doctor("4", "Dr. Omar", "Dermatologie", R.drawable.placeholder_image, Icons.Default.Spa),
        Doctor("5", "Dr. Aisha", "Gynécologie", R.drawable.placeholder_image, Icons.Default.PregnantWoman)
    )

    val services = listOf(
        ServiceOption("1", "Consultation", Icons.Default.RecordVoiceOver),
        ServiceOption("2", "Phytothérapie", Icons.Default.Eco),
        ServiceOption("3", "Signes vitaux", Icons.Default.MonitorHeart),
        ServiceOption("4", "Appel vidéo", Icons.Default.Videocam)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Example background for the whole screen, adjust as needed
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "MaatCare",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFamily, // Ensure MontserratFamily is defined in your theme and imported
            color = Color(0xFFD4AF37) // Consider defining this as a theme color e.g., MaatColorOr
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Nos médecins",
            style = MaterialTheme.typography.titleMedium, // Example using MaterialTheme
            color = Color.White // Example color
            // fontWeight = FontWeight.SemiBold // Already part of titleMedium usually
        )
        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth() // Allow grid to take available width
                .heightIn(max = 240.dp) // Constrain height
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Services rapides",
            style = MaterialTheme.typography.titleMedium, // Example using MaterialTheme
            color = Color.White // Example color
            // fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            services.forEach { svc ->
                ServiceChip(
                    option = svc,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (svc.id) {
                            "1" -> navController.navigate("consultation_symptom_input")
                            "3" -> navController.navigate("vital_parameters_screen")
                            // Consider adding a fallback or log for unhandled cases
                            else -> {
                                // Log.d("MaatCareHomeScreen", "ServiceChip clicked with unhandled id: ${svc.id}")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow), // Using standard stiffness
        label = "cardScale"
    )

    val animatedGradientColors = animateGradientColors(isFocused = isFocused)

    val iconColor by animateColorAsState(
        targetValue = if (isFocused) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(durationMillis = 150), // Standard tween, ensure imports
        label = "iconColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaatColorVertSante else Color.Transparent, // Animate border color
        animationSpec = tween(durationMillis = 150),
        label = "borderColor"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 0.dp, // Animate border width
        animationSpec = tween(durationMillis = 150),
        label = "borderWidth"
    )


    Box(
        modifier = Modifier
            .height(120.dp) // Consider using .aspectRatio(16f/9f) or similar for TV cards if width is flexible
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
            .scale(scale)
            .border(
                width = borderWidth, // Use animated border width
                color = borderColor, // Use animated border color
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = animatedGradientColors, // This is already a List<Color>
                    start = Offset.Zero,
                    end = Offset.Infinite // For linear gradient, end should be finite for specific direction
                    // e.g., end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) for diagonal
                    // or specific points if you want controlled angle. Offset.Infinite might work but is less explicit.
                    // Using Offset(0f, Float.POSITIVE_INFINITY) for vertical for example.
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp)) // Ensure content is clipped to the shape
            .clickable { /* Handle Doctor Click, e.g., navigate to doctor profile */ }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = doctor.avatarRes),
                contentDescription = doctor.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp)) // Clip avatar image if desired
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.align(Alignment.CenterVertically)) { // This Column is fine
                Text(
                    text = doctor.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = iconColor, // This should be text color, not iconColor for clarity
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = doctor.specialty,
                    fontSize = 12.sp,
                    color = iconColor.copy(alpha = 0.8f), // This should be text color
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ServiceChip(
    option: ServiceOption,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "chipScale"
    )

    val animatedGradientColors = animateGradientColors(isFocused = isFocused)

    val textColor by animateColorAsState(
        targetValue = if (isFocused) Color.White else Color(0xFFD4AF37), // Ensure Color(0xFFD4AF37) is what you want
        animationSpec = tween(durationMillis = 150),
        label = "textColor"
    )

    val iconColorState by animateColorAsState( // Renamed to avoid conflict if iconColor is already a val
        targetValue = if (isFocused) Color.White else Color(0xFFD4AF37),
        animationSpec = tween(durationMillis = 150),
        label = "iconColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 0.dp, // Consistent border width animation
        animationSpec = tween(durationMillis = 150),
        label = "borderWidth"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaatColorVertSante else Color.Transparent, // Consistent border color animation
        animationSpec = tween(durationMillis = 150),
        label = "chipBorderColor"
    )

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
            .scale(scale)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = animatedGradientColors, // This is already a List<Color>
                    start = Offset.Zero,
                    end = Offset(
                        Float.POSITIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                    ) // Example for diagonal
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp)) // Clip content
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp), // Adjusted padding
        contentAlignment = Alignment.Center // Center content within the Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
            // .padding(8.dp) // Padding is now on the parent Box
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.name,
                tint = iconColorState, // Use the state variable
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = option.name,
                style = TextStyle( // Consider defining this as a MaterialTheme typography style
                    fontFamily = PoppinsFamily, // Ensure PoppinsFamily is defined and imported
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- Make sure your theme file (e.g., ui/theme/Color.kt) has these colors defined ---
// Example:
// package ai.maatcore.maatcore_android_tv.ui.theme
//
// import androidx.compose.ui.graphics.Color
//
// val MaatColorViolet = Color(0xFF8A2BE2) // Example: BlueViolet
// val MaatColorRose = Color(0xFFFFC0CB)   // Example: Pink
// val MaatColorBleuNuit = Color(0xFF191970) // Example: MidnightBlue
// val MaatColorVertSante = Color(0xFF4CAF50) // Example
// val MaatColorOr = Color(0xFFD4AF37) // For the gold color

// --- Make sure your theme file (e.g., ui/theme/Type.kt) has these fonts defined ---
// Example:
// package ai.maatcore.maatcore_android_tv.ui.theme
//
// import androidx.compose.ui.text.font.FontFamily
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.text.googlefonts.Font // If using Google Fonts
// import androidx.compose.ui.text.googlefonts.GoogleFont // If using Google Fonts
// import ai.maatcore.maatcore_android_tv.R
//
// val provider = GoogleFont.Provider(...) // If using Google Fonts
//
// val MontserratFamily = FontFamily(Font(R.font.montserrat_regular), Font(R.font.montserrat_bold, FontWeight.Bold)) // Example
// val PoppinsFamily = FontFamily(Font(R.font.poppins_regular), Font(R.font.poppins_medium, FontWeight.Medium)) // Example