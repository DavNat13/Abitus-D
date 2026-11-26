package com.abitusd.app.feature.clock.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.abitusd.app.MainActivity
import com.abitusd.app.R
import com.abitusd.app.core.navigation.AppShortcuts
import kotlinx.coroutines.launch

/** Home screen widget: today's workout and the same targets as the launcher shortcuts. */
@dagger.hilt.android.AndroidEntryPoint
class ClockWidget : AppWidgetProvider() {
    @javax.inject.Inject lateinit var database: com.abitusd.app.core.database.AbitusDatabase

    override fun onUpdate(
        context: Context,
        manager: AppWidgetManager,
        ids: IntArray,
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_clock)
        views.setOnClickPendingIntent(R.id.widget_clock, open(context, AppShortcuts.ACTION_CLOCK, REQUEST_CLOCK))
        views.setOnClickPendingIntent(
            R.id.widget_stopwatch,
            open(context, AppShortcuts.ACTION_STOPWATCH, REQUEST_STOPWATCH),
        )
        ids.forEach { manager.updateAppWidget(it, views) }
        val pending = goAsync()
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val today =
                    database.clockDao().todayWorkout(
                        java.time.LocalDate
                            .now()
                            .toString(),
                    )
                val text =
                    today?.let {
                        context.getString(
                            R.string.widget_today,
                            it.ifBlank { context.getString(R.string.nav_train) },
                        )
                    }
                views.setTextViewText(R.id.widget_today, text ?: context.getString(R.string.widget_rest))
                ids.forEach { manager.updateAppWidget(it, views) }
            } finally {
                pending.finish()
            }
        }
    }

    private fun open(
        context: Context,
        action: String,
        request: Int,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            request,
            Intent(context, MainActivity::class.java).setAction(action).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private companion object {
        const val REQUEST_CLOCK = 81
        const val REQUEST_STOPWATCH = 82
    }
}
