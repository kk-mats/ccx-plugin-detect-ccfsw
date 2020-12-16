package com.github.kk_mats.ccx_plugin_detect_ccfsw

import kotlinx.cli.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.github.kk_mats.ccx_plugin_detect_ccfsw.types.*

data class CliArgs(val resources: Path, val artifacts: Path)

fun parse(args: Array<String>): CliArgs {
    val parser = ArgParser("ccx-plugin-detect-ccfsw")
    val resources by parser.option(ArgType.String).required()
    val artifacts by parser.option(ArgType.String).required()

    parser.parse(args)

    return CliArgs(Paths.get(resources).toAbsolutePath(), Paths.get(artifacts).toAbsolutePath())
}

fun main(args: Array<String>) {
    val cliArgs = parse(args)
    val queryJson = cliArgs.resources.resolve("query.json").toFile()
    val query = Json.decodeFromString<Query>(queryJson.readText())

    kotlin.system.exitProcess(CcfswController(cliArgs, query).Exec())
}