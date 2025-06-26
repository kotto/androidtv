package ai.maatcore.maatcore_android_tv.ui.screens

import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.data.ContentSection
import ai.maatcore.maatcore_android_tv.ui.components.SidebarItem
import ai.maatcore.maatcore_android_tv.ui.components.TvSidebarMenu
import ai.maatcore.maatcore_android_tv.ui.theme.* // Assurez-vous que toutes vos couleurs sont ici
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NetflixTvHomeScreen(navController: NavController) {
    var selectedMenuIndex by remember { mutableStateOf(0) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Routes alignées avec votre NavHost dans MainActivity.kt
    val menuItems = listOf(
        SidebarItem("id_home", "Accueil", Icons.Default.Home, "netflix_home"),
        SidebarItem("id_maatflix", "MaâtFlix", Icons.Default.Movie, "maatflix"),
        SidebarItem("id_maattv", "Maât.TV", Icons.Default.Tv, "maattv"),
        SidebarItem("id_maatcare", "MaâtCare", Icons.Default.LocalHospital, "maatcare"),
        SidebarItem("id_maatclass", "MaâtClass", Icons.Default.School, "maatclass"),
        // SidebarItem("id_search", "Recherche", Icons.Default.Search, "search_route"), // Décommentez et définissez "search_route" dans NavHost si besoin
        SidebarItem("id_settings", "Paramètres", Icons.Default.Settings, "settings")
    )

    Row(Modifier.fillMaxSize().padding(start = 24.dp)) {
        TvSidebarMenu(
            items = menuItems,
            selectedIndex = selectedMenuIndex,
            isExpanded = isMenuExpanded,
            onItemSelected = { index ->
                selectedMenuIndex = index
                val selectedRoute = menuItems[index].route
                Log.d("NetflixNav", "Menu Clicked: ${menuItems[index].title}, Route: $selectedRoute, Current Dest: ${navController.currentDestination?.route}")
                if (selectedRoute.isNotBlank() && navController.currentDestination?.route != selectedRoute) {
                    navController.navigate(selectedRoute) {
                        // Options pour la pile de retour, ex:
                        // popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true // Évite de multiples copies du même écran sur la pile
                        restoreState = true // Restaure l'état si on revient à cet écran
                    }
                }
            },
            onExpandedChange = { isMenuExpanded = it },
            modifier = Modifier.width(if (isMenuExpanded) 224.dp else 64.dp)

        )



        TvMainContent(
            scrollState = scrollState,
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .onKeyEvent { event ->
                    handleContentKeyNavigation(event, scrollState, scope)
                }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TvMainContent(
    scrollState: ScrollState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val sections = rememberSections()

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaatColorNoirProfond, MaatColorNoirProfond.copy(alpha = 0.5f)),
                    startY = 0f,
                    endY = 1000f
                )
            )
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        NetflixHeroSection(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        sections.forEach { section ->
            // Correction: L'appel à Box ne prend pas de paramètre 'key' directement.
            // Si une clé est nécessaire pour la stabilité de la recomposition dans une boucle,
            // elle est généralement appliquée au composable `key {}` entourant l'élément.
            // Cependant, pour AnimatedContent, targetState lui-même sert de clé.
            Box { // Le paramètre `key` a été retiré de ce Box.
                this@Column.AnimatedVisibility( // Correction: S'assurer que ceci est appelé dans un contexte composable
                    visible = true,
                    enter = slideInVertically { fullHeight -> fullHeight / 2 } + fadeIn(),
                    exit = slideOutVertically { fullHeight -> fullHeight / 2 } + fadeOut()
                ) {
                    AnimatedContent(
                        targetState = section, // section.title ou section elle-même si stable
                        transitionSpec = {
                            if (targetState.title > initialState.title) {
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            } else {
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            }
                        },
                        label = "sectionAnimation"
                    ) { currentSection ->
                        TvCarouselSection(
                            title = currentSection.title,
                            items = currentSection.items.take(7) + List((7 - currentSection.items.size).coerceAtLeast(0)) {
                                ContentItem("empty_${currentSection.title}_$it", "", imageUrl = "")
                            },
                            onItemClick = { contentItem ->
                                if (contentItem.id.isNotBlank() && !contentItem.id.startsWith("empty_")) {
                                    Log.d("NetflixNav", "Card Clicked: ${contentItem.title}, ID: ${contentItem.id}, Navigating to details/${contentItem.id}")
                                    // Utilisation de la route "details/{id}" définie dans NavHost
                                    navController.navigate("details/${contentItem.id}")
                                }
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun rememberSections() = remember {
    listOf(
        ContentSection("📺 Télévision", listOf(
            ContentItem("maat_flix_promo", "MaâtFlix", imageUrl = "", subtitle = "Plateforme de streaming", imageRes = R.drawable.maat_flix),
            ContentItem("maat_tv_promo", "Maât.TV", imageUrl = "", subtitle = "Télévision en direct", imageRes = R.drawable.maat_tv),
            ContentItem("maat_tube_promo", "MaâtTube", imageUrl = "", subtitle = "Vidéos à la demande", imageRes = R.drawable.maat_tv) // R.drawable.maattube si existe
        )),
        ContentSection("🏥 Télémédecine", listOf(
            ContentItem("maat_care_promo", "MaâtCare", imageUrl = "", subtitle = "Consultations médicales", imageRes = R.drawable.maat_care)
        )),
        ContentSection("🎓 Télééducation", listOf(
            ContentItem("maat_class_promo", "MaâtClass", imageUrl = "", subtitle = "Cours en ligne", imageRes = R.drawable.maat_class),
            ContentItem("maat_foot_promo", "MaâtFoot", imageUrl = "", subtitle = "Formation football", imageRes = R.drawable.maat_foot)
        ))
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCarouselSection(
    title: String,
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var carouselIsFocused by remember { mutableStateOf(false) }
    val titleColor by animateColorAsState(
        if (carouselIsFocused) MaatColorOrangeSolaire else MaatColorOrSable,
        label = "titleColorAnimation"
    )

    Column(
        modifier = modifier.onFocusChanged { carouselIsFocused = it.hasFocus }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = titleColor,
                shadow = Shadow(Color.Black.copy(alpha = 0.7f), Offset(2f, 2f), blurRadius = 4f)
            ),
            modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(items.size.coerceAtLeast(7)) { index ->
                val item = items.getOrNull(index) ?: ContentItem("empty_carousel_${title}_$index", "", "")
                var isCardFocused by remember { mutableStateOf(false) }

                NetflixContentCard(
                    item = item,
                    isFocused = isCardFocused,
                    modifier = Modifier
                        .width(160.dp)
                        .aspectRatio(9f / 14f)
                        .onFocusChanged { focusState ->
                            isCardFocused = focusState.isFocused
                            if (focusState.isFocused) {
                                scope.launch {
                                    listState.animateScrollToItem(index = index, scrollOffset = -50)
                                }
                            }
                        },
                    onCardClick = { if (item.id.isNotBlank() && !item.id.startsWith("empty_")) onItemClick(item) }
                )
            }
        }
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NetflixContentCard(
    item: ContentItem,
    isFocused: Boolean,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit
) {
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.1f else 1f, label = "cardScale", animationSpec = tween(durationMillis = 200))
    val elevation by animateDpAsState(targetValue = if (isFocused) 12.dp else 4.dp, label = "cardElevation", animationSpec = tween(durationMillis = 200))
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaatColorOrSable else Color.Transparent,
        label = "cardBorderColor",
        animationSpec = tween(durationMillis = 200)
    )

    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaatColorGrisClair.copy(alpha = 0.05f)),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = elevation.toPx()
            }
            .border(BorderStroke(if (isFocused) 3.dp else 2.dp, borderColor), RoundedCornerShape(8.dp))
    ) {
        Box(Modifier.fillMaxSize()) {
            if (item.imageRes != null && item.imageRes != 0) {
                Image(
                    painter = painterResource(item.imageRes!!),
                    contentDescription = item.title.ifBlank { "Image de contenu" },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (item.imageUrl.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize().background(MaatColorNoirProfond)) { // MaatColorGrisMoyen remplacé
                    Text("Image URL", Modifier.align(Alignment.Center), color = Color.White)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().background(MaatColorNoirProfond.copy(alpha = 0.5f))) // MaatColorGrisFonce remplacé
            }

            // Correction: S'assurer que cet AnimatedVisibility est appelé correctement.
            // Le this@Card n'est pas nécessaire ici si on importe la bonne version d'AnimatedVisibility.
            this@Card.AnimatedVisibility(
                visible = isFocused && item.title.isNotBlank() && !item.id.startsWith("empty_"),
                enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 100)) + expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 200)) + shrinkVertically(animationSpec = tween(durationMillis = 200)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaatColorNoirProfond.copy(alpha = 0.7f), MaatColorNoirProfond.copy(alpha = 0.9f))
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.subtitle?.isNotBlank() == true) {
                        Text(
                            text = item.subtitle,
                            color = MaatColorOrSable.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NetflixHeroSection(
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        label = "heroScale",
        animationSpec = tween(durationMillis = 300)
    )
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.7f else 0.5f,
        label = "heroOverlayAlpha",
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaatColorGrisClair.copy(alpha = 0.1f))
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = if (isFocused) MaatColorOrangeSolaire.copy(alpha = 0.8f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Image(
            painter = painterResource(R.drawable.maat_header),
            contentDescription = "Bannière principale Bienvenue sur MaätCore",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaatColorNoirProfond.copy(alpha = 0.2f),
                            Color.Transparent,
                            MaatColorNoirProfond.copy(alpha = overlayAlpha)
                        )
                    )
                )
        )
        Box(
            Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaatColorNoirProfond.copy(alpha = 0.8f)),
                        startY = Float.POSITIVE_INFINITY / 3
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 36.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Bienvenue sur MaätCore",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = MaatColorOrangeSolaire,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(Color.Black.copy(alpha = 0.7f), Offset(2f, 2f), 4f)
                )
            )
            Text(
                "Le réveil de la Maât",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    shadow = Shadow(Color.Black.copy(alpha = 0.5f), Offset(1f, 1f), 2f)
                )
            )
            Text(
                "Pour un monde de vérité, de justice, et d'harmonie.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaatColorOrSable.copy(alpha = 0.9f)
                )
            )
        }
    }
}

private fun handleContentKeyNavigation(
    event: androidx.compose.ui.input.key.KeyEvent,
    scrollState: ScrollState,
    scope: CoroutineScope
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    val scrollAmount = 300f
    return when (event.key) {
        Key.DirectionUp -> {
            scope.launch { scrollState.animateScrollBy(-scrollAmount, animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)) }
            true
        }
        Key.DirectionDown -> {
            scope.launch { scrollState.animateScrollBy(scrollAmount, animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)) }
            true
        }
        else -> false
    }
}