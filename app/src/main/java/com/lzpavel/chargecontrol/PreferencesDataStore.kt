package com.lzpavel.chargecontrol

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val chargeSettings: ChargeSettings
)  {

//    private val EXAMPLE_KEY = stringPreferencesKey("example_key")
    private val LEVEL_LIMIT = intPreferencesKey("level_limit")
    private val CURRENT_LIMIT = intPreferencesKey("current_limit")
    private val LOW_START_CURRENT = intPreferencesKey("low_start_current")
    private val IS_LOW_START= booleanPreferencesKey("is_low_start")


//    val exampleFlow: Flow<String> = dataStore.data.map { preferences ->
//        preferences[EXAMPLE_KEY] ?: "default_value"
//    }

    suspend fun load() {
        dataStore.data.map { pref ->
            //pref[EXAMPLE_KEY] ?: "default_value"
            pref[LEVEL_LIMIT]?.let {
                chargeSettings.levelLimit = it
            }
            pref[CURRENT_LIMIT]?.let {
                chargeSettings.currentLimit = it
            }
            pref[IS_LOW_START]?.let {
                chargeSettings.isLowStart = it
            }
            pref[LOW_START_CURRENT]?.let {
                chargeSettings.lowStartCurrent = it
            }
        }.first()
    }

    suspend fun save() {
        dataStore.edit { pref ->
            pref[LEVEL_LIMIT] = chargeSettings.levelLimit
            pref[CURRENT_LIMIT] = chargeSettings.currentLimit
            pref[IS_LOW_START] = chargeSettings.isLowStart
            pref[LOW_START_CURRENT] = chargeSettings.lowStartCurrent
        }
    }
}