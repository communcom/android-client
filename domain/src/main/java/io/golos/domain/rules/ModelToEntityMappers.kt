package io.golos.domain.rules

import io.golos.domain.entities.DiscussionIdEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.model.VoteRequestModel

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
class VoteRequestModelToEntityMapper : ModelToEntityMapper<VoteRequestModel, VoteRequestEntity> {
    private val cash = HashMap<VoteRequestModel, VoteRequestEntity>()
    override suspend fun invoke(model: VoteRequestModel): VoteRequestEntity {
        return cash.getOrPut(model) {
            return when (model) {
                is VoteRequestModel.VoteForPostRequest -> VoteRequestEntity.VoteForAPostRequestEntity(
                    model.power,
                    DiscussionIdEntity(
                        model.discussionIdEntity.userId,
                        model.discussionIdEntity.permlink,
                        model.discussionIdEntity.refBlockNum
                    )
                )
                is VoteRequestModel.VoteForComentRequest -> VoteRequestEntity.VoteForACommentRequestEntity(
                    model.power,
                    DiscussionIdEntity(
                        model.discussionIdEntity.userId,
                        model.discussionIdEntity.permlink,
                        model.discussionIdEntity.refBlockNum
                    )
                )
            }
        }
    }
}