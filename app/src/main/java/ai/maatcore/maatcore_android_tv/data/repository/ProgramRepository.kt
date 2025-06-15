package ai.maatcore.maatcore_android_tv.data.repository

import ai.maatcore.maatcore_android_tv.data.remote.maattv.MaatTVService
import javax.inject.Inject

class ProgramRepository @Inject constructor(private val service: MaatTVService) {
    suspend fun getPrograms() = service.getPrograms()
}
