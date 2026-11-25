package com.abitusd.app.feature.onboarding.domain

import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import java.time.Year

/** Input bounds, not sporting recommendations. Missing answers are always allowed. */
fun AthleteProfileEntity.valid(): Boolean =
    listOf(
        birthYear == null || birthYear in MIN_YEAR..(Year.now().value - ADULT_AGE),
        heightM == null || heightM in MIN_HEIGHT..MAX_HEIGHT,
        weightKg == null || weightKg in MIN_WEIGHT..MAX_WEIGHT,
        trainingYears == null || trainingYears in 0..MAX_YEARS,
        daysPerWeek == null || daysPerWeek in 1..DAYS,
        sessionDurationS == null || sessionDurationS in MIN_SESSION..MAX_SESSION,
        pushUps == null || pushUps in 0..MAX_REPS,
        pullUps == null || pullUps in 0..MAX_REPS,
        plankDurationS == null || plankDurationS in 0..MAX_DURATION,
    ).all { it }

private const val MIN_YEAR = 1900
private const val ADULT_AGE = 18
private const val MIN_HEIGHT = 0.5
private const val MAX_HEIGHT = 2.8
private const val MIN_WEIGHT = 20.0
private const val MAX_WEIGHT = 500.0
private const val MAX_YEARS = 100
private const val DAYS = 7
private const val MIN_SESSION = 300
private const val MAX_SESSION = 14400
private const val MAX_REPS = 1000
private const val MAX_DURATION = 86400
