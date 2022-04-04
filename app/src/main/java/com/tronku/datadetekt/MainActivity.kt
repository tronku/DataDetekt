package com.tronku.datadetekt

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.TextView
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private var textView: TextView? = null
    private val telephonyManager by lazy { getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.text)
        addData()
    }

    private fun addData() {
        val data = StringBuilder()
        data.apply {
            append(getDeviceIds()).append("\n")
            append(deviceInfo()).append("\n")
            append(getCPUInfo()).append("\n")
            append(getDiskData()).append("\n")
            append(getNetworkOperatorData()).append("\n")
            append(getExtraData()).append("\n")
            append(getTimeData()).append("\n")
        }

        textView?.text = data.toString()
    }

    private fun getDeviceIds(): String {
        return "IMEI: ${DataHelper.getImei(telephonyManager)}\n" +
                "IMSI: ${DataHelper.getImsi(telephonyManager)}\n" +
                "ICCID: ${DataHelper.getIccid(telephonyManager)}\n" +
                "MAC ADDRESS: ${DataHelper.getMacAddress(this)}\n" +
                "WIFI MAC ADDRESS: ${DataHelper.getWifiMacAddress()}\n" +
                "BLUETOOTH MAC ADDRESS: ${DataHelper.getBluetoothMacAddress()}\n" +
                "ANDROID ID: ${DataHelper.getAndroidID(this)}\n" +
                "WIFI SSID: ${DataHelper.getWifiSSID(this)}\n" +
                "LOCAL IP: ${DataHelper.getLocalIpAddress()}\n" +
                "LOCAL IP v6: ${DataHelper.getLocalIpAddressV6()}\n"

    }

    private fun deviceInfo(): String {
        return "DEVICE NAME: ${Build.DEVICE}\n" +
                "DEVICE MANUFACTURER: ${Build.MANUFACTURER}\n" +
                "DEVICE MODEL: ${Build.MODEL}\n" +
                "USER AGENT: ${DataHelper.getUserAgent(this)}\n" +
                "BUILD FINGERPRINT: ${Build.FINGERPRINT}\n" +
                "BUILD BOARD: ${Build.BOARD}\n" +
                "BUILD ID: ${Build.ID}\n" +
                "BUILD HOST: ${Build.HOST}\n" +
                "BUILD HARDWARE: ${Build.HARDWARE}\n"
    }

    private fun getCPUInfo(): String {
        return "CPU ABI: ${DataHelper.getABI()}\n" +
                "CPU CORES: ${DataHelper.getCpuCores()}\n" +
                "CPU DETAILS: ${DataHelper.getCPUDetails()}\n"
    }

    private fun getDiskData(): String {
        return "TOTAL DISK SIZE (BYTES): ${DataHelper.getDiskTotalVolume()}\n" +
                "FREE DISK SIZE (BYTES): ${DataHelper.getDiskFreeVolume()}\n" +
                "TOTAL RAM (BYTES): ${DataHelper.getTotalRam(this)}\n" +
                "FREE RAM SIZE (BYTES): ${DataHelper.getFreeRam(this)}\n"
    }

    private fun getExtraData(): String {
        return "SCREEN RESOLUTION: ${DataHelper.getScreenResolution(this)}\n" +
                "DEVICE LANGUAGE: ${DataHelper.getDeviceLanguage()}\n" +
                "DEVICE VOLUME: ${DataHelper.getDeviceVolume(this)}\n" +
                "DEVICE BRIGHTNESS: ${DataHelper.getScreenBrightness(this)}\n" +
                "BATTERY LEVEL: ${DataHelper.getBatteryLevel(this)}\n" +
                "BATTERY STATE: ${DataHelper.getBatteryState(this)}\n" +
                "SENSOR COUNT: ${DataHelper.getSensorCount(this)}\n" + "" +
                "IS USB DEBUGGING ON: ${DataHelper.isUsbDebuggingEnabled(this)}\n" +
                "IS DEV OPTIONS ON: ${DataHelper.isDevOptionsEnabled(this)}\n"
    }

    private fun getNetworkOperatorData(): String {
        return "NETWORK OPERATOR NAME: ${telephonyManager.networkOperatorName}\n" +
                "NETWORK OPERATOR CODE: ${telephonyManager.networkOperator}\n" +
                "NETWORK TYPE: ${DataHelper.getNetworkType(this)}\n"
    }


    private fun getTimeData(): String {
        return "DEVICE TIMEZONE: ${DataHelper.getDeviceTimezone()}\n" +
                "LOCAL TIME: ${DataHelper.getLocalTime()}\n" +
                "TIME SINCE REBOOT: ${SystemClock.elapsedRealtime()}\n"
    }

    private fun getSimData(): String {
        return "SIM CARD: "
    }

}