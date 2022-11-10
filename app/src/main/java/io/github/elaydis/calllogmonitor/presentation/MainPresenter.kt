package io.github.elaydis.calllogmonitor.presentation

interface MainPresenter {

    fun viewOnCreate()

    fun startStopButtonClicked()

    fun permissionsGranted()

    fun permissionsNotGranted()

    fun missingPermissionsDialogPositiveButtonClicked()

    fun viewOnDestroy()
}