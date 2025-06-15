package ai.maatcore.maatcore_android_tv.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorBleuCeruleen
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import kotlinx.coroutines.launch

@Composable
fun CourseDetailScreen(navController: NavHostController, courseId: String?) {
    // Fake fetch course by id
    val courseTitle = "Titre du cours $courseId"
    val courseDescription = "Description détaillée du cours $courseId. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt.".repeat(3)

    val tts = remember {
        TextToSpeech(navController.context) { }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaatColorNoirProfond)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero image
        Image(
            painter = painterResource(id = R.drawable.ic_course_placeholder),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                courseTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = MaatColorBleuCeruleen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                courseDescription,
                fontSize = 16.sp,
                fontFamily = PoppinsFamily,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                coroutineScope.launch {
                    tts.speak("Bienvenue dans $courseTitle", TextToSpeech.QUEUE_FLUSH, null, "intro")
                }
            }) {
                Text("Lire l'introduction avec l'avatar")
            }
        }
    }
}
