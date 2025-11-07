package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class TwoFaVerifyRequest(
    @field:Json(name = "code") val code: String
)