package ir.saltech.sokhanyar.model.data.reports

import com.google.gson.annotations.SerializedName

data class VoicesProperties(
    @SerializedName("challenges_count")
    val challengesCount: Int? = null,
    @SerializedName("sum_of_challenges_duration")
    val sumOfChallengesDuration: Int? = null,
    @SerializedName("conferences_days_count")
    val conferenceDaysCount: Int? = null,
    @SerializedName("sum_of_conferences_duration")
    val sumOfConferencesDuration: Int? = null

)
