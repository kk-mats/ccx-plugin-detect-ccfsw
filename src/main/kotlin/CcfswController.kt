package com.github.kk_mats.ccx_plugin_detect_ccfsw

import com.github.kk_mats.ccx_plugin_detect_ccfsw.types.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.Paths


class CcfswController(val cliArgs: CliArgs, val query: Query) {
    val args = arrayListOf("D")
    val resultRoot = this.cliArgs.artifacts.resolve("0")
    val repo = Paths.get(this.cliArgs.resources.toString(), "repo", this.query.targets[0].revision).toAbsolutePath()
    var cloneSource = Paths.get(this.resultRoot.toString(), "output", query.parameters.output)
    var removeCloneSource = true

    private fun appendArg(key: String, value: String) {
        this.args.add(key)
        this.args.add(value)
    }

    private fun buildCcfswArgs() {
        val d = this.repo.resolve(this.query.targets[0].directory).toAbsolutePath()
        this.appendArg("-d", d.toString())
        this.appendArg("-o", this.cloneSource.toString())
        this.appendArg("-l", this.query.parameters.language)
        this.appendArg("-w", this.query.parameters.w.toString())
        this.appendArg("-tks", this.query.parameters.tks.toString())
        this.appendArg("-rnr", this.query.parameters.rnr.toString())
        this.appendArg("-charset", this.query.parameters.charset)
        this.appendArg("-ccfsw", this.query.parameters.ccfsw)
        if(this.query.parameters.ccf) {
            this.args.add("-ccf")
        }
        if(this.query.parameters.ccfx) {
            this.args.add("-ccfx")
        }

        if (this.query.parameters.json!=null) {
            this.removeCloneSource = false
            this.appendArg("-json", this.query.parameters.json)
        } else {
            this.appendArg("-json", "-")
        }
    }

    private fun launch(): Int {
        return ProcessBuilder(Paths.get("ccfsw", "bin", "CCFinderSW").toString(),
        *this.args.toTypedArray()).start().waitFor()
    }

    private fun convert() {
        val ccfswResult = File("${this.cloneSource}_ccfsw.json")
        val result = Json.decodeFromString<CcfswDetectionResult>(ccfswResult.readText())

        val rawFileMap = HashMap<Int, String>()
        result.fileTable.forEach { rawFileMap[it.id] = it.path }

        val clonePairs = arrayListOf<ClonePair>()

        for (p in result.clonePairs) {
            val ff1 = rawFileMap[p.fragment1.fileId]
            val ff2 = rawFileMap[p.fragment2.fileId]
            if (ff1 != null && ff2 != null) {
                val f1 = Fragment(this.repo.relativize(Paths.get(ff1)).normalize(), p.fragment1.begin, p.fragment1.end)
                val f2 = Fragment(this.repo.relativize(Paths.get(ff2)).normalize(), p.fragment2.begin, p.fragment2.end)

                clonePairs.add(ClonePair(f1, f2, p.similarity / 100f))
            }
        }

        val normalized = DetectionResult(
            Environment("CCFinderSW", "1.0", query.parameters),
            clonePairs
        )

        val out = this.resultRoot.resolve("clones.json").toFile()
        out.writeText(Json.encodeToString(normalized))

        if(this.removeCloneSource) {
            ccfswResult.delete()
        }
    }

    fun Exec(): Int {
        this.buildCcfswArgs()
        val statusCode = this.launch()
        if(statusCode==0) {
            this.convert()
        }
        return statusCode
    }

}