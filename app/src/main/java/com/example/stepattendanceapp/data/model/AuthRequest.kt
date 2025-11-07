package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class AuthRequest(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "otp") val otp: String? = null // ADDED: Nullable OTP field
)