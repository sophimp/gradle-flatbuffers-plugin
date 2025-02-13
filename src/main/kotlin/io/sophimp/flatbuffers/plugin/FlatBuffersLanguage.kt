package io.sophimp.flatbuffers.plugin

enum class FlatBuffersLanguage(val language: String) {
    C("c"),
    C_PLUS_PLUS("cpp"),
    JAVA("java"),
    KOTLIN("kotlin"),
    C_SHARP("csharp"),
    GO("go"),
    PYTHON("python"),
    JAVASCRIPT("js"),
    PHP("php"),
    DART("dart"),
    LUA("lua"),
    SWIFT("swift"),
    RUST("rust"),
    GRPC("grpc");
}

fun String.toFlatBuffersLanguage(): FlatBuffersLanguage? {
    return FlatBuffersLanguage.values().find { this == it.language }
}
