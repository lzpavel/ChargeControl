package com.lzpavel.chargecontrol

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import com.lzpavel.chargecontrol.view.MainScreen

class MainActivity : ComponentActivity() {
    private val LOG_TAG = "MainActivity"

    private val mainViewModel: MainViewModel by viewModels()

    private val listener = Listener()

    //private var chargingService: ChargingService? = null
    //private var isChargingServiceStarted = false

    private val mainActivityReceiver = MainActivityReceiver()

//    private val chargingServiceConnection = object : ServiceConnection {
//
//        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            val binder = service as ChargingService.ChargingServiceBinder
//            chargingService = binder.getService()
//
////            chargingService?.onStartStop = {
////                mainViewModel.isControlEnabled = it
////            }
////            mainViewModel.isControlEnabled = chargingService?.isStarted ?: false
//
//            Log.d(LOG_TAG, "onServiceConnected")
//        }
//
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            chargingService = null
//            Log.d(LOG_TAG, "onServiceDisconnected")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        //ComponentController.mainActivity = this

        enableEdgeToEdge()
        setContent {
            MainScreen(mainViewModel, listener)
        }

        onBackPressedDispatcher.addCallback(this) {
            // Back is pressed... Finishing the activity
            finish()
            //System.exit(0)
            //finishAffinity()
            //exitProcess(0)
        }

        ChargingNotification.createChannel(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        subscribeReceiver()
        checkForStartService()
//        Intent(this, ChargingService::class.java).also { intent ->
//            bindService(intent, chargingServiceConnection, Context.BIND_AUTO_CREATE)
//        }
    }

    override fun onPause() {
        unsubscribeReceiver()
//        unbindService(chargingServiceConnection)
        super.onPause()
    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
        super.onDestroy()
    }



    fun postNotification() {
        ChargingNotification.show(this, "Test notify")
    }

    private fun checkForStartService() {
        Intent().also {
            it.`package` = packageName
            it.action = "CHARGING_RECEIVER"
            it.putExtra("command", 2)
            sendBroadcast(it)
        }
    }

    fun switchChargingService() {
        if (mainViewModel.isControlEnabled) {
            Intent().also {
                it.`package` = packageName
                it.action = "CHARGING_RECEIVER"
                it.putExtra("command", 1)
                sendBroadcast(it)
            }
        } else {
            startService(Intent(this, ChargingService::class.java))
        }


    }

    fun testSetCurrent() {
        val su = SuperUserSession()
        var isOpenedSu = su.open()
        if (isOpenedSu) {
            var isWriting = ChargingDriver.checkWriteAttributes(su)
            if (!isWriting) {
                ChargingDriver.setWriteAttributes(su)
            }
            isWriting = ChargingDriver.checkWriteAttributes(su)
            if (isWriting) {
                ChargingDriver.setCurrent(su, "1500000")
            }
            su.close()
            isOpenedSu = su.isOpened
        }
    }

    private fun subscribeReceiver() {
        ContextCompat.registerReceiver(
            this,
            mainActivityReceiver,
            IntentFilter("MAIN_ACTIVITY_RECEIVER"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun unsubscribeReceiver() {
        unregisterReceiver(mainActivityReceiver)
    }

    inner class MainActivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "MAIN_ACTIVITY_RECEIVER") {
                mainViewModel.isControlEnabled = intent.getBooleanExtra("isStarted", false)
            }
        }

    }

    inner class Listener() {
        val onTestSetCurrent = { testSetCurrent() }
        val onSwitchControl = { switchChargingService() }
    }
}

