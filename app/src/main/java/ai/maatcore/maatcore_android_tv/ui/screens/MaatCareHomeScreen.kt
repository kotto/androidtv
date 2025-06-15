package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.ui.theme.MontserratFamily
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily

/* ---------- data ---------- */

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val avatarRes: Int,
    val iconRes: ImageVector
)

data class ServiceOption(
    val id: String,
    val name: String,
    val icon: ImageVector
)

data class VitalSign(
    val id: String,
    val name: String,
    val value: String,
    val unit: String,
    val icon: ImageVector
)

/* ---------- screen ---------- */

@Composable
fun MaatCareHomeScreen(navController: NavController) {

    /* --- sample data --- */
    val doctors = listOf(
        Doctor("1", "Dr. Amara", "Médecine Générale", R.drawable.placeholder_image, Icons.Default.MedicalServices),
        Doctor("2", "Dr. Kwame", "Cardiologie",        R.drawable.placeholder_image, Icons.Default.Favorite),
        Doctor("3", "Dr. Fatou", "Pédiatrie",          R.drawable.placeholder_image, Icons.Default.ChildCare),
        Doctor("4", "Dr. Omar",  "Dermatologie",       R.drawable.placeholder_image, Icons.Default.Spa),
        Doctor("5", "Dr. Aisha", "Gynécologie",        R.drawable.placeholder_image, Icons.Default.PregnantWoman)
    )

    val services = listOf(
        ServiceOption("1", "Consultation", Icons.Default.RecordVoiceOver),
        ServiceOption("2", "Phytothérapie", Icons.Default.Eco),
        ServiceOption("3", "Signes vitaux", Icons.Default.MonitorHeart),
        ServiceOption("4", "Appel vidéo",   Icons.Default.Videocam)
    )

    /* --- UI --- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Text(
            "MaatCare",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFamily,
            color = Color(0xFFD4AF37)
        )

        Spacer(Modifier.height(24.dp))

        /* --------- doctors --------- */
        Text("Nos médecins", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 240.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor)
            }
        }

        Spacer(Modifier.height(24.dp))

        /* --------- services --------- */
        Text("Services rapides", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            services.forEach { svc ->
                ServiceChip(svc) {
                    when (svc.id) {
                        "1" -> navController.navigate("consultation_symptom_input")
                        "3" -> navController.navigate("vital_parameters_screen")
                        else -> {}  // autres navigations
                    }
                }
            }
        }
    }
}

/* ---------- components ---------- */

@Composable
private fun DoctorCard(doctor: Doctor) {
    Card(
        onClick = { /* détail médecin */ },
        modifier = Modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1810))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = doctor.avatarRes),
                contentDescription = doctor.name,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                Text(
                    doctor.specialty,
                    fontSize = 12.sp,
                    color = Color(0xFFF5E6D3),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ServiceChip(option: ServiceOption, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                option.name,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        },
        leadingIcon = {
            Icon(option.icon, option.name, Modifier.size(18.dp))
        }
    )
}