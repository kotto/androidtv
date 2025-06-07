package ai.maatcore.maatcore_android_tv.ui.screens

// Imports Compose standard (inchangés ou déjà vérifiés)
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // Standard lazy items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api // Standard Material 3 (pas TV)
import androidx.compose.material3.ModalDrawerSheet // Standard Material 3 (pas TV)
import androidx.compose.ui.text.style.TextAlign // Ajout pour le texte centré
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// androidx.compose.ui.unit.sp // Already imported

// Imports for custom fonts
import ai.maatcore.maatcore_android_tv.ui.theme.Montserrat
import ai.maatcore.maatcore_android_tv.ui.theme.Poppins
import ai.maatcore.maatcore_android_tv.ui.theme.Inter

// Imports spécifiques à Android TV Material 3 et Foundation
import androidx.tv.material3.Button // TV Button
import androidx.tv.material3.ButtonDefaults // TV ButtonDefaults
import androidx.tv.material3.Card // TV Card
import androidx.tv.material3.CardDefaults // TV CardDefaults
import androidx.tv.material3.DrawerValue // TV DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api // TV Material 3
import androidx.tv.material3.Icon // TV Icon
import androidx.tv.material3.IconButton // TV IconButton
import androidx.tv.material3.MaterialTheme // Utiliser le MaterialTheme de TV
import androidx.tv.material3.ModalNavigationDrawer // TV ModalNavigationDrawer
import androidx.tv.material3.Surface // Surface de TV pour une meilleure intégration du thème
import androidx.tv.material3.Text // TV Text
import androidx.tv.material3.rememberDrawerState // TV rememberDrawerState
import androidx.tv.foundation.ExperimentalTvFoundationApi // TV foundation experimental APIs
import androidx.tv.foundation.lazy.list.TvLazyColumn // Alternative pour LazyColumn optimisée pour TV
import androidx.tv.foundation.lazy.list.TvLazyRow // Alternative pour LazyRow optimisée pour TV
import androidx.tv.foundation.lazy.list.items // items pour TvLazyRow/Column (TV version)
import androidx.compose.foundation.focusable // focusable modifier from standard compose foundation

// Import de ressources de l'application
import ai.maatcore.maatcore_android_tv.R
// import androidx.compose.animation.core.copy // Not used directly, can be removed if not needed elsewhere

// Import Coroutines
import kotlinx.coroutines.launch

// Couleurs pour le thème TV luxueux africain moderne
val tvBackgroundColor = Color(0xFF0D0D0D) // Deep black background
val tvPrimaryColor = Color(0xFFF5D487) // Golden text
val tvButtonColor = Color(0xFFE0C28A) // Button and highlight color
val tvAccentOrange = Color(0xFFFF8C42) // Solar orange accent
val tvAccentCopper = Color(0xFFA65C2E) // Copper accent secondary

// Data models
data class MenuItem(
    val id: String,
    val title: String,
    val icon: Int? = null
)

data class ContentItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val description: String = "",
    val videoUrl: String = ""
)

data class ContentRow(
    val id: String,
    val title: String,
    val items: List<ContentItem>
)

data class FeaturedContent(
    val id: String,
    val title: String,
    val tagline: String,
    val imageUrl: String,
    val videoUrl: String = ""
)

