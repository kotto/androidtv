package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.ui.components.*
import ai.maatcore.maatcore_android_tv.ui.theme.*
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interface MaâtCare - Plateforme de télémédecine avec interface moderne
 * Header proéminent, menu vertical avec spécialités médicales, et cartes de médecins
 */
@Composable
fun MaatCareScreen(navController: NavHostController) {
    var selectedSpecialty by remember { mutableStateOf("Accueil") }
    var selectedDoctorId by remember { mutableStateOf<String?>(null) }
    var showDoctorDetails by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Animation d'entrée
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D4F3C), // Vert médical foncé
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF0D4F3C)
                    )
                )
            )
    ) {
        // Contenu principal
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Menu vertical (sidebar)
            AnimatedVisibility(
                visible = isMenuExpanded,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                MaatCareSidebar(
                    selectedSpecialty = selectedSpecialty,
                    onSpecialtySelected = { specialty ->
                        selectedSpecialty = specialty
                        isMenuExpanded = false
                    },
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                )
            }
            
            // Zone de contenu principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.Menu, Key.DirectionLeft -> {
                                    if (!isMenuExpanded) {
                                        isMenuExpanded = true
                                        true
                                    } else false
                                }
                                Key.Back -> {
                                    if (isMenuExpanded) {
                                        isMenuExpanded = false
                                        true
                                    } else {
                                        navController.popBackStack()
                                        true
                                    }
                                }
                                else -> false
                            }
                        } else false
                    }
            ) {
                // Header proéminent
                MaatCareHeader(
                    selectedSpecialty = selectedSpecialty,
                    onMenuToggle = { isMenuExpanded = !isMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Contenu selon la spécialité sélectionnée
                MaatCareContent(
                    specialty = selectedSpecialty,
                    onDoctorSelected = { doctorId ->
                        selectedDoctorId = doctorId
                        navController.navigate("consultation_details/$doctorId")
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            }
        }
        
        // Overlay pour fermer le menu
        if (isMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Back) {
                            isMenuExpanded = false
                            true
                        } else false
                    }
            )
        }
    }
}

@Composable
fun MaatCareHeader(
    selectedSpecialty: String,
    onMenuToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerFocusRequester = remember { FocusRequester() }
    var isHeaderFocused by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .height(120.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50), // Vert médical
                        Color(0xFF388E3C),
                        Color.Transparent
                    )
                )
            )
            .focusRequester(headerFocusRequester)
            .onFocusChanged { isHeaderFocused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionCenter) {
                    onMenuToggle()
                    true
                } else false
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo et titre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = "MaâtCare",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "MaâtCare",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = selectedSpecialty,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Bouton menu
            Card(
                modifier = Modifier
                    .size(56.dp)
                    .border(
                        width = if (isHeaderFocused) 3.dp else 0.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        headerFocusRequester.requestFocus()
    }
}

@Composable
fun MaatCareSidebar(
    selectedSpecialty: String,
    onSpecialtySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val specialties = listOf(
        "Accueil" to Icons.Default.Home,
        "Médecine générale" to Icons.Default.MedicalServices,
        "Cardiologie" to Icons.Default.Favorite,
        "Dermatologie" to Icons.Default.Face,
        "Pédiatrie" to Icons.Default.ChildCare,
        "Gynécologie" to Icons.Default.PregnantWoman,
        "Psychiatrie" to Icons.Default.Psychology,
        "Ophtalmologie" to Icons.Default.RemoveRedEye,
        "Dentisterie" to Icons.Default.MedicalInformation,
        "Urgences" to Icons.Default.Emergency
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A4A3A)
        ),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Titre du menu
            Text(
                text = "Spécialités",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Liste des spécialités
            LazyColumn {
                items(specialties) { (specialty, icon) ->
                    MaatCareMenuItem(
                        title = specialty,
                        icon = icon,
                        isSelected = specialty == selectedSpecialty,
                        onClick = { onSpecialtySelected(specialty) }
                    )
                }
            }
        }
    }
}

