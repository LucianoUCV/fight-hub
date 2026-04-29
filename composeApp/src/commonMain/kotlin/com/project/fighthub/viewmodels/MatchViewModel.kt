package com.project.fighthub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fighthub.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatchUiModel(
    val matchId: String,
    val opponentName: String,
    val status: String,
    val didIWin: Boolean?
)

class MatchViewModel : ViewModel() {
    private val repository = MatchRepository()

    private val _uiMatches = MutableStateFlow<List<MatchUiModel>>(emptyList())
    val uiMatches: StateFlow<List<MatchUiModel>> = _uiMatches.asStateFlow()

    fun loadFights() {
        viewModelScope.launch {
            val myId = repository.getCurrentUserId() ?: return@launch
            val rawMatches = repository.getMyFights().getOrNull().orEmpty()

            val mapped = rawMatches.map { match ->
                val opponentId = if (match.user1Id == myId) match.user2Id else match.user1Id
                val opponentProfile = repository.getProfileById(opponentId)

                MatchUiModel(
                    matchId = match.id,
                    opponentName = opponentProfile?.name ?: "Unknown Fighter",
                    status = match.status,
                    didIWin = if (match.status == "completed") match.winnerId == myId else null
                )
            }

            _uiMatches.value = mapped
        }
    }

    fun submitResult(matchId: String, iWon: Boolean) {
        viewModelScope.launch {
            repository.submitMatchResult(matchId, iWon)
            loadFights()
        }
    }
}