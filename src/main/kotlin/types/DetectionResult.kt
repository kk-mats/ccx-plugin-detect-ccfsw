package com.github.kk_mats.ccx_plugin_detect_ccfsw.types

import java.nio.file.Path

data class Fragment(val file: Path, val begin: Int, val end: Int)

data class ClonePair(var f1: Fragment, var f2: Fragment, val similarity: Float)

data class Environment(val detector: String, val version: String, val parameters: Parameters)

data class DetectionResult(
	val environment: Environment,
	val clonePairs: ArrayList<ClonePair>
)
