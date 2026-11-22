package com.abitusd.app.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.abitusd.app.core.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Runs the sync engine when there is network, with exponential backoff (09b). */
@HiltWorker
class SyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val engine: SyncEngine,
        private val sessions: SessionManager,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            if (sessions.current() == null) return Result.success()
            return when (engine.run()) {
                SyncOutcome.DONE -> Result.success()
                SyncOutcome.RETRY -> Result.retry()
            }
        }
    }

/** Single named sync job; a new request runs after the current one, never in parallel. */
@Singleton
class SyncScheduler
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        fun requestSync() {
            val request =
                OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_SECONDS, TimeUnit.SECONDS)
                    .build()
            WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }

        fun cancel() {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        private companion object {
            const val WORK_NAME = "abitusd_sync"
            const val BACKOFF_SECONDS = 30L
        }
    }
