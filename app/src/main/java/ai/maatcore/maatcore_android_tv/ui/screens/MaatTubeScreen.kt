package ai.maatcore.maatcore_android_tv.ui.screens

// Imports existants...
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape // Explicitly add this import
import androidx.compose.material3.MaterialTheme // Use standard MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import ai.maatcore.maatcore_android_tv.ui.viewmodel.VideoViewModel

import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.ui.theme.MontserratFamily
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import ai.maatcore.maatcore_android_tv.ui.theme.InterFamily
import androidx.compose.foundation.lazy.grid.GridCells


// Modèles de données (simplifiés)
data class Video(val id: String, val title: String, val channel: String, val thumbnailRes: Int)
data class UserProfile(val id: String, val name: String, val avatarRes: Int)

@OptIn(ExperimentalMaterial3Api::class) // Removed ExperimentalTvMaterial3Api
@Composable
fun MaatTubeScreen(navController: NavController, viewModel: VideoViewModel = hiltViewModel()) {

    var searchQuery by remember { mutableStateOf("") }

    val videosState by viewModel.videos.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadVideos() }

    val videos = if (videosState.isNotEmpty()) {
        videosState.map { Video(it.id, it.title, it.channel, R.drawable.placeholder_image) }
    } else {
        listOf(
            Video("1", "Les Secrets de l'Afrique Ancienne", "HistoireTV", R.drawable.placeholder_image),
            Video("2", "Concert Exclusif Burna Boy", "MaatMusic", R.drawable.placeholder_image)
        )
    }

    val userProfiles = listOf(
        UserProfile("1", "Amara", R.drawable.placeholder_image),
        UserProfile("2", "Kwame", R.drawable.placeholder_image),
        UserProfile("3", "Fatou", R.drawable.placeholder_image)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header avec logo et profil
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_image),
                contentDescription = "MaâtTube Logo",
                modifier = Modifier.height(40.dp),
                contentScale = ContentScale.Fit
            )
            // Affichage simple du premier profil ou un sélecteur de profil
            if (userProfiles.isNotEmpty()) {
                 Image(
                    painter = painterResource(id = userProfiles[0].avatarRes),
                    contentDescription = userProfiles[0].name,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            } else {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Profil", modifier = Modifier.size(40.dp))
            }
        }
         Text(
            "Explorez MaâtTube",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFamily,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barre de recherche
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher...", fontFamily = PoppinsFamily) },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Recherche") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Effacer")
                        }
                    }
                }
            )
            IconButton(onClick = { /* TODO: Logique recherche IA */ }) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Recherche IA",
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { /* TODO: Logique recherche vocale */ }) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Recherche Vocale",
                    modifier = Modifier.size(28.dp)
                )
            }
        }


        // Bouton "Générer avec l'IA" (simplifié)
        Button(
            onClick = { /* TODO: Action Générer avec IA */ },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Suggérer du contenu avec l'IA", fontFamily = InterFamily)
        }

        Text(
            "Vidéos Populaires",
            fontSize = 20.sp,
            fontFamily = PoppinsFamily,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Grille de vidéos
        LazyVerticalGrid( // Changed to LazyVerticalGrid
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(videos.filter { it.title.contains(searchQuery, ignoreCase = true) }) { video ->
                VideoCard(video) {
                    navController.navigate("video_detail/${video.id}")
                }
            }
        }

        // Section Tendances (exemple simple)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = "Tendances",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Contenus Tendances",
                fontSize = 18.sp,
                fontFamily = InterFamily,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Removed ExperimentalTvMaterial3Api
@Composable
fun VideoCard(video: Video, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
        // .height(200.dp) // La hauteur peut être dynamique ou fixe
    ) {
        Column {
            Image(
                painter = painterResource(id = video.thumbnailRes),
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    fontFamily = PoppinsFamily,
                    fontSize = 16.sp,
                    maxLines = 2,
                    color = Color.White
                )
                Text(
                    text = video.channel,
                    fontFamily = InterFamily,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}
