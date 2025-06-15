package ai.maatcore.maatcore_android_tv.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController

@Composable
fun VideoDetailScreen(navController: NavHostController, videoId: String?) {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Simple ExoPlayer integration
        val context = navController.context
        val player = remember { ExoPlayer.Builder(context).build() }
        DisposableEffect(Unit) {
            val mediaItem = MediaItem.fromUri(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
            onDispose { player.release() }
        }
        AndroidView(factory = {
            PlayerView(it).apply {
                this.player = player
                useController = true
            }
        }, modifier = Modifier.weight(1f))

        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Retour")
            }
            // TODO: like, share, subscribe buttons
        }
    }
}
