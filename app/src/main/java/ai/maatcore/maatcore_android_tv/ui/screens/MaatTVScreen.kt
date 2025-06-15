package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.components.*
import ai.maatcore.maatcore_android_tv.data.ContentItem
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import ai.maatcore.maatcore_android_tv.ui.viewmodel.ProgramViewModel
import ai.maatcore.maatcore_android_tv.data.remote.maattv.ProgramDto

@Composable
fun MaatTVScreen(navController: NavHostController, viewModel: ProgramViewModel = hiltViewModel()) {
    var currentRoute by remember { mutableStateOf("maattv") }
    var selectedContent by remember { mutableStateOf<ContentItem?>(null) }
    
    // Load programs from backend
    val programsState by viewModel.programs.collectAsState()
    LaunchedEffect(Unit) { viewModel.load() }
    
    val vodContent: List<ContentItem> = if (programsState.isNotEmpty()) {
        programsState.filter { !it.live && it.description.contains("Film", true) || it.description.contains("Série", true) }
            .map { it.toContentItem() }
    } else listOf(
        ContentItem("1", "Black Panther: Wakanda Forever", "https://picsum.photos/300/450?random=1", "Action • 2022 • 2h 41min", 
            description = "Après la mort du roi T'Challa, le royaume de Wakanda doit faire face à de nouvelles menaces tout en honorant l'héritage de leur roi défunt."),
        ContentItem("2", "Queen Sono", "https://picsum.photos/300/450?random=2", "Série • Thriller • 2020",
            description = "Une espionne sud-africaine travaille pour une agence secrète tout en cherchant la vérité sur la mort de sa mère."),
        ContentItem("3", "Blood & Water", "https://picsum.photos/300/450?random=3", "Série • Drame • 2020",
            description = "Une adolescente découvre un secret de famille qui bouleverse sa vie dans cette série dramatique sud-africaine."),
        ContentItem("4", "The Woman King", "https://picsum.photos/300/450?random=4", "Action • Historique • 2022",
            description = "L'histoire épique des Agojié, un groupe de guerrières qui ont protégé le royaume africain du Dahomey."),
        ContentItem("5", "Coming 2 America", "https://picsum.photos/300/450?random=5", "Comédie • 2021 • 1h 50min",
            description = "Le prince Akeem retourne en Amérique pour retrouver son fils et héritier du trône de Zamunda.")
    )
    
    val infoChannelContent: List<ContentItem> = if (programsState.isNotEmpty()) {
        programsState.filter { it.live && it.title.contains("Info", true) }
            .map { it.toContentItem() }
    } else listOf(
        ContentItem("6", "Journal Afrique 20h", "https://picsum.photos/300/450?random=6", "En direct • Info",
            description = "L'actualité africaine et internationale présentée par nos journalistes experts."),
        ContentItem("7", "Débat Panafricain", "https://picsum.photos/300/450?random=7", "21h30 • Politique",
            description = "Les grands enjeux politiques et économiques du continent africain débattus par nos invités."),
        ContentItem("8", "Documentaire Histoire", "https://picsum.photos/300/450?random=8", "22h45 • Culture",
            description = "Plongez dans l'histoire riche et fascinante de l'Afrique à travers nos documentaires exclusifs."),
        ContentItem("9", "Météo Afrique", "https://picsum.photos/300/450?random=9", "En continu",
            description = "Prévisions météorologiques détaillées pour toutes les régions d'Afrique.")
    )
    
    val musicChannelContent: List<ContentItem> = if (programsState.isNotEmpty()) {
        programsState.filter { it.title.contains("Musique", true) || it.description.contains("Musique", true) }
            .map { it.toContentItem() }
    } else listOf(
        ContentItem("10", "Afrobeat Live Session", "https://picsum.photos/300/450?random=10", "En direct • Musique",
            description = "Les plus grands artistes afrobeat en concert live depuis nos studios."),
        ContentItem("11", "Top 50 Africa", "https://picsum.photos/300/450?random=11", "Playlist • Hits",
            description = "Le classement des 50 titres les plus populaires du continent africain."),
        ContentItem("12", "Jazz & Soul", "https://picsum.photos/300/450?random=12", "En direct • Jazz",
            description = "Une sélection de jazz africain et de soul music pour une ambiance détendue."),
        ContentItem("13", "Découvertes Musicales", "https://picsum.photos/300/450?random=13", "Nouveautés",
            description = "Les nouveaux talents et les dernières sorties de la scène musicale africaine.")
    )
    
    val sportsChannelContent: List<ContentItem> = if (programsState.isNotEmpty()) {
        programsState.filter { it.title.contains("Sport", true) || it.description.contains("Football", true) }
            .map { it.toContentItem() }
    } else listOf(
        ContentItem("14", "MaâtFoot - CAN 2024", "https://picsum.photos/300/450?random=14", "En direct • Football",
            description = "Suivez tous les matchs de la Coupe d'Afrique des Nations en direct et en exclusivité."),
        ContentItem("15", "Basketball Africa League", "https://picsum.photos/300/450?random=15", "Sport • Basketball",
            description = "Les meilleurs moments de la Basketball Africa League avec analyses et highlights."),
        ContentItem("16", "Athlétisme Africain", "https://picsum.photos/300/450?random=16", "Sport • Athlétisme",
            description = "Découvrez les champions africains d'athlétisme et leurs exploits sur la scène internationale.")
    )
    
    // Header dynamique qui se met à jour selon la sélection
    val currentHeaderContent = selectedContent?.let { content ->
        HeaderContent(
            title = content.title,
            subtitle = content.description ?: content.subtitle ?: "",
            imageUrl = content.imageUrl,
            actionText = when {
                content.subtitle?.contains("En direct") == true -> "Regarder en direct"
                content.subtitle?.contains("Série") == true -> "Voir la série"
                else -> "Regarder maintenant"
            },
            onAction = { 
                // TODO: Lancer la lecture du contenu sélectionné
                println("Lecture de: ${content.title}")
            }
        )
    } ?: HeaderContent(
        title = "Maât.TV",
        subtitle = "Découvrez le meilleur du divertissement africain : films, séries, documentaires, musique et sport en direct.",
        imageUrl = "https://picsum.photos/1920/1080?random=100",
        actionText = "Explorer",
        onAction = { /* Navigation vers contenu recommandé */ }
    )
    
    // Fonction pour gérer la sélection d'un contenu
    fun onContentSelected(content: ContentItem) {
        selectedContent = content
    }
    
    // Fonction pour gérer les clics sur les contenus
    fun onContentClick(content: ContentItem) {
        // Navigation vers la page de détail du contenu
        navController.navigate("content_detail/${content.id}")
    }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Menu vertical
        MenuVertical(
            navController = navController,
            currentRoute = currentRoute
        )
        
        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181818))
                .verticalScroll(rememberScrollState())
        ) {
            // Header dynamique
            DynamicHeader(content = currentHeaderContent)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // VOD - MaâtFlix
            ContentRayon(
                title = "MaâtFlix - Films & Séries",
                items = vodContent,
                onItemClick = { onContentClick(it) },
                onItemFocus = { onContentSelected(it) },
                onSeeAllClick = { 
                    // TODO: Navigation vers catalogue complet
                    println("Navigation vers catalogue MaâtFlix")
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Chaîne Info
            ContentRayon(
                title = "Chaîne Info - Actualités",
                items = infoChannelContent,
                onItemClick = { onContentClick(it) },
                onItemFocus = { onContentSelected(it) },
                onSeeAllClick = {
                    // TODO: Navigation vers grille programme Info
                    println("Navigation vers programme Info")
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Chaîne Musique
            ContentRayon(
                title = "Chaîne Musique - En ce moment",
                items = musicChannelContent,
                onItemClick = { onContentClick(it) },
                onItemFocus = { onContentSelected(it) },
                onSeeAllClick = {
                    // TODO: Navigation vers programme Musique
                    println("Navigation vers programme Musique")
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // MaâtFoot - Sport
            ContentRayon(
                title = "MaâtFoot - Sport en direct",
                items = sportsChannelContent,
                onItemClick = { onContentClick(it) },
                onItemFocus = { onContentSelected(it) },
                onSeeAllClick = {
                    // TODO: Navigation vers programme Sport
                    println("Navigation vers programme Sport")
                }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

// Extension function
private fun ProgramDto.toContentItem() = ContentItem(
    id = id,
    title = title,
    imageUrl = imageUrl,
    subtitle = if (live) "En direct" else null,
    description = description
)