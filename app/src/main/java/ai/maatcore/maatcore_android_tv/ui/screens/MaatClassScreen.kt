package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.ui.theme.*
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset

/* ---------- data models ---------- */

data class Course(
    val id: String,
    val title: String,
    val subject: String,
    val level: String,
    val country: String,
    val description: String,
    val duration: String,
    val instructor: String,
    val imageRes: Int,
    val type: CourseType,
    val featured: Boolean = false
)

enum class CourseType {
    MATHEMATICS, SCIENCE, LANGUAGE, HISTORY, TECHNOLOGY, ART
}

data class CourseCategory(
    val id: String,
    val title: String,
    val courses: List<Course>
)

/* ---------- main screen ---------- */

@Composable
fun MaatClassScreen(navController: NavHostController) {
    
    /* --- sample data --- */
    val featuredCourse = Course(
        id = "featured_1",
        title = "Mathématiques Avancées",
        subject = "Mathématiques",
        level = "Terminale",
        country = "Sénégal",
        description = "Maîtrisez les concepts avancés des mathématiques avec nos experts pédagogiques",
        duration = "12 semaines",
        instructor = "Prof. Aminata Diop",
        imageRes = R.drawable.placeholder_image,
        type = CourseType.MATHEMATICS,
        featured = true
    )
    
    val mathematicsCategory = CourseCategory(
        id = "mathematics",
        title = "Mathématiques",
        courses = listOf(
            Course("math_1", "Algèbre 3e", "Mathématiques", "Collège", "Sénégal", "Équations et fonctions", "8 semaines", "Prof. Moussa Kane", R.drawable.placeholder_image, CourseType.MATHEMATICS),
            Course("math_2", "Géométrie 2nde", "Mathématiques", "Lycée", "Cameroun", "Figures et démonstrations", "10 semaines", "Prof. Marie Ngozi", R.drawable.placeholder_image, CourseType.MATHEMATICS),
            Course("math_3", "Statistiques 1ère", "Mathématiques", "Lycée", "Côte d'Ivoire", "Probabilités et analyses", "6 semaines", "Prof. Kofi Asante", R.drawable.placeholder_image, CourseType.MATHEMATICS),
            Course("math_4", "Calcul Terminale", "Mathématiques", "Lycée", "Mali", "Dérivées et intégrales", "12 semaines", "Prof. Fatou Traoré", R.drawable.placeholder_image, CourseType.MATHEMATICS)
        )
    )
    
    val sciencesCategory = CourseCategory(
        id = "sciences",
        title = "Sciences & Technologie",
        courses = listOf(
            Course("sci_1", "Physique-Chimie 4e", "Sciences", "Collège", "Maroc", "Atomes et molécules", "10 semaines", "Dr. Hassan Alami", R.drawable.placeholder_image, CourseType.SCIENCE),
            Course("sci_2", "Biologie 1ère", "Sciences", "Lycée", "Tunisie", "Cellules et génétique", "14 semaines", "Dr. Leila Ben Ali", R.drawable.placeholder_image, CourseType.SCIENCE),
            Course("sci_3", "Informatique Python", "Technologie", "Lycée", "France", "Programmation avancée", "16 semaines", "Ing. Jean Dupont", R.drawable.placeholder_image, CourseType.TECHNOLOGY),
            Course("sci_4", "Sciences de la Terre", "Sciences", "Lycée", "Burkina Faso", "Géologie et climat", "8 semaines", "Prof. Ousmane Sawadogo", R.drawable.placeholder_image, CourseType.SCIENCE)
        )
    )
    
    val languagesCategory = CourseCategory(
        id = "languages",
        title = "Langues & Littérature",
        courses = listOf(
            Course("lang_1", "Français 6e", "Français", "Collège", "France", "Grammaire et expression", "12 semaines", "Prof. Claire Martin", R.drawable.placeholder_image, CourseType.LANGUAGE),
            Course("lang_2", "Anglais 3e", "Anglais", "Collège", "Ghana", "Communication et vocabulaire", "10 semaines", "Prof. Kwame Osei", R.drawable.placeholder_image, CourseType.LANGUAGE),
            Course("lang_3", "Arabe Terminale", "Arabe", "Lycée", "Maroc", "Littérature classique", "14 semaines", "Prof. Ahmed Benali", R.drawable.placeholder_image, CourseType.LANGUAGE),
            Course("lang_4", "Wolof Débutant", "Wolof", "Tous niveaux", "Sénégal", "Langue locale sénégalaise", "6 semaines", "Prof. Awa Diallo", R.drawable.placeholder_image, CourseType.LANGUAGE)
        )
    )
    
    val historyCategory = CourseCategory(
        id = "history",
        title = "Histoire & Géographie",
        courses = listOf(
            Course("hist_1", "Histoire de l'Afrique", "Histoire", "Lycée", "Sénégal", "Empires et civilisations", "12 semaines", "Prof. Cheikh Diop", R.drawable.placeholder_image, CourseType.HISTORY),
            Course("hist_2", "Géographie 2nde", "Géographie", "Lycée", "Cameroun", "Démographie et urbanisation", "8 semaines", "Prof. Paul Biya", R.drawable.placeholder_image, CourseType.HISTORY),
            Course("hist_3", "Histoire Contemporaine", "Histoire", "Lycée", "Côte d'Ivoire", "XXe et XXIe siècles", "10 semaines", "Prof. Alassane Ouattara", R.drawable.placeholder_image, CourseType.HISTORY),
            Course("hist_4", "Éducation Civique", "Civisme", "Collège", "Mali", "Citoyenneté et démocratie", "6 semaines", "Prof. Ibrahim Keita", R.drawable.placeholder_image, CourseType.HISTORY)
        )
    )
    
    val categories = listOf(mathematicsCategory, sciencesCategory, languagesCategory, historyCategory)
    
    /* --- UI --- */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF1a1a1a),
                        Color.Black
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            
            /* --- Hero Banner --- */
            CourseHeroBanner(featuredCourse) {
                navController.navigate("course_detail/${featuredCourse.id}")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            /* --- Categories Rows --- */
            categories.forEach { category ->
                CourseCategoryRow(
                    category = category,
                    onCourseClick = { course ->
                        navController.navigate("course_detail/${course.id}")
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

/* ---------- components ---------- */

@Composable
fun CourseHeroBanner(
    course: Course,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "heroScale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(horizontal = 48.dp, vertical = 24.dp)
            .scale(scale)
            .focusRequester(FocusRequester())
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 4.dp else 0.dp,
                color = MaatColorVertSante,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        
        /* Background Image */
        Image(
            painter = painterResource(id = course.imageRes),
            contentDescription = course.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        /* Gradient Overlay */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        /* Content */
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = course.title,
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MontserratFamily,
                    color = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = course.description,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = PoppinsFamily,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.width(400.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${course.level} • ${course.country}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = PoppinsFamily,
                        color = MaatColorVertSante
                    )
                )
                Text(
                    text = course.duration,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = PoppinsFamily,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaatColorVertSante
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Commencer",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Commencer le cours",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun CourseCategoryRow(
    category: CourseCategory,
    onCourseClick: (Course) -> Unit
) {
    Column {
        /* Category Title */
        Text(
            text = category.title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MontserratFamily,
                color = Color.White
            ),
            modifier = Modifier.padding(horizontal = 48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        /* Horizontal Scrollable Row */
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp)
        ) {
            items(category.courses) { course ->
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course) }
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 350f),
        label = "cardScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 12.dp else 4.dp,
        animationSpec = tween(200),
        label = "cardElevation"
    )
    
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .scale(scale)
            .focusRequester(FocusRequester())
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = MaatColorVertSante,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2a2a2a)
        )
    ) {
        Box {
            /* Background Image */
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = course.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            /* Gradient Overlay */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            
            /* Content */
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = course.title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFamily,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${course.level} • ${course.country}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = PoppinsFamily,
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = course.instructor,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = PoppinsFamily,
                        color = MaatColorVertSante
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            /* Type Indicator */
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(
                        color = getCourseTypeColor(course.type),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = getCourseTypeLabel(course.type),
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
            
            /* Duration Badge */
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = course.duration,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }
        }
    }
}

/* ---------- helper functions ---------- */

fun getCourseTypeColor(type: CourseType): Color {
    return when (type) {
        CourseType.MATHEMATICS -> Color(0xFF2196F3)
        CourseType.SCIENCE -> Color(0xFF4CAF50)
        CourseType.LANGUAGE -> Color(0xFFFF9800)
        CourseType.HISTORY -> Color(0xFF9C27B0)
        CourseType.TECHNOLOGY -> Color(0xFFF44336)
        CourseType.ART -> Color(0xFFE91E63)
    }
}

fun getCourseTypeLabel(type: CourseType): String {
    return when (type) {
        CourseType.MATHEMATICS -> "MATHS"
        CourseType.SCIENCE -> "SCIENCES"
        CourseType.LANGUAGE -> "LANGUES"
        CourseType.HISTORY -> "HISTOIRE"
        CourseType.TECHNOLOGY -> "TECH"
        CourseType.ART -> "ART"
    }
}
