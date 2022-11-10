package io.github.elaydis.calllogmonitor.data.server.responses

data class OngoingCallResponse(
    val ongoing: Boolean = false,
    val number: String? = null,
    val name: String? = null
)