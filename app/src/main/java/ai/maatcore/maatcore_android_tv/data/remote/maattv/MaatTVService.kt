package ai.maatcore.maatcore_android_tv.data.remote.maattv

import retrofit2.http.GET

interface MaatTVService {
    @GET("maattv/programs")
    suspend fun getPrograms(): List<ProgramDto>
}
