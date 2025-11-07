package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// This class will wrap the list of students
data class CourseStudentsResponse(
    // We assume the key is "students". If it's "data" or something else, change it here.
    @field:Json(name = "students") val students: List<AdminUser>
)