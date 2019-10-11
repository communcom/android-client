package io.golos.domain.mappers.discussion_creation.result

import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.*
import io.golos.domain.mappers.CommunToEntityMapper
import javax.inject.Inject

object DiscussionDeleteResultToEntityMapper {
    fun map(communObject: DeletemssgComnGalleryStruct): DeleteDiscussionResultEntity {
        return DeleteDiscussionResultEntity(
            DiscussionIdEntity(
                communObject.message_id.author.name,
                Permlink(communObject.message_id.permlink)
            )
        )
    }
}