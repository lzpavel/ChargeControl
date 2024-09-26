package com.lzpavel.chargecontrol

import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ChargeSettings private constructor(){

    companion object {

        private var instance: ChargeSettings? = null

        fun get() : ChargeSettings {
            if (instance == null) {
                instance = ChargeSettings()
            }
            return instance!!
        }
    }

    val LOG_TAG = "ChargeSettings"

    private val mutex = Mutex()
//    val preferencesDataStore = PreferencesDataStore()
//    @Inject
//    lateinit var preferencesDataStore: PreferencesDataStore

    var isLoaded = false

    var levelLimit = 80
        get() {
            return runBlocking {
                mutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                mutex.withLock {
                    field = value
                }
            }
        }

    var currentLimit = 1500000
        get() {
            return runBlocking {
                mutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                mutex.withLock {
                    field = value
                }
            }
        }

    var isLowStart = false
        get() {
            return runBlocking {
                mutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                mutex.withLock {
                    field = value
                }
            }
        }

    var lowStartCurrent = 500000
        get() {
            return runBlocking {
                mutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                mutex.withLock {
                    field = value
                }
            }
        }

//    fun setSettings(chargeSettingsData: ChargeSettingsData) {
//        levelLimit = chargeSettingsData.levelLimit
//        currentLimit = chargeSettingsData.currentLimit
//        isLowStart = chargeSettingsData.isLowStart
//        lowStartCurrent = chargeSettingsData.lowStartCurrent
//        isLoaded = true
//        Log.d(LOG_TAG, "Settings was set")
//    }

//    fun load() {
//        val chargeSettingsData = preferencesDataStore.load()
//        levelLimit = chargeSettingsData.levelLimit
//        currentLimit = chargeSettingsData.currentLimit
//        isLowStart = chargeSettingsData.isLowStart
//        lowStartCurrent = chargeSettingsData.lowStartCurrent
//        isLoaded = true
//        Log.d(LOG_TAG, "Loaded")
//    }
//
//    fun save() {
//        val chargeSettingsData = ChargeSettingsData(
//            levelLimit,
//            currentLimit,
//            isLowStart,
//            lowStartCurrent
//        )
//        preferencesDataStore.save(chargeSettingsData)
//        Log.d(LOG_TAG, "Saved")
//    }

}