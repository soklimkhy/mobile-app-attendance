package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class AttendanceRequest(
    @field:Json(name = "scheduleId") val scheduleId: String,
    @field:Json(name = "courseId") val courseId: String,
    @field:Json(name = "studentId") val studentId: String,
    @field:Json(name = "date") val date: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "time") val time: Long,
    @field:Json(name = "notes") val notes: String?
)