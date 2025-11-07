package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class UpdateRoleRequest(
    @field:Json(name = "role") val role: String
)