package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorVertSante
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

@Composable
fun ConsultationSymptomInputScreen(navController: NavHostController) {
    val context = LocalContext.current
    var transcribedText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    // Créer et mémoriser SpeechRecognizer
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
        }
    }

    // Configurer le listener
    DisposableEffect(speechRecognizer) {
        val listener = object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { }
            override fun onError(error: Int) {
                Log.e("SpeechRec", "Erreur $error")
                isRecording = false
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    transcribedText = matches.first()
                }
                isRecording = false
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    transcribedText = partial.first()
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer?.setRecognitionListener(listener)
        onDispose {
            speechRecognizer?.setRecognitionListener(null)
            speechRecognizer?.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Décrivez vos symptômes",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = MaatColorOrSable,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Placeholder pour la zone de texte transcrit
        if (transcribedText.isNotEmpty()) {
            Text(
                text = transcribedText,
                fontSize = 18.sp,
                fontFamily = PoppinsFamily,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .background(Color.DarkGray.copy(alpha = 0.3f))
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (speechRecognizer == null) {
                    transcribedText = "La reconnaissance vocale n'est pas disponible sur cet appareil."
                    return@Button
                }

                if (!isRecording) {
                    // Démarrer l'écoute
                    transcribedText = ""
                    speechRecognizer.startListening(recognizerIntent)
                    isRecording = true
                } else {
                    // Arrêter l'écoute
                    speechRecognizer.stopListening()
                    isRecording = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) MaatColorVertSante.copy(alpha = 0.8f) else MaatColorVertSante,
                contentColor = MaatColorNoirProfond
            ),
            modifier = Modifier.size(120.dp) // Bouton plus grand et rond
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Enregistrer les symptômes",
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer {
                        if (isRecording) {
                            rotationZ = (System.currentTimeMillis() % 3600) / 10f // petite rotation animée
                        }
                    }
            )
        }

        Text(
            text = if (isRecording) "Enregistrement en cours..." else "Appuyez pour parler",
            fontSize = 16.sp,
            fontFamily = PoppinsFamily,
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Pousse le bouton Suivant en bas

        Button(
            onClick = {
                navController.navigate("consultation_ai_avatar")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaatColorOrSable.copy(alpha = 0.7f)
            ),
            enabled = transcribedText.isNotEmpty() // Actif seulement si du texte est présent
        ) {
            Text("Suivant", fontFamily = PoppinsFamily, color = MaatColorNoirProfond)
        }
    }
}
