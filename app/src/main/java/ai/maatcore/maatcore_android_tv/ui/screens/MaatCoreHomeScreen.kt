@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)
package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.R as AndroidR
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import ai.maatcore.maatcore_android_tv.R as AppR
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.data.ContentSection
import androidx.tv.foundation.lazy.list.rememberTvLazyListState

/* ----------------------- DATA CLASSES ----------------------- */
data class MenuItem(val title: String, val iconRes: Int)

/* ----------------------- COLORS ----------------------- */
object MaatCoreColors {
    val Gold = Color(0xFFD4AF37)
    val LightGold = Color(0xFFF4D03F)
    val Beige = Color(0xFFF5E6D3)
    val DarkBrown = Color(0xFF2C1810)
    val Black = Color(0xFF1A1A1A)
    val White = Color(0xFFFFFFFF)
    val Gray = Color(0xFF666666)

    val BackgroundGradient = Brush.radialGradient(
        colors = listOf(Color(0xFF8B4513), Color(0xFF654321), DarkBrown)
    )
    val DrawerOverlay = Color.Black.copy(alpha = 0.6f)
}

/* ----------------------- MAIN SCREEN ----------------------- */
@Composable
fun MaatCoreHomeScreen(navController: NavController) {
    var isDrawerOpen by remember { mutableStateOf(true) }
    var selectedMovieIndex by remember { mutableStateOf(-1) }
    val scope = rememberCoroutineScope()

    /* -------- SAMPLE DATA -------- */
    val menuItems = listOf(
        MenuItem("Maât.TV", AppR.drawable.maat_tv),
        MenuItem("MaâtFlix", AppR.drawable.maat_flix),
        MenuItem("MaâtCare", AppR.drawable.maat_care),
        MenuItem("MaâtClass", AppR.drawable.maat_class),
        MenuItem("MaâtFoot", AppR.drawable.maat_foot)
    )

    val movieSections = listOf(
        ContentSection(
            "Nos services",
            listOf(
                ContentItem("maat_tv", "Maât.TV", imageUrl = "", subtitle = "Chaînes TV", description = "", mainImageUrl = "", imageRes = AppR.drawable.maat_tv),
                ContentItem("maat_flix", "MaâtFlix", imageUrl = "", subtitle = "VOD", description = "", mainImageUrl = "", imageRes = AppR.drawable.maat_flix),
                ContentItem("maat_care", "MaâtCare", imageUrl = "", subtitle = "Santé", description = "", mainImageUrl = "", imageRes = AppR.drawable.maat_care),
                ContentItem("maat_class", "MaâtClass", imageUrl = "", subtitle = "Éducation", description = "", mainImageUrl = "", imageRes = AppR.drawable.maat_class),
                ContentItem("maat_foot", "MaâtFoot", imageUrl = "", subtitle = "Sport", description = "", mainImageUrl = "", imageRes = AppR.drawable.maat_foot)
            )
        ),
        ContentSection(
            "Tendances Actuelles",
            listOf(
                ContentItem("movie1", "Film d'Action", imageUrl = "", subtitle = "Un film plein d'action", description = "Description du film d'action...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2023", rating = 4.5f, durationMinutes = 135),
                ContentItem("movie2", "Comédie Romantique", imageUrl = "", subtitle = "Une histoire d'amour hilarante", description = "Description de la comédie romantique...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2022", rating = 4.0f, durationMinutes = 105),
                ContentItem("movie3", "Documentaire Nature", imageUrl = "", subtitle = "Explorez la faune sauvage", description = "Description du documentaire nature...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2024", rating = 4.8f, durationMinutes = 90),
                ContentItem("movie4", "Série Dramatique", imageUrl = "", subtitle = "Une série captivante", description = "Description de la série dramatique...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2023", rating = 4.2f),
                ContentItem("movie5", "Dessin Animé", imageUrl = "", subtitle = "Aventure pour toute la famille", description = "Description du dessin animé...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2021", rating = 4.7f, durationMinutes = 70)
            )
        ),
        ContentSection(
            "Recommandé pour vous",
            listOf(
                ContentItem("rec1", "Science-Fiction", imageUrl = "", subtitle = "Voyage dans le futur", description = "Description du film de science-fiction...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2020", rating = 4.3f, durationMinutes = 120),
                ContentItem("rec2", "Thriller Psychologique", imageUrl = "", subtitle = "Un esprit torturé", description = "Description du thriller psychologique...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2024", rating = 4.6f, durationMinutes = 110),
                ContentItem("rec3", "Fantaisie Épique", imageUrl = "", subtitle = "Un monde de magie", description = "Description de la fantaisie épique...", mainImageUrl = "", imageRes = AppR.drawable.content_placeholder, releaseDate = "2023", rating = 4.9f, durationMinutes = 150)
            )
        )
    )

    /* -------- UI -------- */
    Box(Modifier.fillMaxSize()) {
        MainContent(
            navController = navController,
            movieSections = movieSections,
            onMenuClick = { isDrawerOpen = true },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = if (isDrawerOpen) 100f else 0f
                    scaleX = if (isDrawerOpen) 0.95f else 1f
                    scaleY = if (isDrawerOpen) 0.95f else 1f
                }
        )

        /* Drawer */
        FluidModalDrawer(
            isOpen = isDrawerOpen,
            menuItems = menuItems,
            onClose = { isDrawerOpen = false }
        )
    }
}

