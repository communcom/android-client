package io.golos.domain.rules

import io.golos.cyber4j.model.CreateDiscussionResult
import io.golos.cyber4j.model.DiscussionCreateMetadata
import io.golos.cyber4j.model.Tag
import io.golos.cyber4j.utils.toCyberName
import io.golos.domain.entities.CommentCreationResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.PostCreationResultEntity
import io.golos.domain.model.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
class RequestEntityToArgumentsMapper : EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest> {

    override suspend fun invoke(entity: DiscussionCreationRequestEntity): DiscussionCreateRequest {
        return when (entity) {
            is PostCreationRequestEntity -> CreatePostRequest(
                entity.title, entity.body,
                entity.tags.map { Tag(it) }, DiscussionCreateMetadata(emptyList(), emptyList()),
                emptyList(), true, 0
            )
            is CommentCreationRequestEntity -> CreateCommentRequest(
                entity.body, entity.parentId.userId.toCyberName(),
                entity.parentId.permlink, entity.parentId.refBlockNum,
                entity.tags.map { Tag(it) }, DiscussionCreateMetadata(emptyList(), emptyList()),
                emptyList(), true, 0
            )
        }
    }
}


class DiscussionCreateResultToEntityMapper :
    CyberToEntityMapper<CreateDiscussionResult, DiscussionCreationResultEntity> {
    override suspend fun invoke(cyberObject: CreateDiscussionResult): DiscussionCreationResultEntity {
        return when (cyberObject.parent_id?.author?.name.orEmpty().isEmpty()) {

            true -> PostCreationResultEntity(
                DiscussionIdEntity(
                    cyberObject.message_id.author.name,
                    cyberObject.message_id.permlink, cyberObject.message_id.ref_block_num
                )
            )
            false -> CommentCreationResultEntity(
                DiscussionIdEntity(
                    cyberObject.message_id.author.name,
                    cyberObject.message_id.permlink, cyberObject.message_id.ref_block_num
                ),
                DiscussionIdEntity(
                    cyberObject.parent_id.author.name,
                    cyberObject.parent_id.permlink, cyberObject.parent_id.ref_block_num
                )
            )
        }
    }
}