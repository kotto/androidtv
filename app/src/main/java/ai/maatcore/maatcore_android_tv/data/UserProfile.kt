package ai.maatcore.maatcore_android_tv.data

data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val avatarRes: Int,
    val isActive: Boolean = false
)