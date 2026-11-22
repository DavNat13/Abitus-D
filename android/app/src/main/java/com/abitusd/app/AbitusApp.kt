package com.abitusd.app

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.abitusd.app.core.i18n.AppLanguage
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/** Application entry. WorkManager uses Hilt to build workers (on-demand initialization). */
@HiltAndroidApp
class AbitusApp :
    Application(),
    Configuration.Provider {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(AppLanguage.wrap(base))
    }

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
