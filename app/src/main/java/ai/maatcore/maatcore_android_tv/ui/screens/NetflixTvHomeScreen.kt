@file:OptIn(ExperimentalTvMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.Image // <-- THIS IS THE ONE YOU NEED for the Composable
import ai.maatcore.maatcore_android_tv.R as AppR
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.data.ContentSection
import ai.maatcore.maatcore_android_tv.ui.components.SidebarItem
import ai.maatcore.maatcore_android_tv.ui.components.TvCarouselSection
import ai.maatcore.maatcore_android_tv.ui.components.TvSidebarMenu
import ai.maatcore.maatcore_android_tv.ui.theme.*
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults as M3CardDefaults
import androidx.compose.material3.Card as M3Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon
import coil.compose.rememberAsyncImagePainter

typealias NetflixMenuItem = SidebarItem

@Composable
fun NetflixTvHomeScreen(navController: NavController) {
    var selectedMenuIndex by remember { mutableStateOf(0) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isMenuFocused by remember { mutableStateOf(false) }

    val menuItems = remember {
        listOf(
            NetflixMenuItem(
                id = "home",
                title = "Accueil",
                icon = Icons.Default.Home,
                route = "netflix_home",
                description = "Découvrez notre contenu"
            ),
            NetflixMenuItem(
                id = "tv",
                title = "Maât.TV",
                icon = Icons.Default.Tv,
                route = "maattv",
                description = "Chaînes TV en direct"
            ),
            NetflixMenuItem(
                id = "care",
                title = "MaâtCare",
                icon = Icons.Default.Favorite,
                route = "maatcare",
                description = "Santé et bien-être"
            ),
            NetflixMenuItem(
                id = "class",
                title = "MaâtClass",
                icon = Icons.Default.School,
                route = "maatclass",
                description = "Formation et éducation"
            ),
            NetflixMenuItem(
                id = "foot",
                title = "MaâtFoot",
                icon = Icons.Default.SportsSoccer,
                route = "maatfoot",
                description = "Sport et football"
            ),
            NetflixMenuItem(
                id = "flix",
                title = "MaâtFlix",
                icon = Icons.Default.Movie,
                route = "maattube",
                description = "Films et séries"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF8B4513),
                        Color(0xFF654321),
                        MaatColorNoirProfond
                    )
                )
            )
    ) {
        // Main content
        TvMainContent(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = if (isMenuExpanded) 280f else 0f
                    scaleX = if (isMenuExpanded) 0.82f else 1f
                    scaleY = if (isMenuExpanded) 0.82f else 1f
                    alpha = if (isMenuExpanded) 0.6f else 1f
                }
                .onPreviewKeyEvent { event ->
                    handleMainContentKeyEvent(
                        event = event,
                        isMenuExpanded = isMenuExpanded,
                        isMenuFocused = isMenuFocused,
                        onToggleMenu = { isMenuExpanded = !isMenuExpanded },
                        onSetMenuFocus = { isMenuFocused = it }
                    )
                }
                .focusable(!isMenuFocused)
        )

        // Sidebar menu
        TvSidebarMenu(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    top = 60.dp,
                    start = 16.dp
                ),
            items = menuItems,
            selectedIndex = selectedMenuIndex,
            isExpanded = isMenuExpanded,
            onItemSelected = { index: Int ->
                if (index in menuItems.indices) {
                    selectedMenuIndex = index
                    navController.navigate(menuItems[index].route)
                    isMenuExpanded = false
                    isMenuFocused = false
                }
            },
            onExpandedChange = { expanded ->
                isMenuExpanded = expanded
                isMenuFocused = expanded
            }
        )
    }
}

private fun handleMainContentKeyEvent(
    event: KeyEvent,
    isMenuExpanded: Boolean,
    isMenuFocused: Boolean,
    onToggleMenu: () -> Unit,
    onSetMenuFocus: (Boolean) -> Unit
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false

    return when (event.key) {
        Key.DirectionLeft -> {
            if (!isMenuFocused) {
                onSetMenuFocus(true)
                onToggleMenu()
                true
            } else false
        }
        Key.DirectionRight -> {
            if (isMenuExpanded && isMenuFocused) {
                onSetMenuFocus(false)
                onToggleMenu()
                true
            } else false
        }
        Key.Back -> {
            if (isMenuExpanded) {
                onSetMenuFocus(false)
                onToggleMenu()
                true
            } else false
        }
        else -> false
    }
}

