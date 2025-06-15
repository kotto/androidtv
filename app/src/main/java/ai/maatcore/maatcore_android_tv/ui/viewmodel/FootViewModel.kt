package ai.maatcore.maatcore_android_tv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.maatcore.maatcore_android_tv.data.repository.FootRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FootViewModel @Inject constructor(private val repo: FootRepository): ViewModel() {
    private val _matches = MutableStateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maatfoot.MatchDto>>(emptyList())
    val matches: StateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maatfoot.MatchDto>> = _matches

    fun load() {
        viewModelScope.launch {
            _matches.value = repo.getMatches()
        }
    }
}
