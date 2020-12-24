package com.github.kk_mats.ccx_plugin_detect_ccfsw.types

import kotlinx.serialization.Serializable

@Serializable
data class Detector(val name: String, val id: String, val version: String)

@Serializable
data class Target(val revision: String, val directory: String)

@Serializable
data class Query(val detector: Detector, val targets: List<Target>, val parameters:  Parameters)
