package com.project.fighthub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {
    private val _leaderboard = MutableStateFlow<List<Profile>>(emptyList())
    val leaderboard: StateFlow<List<Profile>> = _leaderboard.asStateFlow()

    fun loadLeaderboard() {
        viewModelScope.launch {
            try {
                val topFighters = supabaseClient.postgrest["profiles"]
                    .select {
                        order("elo_points", order = Order.DESCENDING)
                        limit(50)
                    }
                    .decodeList<Profile>()

                _leaderboard.value = topFighters
            } catch (e: Exception) {
            }
        }
    }
}