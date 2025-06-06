package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrangeSolaire
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
// import androidx.tv.material3.ExperimentalTvMaterial3Api supprimé

data class MenuItemData( // Renommé pour éviter confusion avec le composable MenuItem
    val id: String,
    val title: String,
    val icon: ImageVector, // TODO: Remplacer par des icônes personnalisées "lignes épaisses minimalistes dorées"
    val route: String
)

@Composable
fun MenuItem( // Composable pour un seul item de menu
    item: MenuItemData,
    isSelected: Boolean, // Pour l'état actif/sélectionné
    modifier: Modifier = Modifier // onItemSelected est géré par le parent via clickable/focus
) {
    var isFocused by remember { mutableStateOf(false) }

    // targetBackgroundColor, targetTextColor, targetIconColor, translationX, glowElevation will use theme colors or derived values

    val targetBackgroundColor = when {
        isSelected -> MaatColorOrSable.copy(alpha = 0.2f) // Fond doré léger pour l'état actif
        isFocused -> MaatColorOrangeSolaire.copy(alpha = 0.15f) // Fond orange léger pour focus
        else -> Color.Transparent
    }
    val backgroundColor by animateColorAsState(targetValue = targetBackgroundColor, label = "backgroundColorMenuItem")

    val targetTextColor = when {
        isSelected -> MaatColorOrSable // Texte doré pour l'état actif
        isFocused -> MaatColorOrangeSolaire // Texte orange pour focus
        else -> MaatColorOrSable.copy(alpha = 0.8f) // Texte doré normal
    }
    val textColor by animateColorAsState(targetValue = targetTextColor, label = "textColorMenuItem")

    val targetIconColor = when {
        isSelected -> MaatColorOrSable // Icône dorée pour l'état actif
        isFocused -> MaatColorOrangeSolaire // Icône orange pour focus
        else -> MaatColorOrSable.copy(alpha = 0.8f) // Icône dorée normale
    }
    val iconColor by animateColorAsState(targetValue = targetIconColor, label = "iconColorMenuItem")

    val translationX by animateDpAsState(targetValue = if (isFocused || isSelected) 4.dp else 0.dp, label = "translationXMenuItem")

    // Configuration de l'effet de Glow
    val glowElevation by animateDpAsState(
        targetValue = if (isFocused || isSelected) 8.dp else 0.dp, // Élévation pour le glow
        label = "glowElevationMenuItem"
    )

    // Utiliser les couleurs du thème pour la lueur
    val activeGlowColor = MaterialTheme.colorScheme.secondary // MaatColorCuivreAncien
    val focusedGlowColor = MaterialTheme.colorScheme.primary  // MaatColorOrangeSolaire (pour "glow en hover")

    val currentGlowColor = when {
        isSelected -> activeGlowColor
        isFocused -> focusedGlowColor
        else -> Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Hauteur standard pour un item de menu
            .offset(x = translationX)
            .shadow(
                elevation = glowElevation,
                shape = RoundedCornerShape(4.dp), // Légèrement arrondi pour la lueur
                clip = false, // Important pour que la lueur dépasse
                ambientColor = currentGlowColor, // Couleur de la lueur ambiante
                spotColor = currentGlowColor     // Couleur de la lueur directionnelle
            )
            .background(backgroundColor) // Appliquer le fond APRÈS l'ombre (glow)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = iconColor,
            modifier = Modifier.size(24.dp) // Taille standard pour icônes de menu
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            color = textColor,
            style = MaterialTheme.typography.labelLarge // Style pour les boutons en Material 3
        )
    }
}

@Composable
fun MenuVertical(
    navController: NavHostController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    val menuItems = remember {
        listOf(
            MenuItemData("maattv", "Maât.TV", Icons.Default.PlayArrow, "maattv"),
            MenuItemData("maatcare", "MaâtCare", Icons.Default.FavoriteBorder, "maatcare"),
            MenuItemData("maatfoot", "MaâtFoot", Icons.Default.Search, "maatfoot"),
            MenuItemData("maatclass", "MaâtClass", Icons.Default.Person, "maatclass"),
            MenuItemData("maattube", "MaâtTube", Icons.Default.Home, "maattube")
        )
    }

    val focusRequester = remember { FocusRequester() }
    // Déterminer l'ID de l'item sélectionné basé sur currentRoute
    // Cela permet de mettre à jour selectedItemId si currentRoute change de l'extérieur
    val selectedItemId = remember(currentRoute, menuItems) {
        menuItems.firstOrNull { it.route == currentRoute }?.id ?: menuItems.firstOrNull()?.id
    }

    LazyColumn(
        modifier = modifier
            .width(240.dp) // Largeur spécifiée
            .fillMaxHeight()
            .background(MaatColorNoirProfond) // Fond noir type Netflix
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .focusRequester(focusRequester)
    ) {
        items(listOf("Logo")) { // Logo Maât.TV
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 24.dp, bottom = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                MaatBrandHeader()
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        items(menuItems, key = { it.id }) { itemData ->
            MenuItem(
                item = itemData,
                isSelected = itemData.id == selectedItemId,
                modifier = Modifier.clickable {
                    // Mettre à jour selectedItemId n'est plus nécessaire ici si currentRoute est la source de vérité
                    // navController.navigate devrait mettre à jour currentRoute, qui mettra à jour selectedItemId
                    if (itemData.route != currentRoute) { // Naviguer seulement si la route est différente
                        navController.navigate(itemData.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Mettre le focus sur la colonne; le système TV gère le focus sur le premier item focusable.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
