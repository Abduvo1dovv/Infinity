package com.example.inifnity.data.network

import com.example.inifnity.data.model.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WallpaperApi {

    companion object {

        const val AUTH_HEADER = "Authorization: B2HLODs8SHSLmm6h4DRV3EbuiIYg7wdQRwR7XX5YtZY2qrjd2nt4StpJ"
    }


    @Headers(AUTH_HEADER)
    @GET("v1/curated")
    suspend fun getTrendingWallpapers(
        @Query("per_page") perPage: Int = 30
    ): PexelsResponse


    @Headers(AUTH_HEADER)
    @GET("v1/search")
    suspend fun getEditorsChoice(
        @Query("query") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): PexelsResponse


    @Headers(AUTH_HEADER)
    @GET("v1/search")
    suspend fun searchWallpapers(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20
    ): PexelsResponse
}