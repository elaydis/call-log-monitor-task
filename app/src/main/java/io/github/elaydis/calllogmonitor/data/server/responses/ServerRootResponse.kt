package io.github.elaydis.calllogmonitor.data.server.responses

import java.util.*

data class ServerRootResponse (val start: Date, val services: List<ServiceModel>)

data class ServiceModel(val name: String, val uri: String)