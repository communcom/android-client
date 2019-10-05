package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.domain.entities.*
import io.golos.domain.mappers.CommunToEntityMapper
import javax.inject.Inject

class DiscussionDeleteResultToEntityMapper
@Inject
constructor() : CommunToEntityMapper<DeletemssgComnGalleryStruct, DeleteDiscussionResultEntity> {
    override suspend fun map(communObject: DeletemssgComnGalleryStruct): DeleteDiscussionResultEntity {
        return DeleteDiscussionResultEntity(
            DiscussionIdEntity(
                communObject.message_id.author.name,
                communObject.message_id.permlink
            )
        )
    }
}