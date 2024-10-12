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
//const val loggedIn = "isLoggedIn"
//const val firstTime = "isFirstTime"
//const val uid = "userId"
//const val email = "email"

private val Context.datastore by preferencesDataStore(name = appsettings)

class AppDatastore @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val _isLoggedIn = booleanPreferencesKey(keys[0])
    private val _isFirstTime = booleanPreferencesKey(keys[1])
    private val _userId = stringPreferencesKey(keys[2])
    private val _userEmail = stringPreferencesKey(keys[3])
    private val _locationSharing = booleanPreferencesKey(keys[4])
    private val _rememberUser = booleanPreferencesKey(keys[5])

    suspend fun saveFirstTime() {
        context.datastore.edit { prefs ->
            prefs[_isFirstTime] = false
        }
    }


    suspend fun loginUser() {
        val user = firebaseAuth.currentUser
        context.datastore.edit { prefs ->
            prefs[_userId] = user!!.uid
            prefs[_userEmail] = user.email.toString()
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
    val userEmail = context.datastore.data.map { prefs ->
        prefs[_userEmail]
    }

    suspend fun toggleLocation(value: Boolean) {
        context.datastore.edit { prefs ->
            prefs[_locationSharing] = value
        }
    }

    suspend fun toggleRememberMe(value: Boolean) {
        context.datastore.edit { prefs ->
            prefs[_rememberUser] = value
        }
    }

    val locationSharing = context.datastore.data.map { prefs ->
        prefs[_locationSharing]?: false
    }

    val rememberUser = context.datastore.data.map { prefs ->
        prefs[_rememberUser]?: false
    }

    companion object {
        val keys = listOf(
            "isLoggedIn",
            "isFirstTime",
            "userId",
            "email",
            "locationSharing",
            "rememberMe"
        )
    }
}
