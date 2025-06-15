package ai.maatcore.maatcore_android_tv.data.repository

import ai.maatcore.maatcore_android_tv.data.remote.maatfoot.MaatFootService
import javax.inject.Inject

class FootRepository @Inject constructor(private val service: MaatFootService) {
    suspend fun getMatches() = service.getMatches()
}
