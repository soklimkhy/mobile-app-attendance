package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class TeacherStudentResponse(
    @field:Json(name = "students") val students: List<AdminUser>
)