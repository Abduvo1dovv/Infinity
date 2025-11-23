package com.example.inifnity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.data.network.RetrofitInstance
import com.example.inifnity.data.paging.WallpaperPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WallpaperRepository {
    private val api = RetrofitInstance.api


    suspend fun getTrending(): List<Wallpaper> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrendingWallpapers()
            response.photos.map {
                Wallpaper(
                    id = it.id,
                    imageUrl = it.src.portrait,
                    fullUrl = it.src.original,
                    category = "Trending"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    fun getCategoryStream(query: String): Flow<PagingData<Wallpaper>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { WallpaperPagingSource(api, query) }
        ).flow
    }
}