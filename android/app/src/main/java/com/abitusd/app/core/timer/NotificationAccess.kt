package com.abitusd.app.core.timer

import android.content.Context
import androidx.core.app.NotificationManagerCompat

/**
 * Whether the app may post notifications. POST_NOTIFICATIONS only exists from Android 13; below it
 * a permission check always fails, so the notification settings switch is the right test everywhere.
 */
fun canNotify(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()
