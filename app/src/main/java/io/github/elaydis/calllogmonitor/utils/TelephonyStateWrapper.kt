package io.github.elaydis.calllogmonitor.utils

interface TelephonyStateWrapper {

    val ringing: String
    val offhook: String
    val idle: String
}