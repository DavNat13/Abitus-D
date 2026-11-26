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

/** Home screen widget: the same targets as the launcher shortcuts, so both open in one step. */
class ClockWidget : AppWidgetProvider() {
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
