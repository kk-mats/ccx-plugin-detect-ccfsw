package com.github.kk_mats.ccx_plugin_detect_ccfsw

import com.github.kk_mats.ccx_plugin_detect_ccfsw.types.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.Paths


class CcfswController(private val cliArgs: CliArgs, private val query: Query) {
    private val args = arrayListOf("D")
    private val resultRoot = this.cliArgs.artifacts.resolve("0")
    private val resultOutput = this.resultRoot.resolve("output")
    private val repo = this.cliArgs.resources.resolve("repo").resolve(this.query.targets[0].revision)
    private var outputBase = this.resultOutput.resolve(query.parameters.output)
    private val ccfswJson = File("${this.outputBase}_ccfsw.json")

    private val binPath: String = Paths.get(
        "CCFinderSW-1.0", "bin", if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            "CCFinderSW.bat"
        } else {
            "CCFinderSW"
        }
    ).toAbsolutePath().toString()

    private fun appendArg(key: String, value: String) {
        this.args.add(key)
        this.args.add(value)
    }

    private fun appendOptional(key: String, value: Any?) {
        if (value != null) {
            this.appendArg(key, value.toString())
        }
    }

    // reads parameters from `query.json` and builds commandline string for CCFinderSW
    private fun buildCcfswArgs() {
        this.appendArg("-d", this.repo.resolve(this.query.targets[0].directory).normalize().toString())
        this.appendArg("-o", this.outputBase.toString())
        this.appendArg("-l", this.query.parameters.language)

        this.appendOptional("-w", this.query.parameters.w)
        this.appendOptional("-tks", this.query.parameters.tks)
        this.appendOptional("-rnr", this.query.parameters.rnr)

        this.appendOptional("-charset", this.query.parameters.charset)
        this.appendOptional("-ccfsw", this.query.parameters.ccfsw)

        if (this.query.parameters.ccf != null && this.query.parameters.ccf) {
            this.args.add("-ccf")
        }
        if (this.query.parameters.ccfx != null && this.query.parameters.ccfx) {
            this.args.add("-ccfx")
        }

        this.appendOptional("-json", this.query.parameters.json)
    }

    private fun launch(): Int {
        return try {
            println("<plugin-detector>")
            ProcessBuilder(this.binPath, *this.args.toTypedArray()).apply { this.inheritIO() }.start().waitFor()
        } finally {
            println("</plugin-detector>")
        }
    }

    // converts CCFinderSW's clone output in Json format into CCX normalized clone format
    private fun convert(): DetectionResult {
        val result = Json { ignoreUnknownKeys = true }.decodeFromString<CcfswDetectionResult>(ccfswJson.readText())

        val rawFileMap = HashMap<Int, String>()
        result.fileTable.forEach { rawFileMap[it.id] = it.path }

        val clonePairs: List<ClonePair> = result.clonePairs.mapNotNull {
            val file1 = rawFileMap[it.fragment1.fileId]
            val file2 = rawFileMap[it.fragment2.fileId]
            if (file1 != null && file2 != null) {
                ClonePair(
                    Fragment(this.repo.relativize(Paths.get(file1)).toString(), it.fragment1.begin, it.fragment1.end),
                    Fragment(this.repo.relativize(Paths.get(file2)).toString(), it.fragment2.begin, it.fragment2.end),
                    it.similarity / 100f
                )
            } else {
                null
            }
        }

        return DetectionResult(clonePairs)
    }

    fun exec(): Int {
        this.buildCcfswArgs()
        this.resultOutput.toFile().mkdirs()

        println("[plugin] Launching CCFinderSW with arguments: ${this.args}")
        val exitCode = this.launch()
        return try {
            if (exitCode == 0) {
                println("[plugin] Converting detection result from CCFinderSW format into CCX format.")
                val result = this.convert()

                println("[plugin] Serializing the detection result.")
                this.resultRoot.resolve("clones.json").toFile().writeText(Json.encodeToString(result))
            }
            exitCode
        } finally {
            if (this.query.parameters.json != null && !this.ccfswJson.delete()) {
                println("[plugin] Failed to delete ${this.ccfswJson.path}.")
            }
        }
    }

}