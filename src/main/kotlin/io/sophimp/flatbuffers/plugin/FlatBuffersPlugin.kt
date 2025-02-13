package io.sophimp.flatbuffers.plugin

import io.sophimp.flatbuffers.plugin.tasks.FlatBuffers
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.SourceSet
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.util.internal.GUtil

class FlatBuffersPlugin : Plugin<Project> {

    companion object {
        const val GROUP = "FlatBuffers"
        const val FLAT_BUFFERS_EXTENSION_NAME = "flatbuffers"
    }

    private lateinit var extension: FlatBuffersPluginExtension
    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project
        configureProject()
    }

    private fun configureProject() {
        extension = project.extensions.create(
            FLAT_BUFFERS_EXTENSION_NAME,
            FlatBuffersPluginExtension::class.java
        )
        project.pluginManager.apply(BasePlugin::class.java)

        project.afterEvaluate {
            project.tasks.withType(FlatBuffers::class.java).forEach { task ->
                applySourceSets(task)
                reconfigurePlugins(task)
                addCleanTask(task)
            }
            applyDependencies(project)
        }
    }

    /**
     * Adds a 'clean' flatBuffers for any FlatBuffers tasks in the project.
     *
     * @param flatBuffersTask {@link FlatBuffers} task
     */
    private fun addCleanTask(flatBuffersTask: FlatBuffers) {
        val taskName = "clean${GUtil.toCamelCase(flatBuffersTask.name)}"
        project.tasks.create(taskName, Delete::class.java) {
            delete(flatBuffersTask.outputDir)
        }
    }

    /**
     * Adds source sets for the FlatBuffers input and output directories.
     *
     * @param task {@link FlatBuffers} task
     */
    private fun applySourceSets(task: FlatBuffers) {
        project.pluginManager.withPlugin("java") {
            val javaPlugin = project.extensions.getByType(JavaPluginExtension::class.java)
            val sourceSets = javaPlugin.sourceSets
            sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java {
                setSrcDirs(listOf(task.inputDir, task.outputDir))
            }
        }
    }

    /**
     * Reconfigures certain plugins to know about the FlatBuffers project structure.
     *
     * @param task {@link FlatBuffers} task
     */
    private fun reconfigurePlugins(task: FlatBuffers) {
        // Intellij specific configurations
        project.pluginManager.withPlugin("idea") {
            val idea = project.extensions.getByType(IdeaModel::class.java)
            idea.module.generatedSourceDirs.add(task.outputDir)
        }
    }

    /**
     * Adds FlatBuffers dependencies to the project.
     *
     * @param project Gradle project
     */
    private fun applyDependencies(project: Project) {
        project.pluginManager.withPlugin("java") {
            project.configurations.getByName(IMPLEMENTATION_CONFIGURATION_NAME) {
            }
            project.configurations.getByName(IMPLEMENTATION_CONFIGURATION_NAME) {
                val flatBufferVersion = extension.flatBuffersVersion
                dependencies.add(
                    project.dependencies.create("com.google.flatbuffers:flatbuffers-java:$flatBufferVersion")
                )
            }
        }
    }
}