package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

// TODO: Déplacer vers un fichier de modèle de données
data class VitalParameter(val id: String, val name: String, val value: String, val unit: String, val timestamp: String, val source: String)

@Composable
fun VitalParametersScreen(navController: NavHostController) {
    // TODO: Remplacer par de vraies données, potentiellement depuis un ViewModel
    val vitalParameters = listOf(
        VitalParameter("vp1", "Tension Artérielle", "120/80", "mmHg", "10/06/2025 08:30", "Bluetooth (Omron)"),
        VitalParameter("vp2", "Saturation Oxygène", "98", "%", "10/06/2025 08:32", "Bluetooth (OxyWatch)"),
        VitalParameter("vp3", "Fréquence Cardiaque", "75", "bpm", "09/06/2025 15:00", "Manuelle")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 48.dp)
    ) {
        Text(
            text = "Mes Paramètres Vitaux",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = MaatColorOrSable,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { /* TODO: Naviguer vers l'écran de connexion Bluetooth */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaatColorOrSable)
            ) {
                Icon(Icons.Default.BluetoothSearching, contentDescription = "Connecter Appareil", tint = MaatColorNoirProfond)
                Spacer(Modifier.width(8.dp))
                Text("Connecter Appareil", fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
            }
            Button(
                onClick = { /* TODO: Naviguer vers l'écran de saisie manuelle */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaatColorOrSable.copy(alpha = 0.7f))
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = "Saisie Manuelle", tint = MaatColorNoirProfond)
                Spacer(Modifier.width(8.dp))
                Text("Saisie Manuelle", fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
            }
        }

        if (vitalParameters.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aucun paramètre vital enregistré.", 
                    color = Color.Gray, 
                    fontSize = 18.sp, 
                    fontFamily = PoppinsFamily
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(vitalParameters) { param ->
                    VitalParameterCard(parameter = param)
                }
            }
        }
    }
}

@Composable
fun VitalParameterCard(parameter: VitalParameter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${parameter.name}: ${parameter.value} ${parameter.unit}",
                fontWeight = FontWeight.SemiBold, 
                color = MaatColorOrSable, 
                fontSize = 18.sp, 
                fontFamily = PoppinsFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Enregistré le: ${parameter.timestamp}", 
                color = Color.White.copy(alpha = 0.8f), 
                fontSize = 14.sp, 
                fontFamily = PoppinsFamily
            )
            Text(
                text = "Source: ${parameter.source}", 
                color = Color.White.copy(alpha = 0.7f), 
                fontSize = 12.sp, 
                fontFamily = PoppinsFamily
            )
        }
    }
}
