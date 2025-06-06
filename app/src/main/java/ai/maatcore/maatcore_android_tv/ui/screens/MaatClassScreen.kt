package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text // Changed to Material 3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MaatClassScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bienvenue sur Maât.Class", modifier = Modifier.padding(16.dp))
    }
}
