package com.abitusd.app.feature.train.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PlannedVolumeTest {
    private val shares =
        listOf(
            MuscleShare("bench", 1, 1.0),
            MuscleShare("bench", 17, 0.5),
            MuscleShare("pushdown", 17, 1.0),
        )

    @Test
    fun primaryCountsFullAndSecondaryHalf() {
        val volume = PlannedVolume.perMuscle(listOf(PlannedSets("bench", 4), PlannedSets("pushdown", 3)), shares)
        assertEquals(4.0, volume.getValue(1), 0.0)
        assertEquals(5.0, volume.getValue(17), 0.0)
    }

    @Test
    fun totalSetsIsTheDayLoad() {
        assertEquals(7, PlannedVolume.totalSets(listOf(PlannedSets("bench", 4), PlannedSets("pushdown", 3))))
    }
}
