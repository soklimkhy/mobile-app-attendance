package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class AttendanceRecord(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "scheduleId") val scheduleId: String,
    @field:Json(name = "courseId") val courseId: String,
    @field:Json(name = "studentId") val studentId: String,
    @field:Json(name = "fullname") val fullname: String?,
    @field:Json(name = "username") val username: String?,
    @field:Json(name = "date") val date: String,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "time") val time: Long,
    @field:Json(name = "verifiedBy") val verifiedBy: String? = null,
    @field:Json(name = "notes") val notes: String? = null
)


