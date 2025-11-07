package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class ScheduleRequest(
    @field:Json(name = "courseId") val courseId: String,
    @field:Json(name = "dayOfWeek") val dayOfWeek: Int?,
    @field:Json(name = "startTime") val startTime: String,
    @field:Json(name = "endTime") val endTime: String,
    @field:Json(name = "room") val room: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "specificDate") val specificDate: String? = null,
    @field:Json(name = "notes") val notes: String? = null
)