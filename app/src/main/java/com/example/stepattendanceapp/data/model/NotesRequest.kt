package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// Used for cancel/complete endpoints
data class NotesRequest(
    @field:Json(name = "notes") val notes: String?
)