package com.abitusd.app.core.i18n

import java.text.Normalizer
import java.util.Locale

/** Lower case without accents, so "flexion" finds "Flexión". */
object SearchText {
    private val MARKS = Regex("\\p{Mn}+")
    private val SPACES = Regex("\\s+")

    fun normalize(value: String): String =
        Normalizer
            .normalize(value, Normalizer.Form.NFD)
            .replace(MARKS, "")
            .lowercase(Locale.ROOT)
            .replace(SPACES, " ")
            .trim()
}
