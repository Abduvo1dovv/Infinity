package com.example.inifnity.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsManager {
    private const val PREF_NAME = "infinity_settings"
    private const val KEY_NOTIFICATIONS = "notifications_enabled"
    private const val KEY_DARK_MODE = "dark_mode_enabled"


    private val _isDarkMode = MutableStateFlow(true)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        _isDarkMode.value = getPrefs(context).getBoolean(KEY_DARK_MODE, true)
    }

    fun isNotificationEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_NOTIFICATIONS, true) // Default: Yoqilgan
    }

    fun setNotificationEnabled(context: Context, isEnabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NOTIFICATIONS, isEnabled).apply()
    }


    fun isDarkModeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DARK_MODE, true) // Default: Yoqilgan
    }

    fun setDarkModeEnabled(context: Context, isEnabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DARK_MODE, isEnabled).apply()
        _isDarkMode.value = isEnabled
    }
}