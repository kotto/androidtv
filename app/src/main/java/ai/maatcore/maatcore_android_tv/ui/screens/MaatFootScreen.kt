package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.maatcore.maatcore_android_tv.ui.viewmodel.FootViewModel
import ai.maatcore.maatcore_android_tv.data.remote.maatfoot.MatchDto

@Composable
fun MaatFootScreen(viewModel: FootViewModel = hiltViewModel()) {
    val matches by viewModel.matches.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("MaâtFoot - Résultats & Matchs en direct", color = Color.White, fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(matches) { match -> MatchRow(match) }
        }
    }
}

@Composable
fun MatchRow(match: MatchDto) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(match.teamHome, modifier = Modifier.weight(1f))
            Text(match.scoreHome?.toString() ?: "-", fontSize = 18.sp)
            Text(" : ")
            Text(match.scoreAway?.toString() ?: "-", fontSize = 18.sp)
            Text(match.teamAway, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        }
        match.status.takeIf { it == "LIVE" }?.let {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color.Red)
        }
    }
}
