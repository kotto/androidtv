@file:OptIn(ExperimentalTvMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.data.AvatarState
import ai.maatcore.maatcore_android_tv.data.AvatarData
import ai.maatcore.maatcore_android_tv.data.AvatarManager
import ai.maatcore.maatcore_android_tv.ui.components.*
import ai.maatcore.maatcore_android_tv.ui.theme.*
import ai.maatcore.maatcore_android_tv.ui.viewmodel.ProgramViewModel
import ai.maatcore.maatcore_android_tv.data.remote.maattv.ProgramDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interface Maât.TV - Studio 3D avec avatar IA présentateur
 * Implémente les spécifications UI/UX pour l'expérience immersive
 */
@Composable
fun MaatTVScreen(
    navController: NavHostController,
    viewModel: ProgramViewModel = hiltViewModel()
) {
    var currentSection by remember { mutableStateOf(MaatTvSection.LIVE) }
    var selectedNewsId by remember { mutableStateOf<String?>(null) }
    var isAvatarSpeaking by remember { mutableStateOf(false) }
    var showVoiceSearch by remember { mutableStateOf(false) }
    
    val avatarManager = remember { AvatarManager() }
    val scope = rememberCoroutineScope()
    
    // Load programs from backend
    val programsState by viewModel.programs.collectAsState()
    LaunchedEffect(Unit) { 
        viewModel.load()
        // Simulation de l'avatar qui commence à parler
        delay(1000)
        isAvatarSpeaking = true
        avatarManager.setAvatarState("maat_tv", AvatarState.SPEAKING)
        delay(5000)
        isAvatarSpeaking = false
        avatarManager.setAvatarState("maat_tv", AvatarState.IDLE)
    }
    
    // Animation d'entrée
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color.Black
                    )
                )
            )
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Back -> {
                            navController.popBackStack()
                            true
                        }
                        Key.Search -> {
                            showVoiceSearch = true
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800)),
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Zone principale du studio 3D (70%)
                StudioMainArea(
                    currentSection = currentSection,
                    isAvatarSpeaking = isAvatarSpeaking,
                    selectedNewsId = selectedNewsId,
                    programsState = programsState,
                    onSectionChanged = { currentSection = it },
                    onNewsSelected = { newsItem ->
                        selectedNewsId = newsItem.id
                        scope.launch {
                            isAvatarSpeaking = true
                            avatarManager.setAvatarState("maat_tv", AvatarState.SPEAKING)
                            delay(3000)
                            isAvatarSpeaking = false
                            avatarManager.setAvatarState("maat_tv", AvatarState.IDLE)
                        }
                    },
                    onContentClick = { content ->
                        navController.navigate("content_detail/${content.id}")
                    },
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                )
                
                // Zone d'information visuelle (30%)
                VisualInfoZone(
                    currentSection = currentSection,
                    selectedNewsId = selectedNewsId,
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                )
            }
        }
        
        // Overlay de recherche vocale
        if (showVoiceSearch) {
            VoiceSearchOverlay(
                onDismiss = { showVoiceSearch = false },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Zone principale du studio 3D
 */
@Composable
fun StudioMainArea(
    currentSection: MaatTvSection,
    isAvatarSpeaking: Boolean,
    selectedNewsId: String?,
    programsState: List<ProgramDto>,
    onSectionChanged: (MaatTvSection) -> Unit,
    onNewsSelected: (NewsItem) -> Unit,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp)
    ) {
        // En-tête avec logo et navigation
        StudioHeader(
            currentSection = currentSection,
            onSectionChanged = onSectionChanged,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Zone de présentation avec avatar IA
        AvatarPresentationZone(
            isAvatarSpeaking = isAvatarSpeaking,
            currentSection = currentSection,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Contenu selon la section
        when (currentSection) {
            MaatTvSection.LIVE -> {
                NewsPlaylist(
                    currentSection = currentSection,
                    selectedNewsId = selectedNewsId,
                    onNewsSelected = onNewsSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                )
            }
            else -> {
                ContentSection(
                    currentSection = currentSection,
                    programsState = programsState,
                    onContentClick = onContentClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                )
            }
        }
    }
}

/**
 * En-tête du studio avec navigation
 */
@Composable
fun StudioHeader(
    currentSection: MaatTvSection,
    onSectionChanged: (MaatTvSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Maât.TV
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TvIcon(
                imageVector = Icons.Default.Tv,
                contentDescription = "Maât.TV",
                tint = MaatTvBlue,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Maât.TV",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaatTvBlue,
                fontFamily = PoppinsFamily
            )
        }
        
        // Navigation des sections
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(MaatTvSection.values()) { section ->
                SectionTab(
                    section = section,
                    isSelected = section == currentSection,
                    onClick = { onSectionChanged(section) }
                )
            }
        }
    }
}

/**
 * Onglet de section
 */
