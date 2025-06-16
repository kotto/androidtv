package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorVertSante
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import androidx.compose.animation.animateColorAsState
import ai.maatcore.maatcore_android_tv.R as AppR

data class MaatCareFeature(val id: String, val title: String, val description: String)



@Composable
fun MaatCareScreen(navController: NavHostController) {
    val features = listOf(
        MaatCareFeature("consultation", "Parcours de Consultation", "Initiez une consultation avec notre assistant IA ou un médecin."),
        MaatCareFeature("plantes", "Soigner par les Plantes", "Découvrez les bienfaits des plantes médicinales traditionnelles."),
        MaatCareFeature("parametres", "Mes Paramètres Vitaux", "Suivez vos indicateurs de santé (tensiomètre, oxymètre...)." )
    )

Column(
    modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.radialGradient(
                colors = listOf(MaatColorNoirProfond, MaatColorOrSable),
                
                radius = 300f
            )
        )
        .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 48.dp)
) {
    Text(
        text = "Bienvenue sur Maât.Care",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = PoppinsFamily,
        color = MaatColorVertSante,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(features) { feature ->
            MaatCareFeatureCard(navController = navController, feature = feature) { featureId -> 
                if (featureId == "consultation") {
                    navController.navigate("consultation_symptom_input")
                } else {
                    if (featureId == "plantes") {
                        navController.navigate("medicinal_plants_screen")
                    } else {
                        if (featureId == "parametres") {
                            navController.navigate("vital_parameters_screen")
                        } else {
                            // Toutes les features de MaatCare ont une navigation de base
                        }
                    }
                } 
            }
        }
    }
}
}

@Composable
fun MaatCareFeatureCard(navController: NavHostController, feature: MaatCareFeature, onClick: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 350f),
        label = "cardScale"
    )

    val gradientBrush = remember(isFocused) {
        Brush.radialGradient(
            colors = if (isFocused) listOf(MaatColorNoirProfond, MaatColorOrSable)
            else listOf(MaatColorNoirProfond, MaatColorOrSable.copy(alpha = 0.7f))
        )
    }

    val textColor by animateColorAsState(
        targetValue = if (isFocused) Color.White else Color(0xFFD4AF37),
        animationSpec = tween(150),
        label = "textColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isFocused) Color.White else Color(0xFFD4AF37),
        animationSpec = tween(150),
        label = "iconColor"
    )

    val border by animateDpAsState(
        targetValue = if (isFocused) 3.dp else 0.dp,
        label = "borderWidth"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick(feature.id) }
            .focusable()
            .clip(CardDefaults.shape)
            .background(
                brush = gradientBrush,
                shape = CardDefaults.shape
            )
            .border(
                width = border,
                color = MaatColorVertSante,
                shape = CardDefaults.shape
            )
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 8.dp else 2.dp),
        shape = CardDefaults.shape
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feature.title,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = textColor.copy(alpha = 0.8f)
                )
            }
            if (feature.id == "consultation") {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = AppR.drawable.ic_ai_doctor_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
