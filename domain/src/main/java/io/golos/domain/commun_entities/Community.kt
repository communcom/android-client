package io.golos.domain.commun_entities

data class Community (
    val id: String,
    val avatarUrl: String?,
    val name: String,
    val subscribersCount: Int
)
