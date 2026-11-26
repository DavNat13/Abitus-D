package com.abitusd.app.feature.progress.ui.body

import androidx.compose.ui.graphics.Path

/**
 * Own body drawing (Phase 6, no third party art): a 200 x 440 view box, left side described once
 * and mirrored on x = 100. Muscle keys match `muscle_groups.svg_key` (svg_ + slug).
 */
object BodyShapes {
    const val WIDTH = 200f
    const val HEIGHT = 440f

    /** Half silhouette: neck, shoulder, arm, flank, leg down to the foot and up the inner leg. */
    private const val OUTLINE =
        "M100 50 L91 50 L91 61 C80 65 64 66 54 71 C43 76 39 88 39 104 C37 128 37 150 40 167 " +
            "C34 192 29 221 27 248 L23 276 C26 282 32 282 35 278 L38 252 C44 226 50 199 54 172 " +
            "L60 130 C62 152 64 180 66 205 C61 222 61 238 63 252 C63 290 67 320 72 334 " +
            "C69 362 72 394 76 420 L72 432 C80 436 90 436 95 432 L93 420 C95 392 97 362 95 336 " +
            "C98 310 99 280 100 252"

    val front: Map<String, String> =
        mapOf(
            "svg_deltoid_anterior" to "M60 72 C52 76 50 86 52 98 L62 96 C64 88 64 79 60 72 Z",
            "svg_deltoid_lateral" to "M55 72 C45 77 41 90 44 106 L51 102 C48 90 50 79 59 72 Z",
            "svg_chest" to "M64 78 C74 73 91 74 98 80 L98 114 C89 122 73 121 65 111 C60 100 60 88 64 78 Z",
            "svg_biceps" to "M46 109 C42 126 42 146 46 161 L57 160 C59 144 59 125 56 106 Z",
            "svg_forearm_flexors" to "M45 168 C40 190 36 214 33 241 L43 243 C47 217 53 193 57 170 Z",
            "svg_abdominals" to "M89 124 L99 124 L99 198 C94 201 90 199 88 193 C87 170 87 146 89 124 Z",
            "svg_obliques" to "M70 120 C78 130 84 140 86 152 L86 190 C80 197 73 196 69 189 C67 166 67 140 70 120 Z",
            "svg_quadriceps" to "M68 240 C64 270 67 302 75 328 L91 329 C93 300 93 268 91 241 C84 235 75 235 68 240 Z",
            "svg_adductors" to "M92 242 C96 258 99 278 99 300 L93 304 C94 282 94 262 92 242 Z",
            "svg_tibialis_anterior" to "M76 344 C74 368 77 392 81 414 L88 414 C89 392 89 366 87 344 Z",
        )

    val back: Map<String, String> =
        mapOf(
            "svg_traps_upper" to "M99 50 L99 70 C90 72 76 74 61 74 C72 66 86 58 99 50 Z",
            "svg_traps_middle_lower" to "M99 72 L99 152 C94 142 88 122 85 102 C88 90 93 80 99 72 Z",
            "svg_deltoid_posterior" to "M58 72 C47 77 43 90 46 105 L60 98 C63 88 63 79 58 72 Z",
            "svg_rhomboids" to "M77 84 C75 98 77 112 84 122 L88 117 C84 106 82 94 83 82 Z",
            "svg_lats" to "M64 102 C62 124 68 152 83 174 L92 170 C88 150 86 128 84 110 C77 105 70 102 64 102 Z",
            "svg_lower_back" to "M90 160 C88 176 88 192 90 206 L98 206 L98 158 Z",
            "svg_triceps" to "M46 109 C42 126 42 146 46 161 L57 160 C59 144 59 125 56 106 Z",
            "svg_forearm_extensors" to "M45 168 C40 190 36 214 33 241 L43 243 C47 217 53 193 57 170 Z",
            "svg_glutes" to "M67 212 C63 228 65 246 75 256 C85 262 95 258 99 248 L99 214 C90 208 77 207 67 212 Z",
            "svg_hamstrings" to "M69 262 C67 290 69 313 75 331 L93 331 C95 311 96 287 95 262 C86 266 77 266 69 262 Z",
            "svg_calves" to "M73 340 C69 360 71 384 79 402 L90 402 C94 384 94 360 90 340 C84 335 78 335 73 340 Z",
        )

    /** Lines that suggest anatomy (abdominal segments, sternum, knee) without being regions. */
    val frontDetail = listOf("M89 146 L99 146", "M89 168 L99 168", "M99 80 L99 124", "M74 333 C80 337 88 337 92 333")
    val backDetail = listOf("M99 72 L99 206", "M72 258 C82 264 92 262 99 256")

    /** Open halves: filled they close on the midline, stroked they leave no line down the body. */
    fun outline(): Path = (parse(OUTLINE, false).also { it.addPath(parse(OUTLINE, true)) })

    /** Both sides of a left-side shape. */
    fun both(data: String): Path = parse(data, false).also { it.addPath(parse(data, true)) }

    /** Minimal SVG path parser (absolute M, L, C, Z), optionally mirrored on the body midline. */
    fun parse(
        data: String,
        mirror: Boolean,
    ): Path {
        val path = Path()
        val tokens = Regex("[MLCZ]|-?[0-9.]+").findAll(data).map { it.value }.toList()
        var i = 0

        fun x() = tokens[i++].toFloat().let { if (mirror) WIDTH - it else it }

        fun y() = tokens[i++].toFloat()
        var command = 'M'
        while (i < tokens.size) {
            val t = tokens[i]
            if (t[0].isLetter()) {
                command = t[0]
                i++
                if (command == 'Z') path.close()
                continue
            }
            when (command) {
                'M' -> path.moveTo(x(), y())
                'L' -> path.lineTo(x(), y())
                'C' -> path.cubicTo(x(), y(), x(), y(), x(), y())
            }
        }
        return path
    }
}
