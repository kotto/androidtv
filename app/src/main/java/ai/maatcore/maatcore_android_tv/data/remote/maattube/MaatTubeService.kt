package ai.maatcore.maatcore_android_tv.data.remote.maattube

import retrofit2.http.GET
import retrofit2.http.Path

interface MaatTubeService {
    @GET("videos")
    suspend fun getVideos(): List<VideoDto>

    @GET("videos/{id}")
    suspend fun getVideo(@Path("id") id: String): VideoDto
}
