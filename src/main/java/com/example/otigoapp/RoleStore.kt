package com.example.otigoapp.ui.auth.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

enum class Role { Parent, Expert }

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class RoleStore(private val context: Context) {
    private val KEY_ROLE = stringPreferencesKey("role")

    suspend fun setRole(role: Role) {
        context.dataStore.edit { it[KEY_ROLE] = role.name }
    }

    // İstersen başka yerlerde kullanırsın
    suspend fun getRoleOrNull(): Role? {
        val v = context.dataStore.data.first()[KEY_ROLE] ?: return null
        return runCatching { Role.valueOf(v) }.getOrNull()
    }
}