/* ----------------------- DRAWER ----------------------- */
@Composable
fun FluidModalDrawer(isOpen: Boolean, menuItems: List<MenuItem>, onClose: () -> Unit) {
    val drawerWidth = 350.dp
    val slideOffset by animateDpAsState(if (isOpen) 0.dp else -drawerWidth, label = "slide")
    val overlayAlpha by animateFloatAsState(if (isOpen) 0.4f else 0f, label = "alpha")

    /** overlay */
    AnimatedVisibility(visible = isOpen, enter = fadeIn(), exit = fadeOut()) {
        Box(Modifier.fillMaxSize().background(MaatCoreColors.DrawerOverlay.copy(alpha = overlayAlpha)))
    }

    /** drawer content */
    AnimatedVisibility(visible = isOpen, enter = slideInHorizontally { -it }, exit = slideOutHorizontally { -it }) {
        Box(Modifier.width(drawerWidth).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().background(MaatCoreColors.DarkBrown.copy(alpha = 0.95f)).padding(32.dp)) {
                /* header */
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("MAATCORE", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaatCoreColors.Gold)
                    Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaatCoreColors.Gold)) {
                        Text("×", fontSize = 24.sp)
                    }
                }
                Spacer(Modifier.height(40.dp))
                /* menu */
                TvLazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(menuItems) { item ->
                        DrawerItem(item)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("Version 1.0.1", fontSize = 12.sp, color = MaatCoreColors.Gray)
            }
        }
    }
}

@Composable
fun DrawerItem(item: MenuItem) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.1f else 1f, label = "scale")

    Card(
        onClick = {},
        modifier = Modifier.fillMaxWidth().scale(scale).onFocusChanged { focused = it.isFocused },
        colors = CardDefaults.colors(containerColor = if (focused) MaatCoreColors.Gold.copy(alpha = 0.1f) else Color.Transparent)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(id = item.iconRes), contentDescription = item.title, Modifier.size(24.dp))
            Spacer(Modifier.width(20.dp))
            Text(item.title, color = if (focused) MaatCoreColors.LightGold else MaatCoreColors.Gold)
        }
    }
}

/* ----------------------- MAIN CONTENT ----------------------- */
@Composable
fun MainContent(
    navController: NavController,
    movieSections: List<ContentSection>,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.background(MaatCoreColors.BackgroundGradient)) {
        HeroSection(onMenuClick)

        TvLazyColumn(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .offset(y = 40.dp)
                .padding(horizontal = 40.dp)
        ) {
            items(movieSections.withIndex().toList()) { (sectionIndex, section) ->
                ImmersiveMovieSection(section, navController)
            }
        }
    }
}

