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

    suspend fun signUp(name: String, email: String, pass: String): Result<Unit> {
        return try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }

            val currentUser = supabaseClient.auth.currentUserOrNull()
            if (currentUser != null) {
                val jsonUpdate = buildJsonObject {
                    put("name", name)
                }
                supabaseClient.postgrest["profiles"].update(jsonUpdate) {
                    filter { eq("id", currentUser.id) }
                }
            }

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
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileDetails(age: Int, height: Int, weight: Int, avatarBytes: ByteArray?): Result<Unit> {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")

            var publicAvatarUrl: String? = null

            if (avatarBytes != null) {
                val imagePath = "${currentUser.id}/avatar.jpg"
                val bucket = supabaseClient.storage["avatars"]

                bucket.upload(imagePath, avatarBytes) { upsert = true }

                publicAvatarUrl = bucket.publicUrl(imagePath)
            }

            val jsonUpdate = buildJsonObject {
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

            val profile = supabaseClient.postgrest["profiles"]
                .select { filter { eq("id", user.id) } }
                .decodeSingle<Profile>()

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
}