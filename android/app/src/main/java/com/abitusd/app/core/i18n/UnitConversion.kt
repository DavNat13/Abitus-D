package com.abitusd.app.core.i18n

/**
 * Unit conversion for the presentation layer only (golden rule 13). Data is stored in kg, m
 * and s; these functions convert to and from what the user chose to see.
 */
enum class WeightUnit { KG, LB }

enum class DistanceUnit { KM, MI }

enum class ShortDistanceUnit { M, YD }

object UnitConversion {
    const val KG_PER_LB = 0.45359237
    const val METERS_PER_MILE = 1609.344
    const val METERS_PER_YARD = 0.9144
    const val CM_PER_INCH = 2.54
    const val INCHES_PER_FOOT = 12
    private const val METERS_PER_KM = 1000.0

    fun weightFromKg(
        kg: Double,
        unit: WeightUnit,
    ): Double = if (unit == WeightUnit.KG) kg else kg / KG_PER_LB

    fun weightToKg(
        value: Double,
        unit: WeightUnit,
    ): Double = if (unit == WeightUnit.KG) value else value * KG_PER_LB

    fun distanceFromMeters(
        meters: Double,
        unit: DistanceUnit,
    ): Double = if (unit == DistanceUnit.KM) meters / METERS_PER_KM else meters / METERS_PER_MILE

    fun distanceToMeters(
        value: Double,
        unit: DistanceUnit,
    ): Double = if (unit == DistanceUnit.KM) value * METERS_PER_KM else value * METERS_PER_MILE

    fun shortDistanceFromMeters(
        meters: Double,
        unit: ShortDistanceUnit,
    ): Double = if (unit == ShortDistanceUnit.M) meters else meters / METERS_PER_YARD

    fun shortDistanceToMeters(
        value: Double,
        unit: ShortDistanceUnit,
    ): Double = if (unit == ShortDistanceUnit.M) value else value * METERS_PER_YARD

    /** Height in whole feet and remaining inches, rounded to the nearest inch. */
    fun heightToFeetInches(cm: Double): Pair<Int, Int> {
        val totalInches = Math.round(cm / CM_PER_INCH).toInt()
        return totalInches / INCHES_PER_FOOT to totalInches % INCHES_PER_FOOT
    }

    fun heightFromFeetInches(
        feet: Int,
        inches: Int,
    ): Double = (feet * INCHES_PER_FOOT + inches) * CM_PER_INCH

    /** Pace in seconds per km or per mile from a pace in seconds per km. */
    fun paceFromSecondsPerKm(
        secondsPerKm: Double,
        unit: DistanceUnit,
    ): Double = if (unit == DistanceUnit.KM) secondsPerKm else secondsPerKm * METERS_PER_MILE / METERS_PER_KM
}
