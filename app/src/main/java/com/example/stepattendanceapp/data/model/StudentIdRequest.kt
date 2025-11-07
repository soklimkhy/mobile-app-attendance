package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class StudentIdRequest(
    @field:Json(name = "studentIds") val studentIds: List<String>
)