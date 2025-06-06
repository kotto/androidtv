package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button // M3 Button
import androidx.compose.material3.ButtonDefaults // M3 ButtonDefaults
import androidx.compose.material3.Text // M3 Text
// import androidx.compose.material.* // Removed M2 wildcard import
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ai.maatcore.maatcore_android_tv.ui.theme.*
import ai.maatcore.maatcore_android_tv.R

data class HeroContent(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageUrl: String? = null,
    val imageResource: Int? = null,
    val actionText: String = "Regarder",
    val onAction: () -> Unit = {}
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HeroHeader(
    content: HeroContent,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = content,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "hero_animation"
    ) { heroContent ->
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Section principale avec image et texte
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp) // Réduit pour faire place aux cartes
            ) {
                // Image de fond plein écran 16:9
                if (heroContent.imageResource != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = heroContent.imageResource),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (heroContent.imageUrl != null) {
                    AsyncImage(
                        model = heroContent.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Gradient overlay pour améliorer la lisibilité
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaatColorNoirProfond.copy(alpha = 0.3f),
                                    MaatColorNoirProfond.copy(alpha = 0.8f)
                                ),
                                startY = 100f
                            )
                        )
                )
                
                // Floutage artistique à gauche pour le menu (selon la charte)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaatColorNoirProfond.copy(alpha = 0.9f),
                                    MaatColorNoirProfond.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                
                // Contenu textuel superposé en bas à gauche (repositionné selon la demande)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart) // Alignement en bas à gauche
                        .padding(start = 80.dp, bottom = 80.dp, end = 80.dp) // Padding réduit
                        .widthIn(max = 600.dp)
                ) {
                    // Description (Texte courant selon la charte)
                    Text(
                        text = heroContent.description,
                        color = MaatColorOrSable.copy(alpha = 0.9f),
                        fontSize = 18.sp, // Texte courant selon la charte
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Bouton call-to-action selon la charte
                    Button(
                        onClick = heroContent.onAction,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaatColorCuivreClair, // Fond #E0C28A selon la charte
                            contentColor = MaatColorNoirProfond // Texte noir selon la charte
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 140.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = heroContent.actionText,
                            fontSize = 18.sp, // Boutons selon la charte
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
            
            // Section des cartes jacquettes en bas
            HeroMovieCards(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaatColorNoirProfond)
                    .padding(horizontal = 240.dp, vertical = 32.dp)
            )
        }
    }
}

@Composable
fun HeroMovieCards(
    modifier: Modifier = Modifier
) {
    val movieCards = listOf(
        "OPPENHEIMER" to "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=200&h=300&fit=crop",
        "AVATAR" to "https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=200&h=300&fit=crop",
        "JOHN WICK" to "https://images.unsplash.com/photo-1489599735734-79b4169c2a78?w=200&h=300&fit=crop",
        "EQUALIZER 3" to "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=200&h=300&fit=crop",
        "CREED III" to "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=200&h=300&fit=crop",
        "SUPER MARIO BROS" to "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=200&h=300&fit=crop",
        "MaâtCare" to "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=200&h=300&fit=crop"
    )
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacement ajusté selon l'image
    ) {
        items(movieCards) { (title, imageUrl) ->
            Box(
                modifier = Modifier
                    .width(140.dp) // Taille ajustée selon l'image
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay avec titre
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaatColorNoirProfond.copy(alpha = 0.8f)
                                ),
                                startY = 120f
                            )
                        )
                )
                
                Text(
                    text = title,
                    color = MaatColorOrSable,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QueenOfMaatHero(
    onWatchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val heroContent = HeroContent(
        title = "", // Titre supprimé selon la demande
        subtitle = "A Journey of Tradition and Culture",
        description = "The untold story of a powerful queen",
        imageResource = R.drawable.maat_header, // Utiliser l'image locale
        actionText = "Regarder",
        onAction = onWatchClick
    )
    
    // Version simplifiée sans HeroMovieCards
    AnimatedContent(
        targetState = heroContent,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "hero_animation"
    ) { content ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(450.dp)
        ) {
            if (content.imageResource != null) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = content.imageResource),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaatColorNoirProfond.copy(alpha = 0.3f),
                                MaatColorNoirProfond.copy(alpha = 0.8f)
                            ),
                            startY = 100f
                        )
                    )
            )
            
            // Floutage artistique à gauche
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaatColorNoirProfond.copy(alpha = 0.9f),
                                MaatColorNoirProfond.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Contenu textuel
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 80.dp, bottom = 80.dp, end = 80.dp)
                    .widthIn(max = 600.dp)
            ) {
                Text(
                    text = content.description,
                    color = MaatColorOrSable.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = content.onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaatColorCuivreClair, // Changed backgroundColor to containerColor
                        contentColor = MaatColorNoirProfond
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 140.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = content.actionText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}
