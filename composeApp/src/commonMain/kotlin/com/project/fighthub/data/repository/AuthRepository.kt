package com.project.fighthub.data.repository

import com.project.fighthub.data.model.Profile
import com.project.fighthub.data.network.supabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

class AuthRepository {
    suspend fun signUp(name: String, email: String, password: String): Result<Unit> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: throw Exception("User was created but could not be retrieved")

            val initialProfile = Profile(id = userId, email = email, name = name)
            supabaseClient.postgrest["profiles"].insert(initialProfile)

            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileDetails(age: Int, height: Int, weight: Int): Result<Unit> {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull() ?: throw Exception("Not logged in")

            val updateData = mapOf(
                "age" to age,
                "height" to height,
                "weight" to weight
            )

            supabaseClient.postgrest["profiles"].update(updateData) {
                filter { eq("id", currentUser.id) }
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}