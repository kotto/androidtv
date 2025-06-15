@file:OptIn(ExperimentalTvMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.screens

// Corrected and organized imports
import ai.maatcore.maatcore_android_tv.R as AppR // Assuming this is your app's R file
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.data.ContentSection
import ai.maatcore.maatcore_android_tv.ui.components.SidebarItem // Single import for SidebarItem
import ai.maatcore.maatcore_android_tv.ui.components.TvCarouselSection
import ai.maatcore.maatcore_android_tv.ui.components.TvSidebarMenu
import ai.maatcore.maatcore_android_tv.ui.theme.*
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults as M3CardDefaults // Alias for clarity
import androidx.compose.material3.Card as M3Card // Alias for clarity
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon // Alias for TV Icon

// Define typealias once
typealias NetflixMenuItem = SidebarItem

@Composable
fun NetflixTvHomeScreen(navController: NavController) {

    var selectedMenuIndex by remember { mutableStateOf(0) }
    var isMenuExpanded by remember { mutableStateOf(true) } // Start expanded or based on preference

    val menuItems = remember {
        listOf(
            NetflixMenuItem(
                id = "home",
                title = "Accueil",
                icon = Icons.Default.Home,
                route = "home",
                description = "Découvrez notre contenu"
            ),
            NetflixMenuItem(
                id = "tv",
                title = "Maât.TV",
                icon = Icons.Default.Tv,
                route = "maat_tv_screen",
                description = "Chaînes TV en direct"
            ),
            NetflixMenuItem(
                id = "care",
                title = "MaâtCare",
                icon = Icons.Default.Favorite,
                route = "maat_care_screen",
                description = "Santé et bien-être"
            ),
            NetflixMenuItem(
                id = "class",
                title = "MaâtClass",
                icon = Icons.Default.School,
                route = "maat_class_screen",
                description = "Formation et éducation"
            ),
            NetflixMenuItem(
                id = "foot",
                title = "MaâtFoot",
                icon = Icons.Default.SportsSoccer,
                route = "maat_foot_screen",
                description = "Sport et football"
            ),
            NetflixMenuItem(
                id = "flix",
                title = "MaâtFlix",
                icon = Icons.Default.Movie,
                route = "netflix_tv_home_screen", // Assuming this is the current screen's route for selection
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
                        Color(0xFF8B4513), // SaddleBrown like
                        Color(0xFF654321), // Darker Brown
                        MaatColorNoirProfond
                    )
                )
            )
    ) {
        // Main content with smooth parallax effect
        TvMainContent( // This composable uses TvHeroSection and TvCarouselSection
            navController = navController, // Pass navController
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = if (isMenuExpanded) 224f else 0f // Adapted to new sidebar width
                    scaleX = if (isMenuExpanded) 0.85f else 1f
                    scaleY = if (isMenuExpanded) 0.85f else 1f
                    alpha = if (isMenuExpanded) 0.7f else 1f
                }
                // Potentially add .onPreviewKeyEvent here if main content needs to close sidebar
                .onPreviewKeyEvent { event ->
                    if (isMenuExpanded && event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                        isMenuExpanded = false
                        true // Consume
                    } else if (!isMenuExpanded && event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft){
                        // This logic might be better handled by focus moving to the sidebar,
                        // which then expands itself. But can be a quick way to reopen.
                        // isMenuExpanded = true
                        // true // consume
                        false // Let sidebar handle open via focus
                    }
                    else {
                        false
                    }
                }
                .focusable(isMenuExpanded.not()) // Content focusable only when menu is collapsed
        )

        // Sidebar menu - uses TvSidebarMenu component
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val targetTopPadding = screenHeight * 0.23f // lower by roughly one-quarter (raised 10%)
        val animatedTop by animateDpAsState(
            targetValue = if (isMenuExpanded) targetTopPadding else 0.dp,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
        TvSidebarMenu(
            modifier = Modifier
                .padding(top = animatedTop)
                .align(Alignment.CenterStart),
            items = menuItems,
            selectedIndex = selectedMenuIndex,
            isExpanded = isMenuExpanded,
            onItemSelected = { index: Int ->
                if (index >= 0 && index < menuItems.size) {
                    selectedMenuIndex = index
                    navController.navigate(menuItems[index].route)
                    isMenuExpanded = false // Collapse menu on item selection
                }
            },
            onExpandedChange = { expanded -> isMenuExpanded = expanded },

        )
    }
}

