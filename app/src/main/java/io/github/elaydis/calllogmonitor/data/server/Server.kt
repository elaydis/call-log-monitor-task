package io.github.elaydis.calllogmonitor.data.server

import io.github.elaydis.calllogmonitor.data.server.responses.CallLogResponse
import io.reactivex.rxjava3.core.Observable
import java.util.*

interface Server {

    val ipAddress: String
    val port: Int

    fun startServer()

    fun stopServer()

    fun getServerStatusObservable(): Observable<ServerStatus>

    fun getNewLogEntryObservable(): Observable<CallLogResponse>

    fun notifyOngoingCall(number: String, name: String)

    fun notifyEndedCall(startTime: Date, endTime: Date, number: String, name: String)
}