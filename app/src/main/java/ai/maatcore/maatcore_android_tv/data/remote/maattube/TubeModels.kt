package ai.maatcore.maatcore_android_tv.data.remote.maattube

import com.google.gson.annotations.SerializedName

data class VideoDto(
    val id: String,
    val title: String,
    val channel: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String,
    @SerializedName("videoUrl") val videoUrl: String
)
