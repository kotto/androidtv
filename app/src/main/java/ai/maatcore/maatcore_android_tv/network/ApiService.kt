package ai.maatcore.maatcore_android_tv.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object ApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private const val BASE_URL = "http://192.168.1.190:8002/tv/vod" // IP locale du PC pour accès depuis la TV

    @Serializable
    data class LoginRequest(val email: String, val password: String)

    @Serializable
    data class LoginResponse(val token: String)

    suspend fun login(email: String, password: String): String? {
        val response: HttpResponse = client.post("$BASE_URL/login") {
            setBody(LoginRequest(email, password))
        }
        return if (response.status.value == 200) {
            response.body<LoginResponse>().token
        } else {
            null
        }
    }
    @Serializable
    data class ContentItemResponse(
        val id: Int,
        val title: String,
        val imageUrl: String,
        val year: String,
        val type: String,
        val crew: String,
        val rating: String,
        val description: String
    )

    suspend fun getContentList(): List<ContentItemResponse> {
        val response: HttpResponse = client.get("$BASE_URL/list")
        return if (response.status.value == 200) {
            response.body()
        } else {
            emptyList()
        }
    }
}