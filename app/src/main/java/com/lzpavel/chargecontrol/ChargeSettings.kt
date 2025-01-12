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



}