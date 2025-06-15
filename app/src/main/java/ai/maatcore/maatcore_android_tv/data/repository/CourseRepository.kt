package ai.maatcore.maatcore_android_tv.data.repository

import ai.maatcore.maatcore_android_tv.data.remote.maatclass.MaatClassService
import javax.inject.Inject

class CourseRepository @Inject constructor(private val service: MaatClassService) {
    suspend fun getCourses() = service.getCourses()
    suspend fun getCourse(id: String) = service.getCourse(id)
}
