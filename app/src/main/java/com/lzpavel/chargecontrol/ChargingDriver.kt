package com.lzpavel.chargecontrol

object ChargingDriver {

    const val FILE_LEVEL = "/sys/class/power_supply/battery/capacity"
    const val FILE_CURRENT = "/sys/class/power_supply/main/current_max"
    const val FILE_CURRENT_BATTERY = "/sys/class/power_supply/battery/constant_charge_current_max"
    const val FILE_MMI_CHARGING_ENABLE = "/sys/class/power_supply/battery/mmi_charging_enable"
    const val FILE_INPUT_SUSPEND = "/sys/class/power_supply/battery/input_suspend"

    const val CHARGING_SWITCH_ON_DEFAULT = 0
    const val CHARGING_SWITCH_OFF = 1
    const val CHARGING_SWITCH_MAINTAIN = 2
    const val CHARGING_SWITCH_INVALID = 3

    fun checkWriteAttributes(su: SuperUserSession) : Boolean {
        val regex = Regex("-rw.*")
        val lst = su.execute("ls -l $FILE_CURRENT $FILE_CURRENT_BATTERY")
        return regex.matches(lst[0]) && regex.matches(lst[1])

//        val lst = su.execute("ls -l $FILE_CURRENT")
//        if (regex.matches(lst[0]) && lst.size == 1) {
//            return true
//        } else {
//            return false
//        }
    }

    fun setWriteAttributes(su: SuperUserSession) {
        su.execute("chmod u+w $FILE_CURRENT $FILE_CURRENT_BATTERY")
    }

    fun getCurrent(su: SuperUserSession) : String {
        return su.execute("cat $FILE_CURRENT")[0]
    }

    fun setCurrent(su: SuperUserSession, value: String) {
        su.execute("echo $value > $FILE_CURRENT")
    }

    fun getCurrentBattery(su: SuperUserSession) : String {
        return su.execute("cat $FILE_CURRENT_BATTERY")[0]
    }

    fun setCurrentBattery(su: SuperUserSession, value: String) {
        su.execute("echo $value > $FILE_CURRENT_BATTERY")
    }

    fun getLevel(su: SuperUserSession) : String {
        return su.execute("cat $FILE_LEVEL")[0]
    }

    fun getChargingSwitch(su: SuperUserSession) : Int {
        val rx = su.execute("cat $FILE_MMI_CHARGING_ENABLE; cat $FILE_INPUT_SUSPEND")
        val mmiChargingEnable = rx[0]
        val inputSuspend = rx[1]
        if (mmiChargingEnable == "1" && inputSuspend == "0") {
            return CHARGING_SWITCH_ON_DEFAULT
        } else if (mmiChargingEnable == "0" && inputSuspend == "1") {
            return CHARGING_SWITCH_OFF
        } else if (mmiChargingEnable == "0" && inputSuspend == "0") {
            return CHARGING_SWITCH_MAINTAIN
        } else {
            return CHARGING_SWITCH_INVALID
        }
    }

    fun setChargingSwitch(su: SuperUserSession, mode: Int) {
        when(mode) {
            CHARGING_SWITCH_ON_DEFAULT -> {
                su.execute("echo 0 > $FILE_INPUT_SUSPEND; echo 1 > $FILE_MMI_CHARGING_ENABLE")
            }
            CHARGING_SWITCH_OFF -> {
                su.execute("echo 0 > $FILE_MMI_CHARGING_ENABLE; echo 1 > $FILE_INPUT_SUSPEND")
            }
            CHARGING_SWITCH_MAINTAIN -> {
                su.execute("echo 0 > $FILE_INPUT_SUSPEND; echo 0 > $FILE_MMI_CHARGING_ENABLE")
            }
            CHARGING_SWITCH_INVALID -> {
                su.execute("echo 1 > $FILE_INPUT_SUSPEND; echo 1 > $FILE_MMI_CHARGING_ENABLE")
            }
            else -> {
                su.execute("echo 0 > $FILE_INPUT_SUSPEND; echo 1 > $FILE_MMI_CHARGING_ENABLE")
            }
        }
    }
}