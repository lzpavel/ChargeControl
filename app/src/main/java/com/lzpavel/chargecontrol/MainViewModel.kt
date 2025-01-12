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
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val LOG_TAG = "MainViewModel"

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

    private var _toastLive = MutableLiveData<String?>()
    val toastLive: LiveData<String?> = _toastLive

    init {
        loadSettings()
    }


    fun loadSettings() {
        viewModelScope.launch {
            dataStoreManager.load()
            postUpdateUI()
            _toastLive.postValue("Load Settings OK")
            Log.d(LOG_TAG, "Load Settings OK")
        }
    }

    fun reloadSettings() {
        viewModelScope.launch {
            dataStoreManager.reload()
            postUpdateUI()
            _toastLive.postValue("Reload Settings OK")
            Log.d(LOG_TAG, "Reload Settings OK")
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            dataStoreManager.save()
            _toastLive.postValue("Save Settings OK")
            Log.d(LOG_TAG, "Save Settings OK")
        }
    }

    fun clearDataStore() {
        viewModelScope.launch {
            dataStoreManager.clear()
            _toastLive.postValue("Clear DataStore OK")
            Log.d(LOG_TAG, "Clear DataStore OK")
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