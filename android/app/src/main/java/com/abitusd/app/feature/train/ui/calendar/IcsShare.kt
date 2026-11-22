package com.abitusd.app.feature.train.ui.calendar

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

/** Writes the calendar to the cache and opens the system share sheet. */
fun shareIcs(
    context: Context,
    ics: String,
    chooserTitle: String,
) {
    val dir = File(context.cacheDir, "ics").apply { mkdirs() }
    val file = File(dir, "abitusd.ics").apply { writeText(ics) }
    val uri = FileProvider.getUriForFile(context, context.packageName + ".files", file)
    val send =
        Intent(Intent.ACTION_SEND)
            .setType("text/calendar")
            .putExtra(Intent.EXTRA_STREAM, uri)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(send, chooserTitle))
}
