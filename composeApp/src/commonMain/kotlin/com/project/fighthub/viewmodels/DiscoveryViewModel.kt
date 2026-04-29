package com.project.fighthub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.repository.DiscoveryRepository
import com.project.fighthub.data.repository.SwipeDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoveryViewModel : ViewModel() {
    private val repository = DiscoveryRepository()

    private val _queue = MutableStateFlow<List<Profile>>(emptyList())
    val queue: StateFlow<List<Profile>> = _queue.asStateFlow()

    private val _current = MutableStateFlow<Profile?>(null)
    val current: StateFlow<Profile?> = _current.asStateFlow()

    private val _isMatch = MutableStateFlow(false)
    val isMatch: StateFlow<Boolean> = _isMatch.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCandidates(lat: Double, lng: Double, radiusKm: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCandidates(lat, lng, radiusKm)
            _isLoading.value = false
            if (result.isSuccess) {
                val profiles = result.getOrNull().orEmpty()
                _queue.value = profiles
                _current.value = profiles.firstOrNull()
            }
        }
    }

    fun swipeLeft() = submitSwipe(SwipeDirection.Left)

    fun swipeRight() = submitSwipe(SwipeDirection.Right)

    fun clearMatchFlag() {
        _isMatch.value = false
    }

    private fun submitSwipe(direction: SwipeDirection) {
        val profile = _current.value ?: return
        viewModelScope.launch {
            val result = repository.submitSwipe(profile.id, direction)
            _isMatch.value = result.getOrNull() == true
            advanceQueue()
        }
    }

    private fun advanceQueue() {
        val nextQueue = _queue.value.drop(1)
        _queue.value = nextQueue
        _current.value = nextQueue.firstOrNull()
    }
}

