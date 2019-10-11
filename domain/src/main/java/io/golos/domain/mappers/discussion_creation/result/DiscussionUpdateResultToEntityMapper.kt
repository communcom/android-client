package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.UpdatePostResultEntity
import io.golos.domain.mappers.CommunToEntityMapper
import javax.inject.Inject

object DiscussionUpdateResultToEntityMapper {
    fun map(communObject: UpdatemssgComnGalleryStruct): UpdatePostResultEntity {
        return UpdatePostResultEntity(
            DiscussionIdEntity(
                communObject.message_id.author.name,
                Permlink(communObject.message_id.permlink)
            )
        )
    }
}