package com.github.kk_mats.ccx_plugin_detect_ccfsw.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CcfswCloneDetector(val name: String, val version: String, val parameters: HashMap<String, Any>)

@Serializable
data class CcfswEnvironment(
    @SerialName("clone_detector") val cloneDetector: CcfswCloneDetector,
    val target: HashMap<String, String>
)

@Serializable
data class CcfswFileTable(val id: Int, val path: String)

@Serializable
data class CcfswFragment(@SerialName("file_id") val fileId: Int, val begin: Int, val end: Int)

@Serializable
data class CcfswClonePair(val similarity: Int, val fragment1: CcfswFragment, val fragment2: CcfswFragment)

@Serializable
data class CcfswDetectionResult(
    val environment: CcfswEnvironment,
    @SerialName("file_table") val fileTable: List<CcfswFileTable>,
    @SerialName("clone_pairs") val clonePairs: List<CcfswClonePair>
)