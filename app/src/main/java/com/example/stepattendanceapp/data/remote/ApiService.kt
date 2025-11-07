package com.example.stepattendanceapp.data.remote

import com.example.stepattendanceapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- 1. Auth Endpoints (General) ---
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<RegisterResponse>

    // --- 2. User Profile Endpoints (General) ---
    @GET("api/user/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("api/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ProfileResponse>

    @PUT("api/user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>

    @POST("api/user/2fa/setup")
    suspend fun setupTwoFa(): Response<TwoFaSetupResponse>

    @POST("api/user/2fa/verify")
    suspend fun verifyTwoFa(@Body request: TwoFaVerifyRequest): Response<TwoFaVerifyResponse>

    // --- 3. Attendance Endpoints (General) ---
    @GET("api/attendance/{id}")
    suspend fun getAttendanceById(@Path("id") attendanceId: String): Response<AttendanceRecord>

    // This is the general endpoint for fetching records
    @GET("api/attendance/schedule/{scheduleId}")
    suspend fun getAttendanceBySchedule(@Path("scheduleId") scheduleId: String): Response<List<AttendanceRecord>>

    @POST("api/attendance")
    suspend fun saveAttendance(@Body request: AttendanceRequest): Response<AttendanceRecord>

    @PUT("api/attendance/{id}")
    suspend fun updateAttendance(
        @Path("id") attendanceId: String,
        @Body request: AttendanceRequest
    ): Response<AttendanceRecord>

    @DELETE("api/attendance/{id}")
    suspend fun deleteAttendance(@Path("id") attendanceId: String): Response<DeleteResponse>

    // --- 4. Admin Endpoints (ADMIN Role) ---
    @GET("api/admin/users")
    suspend fun getAllUsers(): Response<List<AdminUser>>

    @PUT("api/admin/users/{userId}")
    suspend fun updateUserRole(
        @Path("userId") userId: String,
        @Body request: UpdateRoleRequest
    ): Response<AdminUser>

    @GET("api/admin/courses")
    suspend fun getAllCoursesAsAdmin(): Response<List<CourseDetail>>

    @GET("api/admin/courses/{id}")
    suspend fun getCourseById(@Path("id") courseId: String): Response<CourseDetail>

    @POST("api/admin/courses")
    suspend fun createCourse(@Body request: CourseRequest): Response<CourseDetail>

    @PUT("api/admin/courses/{id}")
    suspend fun updateCourse(
        @Path("id") courseId: String,
        @Body request: CourseRequest
    ): Response<CourseDetail>

    @DELETE("api/admin/courses/{id}")
    suspend fun deleteCourse(@Path("id") courseId: String): Response<Unit>

    @GET("api/admin/courses/{courseId}/students")
    suspend fun getStudentsForCourse(@Path("courseId") courseId: String): Response<CourseStudentsResponse>

    @POST("api/admin/courses/{courseId}/students")
    suspend fun addStudentsToCourse(
        @Path("courseId") courseId: String,
        @Body request: StudentIdRequest
    ): Response<Unit>

    @PUT("api/admin/courses/{courseId}/students")
    suspend fun replaceStudentsInCourse(
        @Path("courseId") courseId: String,
        @Body request: StudentIdRequest
    ): Response<Unit>

    @DELETE("api/admin/courses/{courseId}/students/{studentId}")
    suspend fun removeStudentFromCourse(
        @Path("courseId") courseId: String,
        @Path("studentId") studentId: String
    ): Response<Unit>

    @GET("api/admin/schedules/course/{courseId}")
    suspend fun getSchedulesForCourse(@Path("courseId") courseId: String): Response<List<Schedule>>

    @POST("api/admin/schedules/course/{courseId}")
    suspend fun createSchedule(
        @Path("courseId") courseId: String,
        @Body request: ScheduleRequest
    ): Response<Schedule>

    @GET("api/admin/schedules/{id}")
    suspend fun getScheduleById(@Path("id") scheduleId: String): Response<Schedule>

    @PUT("api/admin/schedules/{id}")
    suspend fun updateSchedule(
        @Path("id") scheduleId: String,
        @Body request: UpdateScheduleRequest
    ): Response<Schedule>

    @DELETE("api/admin/schedules/{id}")
    suspend fun deleteSchedule(@Path("id") scheduleId: String): Response<Unit>

    @POST("api/admin/schedules/{id}/cancel")
    suspend fun cancelSchedule(
        @Path("id") scheduleId: String,
        @Body request: NotesRequest
    ): Response<Schedule>

    @POST("api/admin/schedules/{id}/complete")
    suspend fun completeSchedule(
        @Path("id") scheduleId: String,
        @Body request: NotesRequest
    ): Response<Schedule>

    @GET("api/admin/schedules/course/{courseId}/date/{YYYY-MM-DD}")
    suspend fun getSchedulesForDate(
        @Path("courseId") courseId: String,
        @Path("YYYY-MM-DD") date: String
    ): Response<List<Schedule>>

    // --- 5. Teacher Endpoints (TEACHER Role) ---
    @GET("api/teacher/courses")
    suspend fun getCoursesForTeacher(): Response<List<TeacherCourseWrapper>>
    @GET("api/teacher/courses/student/{studentId}")
    suspend fun getStudentDetailsForTeacher(@Path("studentId") studentId: String): Response<TeacherStudentResponse>
    @GET("api/teacher/schedules/course/{courseId}")
    suspend fun getSchedulesForTeacherCourse(@Path("courseId") courseId: String): Response<List<Schedule>>
    @GET("api/teacher/attendance/schedule/{courseId}")
    //suspend fun getAttendanceForTeacherCourse(@Path("courseId") scheduleId: String): Response<List<AttendanceRecord>>
    suspend fun getAttendanceForTeacherCourse(@Path("courseId") scheduleId: String): Response<List<TeacherAttendanceRecord>>



    // --- 6. Student Endpoints (STUDENT Role) ---
    @GET("api/courses")
    suspend fun getAllCourses(): Response<List<CourseDetail>>
    @POST("api/teacher/courses/{courseId}/attendance")
    suspend fun updateBatchAttendance(
        @Path("courseId") courseId: String,
        @Body request: BatchAttendanceRequest
    ): Response<Unit>

    @GET("api/user/course")
    suspend fun getEnrolledCourses(): Response<List<CourseDetail>>

    @GET("api/user/schedule/course/{courseId}")
    suspend fun getSchedulesForStudentCourse(@Path("courseId") courseId: String): Response<List<Schedule>>
    @GET("api/user/attendance/course/{courseId}")
    suspend fun getStudentAttendanceForCourse(@Path("courseId") courseId: String): Response<List<AttendanceRecord>>
}