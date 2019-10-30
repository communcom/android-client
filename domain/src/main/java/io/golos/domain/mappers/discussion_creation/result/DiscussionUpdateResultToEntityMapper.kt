package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.c.gallery.UpdateCGalleryStruct
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.UpdatePostResultEntity

object DiscussionUpdateResultToEntityMapper {
    fun map(communObject: UpdateCGalleryStruct): UpdatePostResultEntity {
        return UpdatePostResultEntity(
            DiscussionIdEntity(
                communObject.message_id.author.name,
                Permlink(communObject.message_id.permlink)
            )
        )
    }
}