package ir.saltech.myapps.stutter.dto.api

import ir.saltech.myapps.stutter.dto.model.ai.Credit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

private const val AUTHORIZATION_HEADER = "Authorization"

interface AvalAiApi {

    @GET("/user/credit")
    fun getCredit(@Header(AUTHORIZATION_HEADER) apiKey: String): Call<Credit>

}