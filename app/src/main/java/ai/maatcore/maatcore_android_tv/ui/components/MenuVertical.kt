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
import androidx.compose.ui.graphics.Brush // Import Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Import pour sp
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrangeSolaire
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.InterFamily // Import police Inter
import androidx.compose.ui.text.font.FontWeight // Ajout pour utiliser FontWeight

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

    val targetTextColor = when {
        isSelected -> MaatColorOrSable // Doré quand actif
        isFocused -> MaatColorOrangeSolaire // Orange quand focus
        else -> MaatColorOrSable.copy(alpha = 0.8f) // Doré atténué
    }
    val textColor by animateColorAsState(targetValue = targetTextColor, label = "textColorMenuItem")

    val targetIconColor = when {
        isSelected -> MaatColorOrSable // Icône dorée pour l'état actif
        isFocused -> MaatColorOrangeSolaire // Icône orange pour focus
        else -> MaatColorOrSable.copy(alpha = 0.8f) // Icône dorée normale
    }
    val iconColor by animateColorAsState(targetValue = targetIconColor, label = "iconColorMenuItem")

    val translationX by animateDpAsState(targetValue = if (isFocused || isSelected) 4.dp else 0.dp, label = "translationXMenuItem")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // Hauteur standard pour un item de menu
            .offset(x = translationX)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Centrer les éléments horizontalement
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = iconColor,
            modifier = Modifier.size(20.dp) // Taille icônes 20x20px
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            color = textColor,
            fontSize = 18.sp, // Taille 18px
            fontFamily = InterFamily,
            fontWeight = FontWeight.Light // Caractères plus fins
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
            .background(Color.Black) // Pure black to merge with header
            .padding(top = 8.dp, bottom = 24.dp, start = 16.dp, end = 16.dp) /* Réduction du padding supérieur général */
            .focusRequester(focusRequester)
    ) {
        items(listOf("Logo")) { // Logo Maât.TV
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 0.dp, bottom = 16.dp), // Padding ajusté pour position logo
                contentAlignment = Alignment.CenterStart
            ) {
                MaatBrandHeader()
            }
            Spacer(modifier = Modifier.height(11.dp)) /* Réduction spacer après logo (-30%) */
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
            Spacer(modifier = Modifier.height(6.dp)) // Espacement réduit à 6dp (~30% de moins)
        }
    }

    // Mettre le focus sur la colonne; le système TV gère le focus sur le premier item focusable.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
