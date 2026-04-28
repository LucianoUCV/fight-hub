package com.project.fighthub.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

val supabaseClient = createSupabaseClient(
    supabaseUrl = SupabaseSecrets.URL,
    supabaseKey = SupabaseSecrets.ANON_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Realtime)
}