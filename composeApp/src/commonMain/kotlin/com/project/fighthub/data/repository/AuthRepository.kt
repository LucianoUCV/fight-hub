package com.project.fighthub.data.repository

import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    private suspend fun ensureProfileRow(name: String? = null, email: String? = null) {
        val currentUser = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")
        val existing = supabaseClient.postgrest["profiles"]
            .select { filter { eq("id", currentUser.id) } }
            .decodeList<Profile>()

        if (existing.isEmpty()) {
            val jsonInsert = buildJsonObject {
                put("id", currentUser.id)
                put("email", email ?: currentUser.email ?: "")
                if (!name.isNullOrBlank()) {
                    put("name", name)
                }
            }
            supabaseClient.postgrest["profiles"].insert(jsonInsert)
        } else if (!name.isNullOrBlank() || !email.isNullOrBlank()) {
            val jsonUpdate = buildJsonObject {
                if (!name.isNullOrBlank()) {
                    put("name", name)
                }
                if (!email.isNullOrBlank()) {
                    put("email", email)
                }
            }
            if (jsonUpdate.isNotEmpty()) {
                supabaseClient.postgrest["profiles"].update(jsonUpdate) {
                    filter { eq("id", currentUser.id) }
                }
            }
        }
    }

    suspend fun signUp(name: String, email: String, pass: String): Result<Unit> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }

            ensureProfileRow(name = name, email = email)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, pass: String): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            ensureProfileRow(email = email)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileDetails(name: String, age: Int, height: Int, weight: Int, avatarBytes: ByteArray?): Result<Unit> {
        return try {
            ensureProfileRow(name = name)
            val currentUser = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")

            var publicAvatarUrl: String? = null

            if (avatarBytes != null) {
                val imagePath = "${currentUser.id}/avatar.jpg"
                val bucket = supabaseClient.storage["avatars"]

                bucket.upload(imagePath, avatarBytes) { upsert = true }

                publicAvatarUrl = bucket.publicUrl(imagePath)
            }

            val jsonUpdate = buildJsonObject {
                put("name", name)
                put("age", age)
                put("height", height)
                put("weight", weight)

                if (publicAvatarUrl != null) {
                    put("avatar_url", publicAvatarUrl)
                }
            }

            supabaseClient.postgrest["profiles"].update(jsonUpdate) {
                filter { eq("id", currentUser.id) }
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentProfile(): Result<Profile> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")

            val profiles = supabaseClient.postgrest["profiles"]
                .select { filter { eq("id", user.id) } }
                .decodeList<Profile>()

            val profile = if (profiles.isEmpty()) {
                val jsonInsert = buildJsonObject {
                    put("id", user.id)
                    put("email", user.email ?: "")
                }
                supabaseClient.postgrest["profiles"].insert(jsonInsert)

                supabaseClient.postgrest["profiles"]
                    .select { filter { eq("id", user.id) } }
                    .decodeSingle<Profile>()
            } else {
                profiles.first()
            }

            val freshProfile = profile.copy(
                avatarUrl = profile.avatarUrl?.let {
                    val randomToken = kotlin.random.Random.nextInt(10000, 99999)
                    "$it?refresh=$randomToken"
                }
            )

            Result.success(freshProfile)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun updateLocation(lat: Double, lng: Double): Result<Unit> {
        return try {
            ensureProfileRow()
            val currentUser = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")
            val jsonUpdate = buildJsonObject {
                put("lat", lat)
                put("lng", lng)
            }
            supabaseClient.postgrest["profiles"].update(jsonUpdate) {
                filter { eq("id", currentUser.id) }
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}