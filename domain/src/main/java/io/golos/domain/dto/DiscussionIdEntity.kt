package io.golos.domain.dto

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.Entity
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.use_cases.model.DiscussionIdModel

data class DiscussionIdEntity(
    val userId: String,
    val permlink: Permlink
) : Entity {
    val asModel = DiscussionIdModel(userId, permlink)

    companion object {
        fun fromModel(model: DiscussionIdModel) = DiscussionIdEntity(model.userId, model.permlink)
        fun fromCyber(cyberObject: DiscussionId) =
            DiscussionIdEntity(cyberObject.userId.name, Permlink(cyberObject.permlink))
    }
}