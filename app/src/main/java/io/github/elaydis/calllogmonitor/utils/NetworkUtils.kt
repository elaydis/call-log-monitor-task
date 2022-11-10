package io.github.elaydis.calllogmonitor.utils

import java.net.InetAddress
import java.net.NetworkInterface

// source https://medium.com/geekculture/android-embedded-server-with-ktor-26e7c576263d
// and matching git repo https://github.com/nphau/android.embeddedserver

object NetworkUtils {

    fun getLocalIpAddress(): String? = getInetAddresses()
        .filter { it.isLocalAddress() }
        .map { it.hostAddress }
        .firstOrNull()

    private fun getInetAddresses() = NetworkInterface.getNetworkInterfaces()
        .iterator()
        .asSequence()
        .flatMap { networkInterface ->
            networkInterface.inetAddresses
                .asSequence()
                .filter { !it.isLoopbackAddress }
        }.toList()
}

fun InetAddress.isLocalAddress(): Boolean {
    try {
        return isSiteLocalAddress
                && !hostAddress!!.contains(":")
                && hostAddress != "127.0.0.1"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}