package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorVertSante
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

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
            .background(MaatColorNoirProfond) // Fond noir profond
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 48.dp) // Ajout de padding pour l'overscan TV
    ) {
        Text(
            text = "Bienvenue sur Maât.Care",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = MaatColorVertSante, // Titre accent vert santé
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp) // Espace entre les cartes
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick(feature.id) }
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) MaatColorVertSante.copy(alpha = 0.25f) else Color.DarkGray.copy(alpha = 0.3f) // Changement de couleur au focus
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFocused) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = feature.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PoppinsFamily,
                    color = if (isFocused) MaatColorVertSante else Color.White // Titre vert si focusé
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = feature.description,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    color = if (isFocused) Color.White.copy(alpha = 0.9f) else Color.Gray // Description plus claire si focusé
                )
            }
            // Avatar image if consultation
            if (feature.id == "consultation") {
                Spacer(modifier = Modifier.width(16.dp))
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = ai.maatcore.maatcore_android_tv.R.drawable.ic_ai_doctor_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