/* ----------------------- SECTION & CARDS ----------------------- */
@Composable
fun ImmersiveMovieSection(section: ContentSection, navController: NavController) {
    Column {
        Text(section.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaatCoreColors.Gold, modifier = Modifier.padding(bottom = 16.dp))
        val lazyRowState = rememberTvLazyListState()
        TvLazyRow(
            state = lazyRowState,
            modifier = Modifier.height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.items) { contentItem ->
                ImmersiveMovieCard(contentItem) {
                    navController.navigate("details/${contentItem.id}")
                }
            }
        }
        // Scroll indicators
        val currentItemIndex = remember { derivedStateOf { lazyRowState.firstVisibleItemIndex } }.value
        val totalItems = section.items.size
        if (totalItems > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalItems) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (index == currentItemIndex) MaatCoreColors.Gold else MaatCoreColors.Gray.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

@Composable
fun ImmersiveMovieCard(contentItem: ContentItem, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.15f else 1f, label = "cardScale")
    val elevation by animateDpAsState(if (focused) 8.dp else 2.dp, label = "cardElev")
    val width by animateDpAsState(if (focused) 140.dp else 100.dp, label = "cardWidth")

    Card(
        onClick = onClick,
        modifier = Modifier.width(width).height(160.dp).scale(scale).onFocusChanged { focused = it.isFocused }.zIndex(if (focused) 1f else 0f),
        colors = CardDefaults.colors(containerColor = Color.Transparent) // Utiliser Color.Transparent ou une couleur par défaut
    ) {
        Box(Modifier.fillMaxSize()) {
            contentItem.imageRes?.let {
                Image(painterResource(id = it), contentDescription = contentItem.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } ?: run {
                // Fallback if imageRes is null, e.g., use a placeholder or a solid color
                Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 0.4f)))
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(contentItem.title, fontSize = if (focused) 12.sp else 10.sp, fontWeight = FontWeight.Bold, color = MaatCoreColors.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (focused && contentItem.releaseDate?.isNotEmpty() == true) {
                    Spacer(Modifier.height(4.dp))
                    val ratingText = contentItem.rating?.let { "%.1f ⭐".format(it) } ?: ""
                    val durationText = contentItem.durationMinutes?.let { "${it / 60}h${it % 60}m" } ?: ""
                    Text("${contentItem.releaseDate} • $ratingText • $durationText", fontSize = 12.sp, color = MaatCoreColors.Beige)
                }
                AnimatedVisibility(visible = focused && contentItem.description?.isNotEmpty() == true, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                    contentItem.description?.let {
                        Text(it, fontSize = 11.sp, color = MaatCoreColors.Beige.copy(alpha = 0.9f), maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

/* ----------------------- HERO ----------------------- */
@Composable
fun HeroSection(onMenuClick: () -> Unit) {
    Card(onClick = {}, modifier = Modifier.fillMaxWidth().height(400.dp).padding(horizontal = 0.dp), colors = CardDefaults.colors(containerColor = Color.Transparent)) {
        Box {
            Image(
                painterResource(id = AppR.drawable.maat_header),
                contentDescription = "Header",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 0.6f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(40.dp)
            ) {
                Text(
                    "QUEEN OF MAÄT",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaatCoreColors.White,
                    fontFamily = FontFamily.SansSerif // Or a specific font family if available
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "The untold story of a powerful queen",
                    fontSize = 18.sp,
                    color = MaatCoreColors.Beige,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Navigate to featured content details */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaatCoreColors.Gold, contentColor = MaatCoreColors.DarkBrown)
                ) {
                    Text("Regarder Maintenant")
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 20.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onMenuClick, colors = ButtonDefaults.buttonColors(containerColor = MaatCoreColors.Gold.copy(alpha = 0.2f), contentColor = MaatCoreColors.Gold)) {
                    Text("☰ Menu")
                }
                Image(painterResource(id = AppR.drawable.maat_logo), contentDescription = "Logo Maât", modifier = Modifier.height(80.dp))
            }
        }
    }
}

/* ----------------------- PREVIEW ----------------------- */
@Preview(showBackground = true)
@Composable
fun MaatCoreHomePreview() {
    val navController = rememberNavController()
    MaatCoreHomeScreen(navController = navController)
}
