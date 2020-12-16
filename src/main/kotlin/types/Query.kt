package com.github.kk_mats.ccx_plugin_detect_ccfsw.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Parameters(
    @SerialName("-d") val target: String,
    @SerialName("-o") val output: String,
    @SerialName("-l") val language: String,
    @SerialName("-t") val t: Int,
    @SerialName("-tks") val tks: Int,
    @SerialName("-rnr") val rnr: Float,
    @SerialName("-w") val w: Int,
    @SerialName("-charset") val charset: String,
    @SerialName("-ccf") val ccf: Boolean,
    @SerialName("-ccfx") val ccfx: Boolean,
    @SerialName("-ccfsw") val ccfsw: String,
    @SerialName("-json") val json: String?,
)

@Serializable
data class Target(val revision: String, val directory: String)

@Serializable
data class Query(val targets: List<Target>, val parameters:  Parameters)
