package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.RedirectMode
import com.example.model.SupportedApp
import com.example.model.VoidSettings
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.voidDataStore: DataStore<Preferences> by preferencesDataStore(name = "void_settings")

class VoidSettingsRepository private constructor(private val context: Context) {

    private object Keys {
        val MASTER_ENABLED = booleanPreferencesKey("master_enabled")
        val INSTAGRAM_ENABLED = booleanPreferencesKey("instagram_enabled")
        val YOUTUBE_ENABLED = booleanPreferencesKey("youtube_enabled")
        val FACEBOOK_ENABLED = booleanPreferencesKey("facebook_enabled")
        val REDIRECT_MODE = stringPreferencesKey("redirect_mode")
        val SHOW_INTERCEPTION_NOTICE = booleanPreferencesKey("show_interception_notice")
        val INSTAGRAM_BLOCKED_COUNT = intPreferencesKey("instagram_blocked_count")
        val YOUTUBE_BLOCKED_COUNT = intPreferencesKey("youtube_blocked_count")
        val FACEBOOK_BLOCKED_COUNT = intPreferencesKey("facebook_blocked_count")
    }

    val settingsFlow: Flow<VoidSettings> = context.voidDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            VoidSettings(
                masterEnabled = prefs[Keys.MASTER_ENABLED] ?: true,
                instagramEnabled = prefs[Keys.INSTAGRAM_ENABLED] ?: true,
                youtubeEnabled = prefs[Keys.YOUTUBE_ENABLED] ?: true,
                facebookEnabled = prefs[Keys.FACEBOOK_ENABLED] ?: true,
                redirectMode = RedirectMode.fromKey(prefs[Keys.REDIRECT_MODE]),
                showInterceptionNotice = prefs[Keys.SHOW_INTERCEPTION_NOTICE] ?: true,
                instagramBlockedCount = prefs[Keys.INSTAGRAM_BLOCKED_COUNT] ?: 0,
                youtubeBlockedCount = prefs[Keys.YOUTUBE_BLOCKED_COUNT] ?: 0,
                facebookBlockedCount = prefs[Keys.FACEBOOK_BLOCKED_COUNT] ?: 0,
            )
        }

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.voidDataStore.edit { prefs ->
            prefs[Keys.MASTER_ENABLED] = enabled
        }
    }

    suspend fun setAppEnabled(app: SupportedApp, enabled: Boolean) {
        context.voidDataStore.edit { prefs ->
            val key = when (app) {
                SupportedApp.INSTAGRAM -> Keys.INSTAGRAM_ENABLED
                SupportedApp.YOUTUBE -> Keys.YOUTUBE_ENABLED
                SupportedApp.FACEBOOK -> Keys.FACEBOOK_ENABLED
            }
            prefs[key] = enabled
        }
    }

    suspend fun setRedirectMode(mode: RedirectMode) {
        context.voidDataStore.edit { prefs ->
            prefs[Keys.REDIRECT_MODE] = mode.key
        }
    }

    suspend fun setShowInterceptionNotice(show: Boolean) {
        context.voidDataStore.edit { prefs ->
            prefs[Keys.SHOW_INTERCEPTION_NOTICE] = show
        }
    }

    suspend fun incrementBlockedCount(app: SupportedApp) {
        context.voidDataStore.edit { prefs ->
            val key = when (app) {
                SupportedApp.INSTAGRAM -> Keys.INSTAGRAM_BLOCKED_COUNT
                SupportedApp.YOUTUBE -> Keys.YOUTUBE_BLOCKED_COUNT
                SupportedApp.FACEBOOK -> Keys.FACEBOOK_BLOCKED_COUNT
            }
            prefs[key] = (prefs[key] ?: 0) + 1
        }
    }

    suspend fun resetBlockedCounts() {
        context.voidDataStore.edit { prefs ->
            prefs[Keys.INSTAGRAM_BLOCKED_COUNT] = 0
            prefs[Keys.YOUTUBE_BLOCKED_COUNT] = 0
            prefs[Keys.FACEBOOK_BLOCKED_COUNT] = 0
        }
    }

    companion object {
        @Volatile
        private var instance: VoidSettingsRepository? = null

        fun getInstance(context: Context): VoidSettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: VoidSettingsRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
