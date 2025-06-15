package ai.maatcore.maatcore_android_tv.data.repository

import ai.maatcore.maatcore_android_tv.data.remote.maattube.MaatTubeService
import javax.inject.Inject

class VideoRepository @Inject constructor(private val service: MaatTubeService) {
    suspend fun getVideos() = service.getVideos()
    suspend fun getVideo(id: String) = service.getVideo(id)
}
