package com.example.stepattendanceapp.data.model

// This import is available if you need to map
// fields like "student_id" to "studentId",
// but for this JSON, it's not strictly required
// as the names already match.
import com.squareup.moshi.Json

data class TeacherAttendanceRecord(
    val courseId: String,
    val createdAt: Long,
    val date: String,
    val notes: String?,
    val scheduleId: String,
    val status: String,
    val studentId: String,
    val time: Long,
    val updatedAt: Long,
    val verifiedBy: String?,

    // This is the key fix:
    // It's nullable and defaults to null, so Moshi
    // won't crash when the 'id' field is missing.
    val id: String? = null
)