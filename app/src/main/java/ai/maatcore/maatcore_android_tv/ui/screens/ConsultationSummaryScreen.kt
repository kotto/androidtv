package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

@Composable
fun ConsultationSummaryScreen(navController: NavHostController, summaryText: String? = null) {
    val defaultSummary = "Après analyse de vos symptômes (mal de gorge, fièvre), l'assistant IA suggère de surveiller votre température, de vous reposer et de bien vous hydrater. Si les symptômes persistent ou s'aggravent après 48h, une consultation médicale est recommandée. \n\nRecommandations complémentaires : \n- Gargarisme à l'eau salée tiède. \n- Tisane de thym et miel.\n\nCes informations ne remplacent pas un avis médical professionnel."
    
    val saved = navController.previousBackStackEntry?.savedStateHandle?.get<String>("consult_summary")
    val displaySummary = summaryText ?: saved ?: defaultSummary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Résumé de la consultation",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = MaatColorOrSable,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Permet au contenu de prendre l'espace disponible et d'être scrollable
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
        ) {
            Text(
                text = displaySummary,
                fontSize = 16.sp,
                fontFamily = PoppinsFamily,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Permet le défilement si le texte est long
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack("maatcare", inclusive = false)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaatColorOrSable
            )
        ) {
            Text("Retour à MaâtCare", fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
        }
    }
}
