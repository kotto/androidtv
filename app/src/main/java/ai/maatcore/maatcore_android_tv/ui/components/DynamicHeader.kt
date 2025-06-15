package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.foundation.Image // Import Image
import androidx.compose.ui.res.painterResource // Import painterResource
import ai.maatcore.maatcore_android_tv.R // Import R for drawable resources
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable // Import MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MontserratFamily // Import police Montserrat
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily // Import police Poppins

data class HeaderContent(
    val title: String,
    val subtitle: String,
    val imageUrl: String, // This will now be ignored if using local drawable
    val imageRes: Int? = null, // Optional resource ID for local drawable
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
            // Image de fond - using custom image or default
            Image(
                painter = painterResource(id = content.imageRes ?: R.drawable.maat_header),
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
                                Color.Black.copy(alpha = 0.5f), // Adjust middle alpha for smoother transition
                                Color.Black // Fully opaque black at the bottom
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
                    color = Color(0xFFF5D487), // Texte doré selon spécifications: #F5D487
                    fontSize = 64.sp, // Taille selon spécifications: 64-84px
                    fontFamily = MontserratFamily, // Police Montserrat selon spécifications
                    fontWeight = FontWeight.ExtraBold, // ExtraBold selon spécifications
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = headerContent.subtitle,
                    color = Color(0xFFF5D487).copy(alpha = 0.9f), // Texte doré selon spécifications: #F5D487
                    fontSize = 20.sp, // Taille selon spécifications: 20-32px
                    fontFamily = PoppinsFamily, // Police Poppins selon spécifications
                    fontWeight = FontWeight.SemiBold, // SemiBold selon spécifications
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 600.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = headerContent.onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD4AF37) // Fond doré visible
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .width(160.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = headerContent.actionText,
                        color = Color(0xFF000000), // Texte noir pur
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = PoppinsFamily, // Police Poppins
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
