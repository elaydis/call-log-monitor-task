package io.github.elaydis.calllogmonitor.presentation

import android.util.Log
import io.github.elaydis.calllogmonitor.data.server.Server
import io.github.elaydis.calllogmonitor.data.server.ServerStatus
import io.github.elaydis.calllogmonitor.utils.disposedBy
import io.github.elaydis.calllogmonitor.utils.scheduleIO
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class MainPresenterImp @Inject constructor(
    private val mainView: MainView,
    private val server: Server,
) : MainPresenter {

    private val disposables = CompositeDisposable()
    private var serverStatus = ServerStatus.NOT_RUNNING

    override fun viewOnCreate() {
        subscribeToServerStatusEvents()
        subscribeToNewLogEntryEvents()
    }

    override fun startStopButtonClicked() {
        Log.d(MainPresenterImp::class.simpleName, "### start stop button clicked")
        mainView.updateStartStopButtonState(false)
        when (serverStatus) {
            ServerStatus.RUNNING -> mainView.stopServerService()
            else -> mainView.requestPermissions()
        }
    }

    override fun permissionsGranted() {
        mainView.startServerService()
        mainView.updateStartStopButtonState(true)
    }

    override fun permissionsNotGranted() {
        mainView.showMissingPermissionsDialog()
        mainView.updateStartStopButtonState(true)
    }

    override fun missingPermissionsDialogPositiveButtonClicked() {
        mainView.openSettings()
    }

    override fun viewOnDestroy() {
        disposables.dispose()
    }

    private fun subscribeToServerStatusEvents() {
        server.getServerStatusObservable()
            .scheduleIO()
            .subscribe {
                Log.d(MainPresenterImp::class.simpleName, "### server status event $it")
                serverStatus = it
                when (it) {
                    ServerStatus.RUNNING -> showServerRunning()
                    else -> showServerNotRunning()
                }
            }
            .addToDisposables()
    }

    private fun subscribeToNewLogEntryEvents() {
        server.getNewLogEntryObservable()
            .scheduleIO()
            .subscribe {
                val displayName = it.name.ifEmpty { it.number }
                val logEntryModel = LogEntryModel(displayName, it.duration)
                mainView.addNewCallLogEntry(logEntryModel)
            }
            .addToDisposables()
    }

    private fun showServerRunning() {
        mainView.showStopServerButtonText()
        mainView.updateStartStopButtonState(true)
        mainView.showServerRunningText()
        mainView.updateIpAddressText("${server.ipAddress}:${server.port}")
    }

    private fun showServerNotRunning() {
        mainView.showStartServerButtonText()
        mainView.updateStartStopButtonState(true)
        mainView.showServerNotRunningText()
        mainView.updateIpAddressText("")
    }

    private fun Disposable.addToDisposables() = disposedBy(disposables)
}