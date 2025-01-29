package ir.saltech.sokhanyar.model.api

import kotlinx.datetime.Clock


data class ResponseObject(
    val status: String,
    val message: String,
    val timestamp: Long = Clock.System.now().epochSeconds
)