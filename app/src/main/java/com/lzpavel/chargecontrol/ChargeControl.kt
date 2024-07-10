package com.lzpavel.chargecontrol

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChargeControl (
    val superUserSession: SuperUserSession,
    val job: Job,
    val scope: CoroutineScope,
    val onStop: (() -> Unit)? = null
) {
    private var isWriteAttributes = false
    private var isPluggedPower = false

    private var isStarted = false
    private var isInit = false
    private var isCharged = false

    private var isLowStart = AppConfig.isLowStart
    private var isLowStartStarted = false
    private var lowStartCnt = 0
    private var currentBatteryDefault = "5400000"

    private val mutex = Mutex()

    fun start() {
        if (!isStarted) {
            isStarted = true
            initControl()
        }
    }

    private fun initControl() {
        if (!ChargingDriver.checkWriteAttributes(superUserSession)) {
            ChargingDriver.setWriteAttributes(superUserSession)
            if (ChargingDriver.checkWriteAttributes(superUserSession)) {
                isWriteAttributes = true
            } else {
                isWriteAttributes = false
            }
        } else {
            isWriteAttributes = true
        }

        isInit = isWriteAttributes

        if (isInit) {
            if (isLowStart) {
                startLowStart()
            }
            scope.launch {
                while(isActive && !isCharged) {
                    mutex.withLock {
                        charge()
                        //tick()
                    }
                    delay(3000L)
                }
                while (isActive && isCharged && isPluggedPower) {
                    mutex.withLock {
                        waitUnplug()
                    }
                    delay(3000L)
                }
                stop()

            }

        }



    }

    private fun startLowStart() {

        if (isLowStart) {
            scope.launch {
                mutex.withLock {
                    isLowStartStarted = true
                    currentBatteryDefault = ChargingDriver.getCurrentBattery(superUserSession)
                    ChargingDriver.setCurrentBattery(superUserSession, "${AppConfig.lowStartCurrent}")
                }
                while (isActive && !isPluggedPower) {
                    delay(1000L)
                }
                if (isActive) {
                    delay(5000L)
                }
                stopLowStart()
            }
        }
    }

    private fun stopLowStart() {
        runBlocking {
            mutex.withLock {
                if (isLowStartStarted) {
                    isLowStartStarted = false
                    ChargingDriver.setCurrentBattery(superUserSession, currentBatteryDefault)
                }
            }
        }

    }

    private fun charge() {
        val current = ChargingDriver.getCurrent(superUserSession).toIntOrNull() ?: -1
        val level = ChargingDriver.getLevel(superUserSession).toIntOrNull() ?: -1
        val switch = ChargingDriver.getChargingSwitch(superUserSession)

        if (current > 0 && current != AppConfig.currentLimit) {
            ChargingDriver.setCurrent(superUserSession, AppConfig.currentLimit.toString())
        }

        if (level < AppConfig.levelLimit) {
            if (switch != ChargingDriver.CHARGING_SWITCH_ON_DEFAULT) {
                ChargingDriver.setChargingSwitch(superUserSession, ChargingDriver.CHARGING_SWITCH_ON_DEFAULT)
            }
        } else {
            isCharged = true
        }
    }

    private fun waitUnplug() {
        val current = ChargingDriver.getCurrent(superUserSession).toIntOrNull() ?: -1
        val switch = ChargingDriver.getChargingSwitch(superUserSession)

        if (current > 0) {
            if (switch != ChargingDriver.CHARGING_SWITCH_OFF) {
                ChargingDriver.setChargingSwitch(superUserSession, ChargingDriver.CHARGING_SWITCH_OFF)
            }
        } else {
            isPluggedPower = false
        }
    }

    fun changePluggedState(newState: Boolean) {
        isPluggedPower = newState
        if (isLowStart && !isLowStartStarted && !isCharged && !isPluggedPower) {
            startLowStart()
        }
        if (isCharged && !isPluggedPower) {
            stop()
        }
    }

    fun stop() {
        if (isStarted) {
            isStarted = false
            runBlocking {
                job.cancelAndJoin()
                stopLowStart()
                mutex.withLock {
                    ChargingDriver.setChargingSwitch(superUserSession, ChargingDriver.CHARGING_SWITCH_ON_DEFAULT)
                }
            }
            onStop?.invoke()
        }

    }
}