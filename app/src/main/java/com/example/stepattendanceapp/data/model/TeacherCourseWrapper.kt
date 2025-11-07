package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

// This class matches your API response: [ { "course": {...} } ]
data class TeacherCourseWrapper(
    @field:Json(name = "course") val course: CourseDetail
)