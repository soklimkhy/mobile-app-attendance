package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// This model can handle both {"error": "..."} and {"errors": [...]}
data class ApiErrorResponse(
    @field:Json(name = "error") val error: String?,
    @field:Json(name = "errors") val errors: List<String>?
)