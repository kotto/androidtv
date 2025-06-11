package ai.maatcore.maatcore_android_tv.ui.screens

// Imports existants...
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text // Assurez-vous que cet import est là si vous l'utilisez
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController // Import NavHostController
import androidx.compose.material3.Card // Use standard Material3 Card
import androidx.compose.material3.MaterialTheme // Use standard Material3 Theme
import androidx.compose.animation.core.*
import androidx.compose.foundation.focusable
import androidx.navigation.compose.currentBackStackEntryAsState // Import for observing current route
import androidx.compose.foundation.background // Import background modifier

import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.ui.components.MaatBrandHeader
import ai.maatcore.maatcore_android_tv.ui.components.MenuVertical
import ai.maatcore.maatcore_android_tv.ui.components.DynamicHeader // Import DynamicHeader
import ai.maatcore.maatcore_android_tv.ui.components.HeaderContent // Import HeaderContent
import ai.maatcore.maatcore_android_tv.ui.theme.MontserratFamily
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import ai.maatcore.maatcore_android_tv.ui.theme.InterFamily
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond // Import MaatColorNoirProfond

// Modèles de données (simplifiés, adaptez les vôtres)
data class Movie(val id: String, val title: String, val director: String, val imageRes: Int, val category: String)
data class MaatService(val id: String, val name: String, val imageRes: Int)
data class FeaturedContent(val id: String, val title: String, val description: String, val imageRes: Int)

