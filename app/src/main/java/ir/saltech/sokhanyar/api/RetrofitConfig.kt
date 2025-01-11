package ir.saltech.sokhanyar.api

import android.util.Log
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.model.api.ErrorResponse
import ir.saltech.sokhanyar.util.fromJson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    @Deprecated("Use `sokhanyar` retrofit client instead")
    val saltechAi: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaseApplication.Constants.SALTECH_AI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val saltechPay: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaseApplication.Constants.SALTECH_PAY_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val sokhanyar: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaseApplication.Constants.SOKHANYAR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    @Deprecated("Use `sokhanyar` api client instead")
    val saltechAi: SaltechAiApi by lazy {
        RetrofitClient.saltechAi.create(SaltechAiApi::class.java)
    }
    val saltechPay: SaltechPayApi by lazy {
        RetrofitClient.saltechPay.create(SaltechPayApi::class.java)
    }
    val sokhanyar: SokhanyarApi by lazy {
        RetrofitClient.sokhanyar.create(SokhanyarApi::class.java)
    }
}

interface ApiCallback<T> {
    fun onSuccessful(responseObject: T? = null)
    fun onFailure(response: ErrorResponse? = null, t: Throwable? = null)
}

inline fun <reified T> Call<T>.call(callback: ApiCallback<T>) {
    this.enqueue(
        object : Callback<T> {
            override fun onResponse(p0: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callback.onSuccessful(response.body())
                } else {
                    val errorJson = response.errorBody()
                    try {
                        val errorMsg =
                            fromJson<ErrorResponse>(errorJson?.string())
                        Log.e(
                            "TAG",
                            "ERROR OCCURRED: ${errorJson?.string()} || ${response.code()} || ${response.message()}"
                        )
                        callback.onFailure(response = errorMsg)
                    } catch (e: Exception) {
                        Log.e(
                            "TAG",
                            "ERROR OCCURRED (NOT JSON!): ${
                                response.errorBody()?.string()
                            } || ${response.code()} || ${response.message()}"
                        )
                        e.printStackTrace()
                        callback.onFailure(t = e)
                    }
                }
            }

            override fun onFailure(p0: Call<T>, t: Throwable) {
                t.printStackTrace()
                callback.onFailure(t = t)
            }
        }
    )
}

