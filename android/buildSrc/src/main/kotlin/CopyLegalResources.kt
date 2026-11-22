import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/** Copies the Markdown files of legal/ into raw resources so the app shows the same text as the repository. */
abstract class CopyLegalResources : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val legalDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun copy() {
        val out = outputDir.get().asFile
        out.deleteRecursively()
        DOCUMENTS.forEach { (source, target) ->
            LOCALES.forEach { (lang, dir) ->
                val file = legalDir.get().file("$source.$lang.md").asFile
                file.copyTo(File(out, "$dir/$target.md"), overwrite = true)
            }
        }
    }

    private companion object {
        val DOCUMENTS = mapOf("PRIVACY_POLICY" to "privacy_policy", "TERMS" to "terms")
        val LOCALES = mapOf("es" to "raw", "en" to "raw-en")
    }
}
