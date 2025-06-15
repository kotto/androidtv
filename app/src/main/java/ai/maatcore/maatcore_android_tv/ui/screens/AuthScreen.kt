package ai.maatcore.maatcore_android_tv.ui.screens

// Imports existants ...
import androidx.compose.material3.Text // Assurez-vous que cet import est là si vous l'utilisez
import androidx.compose.material3.Icon // Pour les icônes Material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
// ... autres imports nécessaires ...

import ai.maatcore.maatcore_android_tv.R // Pour R.drawable.placeholder_image
import ai.maatcore.maatcore_android_tv.ui.theme.MontserratFamily
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import ai.maatcore.maatcore_android_tv.ui.theme.InterFamily
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface // Use standard Material3 Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions


// Début de votre Composable AuthScreen
@Composable // Removed ExperimentalTvMaterial3Api
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel) { // Supposant un AuthViewModel
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(AuthScreenState.LOGIN) } // ou SIGNUP

    // Remplacez les références aux FontFamily par celles importées
    // Exemple d'utilisation dans un Text:
    // Text("Quelque chose", fontFamily = MontserratFamily)
    // Text("Autre chose", fontFamily = PoppinsFamily)
    // Text("Encore autre chose", fontFamily = InterFamily)

    // Voici un exemple de structure pour votre écran, adaptez selon votre code existant.
    // Les FontFamily sont utilisées dans les `TextStyle` ou directement.

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.placeholder_image), // Placeholder pour le logo Maat
                contentDescription = "Logo Maat",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp)
            )

            Text(
                text = if (currentScreen == AuthScreenState.LOGIN) "Connexion" else "Inscription",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MontserratFamily, // UTILISATION DE LA FONTFAMILY
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = PoppinsFamily) }, // UTILISATION
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = TextStyle(fontFamily = InterFamily, color = Color.White) // UTILISATION
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe", fontFamily = PoppinsFamily) }, // UTILISATION
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                    }
                },
                textStyle = TextStyle(fontFamily = InterFamily, color = Color.White)
            )

            // ... (Ajoutez les champs pour Confirmer Mot de Passe, Nom d'utilisateur si currentScreen == AuthScreenState.SIGNUP)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentScreen == AuthScreenState.LOGIN) {
                        // authViewModel.loginUser(email, password)
                    } else {
                        // authViewModel.signUpUser(email, password /*, autres champs */)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (currentScreen == AuthScreenState.LOGIN) "Se Connecter" else "S'inscrire",
                    fontFamily = PoppinsFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.TextButton(onClick = {
                currentScreen = if (currentScreen == AuthScreenState.LOGIN) AuthScreenState.SIGNUP else AuthScreenState.LOGIN
            }) {
                Text(
                    if (currentScreen == AuthScreenState.LOGIN) "Pas de compte ? S'inscrire" else "Déjà un compte ? Se connecter",
                    color = Color.Gray,
                    fontFamily = InterFamily
                )
            }
        }
    }
}

enum class AuthScreenState {
    LOGIN,
    SIGNUP
}

// Simulez votre AuthViewModel si vous n'en avez pas encore un vrai pour les tests UI
class AuthViewModel {
    // fun loginUser(email: String, pass: String) { /* ... */ }
    // fun signUpUser(email: String, pass: String /*...*/) { /* ... */ }
}
