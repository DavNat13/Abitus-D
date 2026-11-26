package com.abitusd.app.feature.progress.ui.body

import android.content.res.Resources
import android.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.vector.PathParser
import com.abitusd.app.R
import org.xmlpull.v1.XmlPullParser

/** Apache-2.0 artwork by Ivan Vulović; conversion and catalog grouping by Abitus-D. */
internal data class BodyRegion(
    val key: String?,
    val path: Path,
)

internal object BodyShapes {
    const val WIDTH = 340f
    const val HEIGHT = 940f
    private const val SCALE = 10f
    private const val BACK_OFFSET = 36.6f

    fun load(
        resources: Resources,
        front: Boolean,
    ): List<BodyRegion> {
        val resource = if (front) R.raw.body_muscles_front else R.raw.body_muscles_back
        val transform =
            Matrix().apply {
                setScale(SCALE, SCALE)
                postTranslate(if (front) 0f else -BACK_OFFSET * SCALE, 0f)
            }
        val regions = mutableListOf<BodyRegion>()
        resources.openRawResource(resource).use { stream ->
            val parser = android.util.Xml.newPullParser()
            parser.setInput(stream, null)
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "region") {
                    val key = parser.getAttributeValue(null, "key").takeIf { it.isNotEmpty() }
                    val path = PathParser().parsePathString(parser.getAttributeValue(null, "path")).toPath()
                    path.asAndroidPath().transform(transform)
                    regions += BodyRegion(key, path)
                }
                parser.next()
            }
        }
        return regions
    }
}
