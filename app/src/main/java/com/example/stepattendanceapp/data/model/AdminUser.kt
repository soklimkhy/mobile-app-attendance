package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class AdminUser(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "username") val username: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "fullName") val fullName: String?,
    @field:Json(name = "role") val role: String?,
    @field:Json(name = "active") val active: Boolean?,
    @field:Json(name = "twoFactorEnabled") val twoFactorEnabled: Boolean?
)