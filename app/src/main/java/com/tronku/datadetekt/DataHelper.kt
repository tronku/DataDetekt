package com.tronku.datadetekt

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.appsearch.StorageInfo
import android.app.usage.StorageStats
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.WebSettings
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.net.*
import java.util.*


object DataHelper {
    fun getImei(telephonyManager: TelephonyManager): String {
        return try {
            telephonyManager.imei
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    fun getImsi(telephonyManager: TelephonyManager): String {
        return try {
            telephonyManager.subscriberId
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    fun getIccid(telephonyManager: TelephonyManager): String {
        return try {
            telephonyManager.subscriptionId.toString()
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    fun getMacAddress(context: Context): String {
        return try {
            val wInfo =
                (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
            wInfo.macAddress
        } catch (e: Exception) {
            ""
        }
    }

    fun getWifiMacAddress(): String {
        try {
            val interfaceName = "wlan0"
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (!intf.name.contentEquals(interfaceName)) {
                    continue
                }
                val mac: ByteArray = intf.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (aMac in mac) {
                    buf.append(String.format("%02X:", aMac))
                }
                if (buf.isNotEmpty()) {
                    buf.deleteCharAt(buf.length - 1)
                }
                return buf.toString()
            }
        } catch (exp: java.lang.Exception) {
            exp.printStackTrace()
        }
        return ""
    }

    fun getBluetoothMacAddress(): String? {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var bluetoothMacAddress: String? = ""
        try {
            val mServiceField: Field = bluetoothAdapter.javaClass.getDeclaredField("mService")
            mServiceField.isAccessible = true
            val btManagerService: Any = mServiceField.get(bluetoothAdapter)
            if (btManagerService != null) {
                bluetoothMacAddress = btManagerService.javaClass.getMethod("getAddress")
                    .invoke(btManagerService) as String
            }
        } catch (e: java.lang.Exception) {
        }
        return bluetoothMacAddress
    }

    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getDiskTotalVolume(): String {
        return StatFs(Environment.getDataDirectory().path).totalBytes.toString()
    }

    fun getDiskFreeVolume(): String {
        return StatFs(Environment.getDataDirectory().path).availableBlocksLong.toString()
    }

    fun getTotalRam(context: Context): String {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem.toString()
    }

    fun getFreeRam(context: Context): String {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.availMem.toString()
    }

    fun getScreenResolution(context: Context): String {
        val width: Int = context.resources.displayMetrics.widthPixels
        val height: Int = context.resources.displayMetrics.heightPixels
        return "$height x $width"
    }

    fun getUserAgent(context: Context): String {
        return WebSettings.getDefaultUserAgent(context);
    }

    fun getDeviceLanguage(): String {
        return Locale.getDefault().language.toString()
    }

    fun getDeviceVolume(context: Context): String {
        val audio = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioObject = JSONObject().apply {
            put("ringer", audio.getStreamVolume(AudioManager.STREAM_RING).toString())
            put("system", audio.getStreamVolume(AudioManager.STREAM_SYSTEM).toString())
            put("music", audio.getStreamVolume(AudioManager.STREAM_MUSIC).toString())
            put("alarm", audio.getStreamVolume(AudioManager.STREAM_ALARM).toString())
            put("call", audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL).toString())
        }
        return audioObject.toString()
    }

    fun getDeviceTimezone(): String {
        return TimeZone.getDefault().id
    }

    fun getLocalTime(): String {
        val currentDate = Calendar.getInstance()
        return (currentDate.timeInMillis + TimeZone.getDefault()
            .getOffset(currentDate.timeInMillis)).toString()
    }

    fun getWifiSSID(context: Context): String {
        return try {
            val wInfo =
                (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
            wInfo.ssid
        } catch (e: Exception) {
            ""
        }
    }

    fun getNetworkType(context: Context): String {
        return try {
            val manager =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            when {
                isWifi(manager) -> {
                    "WIFI"
                }
                isCellular(manager) -> {
                    "MOBILE"
                }
                isVPN(manager) -> {
                    "VPN"
                }
                else -> {
                    "UNKNOWN"
                }
            }
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    private fun isWifi(manager: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.getNetworkCapabilities(manager.activeNetwork)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true &&
                    manager.getNetworkCapabilities(manager.activeNetwork)
                        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            manager.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
        }
    }

    private fun isCellular(manager: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.getNetworkCapabilities(manager.activeNetwork)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true &&
                    manager.getNetworkCapabilities(manager.activeNetwork)
                        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            manager.activeNetworkInfo?.type == ConnectivityManager.TYPE_MOBILE
        }
    }

    private fun isVPN(manager: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.getNetworkCapabilities(manager.activeNetwork)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true &&
                    manager.getNetworkCapabilities(manager.activeNetwork)
                        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            manager.activeNetworkInfo?.type == ConnectivityManager.TYPE_VPN
        }
    }

    fun getScreenBrightness(context: Context): String {
        return Settings.System.getString(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    }

    fun getBatteryLevel(context: Context): String {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
    }

    fun getBatteryState(context: Context): String {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return when (intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
            BatteryManager.BATTERY_STATUS_FULL -> "FULL"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT CHARGING"
            else -> "UNKNOWN"
        }
    }

    fun getSensorCount(context: Context): String {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getSensorList(Sensor.TYPE_ALL).size.toString()
    }

    fun isUsbDebuggingEnabled(context: Context): String {
        return (Settings.Secure.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1).toString()
    }

    fun isDevOptionsEnabled(context: Context): String {
        return (Settings.Secure.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) == 1).toString()
    }

    fun getABI(): String {
        return if (Build.SUPPORTED_ABIS.isNotEmpty())
            Build.SUPPORTED_ABIS[0]
        else
            "UNKNOWN"
    }

    fun getCpuCores(): String {
        return Runtime.getRuntime().availableProcessors().toString()
    }

    fun getCPUDetails(): String {
        val processBuilder: ProcessBuilder
        var cpuDetails = ""
        val DATA = arrayOf("/system/bin/cat", "/proc/cpuinfo")
        val inputStream: InputStream
        val process: Process
        val bArray = ByteArray(1024)
        try {
            processBuilder = ProcessBuilder(*DATA)
            process = processBuilder.start()
            inputStream = process.inputStream
            while (inputStream.read(bArray) !== -1) {
                val info = String(bArray).split("\n")
                info.firstOrNull { it.contains("Hardware") }?.let { cpuDetails += it }

            }
            inputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return cpuDetails
    }

    fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
        }
        return "UNKNOWN"
    }

    fun getLocalIpAddressV6(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
        }
        return "UNKNOWN"
    }

}