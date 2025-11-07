package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class TwoFaVerifyResponse(
    @field:Json(name = "message") val message: String?
)