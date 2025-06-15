package ai.maatcore.maatcore_android_tv.data.remote.maatfoot

import retrofit2.http.GET

interface MaatFootService {
    @GET("maqfoot/matches")
    suspend fun getMatches(): List<MatchDto>
}
