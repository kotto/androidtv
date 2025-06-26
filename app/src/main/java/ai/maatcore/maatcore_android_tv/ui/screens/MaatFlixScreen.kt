package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.components.*
import ai.maatcore.maatcore_android_tv.ui.theme.*
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interface MaâtFlix - Plateforme de streaming avec interface Netflix-like
 * Header proéminent, menu vertical et sections de contenu
 */
@Composable
fun MaatFlixScreen(navController: NavHostController) {
    var selectedCategory by remember { mutableStateOf("Accueil") }
    var selectedMovieId by remember { mutableStateOf<String?>(null) }
    var showMovieDetails by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Animation d'entrée
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF141414),
                        Color(0xFF0F0F0F),
                        Color.Black
                    )
                )
            )
    ) {
        // Menu vertical (sidebar) - toujours visible
        MaatFlixSidebar(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = category
            },
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
        )
        
        // Zone de contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.Back -> {
                                navController.popBackStack()
                                true
                            }
                            else -> false
                        }
                    } else false
                }
        ) {
            // Contenu selon la catégorie sélectionnée
            MaatFlixContent(
                category = selectedCategory,
                onMovieSelected = { movieId ->
                    selectedMovieId = movieId
                    navController.navigate("details/$movieId")
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
fun MaatFlixHeader(
    selectedCategory: String,
    onMenuToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerFocusRequester = remember { FocusRequester() }
    var isHeaderFocused by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .height(120.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE50914), // Rouge Netflix
                        Color(0xFFB71C1C),
                        Color.Transparent
                    )
                )
            )
            .focusRequester(headerFocusRequester)
            .onFocusChanged { isHeaderFocused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionCenter) {
                    onMenuToggle()
                    true
                } else false
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo et titre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Theaters,
                    contentDescription = "MaâtFlix",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "MaâtFlix",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = selectedCategory,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Bouton menu
            Card(
                modifier = Modifier
                    .size(56.dp)
                    .border(
                        width = if (isHeaderFocused) 3.dp else 0.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        headerFocusRequester.requestFocus()
    }
}

@Composable
fun MaatFlixSidebar(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Accueil" to Icons.Default.Home,
        "Films" to Icons.Default.Movie,
        "Séries" to Icons.Default.Tv,
        "Documentaires" to Icons.Default.DocumentScanner,
        "Enfants" to Icons.Default.ChildCare,
        "Africain" to Icons.Default.Public,
        "Ma Liste" to Icons.Default.Bookmark,
        "Récemment ajoutés" to Icons.Default.NewReleases
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Titre du menu
            Text(
                text = "Catégories",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Liste des catégories
            LazyColumn {
                items(categories) { (category, icon) ->
                    MaatFlixMenuItem(
                        title = category,
                        icon = icon,
                        isSelected = category == selectedCategory,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun MaatFlixMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionCenter) {
                    onClick()
                    true
                } else false
            }
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = Color(0xFFE50914),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE50914) else Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun MaatFlixContent(
    category: String,
    onMovieSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        when (category) {
            "Accueil" -> {
                item {
                    MaatFlixSection(
                        title = "Tendances actuelles",
                        movies = getTrendingMovies(),
                        onMovieSelected = onMovieSelected
                    )
                }
                item {
                    MaatFlixSection(
                        title = "Nouveautés",
                        movies = getNewMovies(),
                        onMovieSelected = onMovieSelected
                    )
                }
                item {
                    MaatFlixSection(
                        title = "Cinéma africain",
                        movies = getAfricanMovies(),
                        onMovieSelected = onMovieSelected
                    )
                }
            }
            "Films" -> {
                item {
                    MaatFlixSection(
                        title = "Films populaires",
                        movies = getPopularMovies(),
                        onMovieSelected = onMovieSelected
                    )
                }
                item {
                    MaatFlixSection(
                        title = "Action & Aventure",
                        movies = getActionMovies(),
                        onMovieSelected = onMovieSelected
                    )
                }
            }
            "Séries" -> {
                item {
                    MaatFlixSection(
                        title = "Séries populaires",
                        movies = getPopularSeries(),
                        onMovieSelected = onMovieSelected
                    )
                }
            }
            "Documentaires" -> {
                item {
                    MaatFlixSection(
                        title = "Documentaires",
                        movies = getDocumentaries(),
                        onMovieSelected = onMovieSelected
                    )
                }
            }
            else -> {
                item {
                    MaatFlixSection(
                        title = category,
                        movies = getMoviesByCategory(category),
                        onMovieSelected = onMovieSelected
                    )
                }
            }
        }
    }
}

@Composable
fun MaatFlixSection(
    title: String,
    movies: List<MovieItem>,
    onMovieSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(movies) { movie ->
                MaatFlixMovieCard(
                    movie = movie,
                    onSelected = { onMovieSelected(movie.id) }
                )
            }
        }
    }
}

@Composable
fun MaatFlixMovieCard(
    movie: MovieItem,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    Card(
        modifier = modifier
            .width(200.dp)
            .height(280.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionCenter) {
                    onSelected()
                    true
                } else false
            }
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = Color(0xFFE50914),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Image du film
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Informations du film
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = movie.year,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = movie.rating,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// Modèle de données pour les films
data class MovieItem(
    val id: String,
    val title: String,
    val year: String,
    val rating: String,
    val posterUrl: String,
    val genre: String
)

// Fonctions pour obtenir les données de films (à remplacer par de vraies données)
fun getTrendingMovies(): List<MovieItem> = listOf(
    MovieItem("1", "Black Panther", "2018", "8.5", "https://example.com/poster1.jpg", "Action"),
    MovieItem("2", "The Woman King", "2022", "8.2", "https://example.com/poster2.jpg", "Drame"),
    MovieItem("3", "Coming to America", "1988", "7.1", "https://example.com/poster3.jpg", "Comédie")
)

fun getNewMovies(): List<MovieItem> = listOf(
    MovieItem("4", "Nouveau Film 1", "2024", "7.8", "https://example.com/poster4.jpg", "Drame"),
    MovieItem("5", "Nouveau Film 2", "2024", "8.0", "https://example.com/poster5.jpg", "Action")
)

fun getAfricanMovies(): List<MovieItem> = listOf(
    MovieItem("6", "Tsotsi", "2005", "7.2", "https://example.com/poster6.jpg", "Drame"),
    MovieItem("7", "Queen of Katwe", "2016", "7.4", "https://example.com/poster7.jpg", "Biographie")
)

fun getPopularMovies(): List<MovieItem> = getTrendingMovies()
fun getActionMovies(): List<MovieItem> = getTrendingMovies().filter { it.genre == "Action" }
fun getPopularSeries(): List<MovieItem> = getTrendingMovies()
fun getDocumentaries(): List<MovieItem> = getTrendingMovies()
fun getMoviesByCategory(category: String): List<MovieItem> = getTrendingMovies()