@Composable
fun NetflixMenuItemCard(
    item: NetflixMenuItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    focusRequester: FocusRequester,
    onItemClick: () -> Unit,
    onItemFocus: (Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 350f),
        label = "itemScale"
    )

    val cardColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaatColorOrSable.copy(alpha = 0.3f)
            isFocused -> MaatColorOrangeSolaire.copy(alpha = 0.2f)
            else -> Color.Transparent
        },
        animationSpec = tween(150),
        label = "cardColor"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaatColorOrSable
            isFocused -> MaatColorOrangeSolaire
            else -> Color.White.copy(alpha = 0.8f)
        },
        animationSpec = tween(150),
        label = "iconColor"
    )

    M3Card(
        onClick = onItemClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isExpanded) 72.dp else 56.dp)
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionCenter, Key.Enter -> {
                            onItemClick()
                            true
                        }
                        else -> false
                    }
                } else false
            },
        colors = M3CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isExpanded) 16.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                TvIcon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandHorizontally(
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(200, delayMillis = 50)),
                exit = shrinkHorizontally(animationSpec = tween(120)) +
                        fadeOut(animationSpec = tween(100))
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = item.title,
                        color = when {
                            isSelected -> MaatColorOrSable
                            isFocused -> MaatColorOrangeSolaire
                            else -> Color.White
                        },
                        fontSize = 16.sp,
                        fontWeight = if (isSelected || isFocused) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1
                    )

                    AnimatedVisibility(
                        visible = isFocused && item.description.isNotEmpty(),
                        enter = fadeIn(animationSpec = tween(120, delayMillis = 60)) +
                                expandVertically(animationSpec = tween(120, delayMillis = 60)),
                        exit = fadeOut(animationSpec = tween(80)) +
                                shrinkVertically(animationSpec = tween(80))
                    ) {
                        Text(
                            text = item.description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TvMainContent(navController: NavController, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current

    val servicesSection = remember {
        ContentSection(
            "Nos services",
            listOf(
                ContentItem("maat_tv", "Maât.TV", imageUrl = "", imageRes = AppR.drawable.maat_tv),
                ContentItem("maat_care", "MaâtCare", imageUrl = "", imageRes = AppR.drawable.maat_care),
                ContentItem("maat_class", "MaâtClass", imageUrl = "", imageRes = AppR.drawable.maat_class),
                ContentItem("maat_foot", "MaâtFoot", imageUrl = "", imageRes = AppR.drawable.maat_foot),
                ContentItem("maat_flix", "MaâtFlix", imageUrl = "", imageRes = AppR.drawable.maat_flix)
            )
        )
    }

    val recommendationsSection = remember {
        ContentSection(
            "Recommendations",
            listOf(
                ContentItem("rec_1", "Contenu Recommandé 1", imageUrl = "", imageRes = AppR.drawable.maat_tv),
                ContentItem("rec_2", "Contenu Recommandé 2", imageUrl = "", imageRes = AppR.drawable.maat_care),
                ContentItem("rec_3", "Contenu Recommandé 3", imageUrl = "", imageRes = AppR.drawable.maat_class),
                ContentItem("rec_4", "Contenu Recommandé 4", imageUrl = "", imageRes = AppR.drawable.maat_foot),
                ContentItem("rec_5", "Contenu Recommandé 5", imageUrl = "", imageRes = AppR.drawable.maat_flix),
                ContentItem("rec_6", "Contenu Recommandé 6", imageUrl = "", imageRes = AppR.drawable.maat_header)
            )
        )
    }

    val trendingSection = remember {
        ContentSection(
            "Tendances actuelles",
            listOf(
                ContentItem("trend_1", "Tendance 1", imageUrl = "", imageRes = AppR.drawable.maat_flix),
                ContentItem("trend_2", "Tendance 2", imageUrl = "", imageRes = AppR.drawable.maat_foot),
                ContentItem("trend_3", "Tendance 3", imageUrl = "", imageRes = AppR.drawable.maat_tv)
            )
        )
    }

    val allSections = remember { listOf(servicesSection, recommendationsSection, trendingSection) }

    TvLazyColumn(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            TvHeroSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .padding(bottom = 20.dp)
            )
        }

        items(allSections.size) { index ->
            val section = allSections[index]
            TvCarouselSection(
                title = section.title,
                items = section.items,
                onItemClick = { contentItem ->
                    Log.d("TvMainContent", "Clicked on: ${contentItem.title}")
                    navController.navigate("content_detail_screen/${contentItem.id}")
                }
            )
        }
    }
}

@Composable
fun TvHeroSection(modifier: Modifier = Modifier) {
    val heroFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        heroFocusRequester.requestFocus()
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "heroScale"
    )

    Box(
        modifier = modifier
            .focusRequester(heroFocusRequester)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(MaatColorGrisClair.copy(alpha = 0.2f)),
        contentAlignment = Alignment.BottomStart
    ) {
        // Image principale
        Image(
            painter = painterResource(id = AppR.drawable.maat_header),
            contentDescription = "Image Hero",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient de lisibilité (plus haut et plus opaque)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 200f
                    )
                )
        )

        // Bloc texte avec padding et largeur limitée
        Column(
            modifier = Modifier
                .padding(start = 48.dp, bottom = 36.dp, end = 48.dp)
                .widthIn(max = 540.dp) // Largeur max pour éviter d'aller trop à droite
        ) {
            Text(
                text = "",
                color = Color(0xFFE5C28A), // Or pâle, pour contraste
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Le réveil de la Maât",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pour un monde de vérité, justice et d'harmonie.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}


@Preview(device = "id:tv_1080p")
@Composable
fun NetflixTvHomeScreenPreview() {
    MaatcoreandroidtvTheme {
        NetflixTvHomeScreen(navController = rememberNavController())
    }
}