@Composable
fun SectionTab(
    section: MaatTvSection,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaatTvBlue
            isFocused -> MaatTvBlue.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "tabBackground"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected || isFocused -> Color.White
            else -> Color.White.copy(alpha = 0.7f)
        },
        animationSpec = tween(300),
        label = "tabText"
    )
    
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Text(
            text = section.displayName,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = PoppinsFamily
        )
    }
}

/**
 * Zone de présentation avec avatar IA
 */
@Composable
fun AvatarPresentationZone(
    isAvatarSpeaking: Boolean,
    currentSection: MaatTvSection,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaatTvBlue.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        radius = 600f
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar IA présentateur
                AvatarPresenter(
                    isAvatarSpeaking = isAvatarSpeaking,
                    modifier = Modifier.weight(0.4f)
                )
                
                Spacer(modifier = Modifier.width(32.dp))
                
                // Zone de contenu principal
                MainContentArea(
                    currentSection = currentSection,
                    isAvatarSpeaking = isAvatarSpeaking,
                    modifier = Modifier.weight(0.6f)
                )
            }
        }
    }
}

/**
 * Avatar présentateur IA
 */
@Composable
fun AvatarPresenter(
    isAvatarSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val avatar = AvatarData.getAvatarById("maat_tv")
    val scale by animateFloatAsState(
        targetValue = if (isAvatarSpeaking) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "avatarScale"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar principal
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            avatar?.primaryColor ?: MaatTvBlue,
                            (avatar?.secondaryColor ?: MaatTvBlue).copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = if (isAvatarSpeaking) 4.dp else 2.dp,
                    color = if (isAvatarSpeaking) Color.Green else MaatTvBlue,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            TvIcon(
                imageVector = avatar?.icon ?: Icons.Default.Tv,
                contentDescription = "Avatar Maât.TV",
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            
            // Indicateur de parole
            if (isAvatarSpeaking) {
                SpeechIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-16).dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nom et statut
        Text(
            text = avatar?.name ?: "Maât.TV",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = PoppinsFamily,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = if (isAvatarSpeaking) "En direct" else "Prêt",
            fontSize = 14.sp,
            color = if (isAvatarSpeaking) Color.Green else Color.White.copy(alpha = 0.7f),
            fontFamily = PoppinsFamily,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Indicateur de parole animé
 */
@Composable
fun SpeechIndicator(
    modifier: Modifier = Modifier
) {
    val waves = remember { listOf(0, 1, 2) }
    
    Row(
        modifier = modifier
            .background(
                color = Color.Green.copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        waves.forEach { index ->
            val height by animateFloatAsState(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 100,
                        easing = EaseInOut
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave$index"
            )
            
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((8 + height * 8).dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * Zone de contenu principal
 */
@Composable
fun MainContentArea(
    currentSection: MaatTvSection,
    isAvatarSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Actualités ${currentSection.displayName}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = PoppinsFamily
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contenu dynamique selon la section
        when (currentSection) {
            MaatTvSection.LIVE -> LiveNewsContent(isAvatarSpeaking)
            MaatTvSection.AFRICA -> AfricaNewsContent()
            MaatTvSection.WORLD -> WorldNewsContent()
            MaatTvSection.TECH -> TechNewsContent()
            MaatTvSection.CULTURE -> CultureNewsContent()
        }
    }
}

/**
 * Contenu des actualités en direct
 */
@Composable
fun LiveNewsContent(
    isAvatarSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (isAvatarSpeaking) 
                "Voici les dernières actualités de ce ${java.time.LocalDate.now().dayOfWeek.name.lowercase()}..." 
            else 
                "Prêt à vous présenter les actualités du jour",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontFamily = PoppinsFamily,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Indicateurs en direct
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LiveIndicator("EN DIRECT", Color.Red)
            LiveIndicator("HD", MaatTvBlue)
            LiveIndicator("IA", MaatGold)
        }
    }
}

/**
 * Indicateur en direct
 */
@Composable
fun LiveIndicator(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )
    
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = alpha * 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = PoppinsFamily
        )
    }
}

/**
 * Contenu des autres sections (implémentation simplifiée)
 */
@Composable
fun AfricaNewsContent() {
    Text(
        text = "Actualités d'Afrique - Connecté aux sources locales",
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.9f),
        fontFamily = PoppinsFamily
    )
}

@Composable
fun WorldNewsContent() {
    Text(
        text = "Actualités mondiales - Perspective africaine",
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.9f),
        fontFamily = PoppinsFamily
    )
}

@Composable
fun TechNewsContent() {
    Text(
        text = "Technologie et Innovation - Focus Afrique",
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.9f),
        fontFamily = PoppinsFamily
    )
}

@Composable
fun CultureNewsContent() {
    Text(
        text = "Culture et Société - Richesse africaine",
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.9f),
        fontFamily = PoppinsFamily
    )
}

/**
 * Section de contenu pour les autres sections
 */
