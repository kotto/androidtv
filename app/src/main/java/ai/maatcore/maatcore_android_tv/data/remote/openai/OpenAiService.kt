package ai.maatcore.maatcore_android_tv.data.remote.openai

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse
}