// The NetflixStyleSidebar composable that was causing issues with 'menuItems'
// is commented out below. If you intend to use this, you need to pass 'menuItems'
// as a parameter to it and call it from somewhere (e.g., from NetflixTvHomeScreen
// or from within TvSidebarMenu if it's a sub-component).
// For now, TvSidebarMenu is being used directly by NetflixTvHomeScreen.

/*
@Composable
fun NetflixStyleSidebar(
    menuItems: List<NetflixMenuItem>, // Ensure menuItems is passed if you use this
    selectedIndex: Int,
    isExpanded: Boolean,
    onItemSelected: (Int) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) 224.dp else 56.dp,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 400f,
            visibilityThreshold = 0.5.dp // Optional: helps with smoother snap
        ),
        label = "sidebarWidth"
    )

    val focusRequesters = remember(menuItems.size) { List(menuItems.size) { FocusRequester() } }

    LaunchedEffect(selectedIndex, menuItems.size) { // Added menuItems.size as a key
        if (menuItems.isNotEmpty() && selectedIndex >= 0 && selectedIndex < menuItems.size) {
            try {
                focusRequesters[selectedIndex].requestFocus()
            } catch (e: IndexOutOfBoundsException) {
                Log.e("NetflixStyleSidebar", "Error requesting focus: index $selectedIndex out of bounds for ${menuItems.size}")
            }
        }
    }

    Box(
        modifier = modifier/*addedFocus*/
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                    if (selectedIndex >= 0 && selectedIndex < focusRequesters.size) {
                        focusRequesters[selectedIndex].requestFocus()
                    }
                    onExpandedChange(true)
                    true // consume
                } else {
                    false
                }
            }
            .width(sidebarWidth)
            .fillMaxHeight()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaatColorNoirProfond.copy(alpha = 0.98f),
                        MaatColorNoirProfond.copy(alpha = 0.85f),
                        Color.Transparent // Smooth fade to content
                    ),
                    startX = 0f,
                    endX = if (isExpanded) 700f else 210f // Adjust gradient length
                )
            )
            .zIndex(1f) // Ensure sidebar is above content if parallax shifts content
    ) {
        TvLazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp, start = 4.dp, end = 4.dp), // Adjusted padding
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopStart)
        ) {
            items(menuItems.size) { index ->
                NetflixMenuItemCard( // Assuming NetflixMenuItemCard is defined and working
                    item = menuItems[index],
                    isSelected = selectedIndex == index,
                    isExpanded = isExpanded,
                    focusRequester = focusRequesters[index],
                    onItemClick = {
                        onItemSelected(index)
                        // isMenuExpanded = false // Usually handled by caller
                    },
                    onItemFocus = { focused ->
                        if (focused) {
                            onExpandedChange(true) // Expand menu on item focus
                        }
                        // Optional: Handle collapse if focus moves out of the sidebar
                        // else if (!isAnyItemFocusedInSidebar()) { onExpandedChange(false) }
                    }
                )
            }
        }
    }
}
*/

