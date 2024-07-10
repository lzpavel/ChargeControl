package com.lzpavel.chargecontrol.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.lzpavel.chargecontrol.AppConfig

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun LevelLimitBlock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        //var levelLimitStr by remember { mutableStateOf("80") }
        var levelLimitStr by remember { mutableStateOf("${AppConfig.levelLimit}") }
        fun setLimit() {
            val lim = levelLimitStr.toIntOrNull() ?: -1
            if (lim in 0..100) {
                AppConfig.levelLimit = lim
            } else {
                levelLimitStr = AppConfig.levelLimit.toString()
            }
        }
        OutlinedTextField(
            value = levelLimitStr,
            onValueChange = { levelLimitStr = it },
            label = { Text(text = "Level limit") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Decimal

            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    setLimit()
                }
            )
        )
        Button(onClick = {
            keyboardController?.hide()
            focusManager.clearFocus()
            setLimit()
        }) {
            Text(text = "Set")
        }

    }
}