package io.golos.domain.entities

import io.golos.domain.Entity

data class DiscussionAuthorEntity(val userId: CyberUser, val username: String) : Entity