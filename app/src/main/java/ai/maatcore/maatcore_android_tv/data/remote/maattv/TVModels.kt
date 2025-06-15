package ai.maatcore.maatcore_android_tv.data.remote.maattv

data class ProgramDto(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val live: Boolean,
    val startTime: String?
)
