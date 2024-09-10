package com.lzpavel.chargecontrol

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import com.lzpavel.chargecontrol.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val LOG_TAG = "MainActivity"

    val mainViewModel: MainViewModel by viewModels()

    private val mainActivityReceiver = MainActivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")

        enableEdgeToEdge()
        setContent {
            MainScreen(
                mainViewModel,
                onClickSwitchControl = { switchChargingService() }
            )

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
        mainViewModel.updateUI()
    }

    override fun onPause() {
        unsubscribeReceiver()
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

    private fun switchChargingService() {
        if (ChargingService.isStarted) {
            Intent().also {
                it.`package` = packageName
                it.action = "CHARGING_SERVICE_RECEIVER"
                it.putExtra("command", "stop")
                sendBroadcast(it)
            }
        } else {
            startService(Intent(this, ChargingService::class.java))
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
                if (intent.getStringExtra("command") == "update_switch") {
                    mainViewModel.setControlEnabled()
                }
            }
        }

    }
}

