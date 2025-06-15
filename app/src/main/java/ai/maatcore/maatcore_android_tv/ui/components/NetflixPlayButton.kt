package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrangeSolaire

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NetflixPlayButton(
    text: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isPrimary) MaatColorOrangeSolaire else MaatColorNoirProfond.copy(alpha = 0.7f),
        animationSpec = spring(), label = "backgroundColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isPrimary) Color.Black else Color.White,
        animationSpec = spring(), label = "contentColor"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp)),
        colors = ButtonDefaults.colors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            focusedContainerColor = if (isPrimary) MaatColorOrangeSolaire else Color.White.copy(alpha = 0.9f),
            focusedContentColor = if (isPrimary) Color.Black else MaatColorNoirProfond
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isPrimary) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, fontSize = 16.sp)
        }
    }
}
