package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// This now perfectly matches your Postman login response
data class AuthResponse(
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "user") val user: User?,
    @field:Json(name = "accessToken") val accessToken: String?,
    @field:Json(name = "refreshToken") val refreshToken: String?,
    @field:Json(name = "error") val error: String?
)