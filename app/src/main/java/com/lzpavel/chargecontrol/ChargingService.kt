package com.lzpavel.chargecontrol

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking

class ChargingService : Service() {

    val LOG_TAG = "ChargingService"

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val chargingServiceBinder = ChargingServiceBinder()
    private val chargingServiceReceiver = ChargingServiceReceiver()
    private val powerConnectionReceiver = PowerConnectionReceiver()

    private var superUserSession: SuperUserSession? = null
    var chargeControl: ChargeControl? = null

    private var isStarted = false
        set(value) {
            field = value
            AppConfig.isStartedChargingService = value
            notifyStarted()
//            if (field != value) {
//                field = value
//                notifyStarted()
//            } else {
//                field = value
//            }

        }


    override fun onCreate() {
        super.onCreate()
        subscribeReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand")
        startForeground(1, ChargingNotification.build(this, "level limit: limit value"))
        isStarted = true
        openSu()

        if (superUserSession != null) {
            chargeControl = ChargeControl(
                superUserSession!!,
                job,
                scope
            ) {
                stopChargingService()
            }
            chargeControl!!.start()

        }


        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(LOG_TAG, "onBind")
        return chargingServiceBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(LOG_TAG, "onRebind")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        stopChargingService()
        Log.d(LOG_TAG, "onDestroy")
        super.onDestroy()
    }

    fun stopChargingService() {
        if (isStarted) {
            isStarted = false
            runBlocking {
                job.cancelAndJoin()
            }
            stopChargeControl()
            closeSu()
            unsubscribeReceiver()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    fun stopChargeControl() {
        if (chargeControl != null) {
            chargeControl!!.stop()
            chargeControl = null
        }
    }

    fun openSu() {
        try {
            val su = SuperUserSession()
            if (su.open()) {
                val test = su.execute("echo test123")[0]
                if (test == "test123") {
                    superUserSession = su
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(LOG_TAG, e.toString())
        }
    }

    fun closeSu() {
        if (superUserSession != null) {
            if (superUserSession!!.isOpened) {
                superUserSession!!.close()
                superUserSession = null
            }
        }
    }

    private fun notifyStarted() {
        Intent().also {
            it.`package` = packageName
            it.action = "MAIN_ACTIVITY_RECEIVER"
//            it.putExtra("isStarted", isStarted)
            it.putExtra("command", "updateUI")
            sendBroadcast(it)
        }
    }

    private fun subscribeReceiver() {

        val powerIntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        ContextCompat.registerReceiver(
            this,
            powerConnectionReceiver,
            powerIntentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        ContextCompat.registerReceiver(
            this,
            chargingServiceReceiver,
            IntentFilter("CHARGING_SERVICE_RECEIVER"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun unsubscribeReceiver() {
        unregisterReceiver(powerConnectionReceiver)
        unregisterReceiver(chargingServiceReceiver)
    }

    inner class ChargingServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CHARGING_SERVICE_RECEIVER") {
                val command = intent.getStringExtra("command")
                when (command) {
                    "stop" -> stopChargingService()
                    "ping" -> notifyStarted()
                }
            }
        }
    }

    inner class PowerConnectionReceiver : BroadcastReceiver() {

        val LOG_TAG_R = "PowerConnectionReceiver"

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_POWER_CONNECTED) {
                chargeControl?.changePluggedState(true)
                Log.d(LOG_TAG_R, "ACTION_POWER_CONNECTED")
            } else if (intent?.action == Intent.ACTION_POWER_DISCONNECTED) {
                chargeControl?.changePluggedState(false)
                Log.d(LOG_TAG_R, "ACTION_POWER_DISCONNECTED")
            }
        }
    }

    inner class ChargingServiceBinder : Binder() {
        fun getService(): ChargingService = this@ChargingService
    }
}