package io.golos.cyber_android.ui.dto

data class Votes(
    var downCount: Long,
    var upCount: Long,
    val hasUpVote: Boolean,
    val hasDownVote: Boolean
)