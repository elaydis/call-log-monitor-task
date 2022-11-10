package io.github.elaydis.calllogmonitor.utils

import android.telephony.TelephonyManager

class TelephonyStateWrapperImp : TelephonyStateWrapper {

    override val ringing: String = TelephonyManager.EXTRA_STATE_RINGING
    override val offhook: String = TelephonyManager.EXTRA_STATE_OFFHOOK
    override val idle: String = TelephonyManager.EXTRA_STATE_IDLE
}