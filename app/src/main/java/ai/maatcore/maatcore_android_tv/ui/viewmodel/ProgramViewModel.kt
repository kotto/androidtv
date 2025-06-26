package ai.maatcore.maatcore_android_tv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.maatcore.maatcore_android_tv.data.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramViewModel @Inject constructor(private val repo: ProgramRepository): ViewModel() {
    private val _programs = MutableStateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maattv.ProgramDto>>(emptyList())
    val programs: StateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maattv.ProgramDto>> = _programs

    fun load() {
        viewModelScope.launch {
            try {
                _programs.value = repo.getPrograms()
            } catch (e: Exception) {
                // Handle error gracefully - keep empty list
                _programs.value = emptyList()
            }
        }
    }
}
