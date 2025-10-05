package com.example.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://giicoo.ru/api/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    inline fun <reified T> createService(): T = instance.create(T::class.java)
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()
        requestBuilder.addHeader(
            "Authorization",
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3NjU5M2FkMy05ZTZiLTQ2MDgtODViOS1hY2IyMjkyOGJiYzEiLCJlbWFpbCI6ImFkbWluIiwicHJvdmlkZXIiOiJrZXljbG9hayIsImV4cCI6MTc1OTczMjQyNywiaXNzIjoiZmFzdGFwaS1hcHAiLCJpYXQiOjE3NTk2NDYwMjd9.hx6SxWxxFNrQmM-gOTBJ7s6_Km7mmAOPLq3e-Xhobp8"
        )
        requestBuilder
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }


}