package io.golos.data.repositories.vote

import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.api.vote.VoteApi
import io.golos.data.toCyberName
import io.golos.domain.entities.VoteRequestEntity

abstract class VoteRepositoryBase
constructor(
    private val voteApi: VoteApi,
    private val transactionsApi: TransactionsApi
) {

    protected fun vote(params: VoteRequestEntity) {
        val transactionResult = voteApi.vote(
            params.discussionIdEntity.userId.toCyberName(),
            params.discussionIdEntity.permlink,
            params.power
        )
        transactionsApi.waitForTransaction(transactionResult.transaction_id)
    }

    protected fun unVote() {
        voteApi.unVote()            // It's a stub
    }
}