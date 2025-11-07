package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// Generic response for delete operations
data class DeleteResponse(
    @field:Json(name = "message") val message: String
)