@Composable
fun MaatCareMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionCenter) {
                    onClick()
                    true
                } else false
            }
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF4CAF50) else Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun MaatCareContent(
    specialty: String,
    onDoctorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        when (specialty) {
            "Accueil" -> {
                item {
                    MaatCareSection(
                        title = "Médecins disponibles maintenant",
                        doctors = getAvailableDoctors(),
                        onDoctorSelected = onDoctorSelected
                    )
                }
                item {
                    MaatCareSection(
                        title = "Médecins les mieux notés",
                        doctors = getTopRatedDoctors(),
                        onDoctorSelected = onDoctorSelected
                    )
                }
                item {
                    MaatCareSection(
                        title = "Spécialistes africains",
                        doctors = getAfricanDoctors(),
                        onDoctorSelected = onDoctorSelected
                    )
                }
            }
            "Médecine générale" -> {
                item {
                    MaatCareSection(
                        title = "Médecins généralistes",
                        doctors = getGeneralPractitioners(),
                        onDoctorSelected = onDoctorSelected
                    )
                }
            }
            "Cardiologie" -> {
                item {
                    MaatCareSection(
                        title = "Cardiologues",
                        doctors = getCardiologists(),
                        onDoctorSelected = onDoctorSelected
                    )
                }
            }
            else -> {
                item {
                    MaatCareSection(
                        title = specialty,
                        doctors = getDoctorsBySpecialty(specialty),
                        onDoctorSelected = onDoctorSelected
                    )
                }
            }
        }
    }
}

@Composable
fun MaatCareSection(
    title: String,
    doctors: List<DoctorItem>,
    onDoctorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(doctors) { doctor ->
                MaatCareDoctorCard(
                    doctor = doctor,
                    onSelected = { onDoctorSelected(doctor.id) }
                )
            }
        }
    }
}

@Composable
fun MaatCareDoctorCard(
    doctor: DoctorItem,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    Card(
        modifier = modifier
            .width(220.dp)
            .height(320.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionCenter) {
                    onSelected()
                    true
                } else false
            }
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A4A3A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo du médecin
            AsyncImage(
                model = doctor.photoUrl,
                contentDescription = doctor.name,
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF4CAF50), CircleShape),
                contentScale = ContentScale.Crop
            )
            
            // Informations du médecin
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dr. ${doctor.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = doctor.specialty,
                    fontSize = 14.sp,
                    color = Color(0xFF81C784),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = doctor.experience,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Note et disponibilité
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = doctor.rating,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Indicateur de disponibilité
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (doctor.isAvailable) Color.Green else Color.Red,
                                    shape = CircleShape
                                )
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = if (doctor.isAvailable) "Disponible" else "Occupé",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Prix de consultation
                Text(
                    text = "${doctor.consultationPrice} FCFA",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Modèle de données pour les médecins
data class DoctorItem(
    val id: String,
    val name: String,
    val specialty: String,
    val experience: String,
    val rating: String,
    val photoUrl: String,
    val isAvailable: Boolean,
    val consultationPrice: String,
    val languages: List<String>
)

// Fonctions pour obtenir les données de médecins (à remplacer par de vraies données)
fun getAvailableDoctors(): List<DoctorItem> = listOf(
    DoctorItem("1", "Aminata Traoré", "Médecine générale", "8 ans d'expérience", "4.8", "https://example.com/doctor1.jpg", true, "15,000", listOf("Français", "Bambara")),
    DoctorItem("2", "Moussa Diallo", "Cardiologie", "12 ans d'expérience", "4.9", "https://example.com/doctor2.jpg", true, "25,000", listOf("Français", "Wolof")),
    DoctorItem("3", "Fatou Sow", "Pédiatrie", "6 ans d'expérience", "4.7", "https://example.com/doctor3.jpg", true, "18,000", listOf("Français", "Pulaar"))
)

fun getTopRatedDoctors(): List<DoctorItem> = listOf(
    DoctorItem("4", "Ibrahima Koné", "Chirurgie", "15 ans d'expérience", "4.9", "https://example.com/doctor4.jpg", false, "35,000", listOf("Français", "Malinké")),
    DoctorItem("5", "Aïcha Camara", "Gynécologie", "10 ans d'expérience", "4.8", "https://example.com/doctor5.jpg", true, "22,000", listOf("Français", "Soussou"))
)

fun getAfricanDoctors(): List<DoctorItem> = getAvailableDoctors()

fun getGeneralPractitioners(): List<DoctorItem> = getAvailableDoctors().filter { it.specialty == "Médecine générale" }
fun getCardiologists(): List<DoctorItem> = getAvailableDoctors().filter { it.specialty == "Cardiologie" }
fun getDoctorsBySpecialty(specialty: String): List<DoctorItem> = getAvailableDoctors()
