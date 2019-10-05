package io.golos.domain.mappers

import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.requestmodel.VoteRequestModel
import java.util.*
import javax.inject.Inject

class VoteRequestModelToEntityMapper
@Inject
constructor() : ModelToEntityMapper<VoteRequestModel, VoteRequestEntity> {
    private val cash = Collections.synchronizedMap(HashMap<VoteRequestModel, VoteRequestEntity>())

    override suspend fun invoke(model: VoteRequestModel): VoteRequestEntity {
        return cash.getOrPut(model) {
            return when (model) {
                is VoteRequestModel.VoteForPostRequest -> VoteRequestEntity.VoteForAPostRequestEntity(
                    model.power,
                    DiscussionIdEntity(
                        model.discussionIdEntity.userId,
                        model.discussionIdEntity.permlink
                    )
                )
                is VoteRequestModel.VoteForComentRequest -> VoteRequestEntity.VoteForACommentRequestEntity(
                    model.power,
                    DiscussionIdEntity(
                        model.discussionIdEntity.userId,
                        model.discussionIdEntity.permlink
                    )
                )
            }
        }
    }
}