package com.abitusd.app.core.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.SyncStateDao
import com.abitusd.app.core.database.catalog.CatalogWriteDao
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.database.planning.PlanningDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun database(
        @ApplicationContext context: Context,
    ): AbitusDatabase =
        Room
            .databaseBuilder(context, AbitusDatabase::class.java, AbitusDatabase.NAME)
            .setDriver(BundledSQLiteDriver())
            .build()

    @Provides
    fun syncStateDao(database: AbitusDatabase): SyncStateDao = database.syncStateDao()

    @Provides
    fun catalogWriteDao(database: AbitusDatabase): CatalogWriteDao = database.catalogWriteDao()

    @Provides
    fun libraryDao(database: AbitusDatabase): LibraryDao = database.libraryDao()

    @Provides
    fun planningDao(database: AbitusDatabase): PlanningDao = database.planningDao()

    @Provides
    fun sessionDao(database: AbitusDatabase): com.abitusd.app.core.database.session.SessionDao = database.sessionDao()
}
