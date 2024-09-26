package com.lzpavel.chargecontrol

data class ChargeSettingsData(
    var levelLimit: Int = 80,
    var currentLimit: Int = 1500000,
    var isLowStart: Boolean = false,
    var lowStartCurrent: Int = 500000
)
