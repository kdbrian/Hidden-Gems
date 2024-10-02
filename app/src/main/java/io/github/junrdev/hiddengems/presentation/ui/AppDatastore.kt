package io.github.junrdev.hiddengems.presentation.ui

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val appsettings = "appsettings"
const val loggedIn = "isLoggedIn"
const val firstTime = "isFirstTime"
const val uid = "userId"

private val Context.datastore by preferencesDataStore(name = appsettings)

class AppDatastore @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val _isLoggedIn = booleanPreferencesKey(loggedIn)
    private val _isFirstTime = booleanPreferencesKey(firstTime)
    private val _userId = stringPreferencesKey(uid)

    suspend fun saveFirstTime() {
        context.datastore.edit { prefs ->
            prefs[_isFirstTime] = false
        }
    }


    suspend fun loginUser() {
        val user = firebaseAuth.currentUser
        context.datastore.edit { prefs ->
            prefs[_userId] = user!!.uid
            prefs[_isLoggedIn] = true
        }
    }


    suspend fun logoutUser() {
        context.datastore.edit { prefs ->
            prefs[_userId] = ""
            prefs[_isLoggedIn] = false
        }
    }

    val isFirstTime = context.datastore.data.map { prefs ->
        prefs[_isFirstTime] ?: false
    }

    val isLoggedIn = context.datastore.data.map { prefs ->
        prefs[_isLoggedIn] ?: false
    }

    val userId = context.datastore.data.map { prefs ->
        prefs[_userId]
    }
}
