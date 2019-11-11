package io.golos.domain.mappers

import io.golos.domain.dto.DiscussionIdEntity
import io.golos.domain.dto.VoteRequestEntity
import io.golos.domain.requestmodel.VoteRequestModel
import java.util.*
import javax.inject.Inject

interface VoteRequestModelToEntityMapper : ModelToEntityMapper<VoteRequestModel, VoteRequestEntity>

class VoteRequestModelToEntityMapperImpl
@Inject
constructor() : VoteRequestModelToEntityMapper {
    private val cash = Collections.synchronizedMap(HashMap<VoteRequestModel, VoteRequestEntity>())

    override fun map(model: VoteRequestModel): VoteRequestEntity {
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