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
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel : ViewModel() {
    private val repository = WallpaperRepository()

    private val _query = MutableStateFlow("")

    val searchResults: Flow<PagingData<Wallpaper>> = _query.flatMapLatest { query ->
        if (query.isNotEmpty()) {
            repository.getCategoryStream(query)
        } else {
            repository.getCategoryStream("Curated") // Default
        }
    }.cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        _query.value = query
    }
}