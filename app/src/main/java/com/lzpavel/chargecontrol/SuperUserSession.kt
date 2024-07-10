package com.lzpavel.chargecontrol

import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class SuperUserSession {

    val LOG_TAG = "SuperUserSession"

    lateinit var process: Process

    lateinit var reader: BufferedReader
    lateinit var readerError: BufferedReader
    lateinit var writer: BufferedWriter

    var isOkExecution = false
    var isOpened = false

    var resultExecution: List<String> = mutableListOf<String>()

    fun open() : Boolean {
        try {
            process = Runtime.getRuntime().exec("su")
            reader = BufferedReader(InputStreamReader(process.inputStream))
            readerError = BufferedReader(InputStreamReader(process.errorStream))
            writer = BufferedWriter(OutputStreamWriter(process.outputStream))
            isOpened = true
        } catch (e: Exception) {
            isOpened = false
            e.printStackTrace()
            Log.d(LOG_TAG, e.toString())
        }
        return isOpened
    }

    fun execute(cmd: String): List<String> {
        if (isOpened) {
            val end = """ ; echo "\n$?\nrxend""""

            writer.write("$cmd$end\n")
            writer.flush()
            resultExecution = receive()
            receiveError()
        }
        return resultExecution

    }

    private fun receiveError() {
        if (readerError.ready()) {
            val charList = mutableListOf<Char>()
            var i: Int = 0
            while (readerError.ready()) {
                i = readerError.read()
                if (i != -1) {
                    charList.add(i.toChar())
                } else {
                    break
                }
            }
            val str = String(charList.toCharArray())
            Log.d(LOG_TAG, str)
        }
    }

    private fun receive(): List<String> {
        val rxList = mutableListOf<String>()
        var rxStr: String? = reader.readLine()

        while (true) {
            if (rxStr == "rxend") {
                val status: Int? = rxList.removeLastOrNull()?.toIntOrNull()
                if (status == 0) {
                    if (rxList.last() == "") {
                        rxList.removeLastOrNull()
                    }
                    isOkExecution = true
                    break
                } else {
                    isOkExecution = false
                    Log.d(LOG_TAG, "SU receive status not 0")
                    break
                }

            }
            if (rxStr == null) {
                isOkExecution = false
                Log.d(LOG_TAG,"SU received null")
                break
            } else {
                rxList.add(rxStr)
            }
            rxStr = reader.readLine()
        }

        resultExecution = rxList
        return rxList
    }

    fun close() {
        if (isOpened) {
            writer.write("exit\n")
            writer.flush()
            process.waitFor()
            isOpened = false
        }
    }
}