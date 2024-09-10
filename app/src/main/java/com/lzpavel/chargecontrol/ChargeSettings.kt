package com.lzpavel.chargecontrol

class ChargeSettings private constructor(){

    companion object {

        private var instance: ChargeSettings? = null

        fun get() : ChargeSettings {
            if (instance == null) {
                instance = ChargeSettings()
            }
            return instance!!
        }
    }

    var levelLimit = 80
    var currentLimit = 1500000

    var isLowStart = false
    var lowStartCurrent = 500000

}