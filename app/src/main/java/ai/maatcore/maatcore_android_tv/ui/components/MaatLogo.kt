package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.maatcore.maatcore_android_tv.R // Import R for drawable resources
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable

@Composable
fun MaatLogo(
    modifier: Modifier = Modifier,
    size: Float = 48f,
    showText: Boolean = false // Set to false to remove text by default
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.maat_logo), // Use maat_logo.png here
            contentDescription = "Maât Monogram",
            modifier = Modifier.size(size.dp),
            contentScale = ContentScale.Fit
        )
        
        // Removed the if (showText) block to permanently remove "MAÂTCORE" text
    }
}

@Composable
fun MaatBrandHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        MaatLogo(size = 96f, showText = false) // Increased size and explicitly removed text
    }
}
