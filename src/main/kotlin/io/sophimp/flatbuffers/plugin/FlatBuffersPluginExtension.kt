package io.sophimp.flatbuffers.plugin

import org.gradle.api.tasks.Optional

open class FlatBuffersPluginExtension(
    @Optional
    var flatcPath: String? = "flatc",
    @Optional
    var language: String? = "java",
    @Optional
    var flatBuffersVersion: String? = "25.2.10"
)