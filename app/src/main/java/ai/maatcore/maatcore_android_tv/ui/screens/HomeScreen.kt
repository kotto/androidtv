package ai.maatcore.maatcore_android_tv.ui.screens

// Imports Compose standard (inchangés ou déjà vérifiés)
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // Ajout pour le texte centré
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Imports spécifiques à Android TV Material 3 et Foundation
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme // Utiliser le MaterialTheme de TV
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.Surface // Surface de TV pour une meilleure intégration du thème
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import androidx.tv.foundation.ExperimentalTvFoundationApi // TV foundation experimental APIs
import androidx.tv.foundation.lazy.list.TvLazyColumn // Alternative pour LazyColumn optimisée pour TV
import androidx.tv.foundation.lazy.list.TvLazyRow // Alternative pour LazyRow optimisée pour TV
import androidx.tv.foundation.lazy.list.items // items pour TvLazyRow/Column
import androidx.compose.foundation.focusable // focusable modifier from standard compose foundation

// Import de ressources de l'application
import ai.maatcore.maatcore_android_tv.R
import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn

// Import Coroutines
import kotlinx.coroutines.launch

// Data models (inchangés)
data class MenuItem(
    val id: String,
    val title: String,
    val icon: Int? = null
)

data class ContentItem(
    val id: String,
    val title: String,
    val imageUrl: String, // Pourrait être @DrawableRes Int si local, ou String pour URL
    val description: String = ""
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
    val imageUrl: String // Pourrait être @DrawableRes Int
)

// Helper function to load painter (adapté pour potentiellement utiliser Coil plus tard pour les URLs)
@androidx.compose.runtime.Composable
fun painterResourceFromString(imageName: String?): Painter {
    // Pour l'instant, on garde la logique de ressources locales
    // Pour une app TV réelle avec des URLs, utiliser Coil:
    // implementation("io.coil-kt:coil-compose:2.6.0")
    // return rememberAsyncImagePainter(model = imageName, placeholder = painterResource(R.drawable.content_placeholder), error = painterResource(R.drawable.content_placeholder))

    println("Debug: Loading image: $imageName")
    if (imageName == "maat_header") { // Cas spécifique pour l'en-tête, peut-être à généraliser
        return painterResource(id = R.drawable.maat_header)
    }

    val context = LocalContext.current
    val resourceId = androidx.compose.runtime.remember(imageName) {
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
        painterResource(id = R.drawable.content_placeholder) // Fallback
    }
}

