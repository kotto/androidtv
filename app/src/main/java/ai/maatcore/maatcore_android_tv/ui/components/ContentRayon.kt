package ai.maatcore.maatcore_android_tv.ui.components

import android.util.Log // AJOUTÉ
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon // Changed to Material 3
import androidx.compose.material3.Text // Changed to Material 3
import androidx.compose.material3.TextButton // Changed to Material 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.ui.theme.*

@Composable
fun ContentRayon(
    title: String,
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit,
    onItemFocus: ((ContentItem) -> Unit)? = null,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Log.d("ContentRayon", "Composing rayon: '$title', items count: ${items.size}") // AJOUTÉ
    Column(modifier = modifier.fillMaxWidth()) {
        // En-tête du rayon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp), // Augmenté le padding vertical de 8.dp à 16.dp
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaatColorOrangeSolaire, // Titre section : H2, accent orange selon la charte
                fontSize = 48.sp, // H2 selon la charte
                fontWeight = FontWeight.SemiBold
            )
            
            onSeeAllClick?.let {
                TextButton(onClick = it) {
                    Text(
                        text = "Voir tout >",
                        color = MaatColorCuivreClair,
                        fontSize = 18.sp, // Texte courant selon la charte
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Liste horizontale des contenus
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacement réduit pour 7 cartes par ligne
        ) {
            items(items) { item ->
                // Réduire la taille des cartes pour afficher 7 par ligne
                Box(modifier = Modifier.width(140.dp)) { // Largeur réduite
                    ContentCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onFocus = onItemFocus?.let { { onItemFocus(item) } },
                        modifier = Modifier
                            .width(130.dp) // Largeur réduite
                            .height(195.dp) // Hauteur proportionnelle (ratio 2:3)
                    )
                }
            }
        }
    }
}
