package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// This model is for the PUT request, where all fields are optional
data class UpdateScheduleRequest(
    @field:Json(name = "dayOfWeek") val dayOfWeek: Int?,
    @field:Json(name = "startTime") val startTime: String?,
    @field:Json(name = "endTime") val endTime: String?,
    @field:Json(name = "room") val room: String?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "specificDate") val specificDate: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "notes") val notes: String?
)