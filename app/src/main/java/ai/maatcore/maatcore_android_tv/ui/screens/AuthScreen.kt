package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
//import androidx.compose.material.* // Removed M2 wildcard
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ai.maatcore.maatcore_android_tv.network.ApiService

@Composable
fun AuthScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Connexion / Inscription", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                modifier = Modifier.padding(8.dp),
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    isLoading = true
                    loginMessage = null
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val token = ApiService.login(email, password)
                            if (token != null) {
                                loginMessage = "Connexion réussie !"
                                // TODO : Stocker le token pour les requêtes futures
                                // navController.navigate("home") // ou vers un écran protégé
                            } else {
                                loginMessage = "Erreur de connexion"
                            }
                        } catch (e: Exception) {
                            loginMessage = "Erreur réseau : ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.padding(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Se connecter")
                }
            }
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Retour Accueil")
            }
            loginMessage?.let {
                Text(
                    it,
                    color = if (it.contains("réussie")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
