package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class ProfileResponse(
    @field:Json(name = "user") val user: UserDetail?
)