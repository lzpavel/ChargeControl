package com.lzpavel.chargecontrol

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val chargeSettings: ChargeSettings,
//    private val dataStore: DataStore<Preferences>,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val LOG_TAG = "MainViewModel"

//    private val EXAMPLE_KEY = stringPreferencesKey("example_key")

//    val exampleFlow: Flow<String> = dataStore.data.map { preferences ->
//        preferences[EXAMPLE_KEY] ?: "default_value"
//    }

    private var _isControlEnabledLive = MutableLiveData(ChargingService.isStarted)
    val isControlEnabledLive: LiveData<Boolean> = _isControlEnabledLive

    private var _isLowStartLive = MutableLiveData(chargeSettings.isLowStart)
    val isLowStartLive: LiveData<Boolean> = _isLowStartLive

    private var _levelLimitLive = MutableLiveData(chargeSettings.levelLimit.toString())
    val levelLimitLive: LiveData<String> = _levelLimitLive

    private var _currentLimitLive = MutableLiveData(chargeSettings.currentLimit.toString())
    val currentLimitLive: LiveData<String> = _currentLimitLive

    private var _lowStartCurrentLive = MutableLiveData(chargeSettings.lowStartCurrent.toString())
    val lowStartCurrentLive: LiveData<String> = _lowStartCurrentLive

//    fun saveExampleValue(value: String) {
//        viewModelScope.launch {
//            dataStore.edit { preferences ->
//                preferences[EXAMPLE_KEY] = value
//            }
//        }
//    }

//    private val LEVEL_LIMIT = intPreferencesKey("level_limit")
//    private val CURRENT_LIMIT = intPreferencesKey("current_limit")
//    private val LOW_START_CURRENT = intPreferencesKey("low_start_current")
//    private val IS_LOW_START= booleanPreferencesKey("is_low_start")


    fun loadSettings() {
        viewModelScope.launch {
//            dataStore.data.map { pref ->
//                //pref[EXAMPLE_KEY] ?: "default_value"
//                pref[LEVEL_LIMIT]?.let {
//                    chargeSettings.levelLimit = it
//                }
//                pref[CURRENT_LIMIT]?.let {
//                    chargeSettings.currentLimit = it
//                }
//                pref[IS_LOW_START]?.let {
//                    chargeSettings.isLowStart = it
//                }
//                pref[LOW_START_CURRENT]?.let {
//                    chargeSettings.lowStartCurrent = it
//                }
//
//            }.first()
            dataStoreManager.load()
            postUpdateUI()
            Log.d(LOG_TAG, "Load OK")
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
//            dataStore.edit { pref ->
//                pref[LEVEL_LIMIT] = chargeSettings.levelLimit
//                pref[CURRENT_LIMIT] = chargeSettings.currentLimit
//                pref[IS_LOW_START] = chargeSettings.isLowStart
//                pref[LOW_START_CURRENT] = chargeSettings.lowStartCurrent
//            }
            dataStoreManager.save()
            Log.d(LOG_TAG, "Save OK")
        }
    }

    fun editLevelLimit(value: String) {
        _levelLimitLive.value = value
    }

    fun setLevelLimit(value: String) {
        val lim = value.toIntOrNull() ?: -1
        if (lim in 0..100) {
            chargeSettings.levelLimit = lim
        }
        _levelLimitLive.value = chargeSettings.levelLimit.toString()
    }

    fun editCurrentLimit(value: String) {
        _currentLimitLive.value = value
    }

    fun setCurrentLimit(value: String) {
        val lim = value.toIntOrNull() ?: -1
        if (lim in 0..3500000) {
            chargeSettings.currentLimit = lim
        }
        _currentLimitLive.value = chargeSettings.currentLimit.toString()
    }

    fun editLowStartCurrent(value: String) {
        _lowStartCurrentLive.value = value
    }

    fun setLowStartCurrent(value: String) {
        val lim = value.toIntOrNull() ?: -1
        if (lim in 0..5400000) {
            chargeSettings.lowStartCurrent = lim
        }
        _lowStartCurrentLive.value = chargeSettings.lowStartCurrent.toString()
    }

    fun setLowStartEnabled() {
        chargeSettings.isLowStart = !chargeSettings.isLowStart
        _isLowStartLive.value = chargeSettings.isLowStart
    }

    fun setControlEnabled() {
        _isControlEnabledLive.value = ChargingService.isStarted
    }

    fun updateUI() {
        _isControlEnabledLive.value = ChargingService.isStarted
        _isLowStartLive.value = chargeSettings.isLowStart
        _levelLimitLive.value = chargeSettings.levelLimit.toString()
        _currentLimitLive.value = chargeSettings.currentLimit.toString()
        _lowStartCurrentLive.value = chargeSettings.lowStartCurrent.toString()

    }

    fun postUpdateUI() {
        _isControlEnabledLive.postValue(ChargingService.isStarted)
        _isLowStartLive.postValue(chargeSettings.isLowStart)
        _levelLimitLive.postValue(chargeSettings.levelLimit.toString())
        _currentLimitLive.postValue(chargeSettings.currentLimit.toString())
        _lowStartCurrentLive.postValue(chargeSettings.lowStartCurrent.toString())

    }

    override fun onCleared() {
        super.onCleared()
    }
}