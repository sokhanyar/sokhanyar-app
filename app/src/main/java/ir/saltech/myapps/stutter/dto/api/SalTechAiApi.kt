package ir.saltech.myapps.stutter.dto.api

import ir.saltech.myapps.stutter.dto.model.data.general.Voice
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val AUTHORIZATION_HEADER = "x-goog-api-key"

interface SalTechAiApi {

    @Multipart
    @POST("upload/v1beta/files")
    fun uploadVoice(
        @Header(AUTHORIZATION_HEADER) apiKey: String,
        @Part file : MultipartBody.Part
    ) : Call<Voice>
}