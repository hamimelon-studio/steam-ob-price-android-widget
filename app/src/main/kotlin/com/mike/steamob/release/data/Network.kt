package com.mike.steamob.release.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Create a logging interceptor
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

// Create OkHttpClient with logging
val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

// Set up Retrofit with the OkHttpClient
val retrofit = Retrofit.Builder()
    .baseUrl("https://store.steampowered.com/") // Replace with your base URL
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()