@Composable
fun NetflixMenuItemCard( // This is the definition from your original code
    item: NetflixMenuItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    focusRequester: FocusRequester,
    onItemClick: () -> Unit,
    onItemFocus: (Boolean) -> Unit // Renamed from onItemFocus to avoid conflict if any
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
        animationSpec = tween(200),
        label = "cardColor"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaatColorOrSable
            isFocused -> MaatColorOrangeSolaire
            else -> Color.White.copy(alpha = 0.8f)
        },
        animationSpec = tween(200),
        label = "iconColor"
    )

    M3Card( // Using aliased M3 Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isExpanded) 16.dp else (56.dp - 24.dp) / 2), // Center icon when collapsed
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(if (isExpanded) 32.dp else 24.dp), // Adjust icon container for alignment
                contentAlignment = Alignment.Center
            ) {
                TvIcon( // Using aliased TV Icon
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)) +
                        fadeIn(animationSpec = tween(300, delayMillis = 50)),
                exit = shrinkHorizontally(animationSpec = tween(200)) +
                        fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth() // Take remaining space
                        .align(Alignment.CenterVertically) // Vertically align text column
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
                        maxLines = 1 // Ensure text doesn't wrap unexpectedly
                    )

                    AnimatedVisibility(
                        visible = isFocused && item.description.isNotEmpty(),
                        enter = fadeIn(animationSpec = tween(200, delayMillis = 100)) +
                                expandVertically(animationSpec = tween(200, delayMillis = 100)),
                        exit = fadeOut(animationSpec = tween(150)) +
                                shrinkVertically(animationSpec = tween(150))
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
fun TvMainContent(navController: NavController, modifier: Modifier = Modifier) { // Added navController
    // Dummy data for content sections - replace with your actual data source
    val servicesSection = ContentSection(
        "Nos services",
        listOf(
            ContentItem("maat_tv", "Maât.TV", imageUrl = "", imageRes = AppR.drawable.maat_tv),
            ContentItem("maat_care", "MaâtCare", imageUrl = "", imageRes = AppR.drawable.maat_care),
            ContentItem("maat_class", "MaâtClass", imageUrl = "", imageRes = AppR.drawable.maat_class),
            ContentItem("maat_foot", "MaâtFoot", imageUrl = "", imageRes = AppR.drawable.maat_foot),
            ContentItem("maat_flix", "MaâtFlix", imageUrl = "", imageRes = AppR.drawable.maat_flix)
        )
    )

    val recommendationsSection = ContentSection(
        "Recommendations",
        listOf( // Example: Reusing some items for demonstration
            ContentItem("rec_1", "Contenu Recommandé 1", imageUrl = "", imageRes = AppR.drawable.maat_tv),
            ContentItem("rec_2", "Contenu Recommandé 2", imageUrl = "", imageRes = AppR.drawable.maat_care),
            ContentItem("rec_3", "Contenu Recommandé 3", imageUrl = "", imageRes = AppR.drawable.maat_class),
            ContentItem("rec_4", "Contenu Recommandé 4", imageUrl = "", imageRes = AppR.drawable.maat_foot),
            ContentItem("rec_5", "Contenu Recommandé 5", imageUrl = "", imageRes = AppR.drawable.maat_flix),
            ContentItem("rec_6", "Contenu Recommandé 6", imageUrl = "", imageRes = AppR.drawable.maat_header)
        )
    )
    val trendingSection = ContentSection(
        "Tendances actuelles",
        listOf(
            ContentItem("trend_1", "Tendance 1", imageUrl = "", imageRes = AppR.drawable.maat_flix),
            ContentItem("trend_2", "Tendance 2", imageUrl = "", imageRes = AppR.drawable.maat_foot),
            ContentItem("trend_3", "Tendance 3", imageUrl = "", imageRes = AppR.drawable.maat_tv)
        )
    )

    // Example sections - add more as needed
    val allSections = listOf(servicesSection, recommendationsSection, trendingSection)


    TvLazyColumn(
        modifier = modifier/*addedFocus*/
            .padding(start = 24.dp, end = 24.dp) // Overall padding for the content area
            .fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp) // Spacing between sections
    ) {
        // Hero Section (Optional - if you have one)
        item {
            TvHeroSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp) // Adjust height as needed
                    .padding(bottom = 20.dp)
            )
        }

        // Content Carousels
        items(allSections.size) { index ->
            val section = allSections[index]
            TvCarouselSection(
                title = section.title,
                items = section.items,
                onItemClick = { contentItem ->
                    Log.d("TvMainContent", "Clicked on: ${contentItem.title}")
                    navController.navigate("content_detail_screen/${contentItem.id}") // Navigate to detail screen
                }
            )
        }
    }
}

@Composable
fun TvHeroSection(modifier: Modifier = Modifier) {
    val heroFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { heroFocusRequester.requestFocus() }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier/*addedFocus*/
            .focusRequester(heroFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown) {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                } else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionUp) {
                    // Consume to prevent scrolling outside when already at top
                    true
                } else false
            }
            .clip(RoundedCornerShape(12.dp))
            .background(MaatColorGrisClair.copy(alpha = 0.2f)), // Placeholder background
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(id = AppR.drawable.maat_header), // Replace with your hero image
            contentDescription = "Hero Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box( // Gradient overlay for text readability
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 500f // Adjust gradient start
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Le réveil de la Maât",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pour un monde de vérité, justice et d'harmonie.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(16.dp))
            // You can use your NetflixPlayButton here if defined and compatible
            // For example:
            // NetflixPlayButton(onClick = { /* Handle Play */ })
        }
    }
}


@Preview(device = "id:tv_1080p")
@Composable
fun NetflixTvHomeScreenPreview() {
    MaatcoreandroidtvTheme { // Using the correct theme composable
        NetflixTvHomeScreen(navController = rememberNavController())
    }
}
