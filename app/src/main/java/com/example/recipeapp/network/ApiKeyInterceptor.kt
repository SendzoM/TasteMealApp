package com.example.recipeapp.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = "ae4fd6a66a1d4f8aadf2366b706feb06"
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