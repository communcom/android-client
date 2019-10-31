package io.golos.data.repositories.vote

import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.api.vote.VoteApi
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.VoteRequestModelToEntityMapper
import io.golos.domain.requestmodel.VoteRequestModel
import javax.inject.Inject

class VoteRepositoryImpl
@Inject
constructor(
    voteApi: VoteApi,
    transactionsApi: TransactionsApi,
    private val voteModelToEntityMapper: VoteRequestModelToEntityMapper
) : VoteRepositoryBase(
    voteApi,
    transactionsApi
),  VoteRepository {

    override fun upVote(entityId: DiscussionIdModel) = vote(entityId, 10_000)

    override fun downVote(entityId: DiscussionIdModel) = vote(entityId, -10_000)

    override fun unVote(entityId: DiscussionIdModel) {
        unVote()    // It's a stub
    }

    private fun vote(postId: DiscussionIdModel, power: Short) {
        val voteParams = voteModelToEntityMapper.map(VoteRequestModel.VoteForPostRequest(power, postId))
        vote(voteParams)
    }
}