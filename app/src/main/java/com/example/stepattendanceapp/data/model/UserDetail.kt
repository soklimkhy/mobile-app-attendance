package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class UserDetail(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "username") val username: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "fullName") val fullName: String?,
    @field:Json(name = "photoUrl") val photoUrl: String?,
    @field:Json(name = "phoneNumber") val phoneNumber: String?,
    @field:Json(name = "gender") val gender: String?,
    @field:Json(name = "dateOfBirth") val dateOfBirth: String?,
    @field:Json(name = "role") val role: String?
)