package com.example.recipeapp.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = "d46f000def9548ef949a60faa50ac586"
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