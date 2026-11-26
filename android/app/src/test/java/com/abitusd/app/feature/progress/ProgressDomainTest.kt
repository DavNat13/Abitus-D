package com.abitusd.app.feature.progress

import com.abitusd.app.feature.progress.domain.AnalysisSettings
import com.abitusd.app.feature.progress.domain.ExerciseProgress
import com.abitusd.app.feature.progress.domain.OneRepMax
import com.abitusd.app.feature.progress.domain.OneRmFormula
import com.abitusd.app.feature.progress.domain.SessionPoint
import com.abitusd.app.feature.progress.domain.VerdictEngine
import com.abitusd.app.feature.progress.domain.VerdictKind
import com.abitusd.app.feature.progress.domain.WorkSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProgressDomainTest {
    private val day = 86_400_000L
    private val now = 100 * day

    @Test
    fun oneRepMaxFormulasAndLimits() {
        assertEquals(120.0, OneRepMax.estimate(100.0, 6, OneRmFormula.EPLEY)!!, 1e-9)
        assertEquals(116.129, OneRepMax.estimate(100.0, 6, OneRmFormula.BRZYCKI)!!, 1e-3)
        assertNull(OneRepMax.estimate(100.0, 11, OneRmFormula.EPLEY))
        assertNull(OneRepMax.estimate(0.0, 5, OneRmFormula.EPLEY))
    }

    @Test
    fun verdictNeedsThreeSessionsAndUsesThresholds() {
        val settings = AnalysisSettings()
        assertEquals(VerdictKind.NO_DATA, VerdictEngine.evaluate(listOf(SessionPoint(now, 100.0)), now, settings).kind)
        // +1 kg per week on 100 kg: about +4 % per four weeks.
        val rising = (0..5).map { SessionPoint(now - (5 - it) * 7 * day, 100.0 + it) }
        val v = VerdictEngine.evaluate(rising, now, settings)
        assertEquals(VerdictKind.PROGRESS, v.kind)
        assertEquals(100.0, v.first, 0.0)
        assertEquals(105.0, v.last, 0.0)
        assertEquals(4 / 102.5 * 100, v.changePct4w, 1e-9)
        val flat = (0..5).map { SessionPoint(now - (5 - it) * 7 * day, 100.0) }
        assertEquals(VerdictKind.STABLE, VerdictEngine.evaluate(flat, now, settings).kind)
        val falling = rising.map { it.copy(value = 210.0 - it.value) }
        assertEquals(VerdictKind.REGRESS, VerdictEngine.evaluate(falling, now, settings).kind)
    }

    @Test
    fun theilSenIgnoresOneOutlier() {
        val xy = listOf(0.0 to 10.0, 1.0 to 11.0, 2.0 to 12.0, 3.0 to 40.0, 4.0 to 14.0)
        assertEquals(1.0, VerdictEngine.theilSen(xy), 1e-9)
    }

    @Test
    fun weightedBodyweightUsesBodyWeightPlusAddedLoad() {
        val set = WorkSet("s1", now, 5, null, 20.0, null)
        val score = ExerciseProgress.score(set, "weighted_bodyweight_reps", 80.0, OneRmFormula.EPLEY)!!
        assertEquals(100.0 * (1 + 5 / 30.0), score, 1e-9)
        assertEquals(
            8.0,
            ExerciseProgress.score(
                WorkSet("s", now, 8, null, null, null),
                "bodyweight_reps",
                80.0,
                OneRmFormula.EPLEY,
            )!!,
            0.0,
        )
    }

    @Test
    fun sessionPointsRecordsAndWeeksSinceRecord() {
        val sets =
            listOf(
                WorkSet("a", 10 * day, 5, 100.0, null, null),
                WorkSet("a", 10 * day, 8, 90.0, null, null),
                WorkSet("b", 20 * day, 3, 110.0, null, null),
                WorkSet("c", 30 * day, 5, 95.0, null, null),
            )
        val points = ExerciseProgress.sessionPoints(sets, "weight_reps", null, OneRmFormula.EPLEY)
        assertEquals(listOf(116.7, 121.0), points.take(2).map { Math.round(it.value * 10) / 10.0 })
        val r = ExerciseProgress.records(sets, "weight_reps", null, OneRmFormula.EPLEY)
        assertEquals(110.0, r.maxLoadKg!!, 0.0)
        assertEquals(8, r.maxReps)
        assertEquals(11, ExerciseProgress.weeksSinceRecord(points, 100 * day))
        assertEquals(sets[2], ExerciseProgress.topSets(sets, "weight_reps", null, OneRmFormula.EPLEY).first().first)
    }
}
