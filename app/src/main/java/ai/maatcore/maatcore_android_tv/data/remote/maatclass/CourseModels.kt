package ai.maatcore.maatcore_android_tv.data.remote.maatclass

import com.google.gson.annotations.SerializedName

// DTOs venant du backend

data class LessonDto(
    val id: String,
    val title: String,
    @SerializedName("videoUrl") val videoUrl: String
)

data class CourseDto(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val country: String,
    val level: String,
    val subject: String,
    val lessons: List<LessonDto>
)
