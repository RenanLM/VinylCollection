package br.com.renan.vinylcollection.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.core.stringPreferencesKey

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }

    val sortOrder: Flow<String> = context.dataStore.data.map { it[SORT_ORDER_KEY] ?: "RECENT" }

    suspend fun toggleDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }

    suspend fun updateSortOrder(order: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = order
        }
    }
}