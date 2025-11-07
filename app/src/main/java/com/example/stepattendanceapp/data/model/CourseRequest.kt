package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class CourseRequest(
    @field:Json(name = "code") val code: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "teacher_id") val teacherId: String?,
    @field:Json(name = "academicYear") val academicYear: String,
    @field:Json(name = "semester") val semester: String
)