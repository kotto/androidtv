package ai.maatcore.maatcore_android_tv.data.remote.maatfoot

data class MatchDto(
    val id: String,
    val teamHome: String,
    val teamAway: String,
    val scoreHome: Int?,
    val scoreAway: Int?,
    val status: String, // LIVE, FINISHED, UPCOMING
    val startTime: String
)
