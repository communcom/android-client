package io.golos.domain.mappers

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.requestmodel.VoteRequestModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@ApplicationScope
class VoteRequestEntityToModelMapper
@Inject
constructor() :
    EntityToModelMapper<VoteRequestEntity, VoteRequestModel> {
    private val cash =
        Collections.synchronizedMap(HashMap<VoteRequestEntity, VoteRequestModel>())

    override suspend fun invoke(entity: VoteRequestEntity): VoteRequestModel {
        return cash.getOrPut(entity) {
            when (entity) {
                is VoteRequestEntity.VoteForAPostRequestEntity -> VoteRequestModel.VoteForPostRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink
                    )
                )
                is VoteRequestEntity.VoteForACommentRequestEntity -> VoteRequestModel.VoteForComentRequest(
                    entity.power,
                    DiscussionIdModel(
                        entity.discussionIdEntity.userId,
                        entity.discussionIdEntity.permlink
                    )
                )
            }
        }
    }
}