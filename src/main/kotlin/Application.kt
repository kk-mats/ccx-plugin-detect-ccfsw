package com.github.kk_mats.ccx_plugin_detect_ccfsw

import kotlinx.cli.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.serialization.*
import kotlinx.serialization.json.*

data class WorkspacePaths(val resources: Path, val artifacts: Path)

fun parse(args: Array<String>): WorkspacePaths {
    val parser = ArgParser("ccx-plugin-detect-ccfsw")
    val workspace by parser.option(ArgType.String, null, "w").required()

    parser.parse(args)
    val w = Paths.get(workspace).toAbsolutePath()
    return WorkspacePaths(w.resolve("resources"), w.resolve("artifacts"))
}

fun main(args: Array<String>) {
    val exitCode = try {
        val paths = parse(args)
        val queryJson = paths.resources.resolve("query.json").toFile()
        CcfswController(paths, Json.decodeFromString(queryJson.readText())).exec()
    } catch (e: Exception) {
        println("[plugin] Unhandled exception: ${e.stackTraceToString()}")
        1
    }

    println("[plugin] Finished with exit code ${exitCode}.")
    kotlin.system.exitProcess(exitCode)
}