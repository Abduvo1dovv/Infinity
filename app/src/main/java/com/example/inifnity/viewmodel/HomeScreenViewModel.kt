package com.example.inifnity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.data.repository.WallpaperRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = WallpaperRepository()

    private val _trendingWallpapers = MutableStateFlow<List<Wallpaper>>(emptyList())
    val trendingWallpapers = _trendingWallpapers.asStateFlow()

    init {
        fetchTrending()
    }

    private fun fetchTrending() {
        viewModelScope.launch {
            _trendingWallpapers.value = repository.getTrending()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModel : ViewModel() {
    private val repository = WallpaperRepository()

    val categoriesList = listOf("Abstract", "Nature", "Cars", "Technology", "Space", "Dark", "Minimalism", "Neon")


    private val _selectedCategory = MutableStateFlow(categoriesList.first())
    val selectedCategory = _selectedCategory.asStateFlow()


    val wallpapers: Flow<PagingData<Wallpaper>> = _selectedCategory.flatMapLatest { query ->
        repository.getCategoryStream(query)
    }.cachedIn(viewModelScope)


    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}