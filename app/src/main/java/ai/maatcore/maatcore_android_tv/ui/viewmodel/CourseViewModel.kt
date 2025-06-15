package ai.maatcore.maatcore_android_tv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.maatcore.maatcore_android_tv.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(private val repo: CourseRepository) : ViewModel() {

    private val _courses = MutableStateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maatclass.CourseDto>>(emptyList())
    val courses: StateFlow<List<ai.maatcore.maatcore_android_tv.data.remote.maatclass.CourseDto>> = _courses

    fun loadCourses() {
        viewModelScope.launch {
            _courses.value = repo.getCourses()
        }
    }
}
