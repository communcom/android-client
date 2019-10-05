package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.domain.entities.CommentCreationResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.PostCreationResultEntity
import io.golos.domain.mappers.CommunToEntityMapper
import javax.inject.Inject

class DiscussionCreateResultToEntityMapper
@Inject
constructor() :
    CommunToEntityMapper<CreatemssgComnGalleryStruct, DiscussionCreationResultEntity> {
    override suspend fun map(communObject: CreatemssgComnGalleryStruct): DiscussionCreationResultEntity {
        return when (communObject.parent_id.author.name.orEmpty().isEmpty()) {

            true -> PostCreationResultEntity(
                DiscussionIdEntity(
                    communObject.message_id.author.name,
                    communObject.message_id.permlink
                )
            )
            false -> CommentCreationResultEntity(
                DiscussionIdEntity(
                    communObject.message_id.author.name,
                    communObject.message_id.permlink
                ),
                DiscussionIdEntity(
                    communObject.parent_id.author.name,
                    communObject.parent_id.permlink
                )
            )
        }
    }
}