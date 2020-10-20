package io.golos.cyber_android.ui.dto

data class Votes(
    var downCount: Long,
    var upCount: Long,
    var hasUpVote: Boolean,
    var hasDownVote: Boolean
)