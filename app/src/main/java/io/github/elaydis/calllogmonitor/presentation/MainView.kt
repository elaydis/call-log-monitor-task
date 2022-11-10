package io.github.elaydis.calllogmonitor.presentation

interface MainView {

    fun startServerService()

    fun stopServerService()

    fun showServerRunningText()

    fun showServerNotRunningText()

    fun updateIpAddressText(text: String)

    fun showStartServerButtonText()

    fun showStopServerButtonText()

    fun updateStartStopButtonState(enabled: Boolean)

    fun addNewCallLogEntry(logEntryModel: LogEntryModel)

    fun requestPermissions()

    fun showMissingPermissionsDialog()

    fun openSettings()
}