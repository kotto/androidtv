package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun ConsultationRealDoctorHandoffScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Consultation avec l'assistant IA terminée.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = MaatColorOrSable,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Souhaitez-vous être mis(e) en relation avec un médecin réel pour un avis complémentaire ou préférez-vous consulter le résumé de la session ?",
            fontSize = 18.sp,
            fontFamily = PoppinsFamily,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    // TODO: Implémenter la logique de mise en relation avec un médecin réel
                    // Pour l'instant, naviguer vers un écran placeholder ou un résumé
                    // navController.navigate("real_doctor_call_screen") 
                    // ou navController.navigate("consultation_summary_screen") 
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaatColorOrSable
                )
            ) {
                Text("Parler à un médecin", fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
            }

            Button(
                onClick = {
                    val summary = navController.previousBackStackEntry?.savedStateHandle?.get<String>("consult_summary")
                    navController.currentBackStackEntry?.savedStateHandle?.set("consult_summary", summary)
                    navController.navigate("consultation_summary")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray
                )
            ) {
                Text("Voir le résumé", fontFamily = PoppinsFamily, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = { 
            // Option pour retourner à l'accueil de MaatCare ou à l'écran principal
            navController.popBackStack("maatcare", inclusive = false)
         }) {
            Text("Retour à MaâtCare", color = MaatColorOrSable.copy(alpha = 0.7f), fontFamily = PoppinsFamily)
        }
    }
}
