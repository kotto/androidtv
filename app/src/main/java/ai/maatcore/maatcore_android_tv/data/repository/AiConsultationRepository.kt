package ai.maatcore.maatcore_android_tv.data.repository

import ai.maatcore.maatcore_android_tv.data.remote.openai.ChatMessage
import ai.maatcore.maatcore_android_tv.data.remote.openai.ChatRequest
import ai.maatcore.maatcore_android_tv.data.remote.openai.OpenAiService
import javax.inject.Inject

class AiConsultationRepository @Inject constructor(
    private val openAiService: OpenAiService
) {
    suspend fun ask(question: String, userAnswer: String): String {
        val messages = listOf(
            ChatMessage("system", "Vous êtes le Dr Maât, médecin virtuel francophone. Ne posez pas de diagnostic ferme. Fournissez des conseils généraux et mentionnez toujours de consulter un professionnel."),
            ChatMessage("user", "$question\nRéponse utilisateur: $userAnswer")
        )
        val request = ChatRequest(messages = messages)
        val response = openAiService.chatCompletion(request)
        return response.choices.firstOrNull()?.message?.content ?: "Je n'ai pas compris."
    }
}
