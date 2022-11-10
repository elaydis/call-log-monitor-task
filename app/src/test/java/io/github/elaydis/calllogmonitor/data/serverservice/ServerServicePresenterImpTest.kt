package io.github.elaydis.calllogmonitor.data.serverservice

import io.github.elaydis.calllogmonitor.utils.Clock
import io.github.elaydis.calllogmonitor.utils.TelephonyStateWrapper
import io.github.elaydis.calllogmonitor.data.server.Server
import org.junit.Test
import org.mockito.kotlin.*
import java.util.*

class ServerServicePresenterImpTest {

    private val server = mock<Server>()
    private val view = mock<ServerServiceView>()
    private val telephonyStateWrapper = mock<TelephonyStateWrapper>()
    private val clock = mock<Clock>()

    private val serverPresenter = ServerServicePresenterImp(view, server, telephonyStateWrapper, clock)

    @Test
    fun `should start server and show notification when start server intent received`() {
        // given

        // when
        serverPresenter.startServerIntentReceived()

        // then
        verify(view).startForeground()
        verify(view).registerCallBroadcastReceiver()
        verify(server).startServer()
    }

    @Test
    fun `should stop server and stop foreground when stop server intent received`() {
        // given

        // when
        serverPresenter.stopServerIntentReceived()

        // then
        verify(view).stopForeground()
        verify(server).stopServer()
    }

    @Test
    fun `should stop server when service is destroyed`() {
        // given

        // when
        serverPresenter.serviceOnDestroy()

        // then
        verify(server).stopServer()
    }

    @Test
    fun `should notify server when call is ongoing`() {
        // given
        val offhook = "offhook"
        val number = "0987654321"
        val name = "Gonzalo"
        whenever(view.queryContactForNumber(number)).thenReturn(name)
        whenever(telephonyStateWrapper.offhook).thenReturn(offhook)

        // when
        serverPresenter.broadCastReceived(offhook, number,)

        // then
        verify(server).notifyOngoingCall(number, name)
    }

    @Test
    fun `should notify server when call has ended`() {
        // given
        val offhook = "offhook"
        val idle = "idle"
        val phoneNumber = "098-7654321"
        val name = "Gonzalo"
        val now = mock<Date>()
        whenever(telephonyStateWrapper.offhook).thenReturn(offhook)
        whenever(telephonyStateWrapper.idle).thenReturn(idle)
        whenever(clock.now).thenReturn(now)
        whenever(view.queryContactForNumber(phoneNumber)).thenReturn(name)
        serverPresenter.broadCastReceived(offhook, phoneNumber,)

        // when
        serverPresenter.broadCastReceived(idle, phoneNumber)

        // then
        verify(server).notifyEndedCall(now, now, phoneNumber,name)
    }

    @Test
    fun `should not notify server when call was ringing but not accepted`() {
        // given
        val ringing = "ringing"
        val idle = "idle"
        whenever(telephonyStateWrapper.ringing).thenReturn(ringing)
        whenever(telephonyStateWrapper.idle).thenReturn(idle)
        serverPresenter.broadCastReceived(ringing, "",)

        // when
        serverPresenter.broadCastReceived(idle, "",)

        // then
        verify(server, never()).notifyOngoingCall(any(), any())
    }
}