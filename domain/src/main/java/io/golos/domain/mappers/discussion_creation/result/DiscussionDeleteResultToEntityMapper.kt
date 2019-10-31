package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.c.gallery.RemoveCGalleryStruct
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.DeleteDiscussionResultEntity
import io.golos.domain.entities.DiscussionIdEntity

object DiscussionDeleteResultToEntityMapper {
    fun map(communObject: RemoveCGalleryStruct): DeleteDiscussionResultEntity {
        return DeleteDiscussionResultEntity(
            DiscussionIdEntity(
                communObject.message_id.author.name,
                Permlink(communObject.message_id.permlink)
            )
        )
    }
}