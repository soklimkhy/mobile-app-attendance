package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class UpdateProfileRequest(
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phoneNumber") val phoneNumber: String,
    @field:Json(name = "gender") val gender: String,
    @field:Json(name = "dateOfBirth") val dateOfBirth: String
)