package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.shape.RoundedCornerShape // Not used directly, RectangleShape is used
// import androidx.compose.material.Card // Not used, Box is used as card
import androidx.compose.material3.Text // Changed to Material 3 Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ai.maatcore.maatcore_android_tv.data.ContentItem
import ai.maatcore.maatcore_android_tv.ui.theme.*

@Composable
fun ContentCard(
    item: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFocus: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1f, label = "scale") // Scale 1.05 selon la charte
    
    Box(
        modifier = modifier
            .width(130.dp) // Nouvelle taille réduite
            .height(195.dp) // Ratio 2:3
            .scale(scale)
            .focusable()
            .onFocusChanged { focusState ->
                val wasFocused = isFocused
                isFocused = focusState.isFocused
                if (focusState.isFocused && !wasFocused) {
                    onFocus?.invoke()
                }
            }
            .clickable { onClick() }
            .shadow(
                elevation = if (isFocused) 12.dp else 0.dp, // Glow orange selon la charte
                shape = RectangleShape, // AUCUN arrondi selon la charte
                clip = false,
                ambientColor = if (isFocused) MaatColorOrangeSolaire else Color.Transparent,
                spotColor = if (isFocused) MaatColorOrangeSolaire else Color.Transparent
            )
    ) {
        // Image de fond
        AsyncImage(
            model = item.imageUrl, // Retour à l'utilisation unique de imageUrl
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Overlay gradient pour le texte
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaatColorNoirProfond.copy(alpha = 0.8f)
                        ),
                        startY = 150f
                    )
                )
        )
        
        // Texte superposé bas selon la charte
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                color = MaatColorOrSable,
                fontSize = 12.sp, // Texte plus petit
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            item.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    color = MaatColorCuivreClair.copy(alpha = 0.9f),
                    fontSize = 10.sp, // Texte plus petit
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // Indicateur de focus sans arrondi
        if (isFocused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(3.dp, MaatColorOrangeSolaire, RectangleShape) // Aucun arrondi selon la charte
            )
        }
    }
}
