package io.github.elaydis.calllogmonitor.data.server

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.elaydis.calllogmonitor.data.server.responses.CallLogResponse
import io.github.elaydis.calllogmonitor.data.server.responses.OngoingCallResponse
import io.github.elaydis.calllogmonitor.data.server.responses.ServerRootResponse
import io.github.elaydis.calllogmonitor.data.server.responses.ServiceModel
import io.github.elaydis.calllogmonitor.utils.Clock
import io.github.elaydis.calllogmonitor.utils.NetworkUtils
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerImp @Inject constructor(private val clock: Clock, moshi: Moshi) :
    Server {

    private val ongoingCallResponseAdapter: JsonAdapter<OngoingCallResponse> =
        moshi.adapter(OngoingCallResponse::class.java).serializeNulls()
    private val logEntryModelListType =
        Types.newParameterizedType(List::class.java, CallLogResponse::class.java)
    private val logEntryModelListAdapter =
        moshi.adapter<List<CallLogResponse>>(logEntryModelListType)
    private val rootResponseAdapter = moshi.adapter(ServerRootResponse::class.java)
    private val serverStatusSubject = BehaviorSubject.createDefault(ServerStatus.NOT_RUNNING)
    private val callLogSubject = BehaviorSubject.create<CallLogResponse>()
    private val callLogs = mutableListOf<CallLogResponse>()

    private var ongoingCall = OngoingCallResponse()

    private lateinit var server: NettyApplicationEngine
    private lateinit var serverStartTime: Date

    override val ipAddress: String
        get() = NetworkUtils.getLocalIpAddress().orEmpty()

    override val port: Int
        get() = 8080

    override fun startServer() {
        createServer()
        serverStartTime = clock.now
        CoroutineScope(Dispatchers.IO).launch {
            server.start(true)
        }
        subscribeToServerEvents()

    }

    override fun stopServer() {
        server.stop(1000, 1000)
    }

    override fun getServerStatusObservable(): Observable<ServerStatus> {
        return serverStatusSubject.hide()
    }

    override fun getNewLogEntryObservable(): Observable<CallLogResponse> {
        return callLogSubject.hide()
    }

    override fun notifyOngoingCall(number: String, name: String) {
        ongoingCall = OngoingCallResponse(number = number, name = name)
    }

    override fun notifyEndedCall(startTime: Date, endTime: Date, number: String, name: String) {
        ongoingCall = OngoingCallResponse()
        val duration = endTime.time - startTime.time
        val logEntry = CallLogResponse(startTime, duration, number, name)
        callLogs.add(logEntry)
        callLogSubject.onNext(logEntry)
    }

    private fun subscribeToServerEvents() {
        server.environment.monitor.subscribe(ApplicationStarted) {
            serverStatusSubject.onNext(ServerStatus.RUNNING)
        }
        server.environment.monitor.subscribe(ApplicationStopped) {
            serverStatusSubject.onNext(ServerStatus.NOT_RUNNING)
            server.environment.monitor.unsubscribe(ApplicationStarted) {}
            server.environment.monitor.unsubscribe(ApplicationStopped) {}
        }
    }

    private fun createServer() {
        server = embeddedServer(Netty, port = port) {
            install(CallLogging)
            install(CORS) {
                method(HttpMethod.Get)
                method(HttpMethod.Post)
                method(HttpMethod.Delete)
                anyHost()
            }
            routing {
                get {
                    val serverRootResponse = ServerRootResponse(
                        serverStartTime,
                        listOf(
                            ServiceModel("status", "$ipAddress$port/status"),
                            ServiceModel("logs", "$ipAddress$port/logs")
                        )
                    )
                    val rootResponseJson = rootResponseAdapter.toJson(serverRootResponse)
                    call.respondText(rootResponseJson)
                }
                get("/status") {
                    val ongoingCallJson = ongoingCallResponseAdapter.toJson(ongoingCall)
                    call.respond(ongoingCallJson)
                }
                get("/logs") {
                    val logsJson = logEntryModelListAdapter.toJson(callLogs)
                    call.respond(logsJson)
                    callLogs.forEach { it.timesQueried++ }
                }
            }
        }
    }
}