package com.bigfun.tm

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*

/**
 * 获取ip地址
 */
fun getIp(context: Context): String {
    var ip = ""
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    val wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return when {
        mobile?.isConnected == true -> {
            ip = getLocalIp()
            ip
        }
        wifi?.isConnected == true -> {
            val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ip = getWifiIp(wm.connectionInfo.ipAddress)
            ip
        }
        else -> {
            ip
        }
    }
}

/**
 * 移动网络下获取ip
 */
fun getLocalIp(): String {
    var ipv4 = ""
    val list = Collections.list(NetworkInterface.getNetworkInterfaces())
    list.forEach { it ->
        val childList = Collections.list(it.inetAddresses)
        childList.forEach {
            if (!it.isLoopbackAddress && it is Inet4Address) {
                ipv4 = it.hostAddress
                return ipv4
            }
        }
    }
    return ipv4
}

/**
 * 获取wifi状态下的ip地址
 */
fun getWifiIp(ip: Int): String {
    val sb = StringBuilder()
    sb.append(ip and 0xFF).append(".")
    sb.append(ip shr 8 and 0xFF).append(".")
    sb.append(ip shr 16 and 0xFF).append(".")
    sb.append(ip shr 24 and 0xFF)
    return sb.toString()
}

fun throwException(message: String) {
    throw BigFunSDKException("$PAY_TAG$message")
}