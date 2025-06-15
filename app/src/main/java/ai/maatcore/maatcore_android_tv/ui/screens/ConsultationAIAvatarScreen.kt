@file:OptIn(ExperimentalMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme // Material 3
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.R // Assurez-vous d'avoir un placeholder pour l'avatar
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorVertSante
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import android.speech.tts.TextToSpeech
import androidx.hilt.navigation.compose.hiltViewModel
import ai.maatcore.maatcore_android_tv.viewmodel.AiConsultationViewModel
import java.util.Locale

// TODO: Déplacer vers un fichier de modèle de données si nécessaire
data class AiQuestion(val id: String, val text: String, val options: List<String>? = null)

@Composable
fun ConsultationAIAvatarScreen(navController: NavHostController, viewModel: AiConsultationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    // Simulation d'une liste de questions de l'IA
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val questions = remember {
        listOf(
            AiQuestion("q1", "Bonjour ! Pour mieux comprendre, avez-vous de la fièvre ?", listOf("Oui", "Non")),
            AiQuestion("q2", "Depuis combien de temps ressentez-vous ces symptômes ?", listOf("Moins d'un jour", "1-3 jours", "Plus de 3 jours")),
            AiQuestion("q3", "Avez-vous d'autres symptômes à signaler ?") // Question ouverte
        )
    }
    var userAnswer by remember { mutableStateOf("") }
    val currentQuestion = questions[currentQuestionIndex]

    // TextToSpeech
    val tts = remember {
        TextToSpeech(context) { /* onInit */ }
    }
    LaunchedEffect(Unit) {
        tts.setLanguage(Locale.FRANCE)
    }

    // Observe response from OpenAI
    val aiAnswer by viewModel.aiAnswer.collectAsState()

    LaunchedEffect(aiAnswer) {
        if (aiAnswer.isNotEmpty()) {
            tts.speak(aiAnswer, TextToSpeech.QUEUE_FLUSH, null, "ai_answer")
        }
    }

    LaunchedEffect(currentQuestionIndex) {
        tts.speak(currentQuestion.text, TextToSpeech.QUEUE_FLUSH, null, "question_${currentQuestion.id}")
    }

    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section Avatar IA (Placeholder)
        Image(
            painter = painterResource(id = R.drawable.ic_ai_doctor_placeholder), // Remplacez par votre image d'avatar
            contentDescription = "Avatar IA Docteur",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaatColorOrSable.copy(alpha = 0.2f)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Zone de dialogue avec bulles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Question bulle verte claire gauche
            Box(
                modifier = Modifier
                    .background(MaatColorVertSante.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .align(Alignment.Start)
            ) {
                Text(
                    text = currentQuestion.text,
                    color = MaatColorVertSante,
                    fontFamily = PoppinsFamily,
                    fontSize = 18.sp
                )
            }

            if (userAnswer.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color.DarkGray.copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .align(Alignment.End)
                ) {
                    Text(
                        text = userAnswer,
                        color = Color.White,
                        fontFamily = PoppinsFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Zone de réponse de l'utilisateur
        if (currentQuestion.options != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                currentQuestion.options.forEach { option ->
                    Button(
                        onClick = {
                            userAnswer = option
                            // Avancer
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex += 1
                                userAnswer = ""
                                // TODO: Envoyer la question/answer au backend AI
                                // viewModel.askAi(currentQuestion.text, userAnswer)
                            } else {
                                // Construire un résumé basique
                                val summary = "Symptômes initiaux décrits par l'utilisateur. Réponses: fièvre=$userAnswer ... (exemple)"
                                navController.currentBackStackEntry?.savedStateHandle?.set("consult_summary", summary)
                                navController.navigate("consultation_handoff")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaatColorOrSable)
                    ) {
                        Text(option, fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
                    }
                }
            }
        } else {
            // Champ de texte pour les questions ouvertes (simplifié pour l'instant)
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text("Votre réponse", fontFamily = PoppinsFamily) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaatColorOrSable,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = MaatColorOrSable
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (userAnswer.isNotBlank()) {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex += 1
                            userAnswer = ""
                            // TODO: Envoyer la question/answer au backend AI
                            // viewModel.askAi(currentQuestion.text, userAnswer)
                        } else {
                            val summary = "Symptômes initiaux décrits par l'utilisateur. Réponse libre: $userAnswer"
                            navController.currentBackStackEntry?.savedStateHandle?.set("consult_summary", summary)
                            navController.navigate("consultation_handoff")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaatColorOrSable),
                enabled = userAnswer.isNotBlank()
            ) {
                Text("Envoyer", fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bouton pour quitter ou options supplémentaires (optionnel)
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Quitter la consultation", color = MaatColorOrSable.copy(alpha = 0.7f), fontFamily = PoppinsFamily)
        }
    }
}
