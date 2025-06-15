package com.example.avisonline.util

// Simple object to hold the token.
// For a production app, use Jetpack DataStore for persistence.
object TokenManager {
    var token: String? = null

    fun getAuthHeader(): String {
        return "Bearer $token"
    }
}