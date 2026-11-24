package com.abitusd.app.core.sync

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.planning.BlockSubstitutionEntity
import com.abitusd.app.core.database.planning.MuscleTargetEntity
import com.abitusd.app.core.database.planning.ProgramBlockEntity

/** Advanced planning tables (10f Fase 4b): blocks before their substitutions, then targets. */
fun periodizationAdapters(database: AbitusDatabase): List<SyncTableAdapter> {
    val dao = database.periodizationDao()
    return listOf(
        PlanningSyncAdapter(
            PlanningTableAccess(
                "program_blocks",
                ProgramBlockEntity.serializer(),
                dao::dirtyBlocks,
                dao::block,
                dao::upsertBlocks,
                dao::cleanBlock,
            ),
        ),
        PlanningSyncAdapter(
            PlanningTableAccess(
                "block_substitutions",
                BlockSubstitutionEntity.serializer(),
                dao::dirtySubstitutions,
                dao::substitution,
                dao::upsertSubstitutions,
                dao::cleanSubstitution,
            ),
        ),
        PlanningSyncAdapter(
            PlanningTableAccess(
                "muscle_targets",
                MuscleTargetEntity.serializer(),
                dao::dirtyTargets,
                dao::target,
                dao::upsertTargets,
                dao::cleanTarget,
            ),
        ),
    )
}
