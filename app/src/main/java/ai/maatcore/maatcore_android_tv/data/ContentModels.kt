package ai.maatcore.maatcore_android_tv.data

import kotlinx.serialization.Serializable

@Serializable
data class ContentItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val subtitle: String? = null,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val descriptionLong: String? = null,
    val durationMinutes: Int? = null,
    val releaseDate: String? = null,
    val parentalRating: String? = null,
    val language: String? = null,
    val contentType: ContentType = ContentType.MOVIE,
    val directors: List<String>? = null,
    val cast: List<String>? = null,
    val creators: List<String>? = null,
    val mainCast: List<String>? = null,
    val numberOfSeasons: Int? = null,
    val seasons: List<Season>? = null,
    val genre: String? = null,
    val rating: Float? = null
)

@Serializable
enum class ContentType {
    MOVIE,
    SERIES,
    DOCUMENTARY,
    PODCAST,
    HISTORY_SEGMENT,
    LIVE_CHANNEL
}

@Serializable
data class Season(
    val seasonNumber: Int,
    val title: String,
    val episodes: List<Episode>
)

@Serializable
data class Episode(
    val episodeNumber: Int,
    val title: String,
    val description: String? = null,
    val durationMinutes: Int? = null,
    val imageUrl: String? = null
)

@Serializable
data class UserPlaybackState(
    val contentId: String,
    val currentPositionMs: Long,
    val totalDurationMs: Long,
    val lastWatched: String,
    val completed: Boolean = false
)
