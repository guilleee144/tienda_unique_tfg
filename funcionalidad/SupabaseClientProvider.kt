package com.example.uniqueartifacts.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

     private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://ddatdozwiwxabnlomnad.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRkYXRkb3p3aXd4YWJubG9tbmFkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDMzMjYwMTksImV4cCI6MjA1ODkwMjAxOX0.vrDm9mxlg69scKMuQ3b_XASYMRVmvGYjftqmRsoinUY"
    ) {
        install(Postgrest)
    }

    fun getClient(): SupabaseClient {
        return client
    }
}
