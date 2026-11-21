package com.vi.teamconnect.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_EMPLOYEE_ID = stringPreferencesKey("employee_id")
        val KEY_USER_ROLE = stringPreferencesKey("user_role")
    }

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    val employeeId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMPLOYEE_ID]
    }

    val userRole: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ROLE]
    }

    suspend fun saveUserSession(userId: String, employeeId: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_EMPLOYEE_ID] = employeeId
            prefs[KEY_USER_ROLE] = role
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
