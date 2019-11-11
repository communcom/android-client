package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryStruct
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommentCreationResultEntity
import io.golos.domain.dto.DiscussionCreationResultEntity
import io.golos.domain.dto.DiscussionIdEntity
import io.golos.domain.dto.PostCreationResultEntity

object DiscussionCreateResultToEntityMapper {
    fun map(communObject: CreateCGalleryStruct): DiscussionCreationResultEntity {
        return when (communObject.parent_id.author.name.orEmpty().isEmpty()) {

            true -> PostCreationResultEntity(
                DiscussionIdEntity(
                    communObject.message_id.author.name,
                    Permlink(communObject.message_id.permlink)
                )
            )
            false -> CommentCreationResultEntity(
                DiscussionIdEntity(
                    communObject.message_id.author.name,
                    Permlink(communObject.message_id.permlink)
                ),
                DiscussionIdEntity(
                    communObject.parent_id.author.name,
                    Permlink(communObject.parent_id.permlink)
                )
            )
        }
    }
}