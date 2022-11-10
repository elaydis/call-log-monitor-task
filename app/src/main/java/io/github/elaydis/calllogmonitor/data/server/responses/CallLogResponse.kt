package io.github.elaydis.calllogmonitor.data.server.responses

import java.util.*

data class CallLogResponse(
    val beginning: Date,
    val duration: Long,
    val number: String,
    val name: String,
    var timesQueried: Int = 0
)