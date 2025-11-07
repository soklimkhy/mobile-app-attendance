package com.example.stepattendanceapp.data.remote

import android.content.Context
import com.example.stepattendanceapp.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionManager = SessionManager(context)
        val token = sessionManager.fetchAuthToken()

        val requestBuilder = chain.request().newBuilder()

        // If a token exists, add it to the header
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}