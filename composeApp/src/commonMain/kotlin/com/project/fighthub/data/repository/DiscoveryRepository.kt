package com.project.fighthub.data.repository

import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class DiscoveryRepository {

    suspend fun getCandidates(lat: Double, lng: Double, radiusKm: Double, limit: Int = 20): Result<List<Profile>> {
        return try {
            val response = supabaseClient.postgrest.rpc(
                "get_discovery_candidates",
                buildJsonObject {
                    put("p_lat", lat)
                    put("p_lng", lng)
                    put("p_radius_km", radiusKm)
                    put("p_limit", limit)
                }
            )
            val candidates = response.decodeList<Profile>()
            if (candidates.isNotEmpty()) {
                Result.success(candidates)
            } else {
                Result.success(fetchCandidatesFallback(lat, lng, radiusKm, limit))
            }
        } catch (e: Throwable) {
            Result.success(fetchCandidatesFallback(lat, lng, radiusKm, limit))
        }
    }

    suspend fun submitSwipe(targetId: String, direction: SwipeDirection): Result<Boolean> {
        return try {
            Result.success(submitSwipeFallback(targetId, direction))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun submitSwipeFallback(targetId: String, direction: SwipeDirection): Boolean {
        val currentUserId = getCurrentUserId() ?: return false

        return try {
            val swipeInsert = buildJsonObject {
                put("swiper_id", currentUserId)
                put("target_id", targetId)
                put("direction", direction.value)
            }
            supabaseClient.postgrest["swipes"].insert(swipeInsert)

            if (direction != SwipeDirection.Right) {
                return false
            }

            val reciprocal = supabaseClient.postgrest["swipes"]
                .select {
                    filter {
                        eq("swiper_id", targetId)
                        eq("target_id", currentUserId)
                        eq("direction", SwipeDirection.Right.value)
                    }
                    limit(1)
                }
                .decodeList<SwipeRow>()
                .isNotEmpty()

            if (!reciprocal) {
                return false
            }

            println("MATCH_RECIPROCAL_FOUND: $currentUserId <-> $targetId")

            val (user1Id, user2Id) = normalizedPair(currentUserId, targetId)
            val existingMatch = supabaseClient.postgrest["matches"]
                .select {
                    filter {
                        eq("user1_id", user1Id)
                        eq("user2_id", user2Id)
                    }
                    limit(1)
                }
                .decodeList<MatchRow>()
                .isNotEmpty()

            if (existingMatch) {
                println("MATCH_ALREADY_EXISTS: $user1Id/$user2Id")
                return true
            }

            val matchInsert = buildJsonObject {
                put("user1_id", user1Id)
                put("user2_id", user2Id)
                put("status", "active")
            }
            val insertResult = runCatching { supabaseClient.postgrest["matches"].insert(matchInsert) }

            if (insertResult.isFailure) {
                println("MATCH_INSERT_FAILED: ${insertResult.exceptionOrNull()?.message}")
            } else {
                println("MATCH_INSERT_OK: $user1Id/$user2Id")
            }

            val matchConfirmed = runCatching {
                supabaseClient.postgrest["matches"]
                    .select {
                        filter {
                            eq("user1_id", user1Id)
                            eq("user2_id", user2Id)
                        }
                        limit(1)
                    }
                    .decodeList<MatchRow>()
                    .isNotEmpty()
            }.getOrDefault(false)

            if (!matchConfirmed) {
                println("MATCH_CONFIRM_FAILED: $user1Id/$user2Id")
            }

            matchConfirmed
        } catch (e: Throwable) {
            println("Swipe insert failed: ${e.message}")
            false
        }
    }

    private fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
            ?: supabaseClient.auth.currentSessionOrNull()?.user?.id
    }

    private suspend fun fetchCandidatesFallback(lat: Double, lng: Double, radiusKm: Double, limit: Int): List<Profile> {
        val currentUserId = getCurrentUserId()
        val swipedIds = runCatching {
            supabaseClient.postgrest["swipes"]
                .select {
                    filter { eq("swiper_id", currentUserId ?: "") }
                }
                .decodeList<SwipeRow>()
                .mapNotNull { it.target_id }
                .toSet()
        }.getOrDefault(emptySet())

        val profiles = supabaseClient.postgrest["profiles"]
            .select()
            .decodeList<Profile>()
            .asSequence()
            .filter { profile -> profile.id != currentUserId }
            .filter { profile -> profile.lat != null && profile.lng != null }
            .filter { profile -> !swipedIds.contains(profile.id) }
            .map { profile ->
                profile to distanceKm(lat, lng, profile.lat!!, profile.lng!!)
            }
            .filter { (_, distance) -> distance <= radiusKm }
            .sortedBy { (_, distance) -> distance }
            .map { (profile, _) -> profile }
            .take(limit)
            .toList()

        return profiles
    }

    private fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2.0)
        val c = 2 * asin(sqrt(a))
        return earthRadiusKm * c
    }

    private fun normalizedPair(a: String, b: String): Pair<String, String> {
        return if (a <= b) a to b else b to a
    }
}

enum class SwipeDirection(val value: String) {
    Left("left"),
    Right("right")
}

@Serializable
private data class MatchRow(
    val id: String? = null,
    val user1_id: String? = null,
    val user2_id: String? = null,
    val status: String? = null,
    val winner_id: String? = null,
    val created_at: String? = null
)

@Serializable
private data class SwipeRow(
    val id: String? = null,
    val swiper_id: String? = null,
    val target_id: String? = null,
    val direction: String? = null,
    val created_at: String? = null
)