@Composable
fun HomeScreen(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"

    // Données exemples avec placeholders - Mise à jour pour correspondre à l'image
    val trendingMovies = listOf(
        Movie("1", "OPPENHEIMER", "Christopher Nolan", R.drawable.placeholder_image, "Action"),
        Movie("2", "AVATAR", "James Cameron", R.drawable.placeholder_image, "Science-Fiction"),
        Movie("3", "JOHN WICK", "Chad Stahelski", R.drawable.placeholder_image, "Action"),
        Movie("4", "EQUALIZER 3", "Antoine Fuqua", R.drawable.placeholder_image, "Action"),
        Movie("5", "CREED III", "Michael B. Jordan", R.drawable.placeholder_image, "Drame"),
        Movie("6", "MaätCare", "", R.drawable.maat_care, "Service")
    )

    // Menu vertical avec icônes dorées comme dans l'image
    val maatServices = listOf(
        MaatService("1", "Maât.TV", R.drawable.maat_tv),
        MaatService("2", "MaâtFlix", R.drawable.maat_flix),
        MaatService("3", "MaâtCare", R.drawable.maat_care),
        MaatService("4", "MaâtClass", R.drawable.maat_class),
        MaatService("5", "MaâtFoot", R.drawable.maat_foot)
    )

    val featuredItem = FeaturedContent(
        "1",
        "QUEEN OF MAÄT",
        "The untold story of powerful queen",
        R.drawable.maat_header
    )

    Row(modifier = Modifier.fillMaxSize()) {
        MenuVertical(navController = navController, currentRoute = currentRoute)

        Column(modifier = Modifier.fillMaxSize().background(MaatColorNoirProfond)) { // Set background to deep black
            // Dynamic Header for HomeScreen - Title removed as it's already in the image
            DynamicHeader(
                content = HeaderContent(
                    title = "", // Removed title as it's already in the image
                    subtitle = featuredItem.description,
                    imageUrl = "", // Not used as we are using local drawable
                    imageRes = featuredItem.imageRes, // Using the featured content image
                    actionText = "Regarder",
                    onAction = { /* TODO: Action for featured content */ }
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // All content sections directly within this Column
            // Section Nouveautés (comme dans l'image)
            Text(
                "Nouveautés",
                fontSize = 20.sp,
                fontFamily = PoppinsFamily,
                color = Color(0xFFD4AF37), // Couleur dorée comme dans l'image
                modifier = Modifier.padding(start = 16.dp) // Removed bottom padding
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) { // Wrap LazyRow in a Column for padding
                val movieListState = rememberLazyListState() // Pour conserver la position au retour
                
                LazyRow(
                    state = movieListState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 48.dp) // Marge de sécurité pour l'overscan TV
                ) {
                    items(trendingMovies) { movie ->
                        MovieCard(movie = movie) // Afficher les films dans la section Nouveautés
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Catégories Principales (comme dans l'image)
            Text(
                "Catégories Principales",
                fontSize = 20.sp,
                fontFamily = PoppinsFamily,
                color = Color(0xFFD4AF37), // Couleur dorée comme dans l'image
                modifier = Modifier.padding(start = 16.dp) // Removed bottom padding
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) { // Wrap LazyRow in a Column for padding
                val serviceListState = rememberLazyListState() // Pour conserver la position au retour
                
                LazyRow(
                    state = serviceListState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 48.dp) // Marge de sécurité pour l'overscan TV
                ) {
                    items(maatServices) { service ->
                        ServiceCard(service = service) // Afficher les services comme catégories
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Contenu à la Une
            Text(
                "À la Une",
                fontSize = 20.sp,
                fontFamily = PoppinsFamily,
                modifier = Modifier.padding(start = 16.dp) // Removed bottom padding
            )
            FeaturedCard(featuredItem)

            // ... autres sections ...
            // Exemple d'utilisation d'InterFamily
            Text(
                "Texte informatif en Inter.",
                fontFamily = InterFamily,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp) // Apply padding here
            )
        } // Closing brace for the single content Column
    } // Closing brace for the Row
}

@Composable
fun MovieCard(movie: Movie, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    
    // Animation de scale pour l'effet de focus
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200)
    )
    
    // Animation de l'élévation pour l'effet de focus
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 4.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    // Animation de la bordure pour l'effet de focus
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    Card(
        onClick = { /* Naviguer vers les détails du film */ },
        modifier = modifier
            .width(150.dp)
            .height(220.dp)
            .scale(scale)
            .graphicsLayer {
                shadowElevation = elevation.toPx()
            }
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .zIndex(if (isFocused) 1f else 0f) // Mettre l'élément focusé au-dessus des autres
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = movie.imageRes),
                    contentDescription = "Film: ${movie.title} par ${movie.director}", // Description améliorée pour l'accessibilité
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Bordure dorée quand focusé
                if (isFocused) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .border(borderWidth, Color(0xFFD4AF37), RoundedCornerShape(8.dp))
                    )
                }
                
                // Titre en bas de l'image comme dans la maquette
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0x80000000)) // Fond semi-transparent
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = movie.title,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp,
                        color = Color(0xFFD4AF37), // Texte doré comme dans l'image
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: MaatService, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    
    // Animation de scale pour l'effet de focus
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200)
    )
    
    // Animation de l'élévation pour l'effet de focus
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 4.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    // Animation de la luminosité pour l'effet de focus
    val iconAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1.0f else 0.8f,
        animationSpec = tween(durationMillis = 200)
    )
    
    androidx.compose.material3.Card(
        onClick = { /* Naviguer vers le service */ },
        modifier = modifier
            .size(120.dp)
            .scale(scale)
            .graphicsLayer {
                shadowElevation = elevation.toPx()
            }
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .zIndex(if (isFocused) 1f else 0f) // Mettre l'élément focusé au-dessus des autres
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color(0xFF1A1A1A)), // Fond noir comme dans l'image
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bordure dorée quand focusé
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isFocused) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFD4AF37), CircleShape)
                    )
                }
                
                Image(
                    painter = painterResource(id = service.imageRes),
                    contentDescription = "Service: ${service.name}", // Description améliorée pour l'accessibilité
                    modifier = Modifier
                        .size(60.dp)
                        .alpha(iconAlpha),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                service.name,
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = if (isFocused) Color(0xFFD4AF37) else Color(0xFFBCA136), // Plus brillant quand focusé
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable // Removed ExperimentalTvMaterial3Api
fun FeaturedCard(content: FeaturedContent) {
    androidx.compose.material3.Card( // Use standard Material3 Card
        onClick = { /* Action */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = content.imageRes),
                contentDescription = content.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    content.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MontserratFamily
                )
                Text(
                    content.description,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = InterFamily,
                    maxLines = 2
                )
            }
        }
    }
}
