package ai.maatcore.maatcore_android_tv.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.focusable
// import androidx.compose.ui.res.fontResource // Pas nécessaire si FontFamily est bien défini
import androidx.compose.ui.text.TextStyle

import ai.maatcore.maatcore_android_tv.R

import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items

import kotlinx.coroutines.launch

// --- Data Classes and Color Constants ---
val tvBackgroundColor = Color(0xFF0D0D0D)
val tvPrimaryColor = Color(0xFFF5D487)
val tvButtonColor = Color(0xFFE0C28A)
val tvAccentOrange = Color(0xFFFF8C42)

// Définition de la famille de polices Proxima Nova
// ASSUREZ-VOUS QUE CES NOMS CORRESPONDENT EXACTEMENT À VOS FICHIERS RENOMMÉS DANS res/font/
val ProximaNova = FontFamily(
    Font(R.font.proxima_nova_regular, FontWeight.Normal),
    Font(R.font.proxima_nova_semibold, FontWeight.SemiBold)
)


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

// --- Composable Functions ---

@Composable
fun painterResourceFromString(imageName: String?): Painter {
    if (imageName == "maat_logo") return painterResource(id = R.drawable.maat_logo)
    if (imageName == "maat_header") return painterResource(id = R.drawable.maat_header)
    if (imageName == "maat_care") return painterResource(id = R.drawable.maat_care)
    if (imageName == "maat_tv") return painterResource(id = R.drawable.maat_tv)
    if (imageName == "maat_class") return painterResource(id = R.drawable.maat_class)
    if (imageName == "maat_foot") return painterResource(id = R.drawable.maat_foot)
    if (imageName == "maat_flix") return painterResource(id = R.drawable.maat_flix)
    if (imageName == "content_placeholder") return painterResource(id = R.drawable.content_placeholder)


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
            val id = context.resources.getIdentifier(cleanName, "drawable", context.packageName)
            if (id == 0) {
                Log.w("PainterResource", "Resource not found for imageName: $imageName (cleaned: $cleanName). Using placeholder.")
            }
            id
        }
    }
    return if (resourceId != 0) {
        painterResource(id = resourceId)
    } else {
        painterResource(id = R.drawable.content_placeholder)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MaatTvAppScreen(onNavigate: (String) -> Unit) {
    MaterialTheme {
        AndroidTVHomeScreen(onNavigate = onNavigate)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun AndroidTVHomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MenuItem("maattv", "Maat.TV", R.drawable.ic_tv),
        MenuItem("maatflix", "MaâtFlix", R.drawable.ic_movie),
        MenuItem("maatcare", "MaâtCare", R.drawable.ic_health),
        MenuItem("maatclass", "MaâtClass", R.drawable.ic_education),
        MenuItem("maatfoot", "MaâtFoot", R.drawable.ic_sports)
    )

    val featuredContent = FeaturedContent(
        id = "queen_of_maat",
        title = "Reine de Maât",
        tagline = "L'histoire inédite d'une reine puissante",
        imageUrl = "maat_header",
        videoUrl = "https://example.com/video1"
    )

    val contentRows = listOf(
        ContentRow(
            id = "categories_services",
            title = "Nos Services",
            items = listOf(
                ContentItem("service_maat_header", "Bannière Maât", "maat_header"),
                ContentItem("service_maat_care", "Service MaâtCare", "maat_care"),
                ContentItem("service_maat_tv", "Service Maât.TV", "maat_tv"),
                ContentItem("original_maat_tv", "Maât.TV (Image Normale)", "maat_tv"),
                ContentItem("original_maat_care", "MaâtCare (Image Normale)", "maat_care"),
                ContentItem("maat_class", "MaâtClass", "maat_class"),
                ContentItem("maat_foot", "MaâtFoot", "maat_foot"),
                ContentItem("maat_flix", "MaâtFlix", "maat_flix")
            )
        ),
        ContentRow(
            id = "nouveautes",
            title = "Nouveautés",
            items = listOf(
                ContentItem("item_1", "Contenu 1", "content_placeholder"),
                ContentItem("item_2", "Contenu 2", "content_placeholder"),
                ContentItem("item_3", "Contenu 3", "content_placeholder"),
                ContentItem("item_4", "Contenu 4", "content_placeholder"),
                ContentItem("item_5", "Contenu 5", "content_placeholder")
            )
        )
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(tvBackgroundColor)
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            modifier = Modifier.windowInsetsPadding(WindowInsets(0.dp)),
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.Transparent,
                    modifier = Modifier.width(260.dp).fillMaxHeight()
                ) {
                    SideMenu(
                        menuItems = menuItems,
                        onMenuItemClick = { menuItem ->
                            scope.launch {
                                drawerState.setValue(DrawerValue.Closed)
                            }
                            onNavigate(menuItem.id)
                        },
                        backgroundColor = tvBackgroundColor
                    )
                }
            }
        ) {
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
                }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SideMenu(
    menuItems: List<MenuItem>,
    onMenuItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    val titleOriginalBottomPadding = 48.dp
    val itemOriginalVerticalPadding = 12.dp

    val itemFontSize = 16.sp
    val titleBottomPadding = titleOriginalBottomPadding * 0.6f
    val itemVerticalPadding = itemOriginalVerticalPadding * 0.6f


    Column(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(backgroundColor)
            .padding(vertical = 48.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResourceFromString("maat_logo"),
            contentDescription = "Logo MaâtCore",
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(bottom = titleBottomPadding)
        )

        menuItems.forEach { menuItem ->
            Card(
                onClick = { onMenuItemClick(menuItem) },
                colors = CardDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = tvAccentOrange.copy(alpha = 0.2f)
                ),
                shape = CardDefaults.shape(RectangleShape),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = itemVerticalPadding)
                    .focusable()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    menuItem.icon?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = menuItem.title,
                            tint = tvPrimaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(
                        text = menuItem.title,
                        style = TextStyle(
                            color = tvPrimaryColor,
                            fontSize = itemFontSize,
                            fontFamily = ProximaNova, // Application de Proxima Nova
                            fontWeight = FontWeight.Normal // ou FontWeight.SemiBold si votre Proxima Nova le supporte pour ce poids
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun MaatTVHomeContent(
    featuredContent: FeaturedContent,
    contentRows: List<ContentRow>,
    onOpenMenu: () -> Unit,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            val menuWidth = 260.dp
            val gradientExtensionOriginal = 70.dp
            val gradientExtensionNew = gradientExtensionOriginal * 0.8f
            val totalGradientWidth = menuWidth + gradientExtensionNew

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(totalGradientWidth)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                tvBackgroundColor,
                                tvBackgroundColor.copy(alpha = 0.92f),
                                tvBackgroundColor.copy(alpha = 0.66f),
                                tvBackgroundColor.copy(alpha = 0.33f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = with(localDensity) { totalGradientWidth.toPx() }
                        )
                    )
            )

            Image(
                painter = painterResourceFromString(featuredContent.imageUrl),
                contentDescription = featuredContent.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0x77000000),
                                Color(0xDD000000)
                            ),
                            startY = with(localDensity) { (400.dp * 0.3f).toPx() },
                            endY = with(localDensity) { 400.dp.toPx() }
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                tvAccentOrange.copy(alpha = 0.05f),
                                tvAccentOrange.copy(alpha = 0.1f)
                            ),
                            startX = with(localDensity) { (0.8f * 1920.dp.toPx()) * 0.5f },
                            endX = with(localDensity) { (0.8f * 1920.dp.toPx()) }
                        )
                    )
            )

            FeaturedContentHeader(
                featuredContent = featuredContent,
                onOpenMenu = onOpenMenu,
                onWatchClick = {
                    onContentClick(
                        ContentItem(
                            id = featuredContent.id,
                            title = featuredContent.title,
                            imageUrl = featuredContent.imageUrl,
                            videoUrl = featuredContent.videoUrl
                        )
                    )
                }
            )
        }

        TvLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 350.dp),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {
            items(contentRows) { row ->
                ContentRowSection(
                    rowTitle = row.title,
                    contentItems = row.items,
                    onContentClick = onContentClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedContentHeader(
    featuredContent: FeaturedContent,
    onOpenMenu: () -> Unit,
    onWatchClick: () -> Unit
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(top = statusBarPadding)
    ) {
        IconButton(
            onClick = onOpenMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 32.dp, top = 32.dp)
                .focusable()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 260.dp + 48.dp, bottom = 48.dp, end = 48.dp)
                .fillMaxWidth(0.45f)
        ) {
            /*
            Text(
                text = featuredContent.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    fontFamily = ProximaNova
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = featuredContent.tagline,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 18.sp,
                    fontFamily = ProximaNova
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            */

            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.colors(
                    containerColor = tvButtonColor,
                    contentColor = Color(0xFF0D0D0D)
                ),
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 160.dp)
                    .focusable()
            ) {
                Text(
                    text = "Regarder",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        fontFamily = ProximaNova,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun ContentRowSection(
    rowTitle: String,
    contentItems: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = rowTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                color = tvPrimaryColor,
                fontWeight = FontWeight.SemiBold, // Vous pouvez utiliser ProximaNova ici aussi
                fontSize = 28.sp,
                fontFamily = ProximaNova
            ),
            modifier = Modifier.padding(start = 48.dp, bottom = 12.dp)
        )

        TvLazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(contentItems) { item ->
                ContentCard(
                    contentItem = item,
                    onClick = { onContentClick(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContentCard(
    contentItem: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardWidth = 100.dp
    val cardHeight = 150.dp

    Card(
        onClick = onClick,
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = tvAccentOrange.copy(alpha = 0.2f),
            pressedContainerColor = tvAccentOrange.copy(alpha = 0.3f)
        ),
        shape = CardDefaults.shape(RectangleShape),
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .focusable()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResourceFromString(contentItem.imageUrl),
                contentDescription = contentItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            /*
            Box(
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
            Text(
                text = contentItem.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = ProximaNova
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .fillMaxWidth()
            )
            */
        }
    }
}

// --- Previews ---
@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(
    name = "Full Screen Home",
    device = "id:tv_1080p",
    showBackground = true,
    backgroundColor = 0xFF000000,
    showSystemUi = true
)
@Composable
fun AndroidTVHomeScreenFullScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidTVHomeScreen(onNavigate = {})
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF101010, widthDp = 100, heightDp = 150)
@Composable
fun ContentCardPreview() {
    MaterialTheme {
        ContentCard(
            contentItem = ContentItem("prev_card", "Titre Court", "content_placeholder"),
            onClick = {}
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF000000, widthDp = 260, heightDp = 720)
@Composable
fun SideMenuPreview() {
    MaterialTheme {
        SideMenu(
            menuItems = listOf(
                MenuItem("maattv", "Maât.TV", R.drawable.ic_tv),
                MenuItem("maatflix", "MaâtFlix", R.drawable.ic_movie),
                MenuItem("maatcare", "MaâtCare", R.drawable.ic_health)
            ),
            onMenuItemClick = {},
            backgroundColor = tvBackgroundColor
        )
    }
}