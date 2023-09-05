package com.example.recycleviewwithclicklistener.Login

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class LoginStatusManager (context: Context) {

    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val USERNAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val ISLOGGEDIN = "isLoggedIn"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLoginDetails(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(EMAIL, email)
        editor.putString(PASSWORD, password)
        editor.putBoolean(ISLOGGEDIN, true)
        editor.apply()
    }

    fun getSavedUsername(): String {
        return sharedPreferences.getString(USERNAME, "") ?: ""
    }

    fun getSavedEmail(): String? {
        return sharedPreferences.getString(EMAIL, null)
    }

    fun getSavedPassword(): String? {
        return sharedPreferences.getString(PASSWORD, null)
    }

    fun clearLoginDetails() {
        val editor = sharedPreferences.edit()
        editor.remove(USERNAME)
        editor.remove(EMAIL)
        editor.remove(PASSWORD)
        editor.putBoolean(ISLOGGEDIN, false)
        editor.apply()
    }
}
