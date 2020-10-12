package com.bigfun.tm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static String getIp(Context context) {
        String ip = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobile.isConnected()) {
            getLocalIp();
        } else if (wifi.isConnected()) {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            getWifiIp(wm.getConnectionInfo().getIpAddress());
        }
        return ip;
    }

    /**
     * 移动网络下获取ip
     */
    public static String getLocalIp() {
        String ipv4 = "";
        List<NetworkInterface> list = null;
        try {
            list = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : list) {
                ArrayList<InetAddress> childList = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : childList) {
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipv4 = inetAddress.getHostAddress();
                        return ipv4;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipv4;
    }

    /**
     * 获取wifi状态下的ip地址
     */
    private static String getWifiIp(int ip) {
        StringBuilder sb = new StringBuilder();
        sb.append(ip & 0xFF).append(".");
        sb.append(ip >> 8 & 0xFF).append(".");
        sb.append(ip >> 16 & 0xFF).append(".");
        sb.append(ip >> 24 & 0xFF);
        return sb.toString();
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            name = "1.0.0";
        }
        return name;
    }
}
