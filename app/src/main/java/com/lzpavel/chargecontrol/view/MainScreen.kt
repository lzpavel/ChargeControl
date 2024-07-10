package com.lzpavel.chargecontrol.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lzpavel.chargecontrol.MainActivity
import com.lzpavel.chargecontrol.MainViewModel
import com.lzpavel.chargecontrol.ui.theme.ChargeControlTheme
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue


@Preview(showBackground = true)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel? = null,
    maListener: MainActivity.Listener? = null
) {
    ChargeControlTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                ControlBlock(
                    mainViewModel,
                    maListener?.onSwitchControl
                )
                Divider()
                LevelLimitBlock()
                Divider()
                CurrentLimitBlock()
                Divider()
                LowStartBlock()
                Divider()
                LowStartCurrentBlock()
                Divider()
                Button(
                    onClick = {
                        maListener?.onTestSetCurrent?.invoke()
                    },

                    ) {
                    Text(text = "Test set current")
                }
                Button(
                    onClick = {

                    },

                    ) {
                    Text(text = "Test")
                }

            }

        }
    }
}