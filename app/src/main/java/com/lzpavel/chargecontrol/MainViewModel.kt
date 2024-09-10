package com.lzpavel.chargecontrol

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val chargeSettings: ChargeSettings) : ViewModel() {

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

    override fun onCleared() {
        super.onCleared()
    }
}