package io.golos.data.api.transactions

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.ResultOk
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import javax.inject.Inject

class TransactionsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), TransactionsApi {

    override fun waitForTransaction(transactionId: String): ResultOk = commun4j.waitForTransaction(transactionId).getOrThrow()
}