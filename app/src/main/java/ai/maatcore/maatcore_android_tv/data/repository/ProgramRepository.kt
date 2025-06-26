package ai.maatcore.maatcore_android_tv.data.repository

import ai.maatcore.maatcore_android_tv.data.remote.maattv.MaatTVService
import ai.maatcore.maatcore_android_tv.data.remote.maattv.ProgramDto
import javax.inject.Inject

class ProgramRepository @Inject constructor(private val service: MaatTVService) {
    suspend fun getPrograms(): List<ProgramDto> {
        return try {
            service.getPrograms()
        } catch (e: Exception) {
            // Return empty list if network call fails
            emptyList()
        }
    }
}
