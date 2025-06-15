package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.R // Pour les placeholders
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

// Modèle de données pour les détails d'une plante (à enrichir)
data class PlantDetail(
    val id: String,
    val name: String,
    val scientificName: String,
    val imageUrl: String?, // URL ou ressource drawable
    val description: String,
    val uses: String,
    val precautions: String,
    val pubmedLinks: List<String>
)

// Simuler une fonction pour récupérer les détails d'une plante
fun getPlantDetailsById(plantId: String?): PlantDetail? {
    // TODO: Remplacer par une vraie source de données (API, base de données locale)
    return when (plantId) {
        "p1" -> PlantDetail("p1", "Gingembre", "Zingiber officinale", null, "Le gingembre est une plante herbacée tropicale originaire d'Asie...", "Utilisé pour les nausées, les douleurs articulaires, et comme anti-inflammatoire.", "Peut interagir avec les anticoagulants. À consommer avec modération.", listOf("https://pubmed.ncbi.nlm.nih.gov/gingembre123"))
        "p2" -> PlantDetail("p2", "Curcuma", "Curcuma longa", null, "Le curcuma est une plante herbacée vivace originaire du sud de l'Asie...", "Anti-inflammatoire majeur, antioxydant, utilisé dans la cuisine et la médecine traditionnelle.", "Peut causer des troubles digestifs à haute dose.", listOf("https://pubmed.ncbi.nlm.nih.gov/curcuma456"))
        // Ajouter d'autres plantes ici
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(navController: NavHostController, plantId: String?) {
    var plantDetail by remember { mutableStateOf<PlantDetail?>(null) }

    LaunchedEffect(plantId) {
        plantDetail = getPlantDetailsById(plantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plantDetail?.name ?: "Détail de la plante", fontFamily = PoppinsFamily, color = MaatColorOrSable) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = MaatColorOrSable)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaatColorNoirProfond)
            )
        },
        containerColor = MaatColorNoirProfond
    ) { paddingValues ->
        if (plantDetail == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Chargement des détails de la plante...", color = Color.Gray, fontFamily = PoppinsFamily)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Placeholder pour l'image de la plante
                Image(
                    painter = painterResource(id = R.drawable.ic_plant_placeholder), // Remplacez par une vraie image ou un placeholder
                    contentDescription = plantDetail!!.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.DarkGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(plantDetail!!.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaatColorOrSable, fontFamily = PoppinsFamily)
                Text(plantDetail!!.scientificName, fontSize = 16.sp, color = Color.Gray, fontFamily = PoppinsFamily, modifier = Modifier.padding(bottom = 16.dp))

                // Placeholder pour l'avatar qui lit
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ai_avatar_placeholder), // Remplacez par votre avatar
                        contentDescription = "Avatar IA",
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(MaatColorOrSable.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("L'assistant Maât vous présente cette plante...", fontSize = 14.sp, color = MaatColorOrSable, fontFamily = PoppinsFamily)
                    // TODO: Ajouter un bouton Play/Pause pour la lecture par l'avatar
                }
                
                DetailSection("Description", plantDetail!!.description)
                DetailSection("Usages courants", plantDetail!!.uses)
                DetailSection("Précautions", plantDetail!!.precautions)
                
                if (plantDetail!!.pubmedLinks.isNotEmpty()) {
                    DetailSection("Sources (PubMed)", "")
                    plantDetail!!.pubmedLinks.forEach { link ->
                        TextButton(onClick = { /* TODO: Ouvrir le lien dans un navigateur */ }) {
                            Text(link, color = MaatColorOrSable, fontSize = 14.sp, fontFamily = PoppinsFamily)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaatColorOrSable, fontFamily = PoppinsFamily)
        Spacer(modifier = Modifier.height(4.dp))
        Text(content, fontSize = 16.sp, color = Color.White.copy(alpha = 0.85f), fontFamily = PoppinsFamily)
    }
}