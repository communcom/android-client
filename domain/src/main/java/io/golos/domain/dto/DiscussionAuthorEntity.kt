package io.golos.domain.dto

import io.golos.domain.Entity

data class DiscussionAuthorEntity(val userId: CyberUser, val username: String, var avatarUrl: String) : Entity