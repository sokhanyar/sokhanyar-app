package ir.saltech.sokhanyar.dto.model.data.general

import com.google.gson.annotations.SerializedName
import ir.saltech.sokhanyar.dto.model.api.ServerFile
import ir.saltech.sokhanyar.dto.model.api.VoiceResponse
import java.io.File

data class Voice(
    val selectedFile: File? = null,
    val response: VoiceResponse? = null,
    @SerializedName("file")
    val serverFile: ServerFile? = null,
    val error: Exception? = null,
    val progress: Int? = null
)
