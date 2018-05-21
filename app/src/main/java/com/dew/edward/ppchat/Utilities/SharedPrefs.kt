package com.dew.edward.ppchat.Utilities

import android.content.Context

/*
 * Created by Edward on 5/20/2018.
 */

class SharedPrefs(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    var password: String
        get() = prefs.getString(USER_PASSWORD, "")
        set(value) = prefs.edit().putString(USER_PASSWORD, value).apply()

    var selectedChannelId: String
        get() = prefs.getString(SELECTED_CHANNEL_ID, "")
        set(value) = prefs.edit().putString(SELECTED_CHANNEL_ID, value).apply()

    var userId: String
        get() = prefs.getString(USER_ID, "")
        set(value) = prefs.edit().putString(USER_ID, value).apply()
}