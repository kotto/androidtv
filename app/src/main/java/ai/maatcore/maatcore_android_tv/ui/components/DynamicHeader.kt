package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.shape.RoundedCornerShape // Not used
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class HeaderContent(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val actionText: String = "Regarder",
    val onAction: () -> Unit = {}
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DynamicHeader(
    content: HeaderContent,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = content,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "header_animation"
    ) { headerContent ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            // Image de fond
            AsyncImage(
                model = headerContent.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.9f)
                            ),
                            startY = 200f
                        )
                    )
            )
            
            // Floutage artistique à gauche pour le menu
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Contenu textuel
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 48.dp, bottom = 48.dp, end = 48.dp)
            ) {
                Text(
                    text = headerContent.title,
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = headerContent.subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 600.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = headerContent.onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFCB900) // Changed backgroundColor to containerColor for M3
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = headerContent.actionText,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}
