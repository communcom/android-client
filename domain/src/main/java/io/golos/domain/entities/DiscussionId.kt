package io.golos.domain.entities

data class DiscussionId(
    val userId: String,
    val permlink: String,
    val refBlockNum: Long
)