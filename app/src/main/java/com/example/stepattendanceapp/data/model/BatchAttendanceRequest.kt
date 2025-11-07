package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class BatchAttendanceRequest(
    @field:Json(name = "scheduleId") val scheduleId: String,
    @field:Json(name = "attendanceRecords") val attendanceRecords: List<BatchAttendanceRecord>
)