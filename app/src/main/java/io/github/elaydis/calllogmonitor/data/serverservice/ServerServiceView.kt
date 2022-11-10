package io.github.elaydis.calllogmonitor.data.serverservice

interface ServerServiceView {

    fun startForeground()

    fun stopForeground()

    fun registerCallBroadcastReceiver()

    fun queryContactForNumber(number: String): String
}