package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.data.ContentType
import ai.maatcore.maatcore_android_tv.data.Episode
import ai.maatcore.maatcore_android_tv.data.Season
import ai.maatcore.maatcore_android_tv.data.UserPlaybackState

@Composable
fun ContentDetailScreen(
    contentId: String,
    navController: NavHostController
) {
    // Simulation de données - en production, récupérer via API
    val content = remember { getSampleContentDetail(contentId) }
    val playbackState = remember { getSamplePlaybackState(contentId) }
    
    var selectedSeason by remember { mutableStateOf(1) }
    var selectedEpisode by remember { mutableStateOf<Episode?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Image hero en arrière-plan
        Image(
            painter = painterResource(id = R.drawable.featured_placeholder),
            contentDescription = content.title,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black
                        ),
                        startY = 0f,
                        endY = 1200f
                    )
                ),
            contentScale = ContentScale.Crop
        )
        
        // Contenu principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Bouton retour
                BackButton(onBack = { navController.popBackStack() })
            }
            
            item {
                Spacer(modifier = Modifier.height(200.dp))
            }
            
            item {
                // Titre et métadonnées principales
                ContentHeader(content = content)
            }
            
            item {
                // Bouton principal d'action
                PlayButton(
                    content = content,
                    playbackState = playbackState,
                    selectedEpisode = selectedEpisode,
                    onPlay = { /* TODO: Lancer le lecteur */ }
                )
            }
            
            item {
                // Description longue
                ContentDescription(content = content)
            }
            
            item {
                // Informations détaillées
                ContentMetadata(content = content)
            }
            
            // Sélection saison/épisode pour les séries
            if (content.contentType == ContentType.SERIES && content.seasons != null && content.seasons.isNotEmpty()) {
                item {
                    SeasonSelector(
                        seasons = content.seasons,
                        selectedSeason = selectedSeason,
                        onSeasonSelected = { selectedSeason = it }
                    )
                }
                
                item {
                    EpisodeList(
                        episodes = content.seasons.find { it.seasonNumber == selectedSeason }?.episodes ?: emptyList(),
                        selectedEpisode = selectedEpisode,
                        onEpisodeSelected = { selectedEpisode = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    
    Button(
        onClick = onBack,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) Color.White else Color.White.copy(alpha = 0.2f), // M3 containerColor
            contentColor = if (isFocused) Color.Black else Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // MODIFIÉ
            contentDescription = "Retour",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Retour")
    }
}

@Composable
private fun ContentHeader(content: ai.maatcore.maatcore_android_tv.data.ContentItem) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = content.title,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (content.releaseDate != null) {
                Text(
                    text = content.releaseDate,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            if (content.durationMinutes != null) {
                Text(
                    text = "${content.durationMinutes}min",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            if (content.parentalRating != null) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = content.parentalRating,
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (content.genre != null) {
                Text(
                    text = content.genre,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PlayButton(
    content: ai.maatcore.maatcore_android_tv.data.ContentItem,
    playbackState: UserPlaybackState?,
    selectedEpisode: Episode?,
    onPlay: () -> Unit
) {
    var isFocused by remember { mutableStateOf(true) } // Focus initial sur le bouton principal
    
    val buttonText = when {
        playbackState != null && !playbackState.completed -> {
            val minutes = (playbackState.currentPositionMs / 60000).toInt()
            val seconds = ((playbackState.currentPositionMs % 60000) / 1000).toInt()
            "Reprendre à ${minutes}:${seconds.toString().padStart(2, '0')}"
        }
        content.contentType == ContentType.SERIES && selectedEpisode != null -> {
            "Regarder S${selectedEpisode.episodeNumber.toString().padStart(2, '0')}"
        }
        content.contentType == ContentType.PODCAST -> "Écouter le Podcast"
        else -> "Lire"
    }
    
    Button(
        onClick = onPlay,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFocused) Color.White else Color.White.copy(alpha = 0.9f), // M3 containerColor
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = buttonText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ContentDescription(content: ai.maatcore.maatcore_android_tv.data.ContentItem) {
    if (content.descriptionLong != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Synopsis",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = content.descriptionLong,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun ContentMetadata(content: ai.maatcore.maatcore_android_tv.data.ContentItem) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (content.contentType) {
            ContentType.MOVIE -> {
                if (content.directors != null) {
                    MetadataRow("Réalisateur(s)", content.directors.joinToString(", "))
                }
                if (content.cast != null) {
                    MetadataRow("Casting", content.cast.take(5).joinToString(", "))
                }
            }
            ContentType.SERIES -> {
                if (content.creators != null) {
                    MetadataRow("Créateur(s)", content.creators.joinToString(", "))
                }
                if (content.mainCast != null) {
                    MetadataRow("Casting principal", content.mainCast.take(5).joinToString(", "))
                }
                if (content.numberOfSeasons != null) {
                    MetadataRow("Saisons", "${content.numberOfSeasons} saison${if (content.numberOfSeasons > 1) "s" else ""}")
                }
            }
            else -> {
                // Métadonnées spécifiques aux documentaires, podcasts, etc.
            }
        }
        
        if (content.language != null) {
            MetadataRow("Langue", content.language)
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(150.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SeasonSelector(
    seasons: List<Season>,
    selectedSeason: Int,
    onSeasonSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Saisons",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(seasons) { season ->
                SeasonChip(
                    season = season,
                    isSelected = season.seasonNumber == selectedSeason,
                    onSelected = { onSeasonSelected(season.seasonNumber) }
                )
            }
        }
    }
}

@Composable
private fun SeasonChip(
    season: Season,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(containerColor = when { // M3 Card colors
            isSelected -> Color.White
            isFocused -> Color.White.copy(alpha = 0.3f)
            else -> Color.White.copy(alpha = 0.1f)
        }),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // M3 Card elevation
    ) {
        Text(
            text = "Saison ${season.seasonNumber}",
            fontSize = 14.sp,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun EpisodeList(
    episodes: List<Episode>,
    selectedEpisode: Episode?,
    onEpisodeSelected: (Episode) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Épisodes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(300.dp)
        ) {
            items(episodes) { episode ->
                EpisodeCard(
                    episode = episode,
                    isSelected = episode == selectedEpisode,
                    onSelected = { onEpisodeSelected(episode) }
                )
            }
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: Episode,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(containerColor = when { // M3 Card colors
            isSelected -> Color.White.copy(alpha = 0.2f)
            isFocused -> Color.White.copy(alpha = 0.1f)
            else -> Color.Transparent
        }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // M3 Card elevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = episode.episodeNumber.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.width(40.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = episode.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                episode.description?.let { description ->
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            episode.durationMinutes?.let { duration ->
                Text(
                    text = "${duration}min",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// Fonctions utilitaires pour les données de démonstration
private fun getSampleContentDetail(contentId: String): ai.maatcore.maatcore_android_tv.data.ContentItem {
    return when (contentId) {
        "1" -> ai.maatcore.maatcore_android_tv.data.ContentItem(
            id = "1",
            title = "Black Panther: Wakanda Forever",
            imageUrl = "https://picsum.photos/300/450?random=1",
            mainImageUrl = "https://picsum.photos/1920/1080?random=1",
            subtitle = "Action • 2022 • 2h 41min",
            descriptionLong = "La reine Ramonda, Shuri, M'Baku, Okoye et les Dora Milaje luttent pour protéger leur nation des puissances mondiales qui interviennent à la suite de la mort du roi T'Challa. Alors que les Wakandais s'efforcent d'embrasser leur prochain chapitre, les héros doivent s'unir avec l'aide de War Dog Nakia et d'Everett Ross pour forger un nouveau chemin pour le royaume de Wakanda.",
            durationMinutes = 161,
            releaseDate = "2022",
            parentalRating = "PG-13",
            language = "Français, Anglais",
            contentType = ContentType.MOVIE,
            directors = listOf("Ryan Coogler"),
            cast = listOf("Letitia Wright", "Angela Bassett", "Tenoch Huerta", "Danai Gurira", "Lupita Nyong'o"),
            genre = "Action, Aventure"
        )
        "2" -> ai.maatcore.maatcore_android_tv.data.ContentItem(
            id = "2",
            title = "Queen Sono",
            imageUrl = "https://picsum.photos/300/450?random=2",
            mainImageUrl = "https://picsum.photos/1920/1080?random=2",
            subtitle = "Série • Thriller • 2020",
            descriptionLong = "Une espionne sud-africaine hautement qualifiée doit affronter des défis personnels et professionnels tout en enquêtant sur la mort de sa mère et en découvrant une conspiration qui menace l'Afrique du Sud.",
            durationMinutes = 50,
            releaseDate = "2020",
            parentalRating = "16+",
            language = "Français, Anglais, Zulu",
            contentType = ContentType.SERIES,
            creators = listOf("Kagiso Lediga"),
            mainCast = listOf("Pearl Thusi", "Vuyo Dabula", "Sechaba Morojele", "Chi Mhende"),
            numberOfSeasons = 1,
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Saison 1",
                    episodes = listOf(
                        Episode(1, "Épisode 1", "Queen Sono commence sa mission la plus dangereuse.", 52),
                        Episode(2, "Épisode 2", "Les secrets du passé refont surface.", 48),
                        Episode(3, "Épisode 3", "Une nouvelle menace émerge.", 51)
                    )
                )
            ),
            genre = "Thriller, Action"
        )
        else -> ai.maatcore.maatcore_android_tv.data.ContentItem(
            id = contentId,
            title = "Contenu non trouvé",
            imageUrl = "https://picsum.photos/300/450?random=999",
            subtitle = "Erreur"
        )
    }
}

private fun getSamplePlaybackState(contentId: String): UserPlaybackState? {
    return if (contentId == "1") {
        UserPlaybackState(
            contentId = contentId,
            currentPositionMs = 1510000, // 25:10
            totalDurationMs = 9660000, // 2h 41min
            lastWatched = "2024-01-15"
        )
    } else null
}
