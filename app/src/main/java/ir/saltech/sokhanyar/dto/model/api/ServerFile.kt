package ir.saltech.sokhanyar.dto.model.api

data class ServerFile(
    val createTime: String,
    val expirationTime: String,
    val mimeType: String,
    val name: String,
    val sha256Hash: String,
    val sizeBytes: String,
    val state: String,
    val updateTime: String,
    val uri: String
)