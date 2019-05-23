package io.golos.domain.entities

import io.golos.cyber4j.model.DiscussionId
import io.golos.domain.Entity
import io.golos.domain.interactors.model.DiscussionIdModel

data class DiscussionIdEntity(
    val userId: String,
    val permlink: String
) : Entity {
    val asModel = DiscussionIdModel(userId, permlink)

    companion object {
        fun fromModel(model: DiscussionIdModel) = DiscussionIdEntity(model.userId, model.permlink)
        fun fromCyber(cyberObject: DiscussionId) =
            DiscussionIdEntity(cyberObject.userId, cyberObject.permlink)
    }
}