@Composable
fun ContentSection(
    currentSection: MaatTvSection,
    programsState: List<ProgramDto>,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredContent = programsState.filter { program ->
        when (currentSection) {
            MaatTvSection.AFRICA -> program.description.contains("Afrique", true)
            MaatTvSection.WORLD -> program.description.contains("Monde", true) || program.description.contains("International", true)
            MaatTvSection.TECH -> program.description.contains("Tech", true) || program.description.contains("Innovation", true)
            MaatTvSection.CULTURE -> program.description.contains("Culture", true) || program.description.contains("Art", true)
            else -> true
        }
    }.map { it.toContentItem() }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Contenu - ${currentSection.displayName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = PoppinsFamily
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (filteredContent.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredContent) { content ->
                        ContentPlaylistItem(
                            content = content,
                            onClick = { onContentClick(content) }
                        )
                    }
                }
            } else {
                Text(
                    text = "Aucun contenu disponible pour cette section",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

/**
 * Item de contenu dans la playlist
 */
@Composable
fun ContentPlaylistItem(
    content: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) MaatTvBlue.copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(300),
        label = "itemBackground"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône de contenu
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaatTvBlue,
                        shape = CircleShape
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenu
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = content.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontFamily = PoppinsFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                content.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}

/**
 * Playlist des actualités
 */
@Composable
fun NewsPlaylist(
    currentSection: MaatTvSection,
    selectedNewsId: String?,
    onNewsSelected: (NewsItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val newsItems = remember { generateSampleNews(currentSection) }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Playlist - ${currentSection.displayName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = PoppinsFamily
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(newsItems) { newsItem ->
                    NewsPlaylistItem(
                        newsItem = newsItem,
                        isSelected = newsItem.id == selectedNewsId,
                        onClick = { onNewsSelected(newsItem) }
                    )
                }
            }
        }
    }
}

/**
 * Item de la playlist
 */
@Composable
fun NewsPlaylistItem(
    newsItem: NewsItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaatTvBlue.copy(alpha = 0.6f)
            isFocused -> MaatTvBlue.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "itemBackground"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicateur de lecture
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isSelected) Color.Green else Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenu
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = newsItem.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontFamily = PoppinsFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = newsItem.duration,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

/**
 * Zone d'information visuelle (côté droit)
 */
@Composable
fun VisualInfoZone(
    currentSection: MaatTvSection,
    selectedNewsId: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Text(
            text = "Informations",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = PoppinsFamily
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Météo, heure, etc.
        InfoCard(
            title = "Météo",
            content = "Dakar: 28°C ☀️",
            icon = Icons.Default.WbSunny
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoCard(
            title = "Heure",
            content = java.time.LocalTime.now().toString().substring(0, 5),
            icon = Icons.Default.AccessTime
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoCard(
            title = "Section",
            content = currentSection.displayName,
            icon = Icons.Default.Category
        )
    }
}

/**
 * Carte d'information
 */
@Composable
fun InfoCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TvIcon(
                imageVector = icon,
                contentDescription = title,
                tint = MaatTvBlue,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = PoppinsFamily
                )
                Text(
                    text = content,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

/**
 * Overlay pour la recherche vocale
 */
@Composable
fun VoiceSearchOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.8f))
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Back) {
                    onDismiss()
                    true
                } else false
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animation de microphone
                val micScale by animateFloatAsState(
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "micScale"
                )
                
                TvIcon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(64.dp)
                        .graphicsLayer {
                            scaleX = micScale
                            scaleY = micScale
                        }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Écoute en cours...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = PoppinsFamily
                )
                
                Text(
                    text = "Dites votre recherche",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

// Modèles de données
enum class MaatTvSection(val displayName: String) {
    LIVE("En Direct"),
    AFRICA("Afrique"),
    WORLD("Monde"),
    TECH("Tech"),
    CULTURE("Culture")
}

data class NewsItem(
    val id: String,
    val title: String,
    val duration: String,
    val section: MaatTvSection
)

// Extension function
private fun ProgramDto.toContentItem() = ContentItem(
    id = id,
    title = title,
    imageUrl = imageUrl,
    subtitle = if (live) "En direct" else null,
    description = description
)

// Fonction utilitaire pour générer des actualités d'exemple
fun generateSampleNews(section: MaatTvSection): List<NewsItem> {
    return when (section) {
        MaatTvSection.LIVE -> listOf(
            NewsItem("1", "Actualités du jour", "15:30", section),
            NewsItem("2", "Point économique", "08:45", section),
            NewsItem("3", "Météo et trafic", "05:20", section)
        )
        MaatTvSection.AFRICA -> listOf(
            NewsItem("4", "Sommet de l'UA", "12:15", section),
            NewsItem("5", "Économie ouest-africaine", "09:30", section),
            NewsItem("6", "Culture et traditions", "07:45", section)
        )
        else -> listOf(
            NewsItem("7", "Actualités ${section.displayName}", "10:00", section),
            NewsItem("8", "Analyse ${section.displayName}", "08:30", section),
            NewsItem("9", "Reportage ${section.displayName}", "06:15", section)
        )
    }
}