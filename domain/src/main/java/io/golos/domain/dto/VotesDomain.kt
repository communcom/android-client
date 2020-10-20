package io.golos.domain.dto

data class VotesDomain(
    val downCount: Long,
    val upCount: Long,
    val hasUpVote: Boolean,
    val hasDownVote: Boolean
)