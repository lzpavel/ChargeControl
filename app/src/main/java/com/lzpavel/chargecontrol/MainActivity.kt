package com.lzpavel.chargecontrol

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.lzpavel.chargecontrol.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
                onClickSwitchControl = { switchChargingService() },
                onClickSave = { save() },
                onClickLoad = { load() }
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

    private fun save() {
        //Save
        mainViewModel.saveSettings()
//        mainViewModel.saveExampleValue("my_first_value")
    }
    private fun load() {
        //Load
        mainViewModel.loadSettings()
//        lifecycleScope.launch {
//            mainViewModel.exampleFlow.collect { value ->
//                Toast.makeText(this@MainActivity, value, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun test() {
//        val exampleData = runBlocking { dataStore.data.first() }
//        //Save
//        mainViewModel.saveExampleValue("my_first_value")
//
//        //Load
//        lifecycleScope.launch {
//            mainViewModel.exampleFlow.collect { value ->
//                Toast.makeText(this@MainActivity, value, Toast.LENGTH_SHORT).show()
//            }
//        }

//        val json = Json.encodeToString(MyData.serializer(), MyData("John", 30))
//        Log.d(LOG_TAG, json)
//        val newData = Json.decodeFromString(MyData.serializer(), json)
//        Log.d(LOG_TAG, newData.toString())
//
//        val json2 = Json.encodeToString(MyData("Tom", 42))
//        Log.d(LOG_TAG, json2)
//        val rx2 = Json.decodeFromString<MyData>(json2)
//        Log.d(LOG_TAG, rx2.toString())
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

