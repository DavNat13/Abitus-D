package com.abitusd.app.core.i18n

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConversionTest {
    private val delta = 1e-6

    @Test
    fun weightRoundTripsExactly() {
        assertEquals(220.462262, UnitConversion.weightFromKg(100.0, WeightUnit.LB), delta)
        assertEquals(45.359237, UnitConversion.weightToKg(100.0, WeightUnit.LB), delta)
        assertEquals(
            82.5,
            UnitConversion.weightToKg(UnitConversion.weightFromKg(82.5, WeightUnit.LB), WeightUnit.LB),
            delta,
        )
        assertEquals(60.0, UnitConversion.weightFromKg(60.0, WeightUnit.KG), delta)
    }

    @Test
    fun distanceUsesInternationalMile() {
        assertEquals(1.0, UnitConversion.distanceFromMeters(1609.344, DistanceUnit.MI), delta)
        assertEquals(42.195, UnitConversion.distanceFromMeters(42195.0, DistanceUnit.KM), delta)
        assertEquals(5000.0, UnitConversion.distanceToMeters(5.0, DistanceUnit.KM), delta)
    }

    @Test
    fun shortDistanceInYards() {
        assertEquals(25.0, UnitConversion.shortDistanceToMeters(27.340332, ShortDistanceUnit.YD), 1e-4)
        assertEquals(50.0, UnitConversion.shortDistanceFromMeters(50.0, ShortDistanceUnit.M), delta)
    }

    @Test
    fun heightInFeetAndInches() {
        assertEquals(5 to 11, UnitConversion.heightToFeetInches(180.0))
        assertEquals(180.34, UnitConversion.heightFromFeetInches(5, 11), delta)
    }

    @Test
    fun paceConvertsPerMile() {
        assertEquals(482.8032, UnitConversion.paceFromSecondsPerKm(300.0, DistanceUnit.MI), 1e-4)
    }
}
