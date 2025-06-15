package ai.maatcore.maatcore_android_tv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.maatcore.maatcore_android_tv.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val repo: VideoRepository) : ViewModel() {
    private val _videos = MutableStateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maattube.VideoDto>>(emptyList())
    val videos: StateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maattube.VideoDto>> = _videos

    fun loadVideos() {
        viewModelScope.launch {
            _videos.value = repo.getVideos()
        }
    }
}
