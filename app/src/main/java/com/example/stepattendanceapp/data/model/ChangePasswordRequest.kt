package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class ChangePasswordRequest(
    @field:Json(name = "currentPassword") val currentPassword: String,
    @field:Json(name = "newPassword") val newPassword: String,
    @field:Json(name = "confirmPassword") val confirmPassword: String
)