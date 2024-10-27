package ir.saltech.myapps.stutter.dto.api

import android.util.Log
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.util.fromJson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val avalAi: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaseApplication.Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val avalAi: AvalAiApi by lazy {
        RetrofitClient.avalAi.create(AvalAiApi::class.java)
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
                    val errorMsg =
                        fromJson<ErrorResponse>(errorJson?.string())
                    callback.onFailure(response = errorMsg)
                    Log.e(
                        "TAG",
                        "ERROR OCCURRED: ${errorJson?.string()} || ${response.code()} || ${response.message()}"
                    )
                }
            }

            override fun onFailure(p0: Call<T>, t: Throwable) {
                t.printStackTrace()
                callback.onFailure(t = t)
            }
        }
    )
}

