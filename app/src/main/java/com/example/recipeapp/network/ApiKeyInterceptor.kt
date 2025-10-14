package com.example.recipeapp.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = "2d5c7c7c64e34010a0748f7fb898cf8e"
        val original = chain.request()
        val newUrl = original.url.newBuilder()
            .addQueryParameter("apiKey", apiKey)
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}