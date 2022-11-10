package io.github.elaydis.calllogmonitor.data.serverservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class CallBroadcastReceiver(
    private val callback: (phoneState: String, phoneNumber: String) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        if (state != null && number != null) {
            callback(state, number)
        }
    }
}