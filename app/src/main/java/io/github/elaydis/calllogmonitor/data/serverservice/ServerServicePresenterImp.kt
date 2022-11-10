package io.github.elaydis.calllogmonitor.data.serverservice

import android.telephony.TelephonyManager.EXTRA_STATE_IDLE
import android.telephony.TelephonyManager.EXTRA_STATE_RINGING
import io.github.elaydis.calllogmonitor.data.server.Server
import io.github.elaydis.calllogmonitor.utils.Clock
import io.github.elaydis.calllogmonitor.utils.TelephonyStateWrapper
import javax.inject.Inject

class ServerServicePresenterImp @Inject constructor(
    private val serverServiceView: ServerServiceView,
    private val server: Server,
    private val telephonyStateWrapper: TelephonyStateWrapper,
    private val clock: Clock
) : ServerServicePresenter {

    private var lastState = EXTRA_STATE_IDLE
    private var callStartTime = clock.now

    override fun startServerIntentReceived() {
        serverServiceView.startForeground()
        serverServiceView.registerCallBroadcastReceiver()
        server.startServer()
    }

    override fun stopServerIntentReceived() {
        server.stopServer()
        serverServiceView.stopForeground()
    }

    override fun serviceOnDestroy() {
        server.stopServer()
    }

    override fun broadCastReceived(phoneState: String, phoneNumber: String) {
        var name = ""
        if (lastState != phoneState) {
            when (phoneState) {
                telephonyStateWrapper.ringing -> lastState = phoneState
                telephonyStateWrapper.idle -> {
                    if (lastState != EXTRA_STATE_RINGING) {
                        name = serverServiceView.queryContactForNumber(phoneNumber)
                        server.notifyEndedCall(callStartTime, clock.now, phoneNumber, name)
                    }
                    lastState = phoneState
                }
                telephonyStateWrapper.offhook -> {
                    callStartTime = clock.now
                    name = serverServiceView.queryContactForNumber(phoneNumber)
                    server.notifyOngoingCall(phoneNumber, name)
                    lastState = phoneState
                }
            }
        }
    }
}