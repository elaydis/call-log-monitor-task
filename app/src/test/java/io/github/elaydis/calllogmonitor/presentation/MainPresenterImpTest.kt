package io.github.elaydis.calllogmonitor.presentation

import RxImmediateSchedulerRule
import io.github.elaydis.calllogmonitor.data.server.ServerStatus
import io.github.elaydis.calllogmonitor.data.server.Server
import io.github.elaydis.calllogmonitor.data.server.responses.CallLogResponse
import io.reactivex.rxjava3.core.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class MainPresenterImpTest {

    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    private val mainView = mock<MainView>()
    private val server = mock<Server>()

    private val mainPresenter = MainPresenterImp(mainView, server)

    @Before
    fun mockDefaultSubscriptions() {
        whenever(server.getServerStatusObservable()).thenReturn(Observable.empty())
        whenever(server.getNewLogEntryObservable()).thenReturn(Observable.empty())
    }

    @Test
    fun `should request permissions when start stop button is clicked`() {
        // given

        // when
        mainPresenter.startStopButtonClicked()

        // then
        verify(mainView).updateStartStopButtonState(false)
        verify(mainView).requestPermissions()
    }

    @Test
    fun `should start server when permissions are granted`() {
        // given

        // when
        mainPresenter.permissionsGranted()

        // then
        verify(mainView).updateStartStopButtonState(true)
        verify(mainView).startServerService()
    }

    @Test
    fun `should show missing permissions dialog when permissions are not granted`() {
        // given

        // when
        mainPresenter.permissionsNotGranted()

        // then
        verify(mainView).updateStartStopButtonState(true)
        verify(mainView).showMissingPermissionsDialog()
    }

    @Test
    fun `should stop sever service when start stop button is clicked and server is running`() {
        // given
        whenever(server.getServerStatusObservable())
            .thenReturn(Observable.just(ServerStatus.RUNNING))
        whenever(server.ipAddress).thenReturn("")
        mainPresenter.viewOnCreate()

        // when
        mainPresenter.startStopButtonClicked()

        // then
        verify(mainView).updateStartStopButtonState(false)
        verify(mainView).stopServerService()
    }

    @Test
    fun `should show server running when server status changed to running`() {
        // given
        val ipAddress = "ip address"
        val port = 8080
        whenever(server.getServerStatusObservable())
            .thenReturn(Observable.just(ServerStatus.RUNNING))
        whenever(server.ipAddress).thenReturn(ipAddress)
        whenever(server.port).thenReturn(port)

        // when
        mainPresenter.viewOnCreate()

        // then
        verify(mainView).showStopServerButtonText()
        verify(mainView).updateStartStopButtonState(true)
        verify(mainView).showServerRunningText()
        verify(mainView).updateIpAddressText(eq("$ipAddress:$port"))
    }

    @Test
    fun `should show server not running when server status changed to not running`() {
        // given
        whenever(server.getServerStatusObservable())
            .thenReturn(Observable.just(ServerStatus.NOT_RUNNING))

        // when
        mainPresenter.viewOnCreate()

        // then
        verify(mainView).showStartServerButtonText()
        verify(mainView).updateStartStopButtonState(true)
        verify(mainView).showServerNotRunningText()
        verify(mainView).updateIpAddressText("")
    }

    @Test
    fun `should add new call log entry`() {
        // given
        val callerName = "name"
        val duration = 123L
        val logEntry = LogEntryModel(callerName, duration)
        val callLogResponse = mock<CallLogResponse>()
        whenever(callLogResponse.name).thenReturn(callerName)
        whenever(callLogResponse.duration).thenReturn(duration)
        whenever(server.getNewLogEntryObservable())
            .thenReturn(Observable.just(callLogResponse))

        // when
        mainPresenter.viewOnCreate()

        // then
        verify(mainView).addNewCallLogEntry(eq(logEntry))
    }
}