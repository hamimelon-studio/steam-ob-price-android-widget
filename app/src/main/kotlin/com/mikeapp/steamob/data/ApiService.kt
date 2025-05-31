package com.mikeapp.steamob.data

import com.mikeapp.steamob.data.model.SteamPriceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/appdetails/")
    suspend fun getAppDetails(
        @Query("appids") appId: String,
        @Query("cc") cc: String,
        @Query("l") lang: String
    ): Map<String, SteamPriceResponse>
}

// Create the API service
val apiService: ApiService = retrofit.create(ApiService::class.java)
