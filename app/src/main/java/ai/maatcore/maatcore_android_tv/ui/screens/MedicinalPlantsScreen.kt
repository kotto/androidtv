package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

// TODO: Déplacer vers un fichier de modèle de données
data class Plant(val id: String, val name: String, val shortDescription: String, val imageUrl: String? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicinalPlantsScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    // TODO: Remplacer par une vraie liste de plantes, potentiellement filtrée par la recherche
    val allPlants = listOf(
        Plant("p1", "Gingembre (Zingiber officinale)", "Anti-inflammatoire, aide à la digestion."),
        Plant("p2", "Curcuma (Curcuma longa)", "Puissant antioxydant et anti-inflammatoire."),
        Plant("p3", "Aloe Vera (Aloe barbadensis miller)", "Cicatrisant, hydratant pour la peau."),
        Plant("p4", "Moringa (Moringa oleifera)", "Riche en nutriments, vitamines et minéraux."),
        Plant("p5", "Hibiscus (Hibiscus sabdariffa)", "Aide à réduire la pression artérielle, riche en vitamine C.")
    )
    val filteredPlants = allPlants.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.shortDescription.contains(searchQuery, ignoreCase = true) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 48.dp)
    ) {
        Text(
            text = "Soigner par les Plantes",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = MaatColorOrSable,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rechercher une plante ou un symptôme...", fontFamily = PoppinsFamily) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Recherche") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaatColorOrSable,
                unfocusedBorderColor = Color.Gray,
                cursorColor = MaatColorOrSable,
                focusedLabelColor = MaatColorOrSable,
                unfocusedLabelColor = Color.Gray
            )
        )

        if (filteredPlants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if(searchQuery.isNotEmpty()) "Aucune plante trouvée pour votre recherche." else "Chargement des plantes...",
                    color = Color.Gray, 
                    fontSize = 18.sp, 
                    fontFamily = PoppinsFamily
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredPlants) { plant ->
                    PlantCard(plant = plant) {
                        navController.navigate("plant_detail/${plant.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun PlantCard(plant: Plant, onClick: () -> Unit) {
    // TODO: Améliorer le design de la carte, ajouter image si disponible
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(plant.name, fontWeight = FontWeight.SemiBold, color = MaatColorOrSable, fontSize = 18.sp, fontFamily = PoppinsFamily)
            Spacer(modifier = Modifier.height(4.dp))
            Text(plant.shortDescription, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontFamily = PoppinsFamily)
        }
    }
}
