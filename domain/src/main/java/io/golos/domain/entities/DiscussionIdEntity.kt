package io.golos.domain.entities

import io.golos.domain.Entity

data class DiscussionIdEntity(
    val userId: String,
    val permlink: String,
    val refBlockNum: Long
):Entity