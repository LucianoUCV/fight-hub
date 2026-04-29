package com.project.fighthub.data.repository

import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
            Result.success(response.decodeList<Profile>())
        } catch (e: Throwable) {
            Result.failure(e)
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
}

enum class SwipeDirection(val value: String) {
    Left("left"),
    Right("right")
}
