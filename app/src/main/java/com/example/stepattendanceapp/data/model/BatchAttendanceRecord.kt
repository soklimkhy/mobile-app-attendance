package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class BatchAttendanceRecord(
    @field:Json(name = "studentId") val studentId: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "notes") val notes: String?
)