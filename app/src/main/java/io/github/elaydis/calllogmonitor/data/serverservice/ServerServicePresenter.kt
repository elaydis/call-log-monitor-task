package io.github.elaydis.calllogmonitor.data.serverservice

interface ServerServicePresenter {

    fun startServerIntentReceived()

    fun stopServerIntentReceived()

    fun serviceOnDestroy()

    fun broadCastReceived(phoneState: String, phoneNumber: String)
}