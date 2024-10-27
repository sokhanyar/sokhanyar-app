package ir.saltech.myapps.stutter.dto.model

import com.google.gson.annotations.SerializedName

data class VoicesProperties(
    @SerializedName("challenges_count")
    val challengesCount: Int? = null,
    @SerializedName("conferences_days_count")
    val conferenceDaysCount: Int? = null,
    @SerializedName("sum_of_conferences_duration")
    val sumOfConferencesDuration: Int? = null
)
