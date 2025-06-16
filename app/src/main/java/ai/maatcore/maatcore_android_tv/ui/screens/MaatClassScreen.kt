package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.maatcore.maatcore_android_tv.R
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorBleuCeruleen
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorVertSante
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorNoirProfond
import ai.maatcore.maatcore_android_tv.ui.theme.PoppinsFamily
import androidx.navigation.NavHostController

data class Course(
    val id: String,
    val title: String,
    val subject: String,
    val level: String,
    val country: String,
    val imageRes: Int = R.drawable.ic_course_placeholder
)

@Composable
fun MaatClassScreen(navController: NavHostController) {

    // Sample data
    val allCourses = remember {
        listOf(
            Course("1", "Mathématiques 6e", "Maths", "Collège", "Sénégal"),
            Course("2", "Sciences 3e", "Sciences", "Collège", "Cameroun"),
            Course("3", "Histoire Terminale", "Histoire", "Lycée", "France"),
            Course("4", "Physique 2nde", "Physique", "Lycée", "Maroc"),
            Course("5", "Programmation Python", "Informatique", "Lycée", "Côte d'Ivoire")
        )
    }

    var selectedCountry by remember { mutableStateOf("Tous") }
    var selectedLevel by remember { mutableStateOf("Tous") }
    var selectedSubject by remember { mutableStateOf("Tous") }

    val countries = listOf("Tous", "Sénégal", "Cameroun", "Maroc", "France", "Côte d'Ivoire")
    val levels = listOf("Tous", "Collège", "Lycée")
    val subjects = listOf("Tous", "Maths", "Sciences", "Histoire", "Physique", "Informatique")

    val filteredCourses = allCourses.filter { c ->
        (selectedCountry == "Tous" || c.country == selectedCountry) &&
                (selectedLevel == "Tous" || c.level == selectedLevel) &&
                (selectedSubject == "Tous" || c.subject == selectedSubject)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(MaatColorNoirProfond, MaatColorOrSable),
                    center = Offset.Unspecified,
                    radius = 300f
                )
            )
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 48.dp)
    ) {
        Text(
        text = "Maât.Class",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = PoppinsFamily,
        color = MaatColorVertSante,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // Filters row
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        FilterButton(
            label = "Pays/Programme",
            selectedValue = selectedCountry,
            options = countries,
            onSelected = { selectedCountry = it }
        )
        FilterButton(
            label = "Niveau",
            selectedValue = selectedLevel,
            options = levels,
            onSelected = { selectedLevel = it }
        )
        FilterButton(
            label = "Matière",
            selectedValue = selectedSubject,
            options = subjects,
            onSelected = { selectedSubject = it }
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Grid of courses 3 columns
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.weight(1f)
    ) {
        items(filteredCourses) { course ->
            CourseCard(course = course) {
                navController.navigate("course_detail/${course.id}")
            }
        }
    }
}

}

@Composable
fun FilterButton(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = MaatColorBleuCeruleen),
            modifier = Modifier.focusable()
        ) {
            Text("$label: $selectedValue", fontFamily = PoppinsFamily, color = Color.Black)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun CourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.4f))
    ) {
        Column {
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = course.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PoppinsFamily,
                    color = Color.White
                )
                Text(
                    text = "${course.subject} • ${course.level}",
                    fontSize = 14.sp,
                    fontFamily = PoppinsFamily,
                    color = Color.Gray
                )
            }
        }
    }
}
