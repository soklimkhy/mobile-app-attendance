package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// This now matches your Postman register response
data class RegisterResponse(
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "user") val user: User?
)