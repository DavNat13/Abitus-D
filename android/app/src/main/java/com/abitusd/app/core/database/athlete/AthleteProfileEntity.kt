package com.abitusd.app.core.database.athlete

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.abitusd.app.core.database.planning.SyncRow
import kotlinx.serialization.Serializable

/** The account UUID identifies its single profile across devices. Measurements use SI units. */
@Serializable
@Entity(tableName = "athlete_profiles")
data class AthleteProfileEntity(
    @PrimaryKey override val id: String,
    val birthYear: Int? = null,
    val sex: String? = null,
    val heightM: Double? = null,
    val weightKg: Double? = null,
    val trainingYears: Int? = null,
    val level: String? = null,
    val disciplines: String = "",
    val goal: String? = null,
    val secondaryGoal: String? = null,
    val daysPerWeek: Int? = null,
    val sessionDurationS: Int? = null,
    val place: String? = null,
    val equipment: String = "",
    val healthConsent: Boolean = false,
    val healthLimited: Boolean? = null,
    val injuries: String = "",
    val pushUps: Int? = null,
    val pullUps: Int? = null,
    val plankDurationS: Int? = null,
    val onboardingDone: Boolean = false,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow
