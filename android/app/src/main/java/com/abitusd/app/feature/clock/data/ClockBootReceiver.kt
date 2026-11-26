package com.abitusd.app.feature.clock.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Exported only for the protected system broadcasts of its filter; any other action is ignored.
 * Forwards to the private [ClockReceiver], which re-arms the clock and calendar reminders.
 */
class ClockBootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        context.sendBroadcast(Intent(context, ClockReceiver::class.java).setAction(action))
    }
}