@Composable
fun painterResourceFromString(imageName: String?): Painter {
    println("Debug: Loading image: $imageName")
    if (imageName == "maat_header") {
        return painterResource(id = R.drawable.maat_header)
    }

    val context = LocalContext.current
    val resourceId = remember(imageName) {
        if (imageName.isNullOrBlank()) {
            0
        } else {
            val cleanName = imageName
                .substringAfterLast('/')
                .substringBeforeLast('.')
                .lowercase()
                .replace("[^a-z0-9_]".toRegex(), "_")
            context.resources.getIdentifier(cleanName, "drawable", context.packageName)
        }
    }
    return if (resourceId != 0) {
        painterResource(id = resourceId)
    } else {
        painterResource(id = R.drawable.content_placeholder) // Ensure content_placeholder exists
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MaatTvAppScreen(onNavigate: (String) -> Unit) {
    MaterialTheme { // Uses androidx.tv.material3.MaterialTheme
        AndroidTVHomeScreen(onNavigate = onNavigate)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AndroidTVHomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MenuItem("maattv", "Maat.TV", R.drawable.ic_tv),
        MenuItem("maatflix", "MaatFlix", R.drawable.ic_movie),
        MenuItem("maatcare", "MaatCare", R.drawable.ic_health),
        MenuItem("maatclass", "MaatClass", R.drawable.ic_education),
        MenuItem("maatfoot", "MaatFoot", R.drawable.ic_sports)
    )

    val featuredContent = FeaturedContent(
        id = "queen_of_maat",
        title = "Queen of Maât",
        tagline = "The untold story of a powerful queen",
        imageUrl = "maat_header",
        videoUrl = "https://example.com/video1"
    )

    val contentRows = listOf(
        ContentRow(
            id = "categories_services",
            title = "Nos Services",
            items = listOf(
                ContentItem("maat_tv", "Maât.TV", "maat_tv"),
                ContentItem("maat_care", "MaâtCare", "maat_care"),
                ContentItem("maat_class", "MaâtClass", "maat_class"),
                ContentItem("maat_foot", "MaâtFoot", "maat_foot"),
                ContentItem("maat_flix", "MaâtFlix", "maat_flix")
            )
        ),
        ContentRow(
            id = "nouveautes",
            title = "Nouveautés",
            items = listOf(
                ContentItem("oppenheimer", "OPPENHEIMER", "content_placeholder"),
                ContentItem("avatar", "AVATAR", "content_placeholder"),
                ContentItem("johnwick", "JOHN WICK", "content_placeholder")
            )
        )
        // Add more content rows as needed
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer( // This is from androidx.tv.material3
        drawerState = drawerState,
        drawerContent = {
            // Using androidx.compose.material3.ModalDrawerSheet as per your original code.
            // If you intend to use TV specific drawer sheet, ensure you have the correct import and usage.
            // For TV, the drawerContent is typically a Column or other layout directly.
            ModalDrawerSheet( // This is from androidx.compose.material3 (standard Material)
                drawerContainerColor = Color.Transparent, // Make drawer background transparent
                modifier = Modifier.width(260.dp) // Ensure the sheet has the menu width
            ) {
                SideMenu(
                    menuItems = menuItems,
                    onMenuItemClick = { menuItem ->
                        scope.launch {
                            drawerState.setValue(DrawerValue.Closed)
                        }
                        onNavigate(menuItem.id)
                    },
                    backgroundColor = Color(0xFF0D0D0D) // Deep black for menu background
                )
            }
        }
    ) {
        Box(modifier = modifier // Use the modifier passed to AndroidTVHomeScreen
            .fillMaxSize()
            .background(tvBackgroundColor)
        ) {
            // Image de fond réduite de 20% avec floutage à gauche
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 48.dp) // Décalage pour éviter le menu
            ) {
                Image(
                    painter = painterResourceFromString(featuredContent.imageUrl),
                    contentDescription = featuredContent.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // Réduit de 20%
                        .fillMaxHeight(0.8f) // Réduit de 20%
                        .align(Alignment.CenterEnd)
                        .blur(12.dp)
                )
                // Floutage progressif à gauche pour se fondre avec le menu
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    tvBackgroundColor, // Fond noir complet à gauche
                                    tvBackgroundColor.copy(alpha = 0.8f),
                                    Color.Transparent, // Transparent à droite
                                    Color(0x33FF8C42) // Léger accent orange
                                )
                            )
                        )
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                MaatTVHomeContent(
                    featuredContent = featuredContent,
                    contentRows = contentRows,
                    onOpenMenu = {
                        scope.launch {
                            drawerState.setValue(DrawerValue.Open)
                        }
                    },
                    onContentClick = { contentItem ->
                        onNavigate("details/${contentItem.id}")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SideMenu(
    menuItems: List<MenuItem>,
    onMenuItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black
) {
    Column( // Standard Column
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(backgroundColor)
            .padding(vertical = 48.dp, horizontal = 16.dp) // Réduit de 60dp à 48dp
    ) {
        Text(
            text = "MAÂTCORE",
            style = MaterialTheme.typography.headlineMedium.copy( // TV MaterialTheme
                color = tvPrimaryColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                fontFamily = Poppins
            ),
            modifier = Modifier
                .padding(bottom = 36.dp) // Réduit de 60dp à 36dp
                .focusable()
        )

        menuItems.forEach { menuItem ->
            Card( // TV Card
                onClick = { onMenuItemClick(menuItem) },
                colors = CardDefaults.colors( // TV CardDefaults
                    containerColor = Color.Transparent,
                    focusedContainerColor = tvAccentOrange.copy(alpha = 0.2f)
                ),
                shape = CardDefaults.shape(RectangleShape), // Explicitly rectangular if needed
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Réduit de 15dp à 8dp (espacement harmonieux)
                    .focusable()
            ) {
                Row( // Standard Row
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp), // Légèrement réduit
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    menuItem.icon?.let {
                        Icon( // TV Icon
                            painter = painterResource(id = it),
                            contentDescription = menuItem.title,
                            tint = tvPrimaryColor,
                            modifier = Modifier.size(24.dp) // Réduit de 28dp à 24dp
                        )
                        Spacer(modifier = Modifier.width(12.dp)) // Réduit de 16dp à 12dp
                    }
                    Text( // TV Text
                        text = menuItem.title,
                        style = MaterialTheme.typography.bodyLarge.copy( // TV MaterialTheme
                            color = tvPrimaryColor,
                            fontSize = 22.sp, // Réduit de 24sp à 22sp
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins
                        )
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun MaatTVHomeContent(
    featuredContent: FeaturedContent,
    contentRows: List<ContentRow>,
    onOpenMenu: () -> Unit,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box( // Standard Box
        modifier = modifier.fillMaxSize()
    ) {
        // This will be the main background controlled by FeaturedContentHeader
        // And content rows will be overlaid on top
        FeaturedContentHeader( // DEFINITION ADDED BELOW
            featuredContent = featuredContent,
            onOpenMenu = onOpenMenu,
            onWatchClick = {
                onContentClick(
                    ContentItem(
                        featuredContent.id,
                        featuredContent.title,
                        featuredContent.imageUrl,
                        videoUrl = featuredContent.videoUrl
                    )
                )
            }
        )

        Column( // Standard Column for scrolling content over the header
            modifier = Modifier
                .fillMaxSize()
                // Ajusté pour s'adapter à la réduction du header
                .padding(top = 240.dp) // Réduit de 300dp à 240dp
        ) {
            // Using TvLazyColumn for the scrollable part containing content rows and avatar section
            TvLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp) // Space at the bottom
            ) {
                // First content row (Nos Services)
                if (contentRows.isNotEmpty()) {
                    item {
                        ContentRowSection( // DEFINITION ADDED BELOW
                            rowTitle = contentRows[0].title,
                            contentItems = contentRows[0].items,
                            onContentClick = onContentClick
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    AvatarSection(onContentClick = onContentClick)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Display the remaining content rows
                if (contentRows.size > 1) {
                    items(contentRows.subList(1, contentRows.size)) { row ->
                        ContentRowSection(
                            rowTitle = row.title,
                            contentItems = row.items,
                            onContentClick = onContentClick
                        )
                    }
                }
            }
        }
    }
}

// DEFINITION FOR FeaturedContentHeader - Réduit de 20%
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedContentHeader(
    featuredContent: FeaturedContent,
    onOpenMenu: () -> Unit,
    onWatchClick: () -> Unit
) {
    Box( // Standard Box
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp) // Réduit de 20% (600dp -> 480dp)
    ) {
        Image(
            painter = painterResourceFromString(featuredContent.imageUrl),
            contentDescription = featuredContent.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.8f) // Réduit la largeur de 20%
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        )
        // Overlay a gradient for text readability at the bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000), Color(0xDD000000)),
                        startY = 480f // Ajusté pour la nouvelle hauteur
                    )
                )
        )

        // Menu button at the top left
        IconButton( // TV IconButton
            onClick = onOpenMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 32.dp, top = 32.dp) // Adjusted padding
                .focusable()
        ) {
            Icon( // TV Icon
                painter = painterResource(id = R.drawable.ic_menu), // Ensure ic_menu drawable exists
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Textual content and button at the bottom left
        Column( // Standard Column
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 48.dp, end = 48.dp)
                .fillMaxWidth(0.6f) // Take about 60% of width to avoid overlap with right gradient
        ) {
            Text( // TV Text
                text = featuredContent.title.uppercase(),
                style = MaterialTheme.typography.displaySmall.copy( // Adjusted for better fit
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    letterSpacing = 1.15.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text( // TV Text
                text = featuredContent.tagline,
                style = MaterialTheme.typography.titleMedium.copy( // Adjusted for better fit
                    color = Color.White.copy(alpha = 0.85f),
                    fontFamily = Poppins
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button( // TV Button
                onClick = onWatchClick,
                colors = ButtonDefaults.colors( // TV ButtonDefaults
                    containerColor = tvButtonColor,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .height(48.dp)
                    .focusable()
            ) {
                Text( // TV Text
                    text = "Regarder", // Watch in French
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold, // Bolder
                        fontFamily = Poppins,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}


// DEFINITION FOR ContentRowSection - Cartes réduites de moitié
@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun ContentRowSection(
    rowTitle: String,
    contentItems: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column( // Standard Column
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 12.dp) // Légèrement réduit
    ) {
        Row( // Standard Row
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 8.dp), // Réduit le padding vertical
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text( // TV Text
                text = rowTitle,
                style = MaterialTheme.typography.headlineSmall.copy( // TV MaterialTheme
                    color = tvPrimaryColor, // Using your theme color
                    fontFamily = Poppins
                )
            )
        }

        TvLazyRow( // TV TvLazyRow
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Réduit l'espacement entre cartes
        ) {
            items(contentItems) { item -> // TV items
                ContentCard(
                    contentItem = item,
                    onClick = { onContentClick(item) }
                )
            }
        }
    }
}


@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun ContentCard(
    contentItem: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dimensions réduites de moitié
    val cardWidth = 100.dp // Réduit de 200dp à 100dp
    val cardHeight = 150.dp // Réduit de 300dp à 150dp

    Card( // TV Card
        onClick = onClick,
        colors = CardDefaults.colors( // TV CardDefaults
            containerColor = Color.Transparent, // Card itself is transparent
            focusedContainerColor = tvAccentOrange.copy(alpha = 0.2f) // Glow on focus
        ),
        shape = CardDefaults.shape(RectangleShape), // Ensure rectangular if default has rounding
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .focusable()
    ) {
        Box( // Standard Box
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResourceFromString(contentItem.imageUrl),
                contentDescription = contentItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box( // Scrim for text
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xA0000000))
                        )
                    )
            )
            Text( // TV Text
                text = contentItem.title,
                style = MaterialTheme.typography.titleSmall.copy( // TV MaterialTheme
                    color = Color.White,
                    fontSize = 12.sp, // Réduit de 14sp à 12sp
                    fontFamily = Poppins // Consistent font
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 6.dp, vertical = 8.dp) // Réduit le padding
                    .fillMaxWidth()
            )
        }
    }
}


@OptIn(ExperimentalTvMaterial3Api::class) // Added OptIn
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AndroidTVHomeScreenPreview() {
    MaterialTheme {
        AndroidTVHomeScreen(onNavigate = {})
    }
}

@OptIn(ExperimentalTvMaterial3Api::class) // Added OptIn
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF101010, widthDp = 100, heightDp = 150)
@Composable
fun ContentCardPreview() {
    MaterialTheme {
        ContentCard(
            contentItem = ContentItem("prev", "Titre de l'émission", "content_placeholder"),
            onClick = {}
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class) // Added OptIn
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF000000, widthDp=300, heightDp=720)
@Composable
fun SideMenuPreview() {
    MaterialTheme {
        SideMenu(
            menuItems = listOf(
                MenuItem("maattv", "Maât.TV", R.drawable.ic_tv),
                MenuItem("maatflix", "MaâtFlix", R.drawable.ic_movie),
                MenuItem("maatcare", "MaâtCare", R.drawable.ic_health)
            ),
            onMenuItemClick = {}
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun AvatarSection(onContentClick: (ContentItem) -> Unit) {
    Row( // Standard Row
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column( // Standard Column
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onContentClick(ContentItem("maatcare", "MaâtCare", "maat_care")) }
                .focusable()
                .padding(horizontal = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.maat_care), // Ensure drawable exists
                contentDescription = "MaâtCare Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp)
                // .clip(CircleShape) // If you want circular avatars
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text( // TV Text
                text = "MaâtCare",
                style = MaterialTheme.typography.bodySmall.copy( // TV MaterialTheme
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = Inter
                )
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column( // Standard Column
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onContentClick(ContentItem("maatclass", "MaâtClass", "maat_class")) }
                .focusable()
                .padding(horizontal = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.maat_class), // Ensure drawable exists
                contentDescription = "MaâtClass Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp)
                // .clip(CircleShape) // If you want circular avatars
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text( // TV Text
                text = "MaâtClass",
                style = MaterialTheme.typography.bodySmall.copy( // TV MaterialTheme
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = Inter
                )
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class) // Added OptIn
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF000000, widthDp = 960, heightDp = 100)
@Composable
fun AvatarSectionPreview() {
    MaterialTheme {
        AvatarSection(onContentClick = {})
    }
}