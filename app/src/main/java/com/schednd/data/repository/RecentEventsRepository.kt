package com.schednd.data.repository

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentEventsRepository @Inject constructor(
    private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences("recent_events", Context.MODE_PRIVATE)
    }

    fun saveEvent(code: String) {
        val current = getSavedCodes().toMutableSet()
        current.add(code)
        prefs.edit().putStringSet("codes", current).apply()
    }

    fun removeEvent(code: String) {
        val current = getSavedCodes().toMutableSet()
        current.remove(code)
        prefs.edit().putStringSet("codes", current).apply()
    }

    fun getSavedCodes(): Set<String> {
        return prefs.getStringSet("codes", emptySet()) ?: emptySet()
    }
}
