package com.example.screens.network

import com.example.screens.data.DirectionsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("mode") mode: String = "walking"
    ): DirectionsResponse

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"

        fun create(): DirectionsApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(DirectionsApiService::class.java)
        }
    }
}