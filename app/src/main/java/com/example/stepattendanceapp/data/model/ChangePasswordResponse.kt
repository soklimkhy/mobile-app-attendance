package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// For the success response: {"message": "Password updated successfully"}
data class ChangePasswordResponse(
    @field:Json(name = "message") val message: String?
)