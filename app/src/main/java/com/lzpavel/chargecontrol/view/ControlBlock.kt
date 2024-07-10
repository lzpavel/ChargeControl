package com.lzpavel.chargecontrol.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lzpavel.chargecontrol.MainViewModel

@Preview(showBackground = true)
@Composable
fun ControlBlock(
    mainViewModel: MainViewModel? = null,
    onSwitchControl: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
//        var control by remember { mutableStateOf(ChargingConfig.isControlEnabled) }
//        ChargingConfig.onControlEnabledChanged = {
//            control = it
//        }
//        var control = mainViewModel?.isControl?.observeAsState()?.value ?: false
        var control =
            mainViewModel?.isControlEnabledLive?.observeAsState()?.value ?: false


        Text(text = "Control")
        Switch(
            checked = control,
            onCheckedChange = null,
            modifier = Modifier.clickable {
                onSwitchControl?.invoke()
            }
        )
    }
}