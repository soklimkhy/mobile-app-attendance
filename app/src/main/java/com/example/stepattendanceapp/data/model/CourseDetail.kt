package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class CourseDetail(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "code") val code: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "teacherId") val teacherId: String?,
    @field:Json(name = "academicYear") val academicYear: String?,
    @field:Json(name = "semester") val semester: String?,
    @field:Json(name = "active") val active: Boolean?,
    @field:Json(name = "studentIds") val studentIds: List<String>?
)