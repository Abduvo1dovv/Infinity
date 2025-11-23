package com.example.inifnity.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.data.network.WallpaperApi

class WallpaperPagingSource(
    private val api: WallpaperApi,
    private val query: String
) : PagingSource<Int, Wallpaper>() {

    override fun getRefreshKey(state: PagingState<Int, Wallpaper>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Wallpaper> {
        return try {
            val currentPage = params.key ?: 1


            val response = api.getEditorsChoice(
                query = query,
                perPage = params.loadSize,
                page = currentPage
            )

            val wallpapers = response.photos.map {
                Wallpaper(
                    id = it.id,
                    imageUrl = it.src.portrait,
                    fullUrl = it.src.original,
                    category = query
                )
            }

            LoadResult.Page(
                data = wallpapers,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (wallpapers.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}