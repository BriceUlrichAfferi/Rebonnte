package com.openclassrooms.rebonnte.notification

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class NotificationViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun enableNotifications() {
        sharedPreferences.edit().putBoolean("notifications_enabled", true).apply()
    }

    fun disableNotifications() {
        sharedPreferences.edit().putBoolean("notifications_enabled", false).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("notifications_enabled", false)
    }
}