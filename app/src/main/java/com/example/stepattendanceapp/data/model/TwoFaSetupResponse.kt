package com.example.stepattendanceapp.data.model

import com.squareup.moshi.Json

data class TwoFaSetupResponse(
    @field:Json(name = "secretKey") val secretKey: String,
    @field:Json(name = "qrCodeUrl") val qrCodeUrl: String
)