// Thème racine pour l'écran TV (ou l'application entière)
@OptIn(ExperimentalTvMaterial3Api::class)
@androidx.compose.runtime.Composable
fun MaatTvAppScreen(onNavigate: (String) -> Unit) {
    MaterialTheme { // Utilise androidx.tv.material3.MaterialTheme
        AndroidTVHomeScreen(onNavigate = onNavigate)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@androidx.compose.runtime.Composable
fun AndroidTVHomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MenuItem("maattv", "Maât.TV", R.drawable.ic_tv),
        MenuItem("maatflix", "MaâtFlix", R.drawable.ic_movie),
        MenuItem("maatcare", "MaâtCare", R.drawable.ic_health),
        MenuItem("maatclass", "MaâtClass", R.drawable.ic_education),
        MenuItem("maatfoot", "MaâtFoot", R.drawable.ic_sports)
    )

    // TODO: Remplacer les URLs par des références @DrawableRes ou intégrer Coil pour le chargement réseau
    val featuredContent = FeaturedContent(
        id = "queen_of_maat",
        title = "QUEEN OF MAÄT",
        tagline = "The untold story of a powerful queen",
        imageUrl = "maat_header" // Utilise le nom qui sera résolu par painterResourceFromString
    )

    val contentRows = listOf(
        ContentRow(
            id = "nouveautes",
            title = "Nouveautés",
            items = listOf(
                ContentItem("oppenheimer", "OPPENHEIMER", "content_placeholder"), // Exemples d'images placeholder
                ContentItem("avatar", "AVATAR", "content_placeholder"),
                ContentItem("johnwick", "JOHN WICK", "content_placeholder"),
                ContentItem("equalizer", "EQUALIZER 3", "content_placeholder"),
            )
        ),
        ContentRow(
            id = "categories",
            title = "Catégories Principales",
            items = listOf(
                ContentItem("action", "Action", "content_placeholder"),
                ContentItem("comedy", "Comédie", "content_placeholder"),
            )
        )
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // Surface racine pour appliquer le fond du thème TV
    Surface(modifier = modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SideMenu(
                    menuItems = menuItems,
                    onMenuItemClick = { menuItem ->
                        scope.launch {
                            drawerState.setValue(DrawerValue.Closed)
                        }
                        onNavigate(menuItem.id) // Naviguer vers la route de l'item
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)) // Fond du drawer
                )
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
                    // Gérer le clic sur un contenu, ex: naviguer vers une page de détails
                    onNavigate("details/${contentItem.id}")
                }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@androidx.compose.runtime.Composable
fun SideMenu(
    menuItems: List<MenuItem>,
    onMenuItemClick: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .width(280.dp) // Un peu plus large pour TV
            .fillMaxHeight()
            .padding(vertical = 48.dp, horizontal = 16.dp) // Plus de padding vertical
    ) {
        Text(
            text = "MAÄT.TV",
            style = MaterialTheme.typography.headlineSmall.copy( // Utiliser les styles du thème TV
                color = MaterialTheme.colorScheme.primary // Couleur thématique
            ),
            modifier = Modifier
                .padding(start = 16.dp, bottom = 32.dp)
                .focusable() // Le titre peut être focusable si interactif
        )

        menuItems.forEach { menuItem ->
            Card( // Card est mieux adapté avec CardDefaults et gère le clic
                onClick = { onMenuItemClick(menuItem) },
                colors = CardDefaults.colors( // Style pour l'état normal / focus / press
                    containerColor = Color.Transparent, // Transparent par défaut
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), // Feedback visuel pour le focus
                    pressedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp) // Espacement entre les items
                    .focusable() // Chaque item doit être focusable
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp), // Padding interne
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    menuItem.icon?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = menuItem.title,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, // Couleur thématique
                            modifier = Modifier.size(28.dp) // Icônes plus grandes
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(20.dp)) // Plus d'espacement
                    }
                    Text(
                        text = menuItem.title,
                        style = MaterialTheme.typography.bodyLarge.copy( // Style de texte TV
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@androidx.compose.runtime.Composable
fun MaatTVHomeContent(
    featuredContent: FeaturedContent,
    contentRows: List<ContentRow>,
    onOpenMenu: () -> Unit,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // TvLazyColumn est optimisé pour la performance et la gestion du focus sur TV
    TvLazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 48.dp) // Espace en bas pour ne pas coller au bord
    ) {
        // Featured content header
        item {
            FeaturedContentHeader(
                featuredContent = featuredContent,
                onOpenMenu = onOpenMenu,
                onWatchClick = { onContentClick(ContentItem(featuredContent.id, featuredContent.title, featuredContent.imageUrl)) }
            )
        }

        // Content rows
        items(contentRows) { row ->
            ContentRowSection(
                rowTitle = row.title,
                contentItems = row.items,
                onContentClick = onContentClick
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@androidx.compose.runtime.Composable
fun FeaturedContentHeader(
    featuredContent: FeaturedContent,
    onOpenMenu: () -> Unit,
    onWatchClick: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp) // Un peu plus haut pour un effet cinématique
    ) {
        Image(
            painter = painterResourceFromString(featuredContent.imageUrl),
            contentDescription = featuredContent.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Gradient pour la lisibilité du texte
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x33000000), Color(0xCC000000)),
                        startY = 400f // Démarrer le gradient plus bas
                    )
                )
        )
        // Flou à gauche pour intégration du menu (optionnel, peut être lourd en performance)
        // Box(
        // modifier = Modifier
        // .fillMaxHeight()
        // .width(280.dp) // Correspond à la largeur du menu
        // .background(
        // Brush.horizontalGradient(
        // colors = listOf(MaterialTheme.colorScheme.background.copy(alpha = 0.7f), Color.Transparent)
        // )
        // )
        // .blur(radiusX = 10.dp, radiusY = 10.dp) // Flou plus prononcé
        // )

        IconButton(
            onClick = onOpenMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(32.dp) // Plus de padding pour accessibilité
                .focusable()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "Menu",
                tint = Color.White, // Assurer un bon contraste
                modifier = Modifier.size(32.dp) // Icône plus grande
            )
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 48.dp, end = 48.dp) // Padding sur tous les côtés
                .fillMaxWidth() // Permettre au texte de prendre plus de largeur si besoin
        ) {
            Text(
                text = featuredContent.title.uppercase(), // Majuscules pour un style plus "titre"
                style = MaterialTheme.typography.displaySmall.copy( // Style d'affichage TV
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = featuredContent.tagline,
                style = MaterialTheme.typography.titleMedium.copy( // Style pour le slogan
                    color = Color.White.copy(alpha = 0.8f) // Un peu moins proéminent
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.colors( // Couleurs thématiques
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .widthIn(min = 200.dp) // Bouton plus large
                    .height(56.dp) // Bouton plus haut
                    .focusable()
            ) {
                Text(
                    text = "Regarder",
                    style = MaterialTheme.typography.labelLarge // Style pour le texte du bouton
                )
            }
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@androidx.compose.runtime.Composable
fun ContentRowSection(
    rowTitle: String,
    contentItems: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp) // Plus d'espacement vertical
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 12.dp), // Padding standard pour les titres de section
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rowTitle,
                style = MaterialTheme.typography.headlineSmall.copy( // Style de titre de section TV
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            // Optionnel: "Voir tout" si la ligne est très longue et que vous avez une page dédiée
            // Text(
            // text = "Voir tout >",
            // style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            // modifier = Modifier.clickable { /* Handle see all */ }.focusable()
            // )
        }

        // TvLazyRow est optimisé pour la performance et la gestion du focus sur TV
        TvLazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 40.dp), // Moins de padding si les cartes ont déjà leur propre padding
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp) // Espacement entre les cartes
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

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@androidx.compose.runtime.Composable
fun ContentCard(
    contentItem: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Les cartes TV sont typiquement plus grandes
    val cardWidth = 180.dp // Augmenter la largeur
    val cardHeight = 270.dp // Augmenter la hauteur (ratio ~2:3 ou 16:9 pour des images de films/séries)

    Card(
        onClick = onClick,
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // Couleur de fond pour la carte
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f), // Ajuster pour le focus
            pressedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        // scale = CardDefaults.scale(focusedScale = 1.05f), // Effet de zoom au focus (optionnel)
        // border = CardDefaults.border(focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))), // Bordure au focus
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .focusable()
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResourceFromString(contentItem.imageUrl), // Devrait être remplacé par Coil pour les URLs
                contentDescription = contentItem.title,
                contentScale = ContentScale.Crop, // Crop pour remplir la carte
                modifier = Modifier.fillMaxSize()
            )
            // Scrim pour améliorer la lisibilité du titre sur l'image
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f) // Le scrim prend 40% de la hauteur en bas
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xA0000000)) // Noir semi-transparent
                        )
                    )
            )
            Text(
                text = contentItem.title,
                style = MaterialTheme.typography.titleSmall.copy( // Style de texte pour le titre de la carte
                    color = Color.White
                ),
                maxLines = 2, // Permettre deux lignes pour des titres plus longs
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp) // Padding interne pour le texte
                    .fillMaxWidth()
            )
        }
    }
}


@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF000000)
@androidx.compose.runtime.Composable
fun AndroidTVHomeScreenPreview() {
    MaterialTheme { // Assurez-vous d'utiliser androidx.tv.material3.MaterialTheme pour la preview
        AndroidTVHomeScreen(onNavigate = {})
    }
}

// Preview pour une carte individuelle
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF101010, widthDp = 200, heightDp = 300)
@androidx.compose.runtime.Composable
fun ContentCardPreview() {
    MaterialTheme {
        ContentCard(
            contentItem = ContentItem("prev", "Titre de l'émission", "content_placeholder"),
            onClick = {}
        )
    }
}

// Preview pour le menu latéral
@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF000000, widthDp=300, heightDp=720)
@androidx.compose.runtime.Composable
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