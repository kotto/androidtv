package ai.maatcore.maatcore_android_tv.data.remote.maatclass

import retrofit2.http.GET
import retrofit2.http.Path

interface MaatClassService {
    @GET("courses")
    suspend fun getCourses(): List<CourseDto>

    @GET("courses/{id}")
    suspend fun getCourse(@Path("id") id: String): CourseDto
}
