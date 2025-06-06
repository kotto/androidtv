package ai.maatcore.maatcore_android_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.ExperimentalTvMaterial3Api
import ai.maatcore.maatcore_android_tv.ui.screens.*
import ai.maatcore.maatcore_android_tv.ui.theme.AppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
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

    LaunchedEffect(Unit) {
        delay(3000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                AndroidTVHomeScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }
    }
}