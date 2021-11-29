package com.csds393.yacht.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object Settings {
    private const val SETTINGS_FILE_NAME = "com.csds393.yacht.PREFERENCE_FILE_KEY"

    private lateinit var preferences: SharedPreferences

    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE).apply { edit {
            if (! contains("theme")) putString("theme", "dark")
        } }
    }

    @JvmStatic
    fun getPreferences() = preferences
}
