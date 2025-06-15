package ai.maatcore.maatcore_android_tv.data.remote.openai

import com.google.gson.annotations.SerializedName

// Simple request/response models for OpenAI Chat Completion

data class ChatMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class ChatRequest(
    val model: String = "gpt-3.5-turbo", // or "gpt-4o"
    val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class ChatResponse(
    @SerializedName("choices") val choices: List<Choice>
) {
    data class Choice(
        @SerializedName("message") val message: ChatMessage
    )
}
