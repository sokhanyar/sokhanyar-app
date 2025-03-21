package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.saltech.sokhanyar.data.local.entity.treatment.Advice
import ir.saltech.sokhanyar.data.local.entity.treatment.Call
import ir.saltech.sokhanyar.data.local.entity.treatment.CallParticipant
import ir.saltech.sokhanyar.data.local.entity.treatment.DailyReport
import ir.saltech.sokhanyar.data.local.entity.treatment.PracticalVoice
import ir.saltech.sokhanyar.data.local.entity.treatment.Visit
import ir.saltech.sokhanyar.data.local.entity.treatment.WeeklyReport
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticalVoiceDao {
    @Query("SELECT * FROM practical_voices WHERE patientId = :patientId")
    fun getByPatientId(patientId: String): List<PracticalVoice>

    @Query("SELECT * FROM practical_voices WHERE mediaId = :mediaId")
    fun findByMediaId(mediaId: String): PracticalVoice?

    @Insert
    fun add(voice: PracticalVoice)

    @Delete 
    fun remove(voice: PracticalVoice)

    @Update
    fun update(voice: PracticalVoice)
}

@Dao
interface AdviceDao {
    @Query("SELECT * FROM advice WHERE adviserId = :adviserId")
    fun getByAdviserId(adviserId: String): List<Advice>

    @Query("SELECT * FROM advice WHERE id = :adviceId")
    fun findById(adviceId: String): Advice?

    @Insert
    fun add(advice: Advice)

    @Delete
    fun remove(advice: Advice)

    @Update
    fun update(advice: Advice)
}

@Dao 
interface PerformanceReportDao {
    @Query("SELECT * FROM weekly_reports WHERE patientId = :patientId ORDER BY date DESC")
    fun getWeeklyReports(patientId: String): Flow<List<WeeklyReport>>

    @Query("SELECT * FROM daily_reports WHERE patientId = :patientId ORDER BY date DESC") 
    fun getDailyReports(patientId: String): Flow<List<DailyReport>>

    @Insert
    fun addWeeklyReport(weekly: WeeklyReport)

    @Insert
    fun addDailyReport(dailyReport: DailyReport)

    @Delete
    fun remove(weeklyReport: WeeklyReport)

    @Delete
    fun remove(dailyReport: DailyReport)

    @Update
    fun update(weeklyReport: WeeklyReport)

    @Update
    fun update(dailyReport: DailyReport)
}

@Dao
interface CallDao {
    @Query("SELECT * FROM call")
    fun getAll(): List<Call>

    @Query("SELECT * FROM call_participants WHERE callId = :callId")
    fun getParticipants(callId: String): List<CallParticipant>

    @Insert
    fun add(call: Call)

    @Insert 
    fun addParticipant(participant: CallParticipant)

    @Delete
    fun remove(call: Call)

    @Update
    fun update(call: Call)
}

@Dao
interface VisitDao {
    @Query("SELECT * FROM visit WHERE issuerId = :issuerId")
    fun getByIssuerId(issuerId: String): List<Visit>

    @Insert
    fun add(visit: Visit)

    @Delete
    fun remove(visit: Visit) 

    @Update
    fun update(visit: Visit)
}