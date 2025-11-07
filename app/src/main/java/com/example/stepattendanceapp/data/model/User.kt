package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class User(
    @field:Json(name = "id") val id: String,
    // CHANGED: from username to fullName
    @field:Json(name = "fullName") val fullName: String,
    @field:Json(name = "role") val role: String
)