package com.lzpavel.chargecontrol.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lzpavel.chargecontrol.MainViewModel
import com.lzpavel.chargecontrol.ui.theme.ChargeControlTheme
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue


@Preview(showBackground = true)
@Composable
fun MainScreen(
    viewModel: MainViewModel? = null,
    onClickSwitchControl: () -> Unit = {}
) {
    ChargeControlTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                SwitchBlock(
                    name = "Control",
                    value = viewModel?.isControlEnabledLive?.observeAsState()?.value ?: false,
                    onClickSwitch = onClickSwitchControl
                )
                HorizontalDivider()
                EditBlock(
                    name = "Level limit",
                    value = viewModel?.levelLimitLive?.observeAsState()?.value ?: "Value",
                    onValueChange = { viewModel?.editLevelLimit(it) },
                    onDone = { viewModel?.setLevelLimit(it) }
                )
                HorizontalDivider()
                EditBlock(
                    name = "Current limit",
                    value = viewModel?.currentLimitLive?.observeAsState()?.value ?: "Value",
                    onValueChange = { viewModel?.editCurrentLimit(it) },
                    onDone = { viewModel?.setCurrentLimit(it) }
                )
                HorizontalDivider()
                SwitchBlock(
                    name = "Low Start",
                    value = viewModel?.isLowStartLive?.observeAsState()?.value ?: false,
                    onClickSwitch = { viewModel?.setLowStartEnabled() }
                )
                HorizontalDivider()
                EditBlock(
                    name = "Low start current",
                    value = viewModel?.lowStartCurrentLive?.observeAsState()?.value ?: "Value",
                    onValueChange = { viewModel?.editLowStartCurrent(it) },
                    onDone = { viewModel?.setLowStartCurrent(it) }
                )

            }
        }
    }
}
