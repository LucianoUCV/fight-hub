package com.project.fighthub.data.repository

import com.project.fighthub.data.model.Match
import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.math.pow

class MatchRepository {

    fun getCurrentUserId(): String? = supabaseClient.auth.currentUserOrNull()?.id

    suspend fun getMyFights(): Result<List<Match>> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("Not logged in")
            val matches = supabaseClient.postgrest["matches"]
                .select {
                    filter {
                        or {
                            eq("user1_id", userId)
                            eq("user2_id", userId)
                        }
                    }
                }
                .decodeList<Match>()
            Result.success(matches)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun getProfileById(userId: String): Profile? {
        return try {
            supabaseClient.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<Profile>()
        } catch (e: Throwable) {
            null
        }
    }

    suspend fun submitMatchResult(matchId: String, iWon: Boolean): Result<Unit> {
        return try {
            val myId = getCurrentUserId() ?: throw Exception("Not logged in")

            val match = supabaseClient.postgrest["matches"].select { filter { eq("id", matchId) } }.decodeSingle<Match>()

            val winnerId = if (iWon) myId else (if (match.user1Id == myId) match.user2Id else match.user1Id)
            val loserId = if (winnerId == match.user1Id) match.user2Id else match.user1Id

            supabaseClient.postgrest["matches"].update(buildJsonObject {
                put("status", "completed")
                put("winner_id", winnerId)
            }) { filter { eq("id", matchId) } }

            applyEloChanges(winnerId, loserId)

            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun applyEloChanges(winnerId: String, loserId: String) {
        val winner = getProfileById(winnerId) ?: return
        val loser = getProfileById(loserId) ?: return

        val expectedWinner = 1.0 / (1.0 + 10.0.pow((loser.eloPoints - winner.eloPoints) / 400.0))
        val expectedLoser = 1.0 / (1.0 + 10.0.pow((winner.eloPoints - loser.eloPoints) / 400.0))

        val newEloWinner = (winner.eloPoints + k * (1.0 - expectedWinner)).toInt()
        val newEloLoser = (loser.eloPoints + k * (0.0 - expectedLoser)).toInt()

        supabaseClient.postgrest["profiles"].update(buildJsonObject { put("elo_points", newEloWinner) }) { filter { eq("id", winnerId) } }
        supabaseClient.postgrest["profiles"].update(buildJsonObject { put("elo_points", newEloLoser) }) { filter { eq("id", loserId) } }
    }
}