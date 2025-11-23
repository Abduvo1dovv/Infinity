package com.example.inifnity.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.inifnity.data.model.Wallpaper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object FavoritesManager {
    private const val PREF_NAME = "infinity_favorites"
    private const val KEY_FAVORITES = "favorite_wallpapers"

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Wallpaper::class.java)
    private val jsonAdapter = moshi.adapter<List<Wallpaper>>(listType)

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context): List<Wallpaper> {
        val json = getPrefs(context).getString(KEY_FAVORITES, null)
        return if (json != null) {
            try {
                jsonAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun toggleFavorite(context: Context, wallpaper: Wallpaper): Boolean {
        val currentList = getFavorites(context).toMutableList()
        val exists = currentList.any { it.id == wallpaper.id }

        if (exists) {
            currentList.removeAll { it.id == wallpaper.id }
        } else {
            currentList.add(0, wallpaper)
        }

        val json = jsonAdapter.toJson(currentList)
        getPrefs(context).edit().putString(KEY_FAVORITES, json).apply()

        return !exists
    }

    fun isFavorite(context: Context, wallpaperId: Int): Boolean {
        return getFavorites(context).any { it.id == wallpaperId }
    }
}