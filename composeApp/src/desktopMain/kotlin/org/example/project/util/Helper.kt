package org.example.project.util


import java.net.Inet4Address
import java.net.NetworkInterface


object Helper {

    fun getLocalIpAddress(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces().toList()
        for (iface in interfaces) {
            val addresses = iface.inetAddresses.toList()
            for (addr in addresses) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
        return null
    }


}