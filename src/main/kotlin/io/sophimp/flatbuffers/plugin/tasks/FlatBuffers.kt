package io.sophimp.flatbuffers.plugin.tasks

import io.sophimp.flatbuffers.plugin.FlatBuffersPlugin
import io.sophimp.flatbuffers.plugin.FlatBuffersPluginExtension
import io.sophimp.flatbuffers.plugin.toFlatBuffersLanguage
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.execution.commandline.TaskConfigurationException
import org.gradle.process.internal.ExecException
import java.io.File
import java.util.Locale

open class FlatBuffers : DefaultTask() {

    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputDirectory
    var inputDir: File? = null

    @get:OutputDirectory
    lateinit var outputDir: File

    @get:Input
    @get:Optional
    var extraArgs: String? = null

    @TaskAction
    fun run() {
        createOutputDir()
        val flatcPath = getFlatcPath()

        getSchemas().forEach { schema ->
            println("Compiling: '${schema.absolutePath}'")
            try {
                project.exec {
                    executable(flatcPath)
                    args("--${getEffectiveLanguage()}")
                    args("-o", outputDir.absolutePath)
                    extraArgs?.takeIf { it.isNotBlank() }?.let {
                        args(*it.split("\\s+".toRegex()).toTypedArray())
                    }
                    args(schema.absolutePath)
                    workingDir(project.projectDir)
                    logger.error("Running command: '${commandLine.joinToString(" ")}'")
                }.assertNormalExitValue()
            } catch (e: ExecException) {
                throw TaskExecutionException(this, e)
            }
        }
    }

    private fun createOutputDir() {
        if (!outputDir.exists()) {
            logger.debug("Creating output directory '${outputDir.absolutePath}'.")
            outputDir.mkdirs()
        } else {
            logger.debug("Skipping creation of output directory '${outputDir.absolutePath}' as it already exists.")
        }
    }

    private fun getFlatcPath(): String {
        return project.extensions.getByType(FlatBuffersPluginExtension::class.java).flatcPath ?: "flatc"
    }

    private fun getSchemas(): List<File> {
        return resolvedInputDir.walk()
            .filter { it.isFile && it.extension == "fbs" }
            .toList()
    }

    private val resolvedInputDir: File
        get() = inputDir ?: defaultInputDir()

    private fun defaultInputDir(): File {
        logger.debug("No 'inputDir' specified, using default inputDir '${project.projectDir}/src/main/flatbuffers'.")
        return File(project.projectDir, "src/main/flatbuffers")
    }

    private fun getEffectiveLanguage(): String {
        val lang = project.extensions.getByType(FlatBuffersPluginExtension::class.java).language
        ?: throw GradleException("No language specified for FlatBuffers task")

        validateLanguage(lang)
        return lang.lowercase(Locale.getDefault())
    }

    private fun validateLanguage(lang: String) {
        if (lang.toFlatBuffersLanguage() == null) {
            throw TaskConfigurationException(
                path,
                "A problem was found with the configuration of task '$name'.",
                IllegalArgumentException("Unsupported value '$lang' specified for property 'language'.")
            )
        }
    }

    override fun getGroup(): String {
        return FlatBuffersPlugin.GROUP
    }

    override fun getDescription(): String {
        return "Assembles FlatBuffers for this project."
    }
}