package com.github.kk_mats.ccx_plugin_detect_ccfsw.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class Parameters(
    @SerialName("d") val target: String,
    @SerialName("o") val output: String,
    @SerialName("l") val language: String,

    @SerialName("t") val t: Int?,
    @SerialName("tks") val tks: Int?,
    @SerialName("rnr") val rnr: Float?,
    @SerialName("w") val w: Int?,

    @SerialName("charset") val charset: String?,
    @SerialName("ccf") val ccf: Boolean?,
    @SerialName("ccfx") val ccfx: Boolean?,
    @SerialName("ccfsw") val ccfsw: String?,
    @SerialName("json") val json: String?,
) {
    init {
        require(this.t == null || this.t > 0) { "If not null, parameters.t must be greater than zero." }
        require(this.tks == null || this.tks > 0) { "If not null, parameters.tks must be greater than zero." }
        require(this.rnr == null || (0 < this.rnr && this.rnr <= 1)) { "If not null, parameters.rnr must in range from zero to one." }
        require(this.w == null || (0 <= this.w && this.w <= 2)) { "If not null, parameters.w must be 0, 1, or 2." }

        require(arrayOf(null, "sjis", "utf8", "euc", "auto").contains(this.charset)) { "If not null, parameters.charset must be \"sjis\", \"utf8\", \"euc\", or \"auto\"." }

        require(arrayOf(null, "pair", "set").contains(this.ccfsw)) { "If not null, parameters.ccfsw must be \"pair\", or \"set\"." }
        require(arrayOf(null, "+", "-").contains(this.json)) { "If not null, parameters.json must be \"+\" or \"-\"." }
    }
}

@Serializable
data class CcfswFileTable(val id: Int, val path: String)

@Serializable
data class CcfswFragment(@SerialName("file_id") val fileId: Int, val begin: Int, val end: Int)

@Serializable
data class CcfswClonePair(val similarity: Int, val fragment1: CcfswFragment, val fragment2: CcfswFragment)

@Serializable
data class CcfswDetectionResult(
    @SerialName("file_table") val fileTable: List<CcfswFileTable>,
    @SerialName("clone_pairs") val clonePairs: List<CcfswClonePair>
)