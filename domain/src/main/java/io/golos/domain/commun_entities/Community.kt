package io.golos.domain.commun_entities

data class Community (
    val id: String,
    val name: String,
    val followersQuantity: Int,
    val logoUrl: String
)
