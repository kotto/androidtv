package ai.maatcore.maatcore_android_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ai.maatcore.maatcore_android_tv.ui.screens.*
import ai.maatcore.maatcore_android_tv.ui.theme.AppTheme
import kotlinx.coroutines.delay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TVAppNavigation()
                }
            }
        }
    }
}

@Composable
fun TVAppNavigation() {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }

    // Splash de 3 s
    LaunchedEffect(Unit) {
        delay(3000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = "netflix_home"
        ) {
            // Écran d'accueil principal Netflix-like
            composable("netflix_home") {
                NetflixTvHomeScreen(
                    navController = navController
                )
            }

            composable("settings") {
                SettingsScreen(onBackPressed = { navController.popBackStack() })
            }

            // Services existants
            composable("maattv")       { MaatTVScreen(navController) }
            composable("maatcare")     { MaatCareScreen(navController) }
            composable("maatfoot")     { MaatFootScreen() }
            composable("maatclass")    { MaatClassScreen(navController) }
            composable("maattube")     { MaatTubeScreen(navController) }
            composable("maatflix")     { MaatFlixScreen(navController) }

            // Détails cours / vidéo
            composable(
                "course_detail/{courseId}",
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("courseId")
                CourseDetailScreen(navController, id)
            }

            composable(
                "video_detail/{videoId}",
                arguments = listOf(navArgument("videoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("videoId")
                VideoDetailScreen(navController, id)
            }

            // Parcours consultation
            composable("consultation_symptom_input") { ConsultationSymptomInputScreen(navController) }
            composable("consultation_ai_avatar")     { ConsultationAIAvatarScreen(navController) }
            composable("consultation_handoff")       { ConsultationRealDoctorHandoffScreen(navController) }
            composable("consultation_summary")       { ConsultationSummaryScreen(navController) }

            // Plantes médicinales
            composable("medicinal_plants_screen") { MedicinalPlantsScreen(navController) }
            composable(
                "plant_detail/{plantId}",
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("plantId")
                PlantDetailScreen(navController, id)
            }

            // Signes vitaux
            composable("vital_parameters_screen") { VitalParametersScreen(navController) }

            // Nouveau détail film
            composable(
                "details/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                MovieDetailsScreen(navController = navController, movieId = id)
            }
        }
    }
}