package ai.maatcore.maatcore_android_tv.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Very simple placeholder ViewModel used by [ConsultationAIAvatarScreen].
 * It just exposes a single StateFlow containing the current AI answer so the
 * Composable can observe it.
 */
class AiConsultationViewModel : ViewModel() {

    private val _aiAnswer = MutableStateFlow("")
    val aiAnswer: StateFlow<String> = _aiAnswer

    fun setAnswer(answer: String) {
        _aiAnswer.value = answer
    }
}
