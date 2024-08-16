package com.lzpavel.chargecontrol

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

//    var isControlEnabled = false
//        set(value) {
//            field = value
//            //_isControlEnabledLive.value = value
//            _isControlEnabledLive.postValue(value)
//        }
    private var _isControlEnabledLive = MutableLiveData<Boolean>(AppConfig.isStartedChargingService)
    val isControlEnabledLive: LiveData<Boolean> = _isControlEnabledLive

//    init {
//        AppConfig.onControlEnabledChanged = {
//            _isControlEnabledLive.postValue(it)
//            //_isControlEnabledLive.value = it
//            //isControlEnabled = it
//        }
//    }

    fun updateUI() {
        if (isControlEnabledLive.value != AppConfig.isStartedChargingService) {
            _isControlEnabledLive.value = AppConfig.isStartedChargingService
        }
    }



    override fun onCleared() {
        //AppConfig.onControlEnabledChanged = null

        super.onCleared()
    }
}