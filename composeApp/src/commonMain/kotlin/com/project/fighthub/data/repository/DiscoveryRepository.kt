package com.project.fighthub.data.repository

import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
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
            val response = supabaseClient.postgrest.rpc(
                "submit_swipe",
                buildJsonObject {
                    put("p_target", targetId)
                    put("p_direction", direction.value)
                }
            )
            Result.success(response.decodeSingle<Boolean>())
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private suspend fun fetchCandidatesFallback(lat: Double, lng: Double, radiusKm: Double, limit: Int): List<Profile> {
        val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
        val profiles = supabaseClient.postgrest["profiles"]
            .select()
            .decodeList<Profile>()
            .asSequence()
            .filter { profile -> profile.id != currentUserId }
            .filter { profile -> profile.lat != null && profile.lng != null }
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
}

enum class SwipeDirection(val value: String) {
    Left("left"),
    Right("right")
}
